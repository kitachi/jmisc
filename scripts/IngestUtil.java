package utils;

import com.avaje.ebean.Ebean;

import models.Relationship;
import models.Thing;

public class IngestUtil {
    public static Thing createThing(String type, String subType) {
        Thing t = new Thing();
        t.tType = type;
        t.subType = subType;
        t.save();
        return t;
    }
    
    public static Relationship createRelationship(Long thing1Id, Long thing2Id, int relationship) {
        Relationship r = new Relationship();
        r.thing1Id = thing1Id;
        r.thing2Id = thing2Id;
        r.relationship = relationship;
        r.save();
        return r;
    }
    
}
