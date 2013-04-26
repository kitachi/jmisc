package models.ingest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.Thing;

public class JellyGraph {
    protected List<Thing> things = new ArrayList<Thing>();
    protected List<Relationship> relationships = new ArrayList<Relationship>();
    private   boolean logIngest = false;
    private   Long    jobId;
    private   String  ts;
    private   String  topUUID;
    
    public void logIngest(Long jobId, String ts, String topUUID) {
        logIngest = true;
        this.jobId = jobId;
        this.ts = ts;
        this.topUUID = topUUID;
    }
    
    public Thing addThing(String collectionArea, String pi, String tType, String subType) {
        return addThing(collectionArea, "", pi, tType, subType, null);
    }
    
    public Thing addThing(String collectionArea, String link, String pi, String tType, String subType, byte[] description) {
        Thing t = new Thing();
        t.collectionArea = collectionArea;
        t.link = link;
        t.oldId = pi;
        t.pi = pi;
        t.tType = tType;
        t.subType = subType;
        t.description = description;
        t.save();
        things.add(t);
        
        if (logIngest) {
            logIngest(jobId, ts, topUUID, t.id, Thing.class, t.tType + "-" + t.subType);
        }
        return t;
    }     
    
    public Relationship addRelationship(Long thing1Id, Thing.ThingRelationship relationship, Long thing2Id, Integer relOrder) {
        Relationship r = new Relationship();
        r.thing1Id = thing1Id;
        r.thing2Id = thing2Id;
        r.relationship = relationship.code();
        r.relOrder = relOrder;
        r.save();
        relationships.add(r);
        
        if (logIngest) {
            logIngest(jobId, ts, topUUID, r.id, Relationship.class, relationship.name());
        }
        return r;
    }
    
    public Iterator<Thing> getThingIterator() {
        return things.iterator();
    }
    
    public Iterator<Relationship> getRelationshipIterator() {
        return relationships.iterator();
    }     
    
    public void logIngest(Long jobId, String ts, String topUUID, Long internalId, Class jellyType, String objType) {
        IngestJob j = new IngestJob();
        j.jobNo = jobId;
        j.ts = ts;
        j.topUUID = topUUID;
        j.internalId = internalId;
        j.tableName = jellyType.getName();
        j.objType = objType;
        j.save();
    }
    
    public JobStatus updateIngestStatus(Long jobId, String jobName, String ts, String topUUID, Integer status, String statusDesc, Timestamp start, Timestamp end) {
        JobStatus s = new JobStatus();
        s.jobNo = jobId;
        s.jobName = jobName;
        s.jobTS = ts;
        s.topUUID = topUUID;
        s.status = status;
        s.statusDescription = statusDesc;
        s.startDate = start;
        s.endDate = end;
        s.save();
        
        return s;
    }
}
