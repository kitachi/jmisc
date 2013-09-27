package graph.domain.ingest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.tinkerpop.blueprints.Vertex;

import graph.domain.Entity;
import graph.domain.Repository;
import graph.domain.json.JsonNodeWrapper;

public class IngestedEntity implements Entity {
    private Vertex v = Repository.getGraph().addVertex(null);
    private Set<String> fieldNames = new LinkedHashSet<String>();
    private Map<Integer, IngestedEntity> subEntities = new HashMap<Integer, IngestedEntity>();
    
    public Vertex asVertex() {
        return v;
    }

    public Map<Integer, IngestedEntity> getSubEntities() {
        return subEntities;
    }
    
    public Entity addSubEntity(Entity subEntity) {
        subEntities.put(subEntities.size() + 1, (IngestedEntity) subEntity);
        return this;
    }
    
    public Entity addSubEntity(Entity subEntity, int order) {
        subEntities.put(order, (IngestedEntity) subEntity);
        return this;
    }
    
    @Override
    public Entity addSubEntity(ArrayNode group, Entity subEntity) {
        return addSubEntity(subEntity);
    }

    @Override
    public Entity addField(String name, Object value) {
        fieldNames.add(name);
        v.setProperty(name, value);
        return this;
    }
    
    public IngestedEntity addToDescription(String name, String value) throws IOException {
        addField(name, value);
        
        if (v.getProperty("description") == null) {
            JsonNodeWrapper attributes = new JsonNodeWrapper();
            attributes.put(name, value);
            addField("description", attributes.getNode().toString());
        } else {
            JsonNode n = new ObjectMapper().reader().readTree((String) v.getProperty("description"));
            ((ObjectNode) n).put(name, value);
            v.setProperty("description", n.toString());
        }
        return this;
    }

    @Override
    public ObjectNode getFields() {
        JsonNodeWrapper fields = new JsonNodeWrapper();
        for (String fldName : fieldNames) {
            fields.put(fldName, v.getProperty(fldName));
        }
        return fields.getNode();
    }

    @Override
    public ArrayNode getArray() {
        ArrayNode arryNode = new ObjectMapper().createArrayNode().arrayNode();
        for (int i=0; i < subEntities.size(); i++) {
            arryNode.add(subEntities.get(i+1).getFields());
        }
        return arryNode;
    }
}
