package models;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import models.ingest.FileLocation;
import models.ingest.ThingCopy;
import nu.xom.Document;
import nu.xom.Element;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.libs.Json;
import utils.DbUtil;
import utils.GeneralUtil;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlRow;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "dlThing")
public class Thing extends Model implements AbstractThing {
    public static enum ThingType {
        WORK, COPY, FILE
    }
    
    public static enum ThingRelationship {
        ISPARTOF, ISCOPYOF, ISFILEOF, EXISTSON;

        public int code() {
            return this.ordinal() + 1;
        }
    }
    
    public static enum CopyRole {
        ACCESS_COPY("ac"), MASTER_COPY("m"), OCR_JSON_COPY("oc"), OCR_ALTO_COPY("at"), OCR_METS_COPY("mt");
        
        private String code;
        private CopyRole(String code) {
            this.code = code;
        }
        
        public String code() {
            return code;
        }
        
        public int idx() {
            return this.ordinal();
        }
        
        public static CopyRole isCopyRole(String copyRole) {
            for (CopyRole _copyRole : CopyRole.values()) {
                if (_copyRole.name().equalsIgnoreCase(copyRole))
                    return _copyRole;
            }
            return null;
        }
    }
    
    public static final String ACCESS_COPY = "ac";
    
    // @GremlinGroovy("g.v(long)")
    public static Finder<Long, Thing> find = new Finder<Long, Thing>(
            Long.class, Thing.class);

    public static Finder<String, Thing> findByPI = new Finder<String, Thing>(
            String.class, Thing.class) {
        // find Thing by its PI
        @Override
        public Thing byId(String id) {
        	System.out.println("in findByPI. id is " + id);
            if (id == null) {
            	System.out.println("in findByPI. id is null.");
                throw new InvalidParameterException("id is null.");
            }
            Thing t = Thing.find.where().eq("pi", id).findUnique();
            if (t == null) {
            	System.out.println("in findByPI. thing is null");
                throw new ThingNotFoundException("No thing is found for " + id
                        + ".");
            }

            System.out.println("in findByPI. return a not null thing.");
            return t;
        }
    };

    public static final String OCR_COPY = "oc";
    
    public static final ObjectMapper jsonMapper = new ObjectMapper();

    @Transient
    public String _description;

    @Transient
    public String bibData;

    @Transient
    public List<Thing> children;

    @Column(name = "collectionArea")
    public String collectionArea;

    // Blob field
    public byte[] description;

    @Id
    public Long id;

    public String link;

    @Column(name = "oldId")
    public String oldId;

    @Column(name = "pi")
    public String pi;

    @Transient
    public Integer order;

    @Column(name = "subType")
    public String subType;

    @Transient
    public String thumbUrl;

    @Column(name = "type")
    public String tType;

	private DbUtil du = new DbUtil();
	private GeneralUtil gu = new GeneralUtil();

	public static void delete(Set<Thing> things) {
		if (things != null) {
			for (Thing t : things) {
				if (t != null) {
					
					if (t.tType.equalsIgnoreCase(Thing.ThingType.COPY.name())) {
						// check for ThingCopy, del from ThingCopy
						ThingCopy c = ThingCopy.find.byId(t.id);
						if (c != null) c.delete();
					} else if (t.tType.equalsIgnoreCase(Thing.ThingType.FILE.name())) {
						// check for ThingFile, del from ThingFile, FileLocation
						ThingFile f = ThingFile.find.byId(t.id);
						FileLocation fl = FileLocation.find.byId(t.id);
						if (f != null) f.delete();
						if (fl != null) fl.delete();
					}
					t.delete();
				}
			}
		}
	}

    @GremlinGroovy("it.has('tType', 'work').has('oldId').filter{it.oldId[0..3] == 'nla.'}.filter{it.oldId[0..6] != 'nla.oh-'}?true:false")
    public boolean canBeDelivered() {
        return "work".equals(tType) && oldId != null
                && oldId.startsWith("nla.") && !oldId.startsWith("nla.oh-");
    }

    @JavaHandler
    public boolean canBeUpdated(String userId) {
        return ("work".equals(tType) || "arrangement".equals(tType)) ? new User(
                userId).hasUpdatePrivilege() : null;
    }

    // Get permissions of self and ancestors
    @JavaHandler
    public List<ThingPermission> getAllPermissions() {
        List<ThingPermission> permissions = new ArrayList<ThingPermission>();
        LinkedHashMap<Long, Thing> ancestorMap = new LinkedHashMap<Long, Thing>();
        ancestorMap.put(id, this);
        List<List<Thing>> ancestors = getAncestors();
        if (ancestors != null && ancestors.size() > 0) {
            for (List<Thing> ancestor : ancestors) {
                for (Thing parent : ancestor) {
                    if (!ancestorMap.containsKey(parent.id)) {
                        ancestorMap.put(parent.id, parent);
                    }
                }
            }
        }

        for (Iterator<Thing> ir = ancestorMap.values().iterator(); ir.hasNext();) {
            Thing t = ir.next();
            permissions.addAll(t.getPermissions());
        }

        return permissions;
    }

    
    @GremlinGroovy("it.out('isPartOf').path.gather()")
    public List<List<Thing>> getAncestors() {
        return getAncestors(false);
    }

    @GremlinGroovy("it.out('knows').loop(1){true}{true}.path().gather()")
    public List<List<Thing>> getAncestors(boolean reverseOrder) {
        List<String> ancestorIdList = new ArrayList<String>();
        getAncestorIdList(this, ancestorIdList, -1);

        if (ancestorIdList.size() > 0) {
            List<List<Thing>> ancestors = new ArrayList<List<Thing>>();
            for (String idStr : ancestorIdList) {
                String ids[] = idStr.split(",");
                List<Thing> lst = new ArrayList<Thing>(ids.length);
                for (String s : ids) {
                    Thing t = Thing.find.byId(Long.parseLong(s, 10));
                    if (reverseOrder) {
                        lst.add(0, t);
                    } else {
                        lst.add(t);
                    }
                }
                ancestors.add(lst);
            }
            return ancestors;
        } else {
            return null;
        }
    }

    @GremlinGroovy("it.out('knows').loop(1){true}{true}.path().transform({it.reverse()}).gather()")
    public List<List<Thing>> getAncestorsInReverseOrder() {
        return getAncestors(true);
    }

    @GremlinGroovy("it.out('knows').loop(1){true}{true}.path().reverse()[0]._().gather()")
    public List<Thing> getAncestorsOfFirstParent() {
        List<Thing> ancestors = new ArrayList<Thing>();
        Thing parent = getFirstParent();
        if (parent != null) {
            ancestors.add(0, parent);
            while ((parent = parent.getFirstParent()) != null) {
                ancestors.add(0, parent);
            }
        }
        return ancestors;
    }

    @GremlinGroovy("it.out('knows').loop(1){true}{true}.path().reverse()[0]._().gather.transform({it.reverse()})")
    public List<Thing> getAncestorsOfFirstParentInReverseOrder() {
        List<Thing> ancestorList = getAncestorsOfFirstParent();
        List<Thing> reverseAncestorList = new ArrayList<Thing>(
                ancestorList.size());
        for (Thing t : ancestorList) {
            reverseAncestorList.add(0, t);
        }
        return reverseAncestorList;
    }

    public String getBibData() {
        if ((bibData == null) || (bibData.isEmpty())) {
            bibData = "";
            if (link != null) {
                BibData bibdataInfo = new BibData(link);
                bibData = bibdataInfo.getBibData();
            }
        }

        return ((bibData == null) || (bibData.isEmpty()) ? "{\"info\":\"Sorry, no metadata found for this item.\"}"
                : bibData);
    }

    public List<Thing> getChildren() {
        List<Thing> _children = getChildrenThing(1);
        try{
        	children = getChildrenThing(4);
        	if (children == null) 
        		children = new ArrayList<Thing>();
        	if (_children != null)
        		children.addAll(_children);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return children;
    }

    public String getChildrenIdsWithOrderAsJson() {
        String sql = "select thing1Id, relOrder from dlRelationship where relationship = ? and thing2Id = ? order by relOrder, thing1Id";
        List<SqlRow> lst = du.executeQuery(sql, 1, id);
        ObjectNode node = Json.newObject();
        for (SqlRow row : lst) {
            addChildNode(node, gu.nvl(row.getString("thing1Id")),
                    "" + gu.nvl(row.getString("relOrder")));
        }
        return node.toString();
    }

    public List<Thing> getChildrenThing(String relationship) {
        String sql = "from dlThing t, dlRelationship r, dlRelationshipLut l where t.id = r.thing1Id and r.relationship = l.id and l.name = ? and r.thing2Id = ? order by r.relOrder, t.id";
        children = getThingsFromJoinQuery(sql, new String[][] { { "relOrder",
                "order" } }, relationship, id);
        return children;
    }

    public List<Thing> getCopies() {
        return getChildrenThing(2);
    }

    public List<Thing> getCurrentCopies() {
        String sql = "from dlThing t, dlThingCopy c, dlRelationship r where t.id = r.thing1Id and t.id = c.id and r.relationship = ? and r.thing2Id = ? and c.currentVersion = ? order by r.relOrder, t.id";
        return getThingsFromJoinQuery(sql, 2, id, "y");
    }

    public Thing getCurrentCopy(String copyRole) {
        String sql = "from dlThing t, dlThingCopy c, dlRelationship r where t.id = r.thing1Id and t.id = c.id and r.relationship = ? and r.thing2Id = ? and c.currentVersion = ? and c.copyRole = ? order by r.relOrder, t.id";
        List<Thing> copies = getThingsFromJoinQuery(sql, 2, id, "y", copyRole);
        return ((copies == null) || (copies.isEmpty())) ? null : copies.get(0);
    }

    public void getDescendants() {
        children = getChildren();
        if (children != null && children.size() > 0) {
            for (Thing child : children) {
                child.getDescendants();
            }
        }
    }

    public String getDescription() {
        if ((description != null) && (_description == null)) {
            try {
                _description = new String(description, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return _description;
    }

    public List<Thing> getFiles() {
        return getChildrenThing(3);
    }

    public Thing getFirstParent() {
        List<Thing> parents = getParents();
        return (parents != null && parents.size() > 0) ? parents.get(0) : null;
    }

    public String getHierarchyJson() {
        List<List<Thing>> ancestors = getAncestors();
        ArrayNode rootNode = Json.newObject().arrayNode();
        if (ancestors != null && ancestors.size() > 0) {
            for (List<Thing> ancestor : ancestors) {
                ArrayNode ancestorNode = Json.newObject().arrayNode();
                for (Thing parent : ancestor) {
                    // Add parents node
                    ancestorNode.add(newNode(parent, false));
                }
                // Add self node
                ancestorNode.add(newNode(this, true));
                rootNode.add(ancestorNode);
            }
        }
        return rootNode.toString();
    }

    // Must have! Ebeans problem?
    public Long getId() {
        return id;
    }

    public List<Thing> getLeafChildren(String relationship) {
        List<Thing> children = getChildrenThing(relationship);
        if ((children == null) || (children.isEmpty()))
            return children;

        List<Thing> leaves = new ArrayList<Thing>();
        for (Thing child : children) {
            List<Thing> _leaves = child.getLeafChildren(relationship);
            if ((_leaves == null) || (_leaves.isEmpty()))
                leaves.add(child);
            else
                leaves.addAll(_leaves);
        }

        return leaves;
    }

    public List<Thing> getLeafChildren(String subType, String relationship) {
        List<Thing> leaves = getLeafChildren(relationship);
        if ((leaves == null) || (leaves.isEmpty()))
            return leaves;

        List<Thing> filtered = new ArrayList<Thing>();
        for (Thing leaf : leaves) {
            if (leaf.subType.equals(subType)) {
                filtered.add(leaf);
            }
        }
        return filtered;
    }

    public String getLink() {
        return this.link;
    }

    public List<Thing> getParents() {
        // Get all parents regardless of relationship: isPartOf, isCopyOf,
        // isFileOf
        String sql = "from dlThing t, dlRelationship r where t.id = r.thing2Id and r.relationship in (1, 2, 3) and r.thing1Id = ? order by r.relOrder, t.id";
        return getThingsFromJoinQuery(sql, id);
    }
    
    public List<Thing> getParents(Thing.ThingRelationship relationship) {
    	if (relationship == null) return getParents();
    	
        // Get all parents of specified relationship
        String sql = "from dlThing t, dlRelationship r where t.id = r.thing2Id and r.relationship = ? and r.thing1Id = ? order by r.relOrder, t.id";
        return getThingsFromJoinQuery(sql, relationship.code(), id);
    }

    public String getParseDescriptionInHtml() {
        return getParseDescriptionInHtml(true);
    }

    public String getParseDescriptionInHtml(boolean skipEmptyFields) {
        Map<String, Object> obj = gu.parseJsonObject(getDescription());
        if (obj != null) {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, Object> entry : obj.entrySet()) {
                String value = (String) Objects.firstNonNull(entry.getValue(), "").toString();
                if (!skipEmptyFields || !value.equals("")) {
                    if (sb.length() > 0) {
                        sb.append("<br/>");
                    }
                    sb.append("<em>" + escapeHtml(entry.getKey()) + "</em>: "
                            + escapeHtml(value));
                }
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    public String getThumbUrl() {
        if ("work".equals(tType) && (oldId != null && oldId.startsWith("nla."))) {
            if (oldId.startsWith("nla.news-page")) {
                thumbUrl = play.Play.application().configuration()
                        .getString("newsImageService")
                        + oldId + "/thumb";
            } else if (!oldId.startsWith("nla.news-")) {
                String sql = "select d.oldId from dlThing d, dlThingCopy c, dlRelationship r where d.id = c.id and c.copyRole = ? and d.id = r.thing1Id and r.relationship = 2 and r.thing2Id = ?";
                List<SqlRow> results = du.executeQuery(sql, "t", "" + id);
                if (results.size() > 0) {
                    String copyPid = results.get(0).getString("oldId");
                    String copyWithoutRole = copyPid.substring(0,
                            copyPid.lastIndexOf('-'));
                    copyWithoutRole = copyWithoutRole.substring(0,
                            copyWithoutRole.lastIndexOf('-'));
                    thumbUrl = play.Play.application().configuration()
                            .getString("resolver")
                            + copyWithoutRole + "-t";
                }
            }
        }
        return thumbUrl;
    }

    public String getTitleFromDescription() {
        try {
            return (String) gu.parseJsonObject(getDescription()).get("title");
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getType() {
        return tType + (subType.equals(tType) ? "" : "," + subType);
    }

    public Thing getWorkThing() {
        // Get the closest work which is a parent, grandparent, etc. of this
        // thing
        // If thing is a work, returns itself
        if ("work".equals(tType)) {
            return this;
        } else if ("copy".equals(tType)) {
            String sql = "from dlThing t, dlRelationship r where t.id = r.thing2Id and r.relationship = ? and r.thing1Id = ? order by r.relOrder, t.id";
            List<Thing> things = getThingsFromJoinQuery(sql, 2, id);
            if (things != null && things.size() > 0) {
                return things.get(0).getWorkThing();
            }
        } else if ("file".equals(tType)) {
            String sql = "from dlThing t, dlRelationship r where t.id = r.thing2Id and r.relationship = ? and r.thing1Id = ? order by r.relOrder, t.id";
            List<Thing> things = getThingsFromJoinQuery(sql, 3, id);
            if (things != null && things.size() > 0) {
                return things.get(0).getWorkThing();
            }
        }
        return null;
    }

    public boolean isReadable(String userId) {
        int permission = getReadPermission(this, userId);
        switch (permission) {
        case 1:
            // Read permission is granted for this thing => it's unrestricted
            return true;
        case 0:
            // Permission is not recorded - Look for parents
            List<List<Thing>> ancestors = getAncestorsInReverseOrder();
            if (ancestors != null && ancestors.size() > 0) {
                boolean hasSingleParents = (ancestors.size() == 1);
                Map<Long, Integer> commonParentIds = hasSingleParents ? null
                        : getCommonParentIds(ancestors);
                for (List<Thing> ancestor : ancestors) {
                    for (Thing parent : ancestor) {
                        int prp = getReadPermission(parent, userId);
                        if (prp > 0
                                && (hasSingleParents || commonParentIds
                                        .containsKey(parent.id))) {
                            // This parent is records as unrestricted => this
                            // thing is restricted
                            return true;
                        } else if (prp < 0) {
                            // This parent is recorded as restricted => this
                            // thing is restricted
                            return false;
                        }
                    }
                }
            }
            // Permission is not recorded or recorded as unrestricted => thing
            // is unrestricted
            return true;
        default:
            return false;
        }
    }

    public void setBibData(String bibData) {
        this.bibData = bibData;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public JsonNode toJson() {
        return toJson(true, false, false);
    }

    public JsonNode toJson(boolean withChildren, boolean encodeDescription,
            boolean thumbURL) {
        ObjectNode node = Json.newObject();
        addChildNode(node, "id", id);
        addChildNode(node, "collectionArea", collectionArea);
        addChildNode(node, "type", tType);
        addChildNode(node, "subType", subType);
        addChildNode(node, "description", encodeDescription ? getDescription()
                .replace("\"", "\\\"") : getDescription());
        if (bibData != null) {
            addChildNode(node, "metadata", bibData);
        }
        addChildNode(node, "link", link);
        addChildNode(node, "pid", oldId);

        if (thumbURL) {
            addChildNode(node, "thumbUrl", getThumbUrl());
        }
        addChildNode(node, "order", order);

        if (withChildren) {
            children = getChildren();
            if (children != null && children.size() > 0) {
                ArrayNode aryNode = Json.newObject().arrayNode();
                node.put("children", aryNode);
                for (Thing childThing : children) {
                    aryNode.add(childThing.toJson(withChildren,
                            encodeDescription, thumbURL));
                }
            }
        }
        return node;
    }

    public String toXml() {
        return toXml(true);
    }

    public String toXml(boolean withChildren) {
        Document doc = new Document(getElement("item", withChildren));
        return doc.toXML();
    }
    
    public void updateChildrenWithOrderFromJson(String jsonTxt) {
        Map<String,Object> obj = gu.parseJsonObject(jsonTxt);
            String updateSql = "update dlRelationship set relOrder = ? where thing1Id = ? and relationship = ? and thing2Id = ?";
            String insertSql = "insert into dlRelationship (thing1Id, relationship, thing2Id, relOrder) values (?, ?, ?, ?)";
            for (Entry<String, Object> entry : obj.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue().toString();
                int cnt = du.executeUpdate(updateSql, val, key, 1, id);
                if (cnt == 0) {
                    // Try insert
                    cnt = du.executeUpdate(insertSql, key, 1, id, val);
                }
            }
    }
    
    public synchronized void updateDescription(ThingDescription desc) throws JsonGenerationException, JsonMappingException, IOException {
        description = desc.getBytes();
        this.save();
    }

    private void addChildElement(Element element, String name, Object value) {
        if (value != null) {
            Element child = new Element(name);
            child.appendChild("" + value);
            element.appendChild(child);
        }
    }

    private void addChildNode(ObjectNode node, String name, Object value) {
        if (value != null) {
            node.put(name, "" + value);
        }
    }

    public void getAncestorIdList(Thing t, List<String> ancestorIdList, int idx) {
    	getAncestorIdList(t, ancestorIdList, idx, null);
    }
    
    public void getAncestorIdList(Thing t, List<String> ancestorIdList, int idx, Thing.ThingRelationship rel) {
        List<Thing> parents = t.getParents(rel);
        if (parents != null && parents.size() > 0) {
            if (ancestorIdList.isEmpty()) {
                ancestorIdList.add("");
                idx = 0;
            }

            String oldVal = ancestorIdList.get(idx);
            for (int i = 0; i < parents.size(); i++) {
                Thing parent = parents.get(i);
                if (i > 0) {
                    // Not first child
                    ancestorIdList.add(oldVal);
                    idx = ancestorIdList.size() - 1;
                }
                String newVal = parent.id
                        + ("".equals(ancestorIdList.get(idx)) ? "" : ",")
                        + ancestorIdList.get(idx);
                ancestorIdList.set(idx, newVal);
                getAncestorIdList(parent, ancestorIdList, idx, rel);
            }
        }
    }

    private List<Thing> getChildrenThing(int relationship) {
        String sql = "from dlThing t, dlRelationship r where t.id = r.thing1Id and r.relationship = ? and r.thing2Id = ? order by r.relOrder, t.id";
        children = getThingsFromJoinQuery(sql, new String[][] { { "relOrder",
                "order" } }, relationship, id);
        return children;
    }

    private Map<Long, Integer> getCommonParentIds(List<List<Thing>> ancestors) {
        Map<Long, Integer> map = new HashMap<Long, Integer>();
        Map<Long, Integer> cntMap = new HashMap<Long, Integer>();
        for (List<Thing> ancestor : ancestors) {
            for (Thing parent : ancestor) {
                Long pId = parent.id;
                Integer cnt = cntMap.get(pId);
                cntMap.put(pId, (cnt == null) ? 1 : cnt + 1);
            }
        }
        int size = ancestors.size();
        for (Iterator<Long> ir = cntMap.keySet().iterator(); ir.hasNext();) {
            Long pId = ir.next();
            int cnt = cntMap.get(pId);
            if (cnt == size) {
                map.put(pId, cnt);
            }
        }
        return map;
    }

    private Element getElement(String name, boolean withChildren) {
        Element element = new Element(name);
        addChildElement(element, "id", "" + id);
        addChildElement(element, "collectionArea", collectionArea);
        addChildElement(element, "type", tType);
        addChildElement(element, "subType", subType);
        addChildElement(element, "description", getDescription());
        addChildElement(element, "link", link);
        addChildElement(element, "oldId", oldId);
        if (withChildren) {
            children = getChildren();
            if (children != null && children.size() > 0) {
                Element childrenEl = new Element("children");
                element.appendChild(childrenEl);
                for (Thing childThing : children) {
                    Element childEl = childThing.getElement("item",
                            withChildren);
                    childrenEl.appendChild(childEl);
                }
            }
        }
        return element;
    }

    private List<ThingPermission> getPermissions() {
        return Ebean.find(ThingPermission.class).where().eq("thingId", this.id)
                .gt("permission", 0).findList();
    }

    private int getReadPermission(Thing thing, String userId) {
        User user = new User(userId);

        // Assuming permission > 0 means the item/file can be read/downloaded
        List<ThingPermission> permissionList = thing.getPermissions();
        if (permissionList.size() > 0) {
            for (ThingPermission tp : permissionList) {
                if (user.belongsToGroup(tp.userGroup)) {
                    return 1;
                }
            }
            // Permission is recorded for this thing - unreadable
            return -1;
        } else {
            // Permission is not recorded for this thing - defaul to readable!
            return 0;
        }
    }

    private List<Thing> getThingsFromJoinQuery(String sql, Object... params) {
        return getThingsFromJoinQuery(sql, null, params);
    }

    private List<Thing> getThingsFromJoinQuery(String sql,
            String otherFields[][], Object... params) {
        // Construct sql
        Map<String, String> dbFieldsMap = new HashMap<String, String>();
        StringBuffer sb = new StringBuffer();
        Field fields[] = Thing.class.getDeclaredFields();
        for (Field f : fields) {
            if (f.getModifiers() == 1
                    && !f.isAnnotationPresent(Transient.class)) {
                // Column in the database - Public and not Transient
                Column c = f.getAnnotation(Column.class);
                String dbField = "t." + ((c != null) ? c.name() : f.getName());
                dbFieldsMap.put(dbField, f.getName());
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(dbField);
            }
        }

        if (otherFields != null && otherFields.length > 0) {
            for (String field[] : otherFields) {
                sb.append(", " + field[0]);
            }
        }

        String newSql = "select " + sb.toString() + " " + sql;
        RawSqlBuilder builder = RawSqlBuilder.parse(newSql);
        // Column mappings
        for (Iterator<String> ir = dbFieldsMap.keySet().iterator(); ir
                .hasNext();) {
            String s = ir.next();
            builder.columnMapping(s, dbFieldsMap.get(s));
        }

        if (otherFields != null && otherFields.length > 0) {
            for (String field[] : otherFields) {
                builder.columnMapping(field[0], field[1]);
            }
        }

        RawSql rawSql = builder.create();

        Query<Thing> query = Thing.find.setRawSql(rawSql);
        // Parameters
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }

        return query.findList();
    }

    private ObjectNode newNode(Thing thing, boolean self) {
        ObjectNode node = Json.newObject();
        node.put("id", "" + thing.id);
        node.put("self", self);
        node.put("type", thing.tType);
        node.put("subType", thing.subType);
        List<ThingPermission> permissions = thing.getPermissions();
        if (permissions != null && permissions.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (ThingPermission tp : permissions) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(tp.userGroup);
            }
            node.put("groups", sb.toString());
        }
        return node;
    }

    public Long getInternalId() {
        return getId();
    }

    public String getPI() {
        return oldId;
    }

    public String getSubType() {
        return subType;
    }

    public List<AbstractThing> getFlattenedAncestorLineages() {
        List<AbstractThing> flattenedLineages = new ArrayList<AbstractThing>();
        List<List<Thing>> ancestors = getAncestors();
        for (List<Thing> lineage : ancestors) {
            for (Thing ancestor : lineage) {   
                flattenedLineages.add(ancestor);
            }
        } 
        return flattenedLineages;
    }
}
