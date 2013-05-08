package controllers.helpers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import models.IngestParams;
import models.Thing;
import models.ThingDescription;
import models.ThingNotFoundException;
import models.ingest.IngestArticle;
import models.ingest.IngestData;
import models.ingest.IngestEntry.ThingSubType;
import models.ingest.IngestEntry;
import models.ingest.IngestMETS;
import models.ingest.JellyGraph;
import models.ingest.JobStatus;
import models.ingest.Relationship;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.avaje.ebean.Ebean;

public class JournalIngestHelper {
    public synchronized static void updateJournalTitle(String pi, String title) throws IngestException {
        // TODO: parse input params: pi, title
        Thing t;
        try {
            t = Thing.findByPI.byId(pi);
        } catch (ThingNotFoundException e) {
            t = new Thing();
            t.collectionArea = "nla.news";
            t.pi = pi;
            t.oldId = pi;
            t.tType = Thing.ThingType.WORK.name().toLowerCase();
            t.subType = IngestEntry.ThingSubType.TITLE.name().toLowerCase();
            t.description = "{}".getBytes();
        }
        
        try {
            ObjectNode desc;
            
            desc = (ObjectNode) new ObjectMapper().reader().readTree(new String(t.description));
            desc.put("title", title);
            t.description = desc.toString().getBytes();
            t.save();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IngestException("Fail to update journal title for " + title, e);
        }   
    }
    
//    private synchronized static void ingest(String titlePI, String metspath, IngestParams ingestParams, ObjectNode mets) {
//    	if () {
//    		
//    	}
//    }
    
    public synchronized static void ingest(String titlePI, String metspath, IngestParams ingestParams, ObjectNode mets) throws IngestException, NoSuchAlgorithmException, IOException {
        Thing journalTitle;
        String collectionArea = "nla.news";
        
        try {
            journalTitle = Thing.findByPI.byId(titlePI);
            ingest(metspath, ingestParams, collectionArea, journalTitle, IngestEntry.ThingSubType.ISSUE, mets);
        } catch (ThingNotFoundException ex) {
            throw new IngestException(ex.getLocalizedMessage());
        }
        

    }
    
    protected synchronized static void ingest(String metspath, IngestParams ingestParams, String collectionArea, Thing topItem, IngestEntry.ThingSubType ingestType, ObjectNode mets) throws IngestException {
        try {
            Ebean.beginTransaction();
            String ts = ingestParams.ts;
            // Path metsPath = Paths.get(IngestUnion.DLIR_FS_WORKING).resolve(ingestParams.pi);
            Path metsPath = Paths.get(metspath);
            Path dlirStoragePath = Paths.get(IngestUnion.DLIR_FS_BASE);
            IngestMETS data = new IngestMETS(dlirStoragePath, ingestParams.pi, metsPath, ts);
            data.setCollectionArea(collectionArea);

            List<IngestEntry> entries = data.validateIngestData(ingestType, mets);            
            if (entries != null) {
                JellyGraph ingestGraph = data.getMetadataGraph();
                String jobName = "ingest " + ingestParams.pi;
                String topUUID = topItem.pi;
                Integer status = IngestUnion.IngestStatus.CLEAN.ordinal() + 1;
                String statusDesc = IngestUnion.IngestStatus.CLEAN.name();
                Timestamp start = new Timestamp(new Date().getTime());
                
                // TODO: check whether ts, topUUID has already been inserted, and its status is not eq. ingested
                Long jobNo = ingestGraph.newIngestStatus(jobName, ts, topUUID, status, statusDesc, null, null).id;
                data.setIngestJobId(jobNo);
                ingestGraph.logIngest(new Long(jobNo), ts, ingestParams.pi);
            
                Thing parent = topItem;
                int relOrder = 1;
                String pageLink = "";
                for (IngestEntry entry : entries) {
                    Thing work;
                    if (entry.parentEntry() != null) {
                        parent = Thing.find.byId(entry.parentEntry().getId());
                    }
                    ThingDescription desc;
                    if (entry.getEntryType() == IngestEntry.ThingSubType.PAGE) {
                        desc = desc(ingestParams.title, ingestParams.creator, entry.pi(), relOrder);
                        work = data.createJellyItem(parent, entry, pageLink, desc, relOrder);
                    } else if (entry.getEntryType() == IngestEntry.ThingSubType.ARTICLE) {
                        desc = null; // TODO: "??";
                        work = data.createJellyItem(parent, (IngestArticle) entry, pageLink, desc, relOrder);        
                    } else if (entry.getEntryType().existInJournal()){
                        desc = desc(ingestParams.title);
                        String link = ingestParams.bibSrc + ":" + ingestParams.bibId;
                        work = data.createJellyItem(parent, entry, link, desc, relOrder);
                    } else {
                        work = null;
                        // TODO: log a invalid Thing subType error
                    }
                    
                    if (work != null)
                        entry.setId(work.id);
                    relOrder++;
                }
                
                JobStatus s = JobStatus.find.byId(jobNo);
                ingestGraph.updateIngestStatus(s, jobName, ts, topUUID, IngestUnion.IngestStatus.INGESTED.ordinal() + 1, "ingested", null, null);
            }
            
            Ebean.commitTransaction();
        } catch (NoSuchAlgorithmException ex) {
            throw new IngestException("Fail to ingest " + ingestParams.pi, ex);
        } catch (IOException ex) {
            throw new IngestException("Fail to ingest " + ingestParams.pi, ex);
        }
    }
    
    private synchronized static ThingDescription desc(String title) {
        ThingDescription description = new ThingDescription();
        description.handleUnknown("title", title);
        return description;
    }
    
    private synchronized static ThingDescription desc(String title, String creator, String uuid, int relOrder) {
        ThingDescription description = new ThingDescription();
        description.handleUnknown("workPid", uuid);
        description.handleUnknown("subUnitType", "Page " + relOrder);
        description.handleUnknown("bibLevel", "Part");
        description.handleUnknown("digitalStatus", "Digitised");
        description.handleUnknown("title", title);
        description.handleUnknown("creator", creator);
        return description;
    }
}
