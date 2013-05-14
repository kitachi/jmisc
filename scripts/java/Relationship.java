package models.ingest;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import models.Thing;
import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
@Table(name = "dlRelationship")
public class Relationship extends Model {
    public static Finder<Long, Relationship> find = new Finder<Long, Relationship>(Long.class, Relationship.class);
    
    @Id
    Long id;
    
    @Column(name = "thing1Id")
    Long thing1Id;
    
    @Column(name = "relationship")
    int relationship;
    
    @Column(name = "thing2Id")
    Long thing2Id;
    
    @Column(name = "relOrder")
    Integer relOrder;
    
    // TODO: move this into Thing class
//    public static void delete(List<Thing> things) {
//        if (things != null) {
//            for (Thing t : things) {
//                if (t != null) {
//                    // check for ThingCopy, del from ThingCopy
//
//                    // check for ThingFile, del from ThingFile, FileLocation
//                    t.delete();
//                }
//            }
//        }
//    }

    public static void delete(List<Relationship> relationships) {
        if (relationships != null) {
            for (Relationship r : relationships) {
                if (r != null)
                    r.delete();
            }
        }
    }
    
    public static List<Relationship> descendents(Thing root) {
        List<Relationship> relationships = new ArrayList<Relationship>();
        descend(root, relationships);
        return relationships;
    }
    
    static void descend(Thing parent, List<Relationship> relationships) {
        List<Relationship> _relationships = Relationship.find.where().eq("thing2Id", parent.id).findList();
        if (_relationships != null && _relationships.size() > 0)
            relationships.addAll(_relationships);
        
        List<Thing> children = parent.getChildren();
        if (children != null && children.size() > 0) {
            for (Thing child : children) {
                if (child != null) {
                    descend(child, relationships);
                }
            }
        }
    }
}
