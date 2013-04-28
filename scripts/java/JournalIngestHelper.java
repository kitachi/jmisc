package controllers.helpers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import models.IngestParams;
import models.Thing;
import models.ThingNotFoundException;
import models.ingest.IngestData;
import models.ingest.IngestEntry.ThingSubType;
import models.ingest.IngestEntry;
import models.ingest.IngestMETS;
import models.ingest.JellyGraph;
import models.ingest.Relationship;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.avaje.ebean.Ebean;

public class JournalIngestHelper {
    public synchronized static void updateJournalTitle(String pi, String title) throws IngestException {
        // TODO: parse input params: pi, title
        Thing t = Thing.findByPI.byId(pi);
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
    
    public synchronized static void newJournalIssue(String titlePI, IngestParams ingestParams) throws IngestException, NoSuchAlgorithmException, IOException {
        Thing journalTitle;
        String collectionArea = "nla.news";
        
        try {
            journalTitle = Thing.findByPI.byId(titlePI);
            ingest(ingestParams, collectionArea, journalTitle, IngestEntry.ThingSubType.ISSUE);
        } catch (ThingNotFoundException ex) {
            throw new IngestException(ex.getLocalizedMessage());
        }
        

    }
    
    protected synchronized static void ingest(IngestParams ingestParams, String collectionArea, Thing topItem, IngestEntry.ThingSubType ingestType) throws IngestException {
        try {
            Ebean.beginTransaction();
            String ts = IngestUnion.getTimestamp();
            String metsPath = IngestUnion.DLIR_FS_WORKING + "/" + ingestParams.pi + "/";
            String dlirStoragePath = IngestUnion.DLIR_FS_BASE + "/" + ingestParams.pi + "/";
            IngestMETS data = new IngestMETS(dlirStoragePath, ingestParams.pi, metsPath, ts);
            data.setCollectionArea(collectionArea);

            List<IngestEntry> entries = data.validateIngestData(ingestType);            
            if (entries != null) {
                JellyGraph ingestGraph = data.getMetadataGraph();
                String jobName = "ingest " + ingestParams.pi;
                String topUUID = topItem.pi;
                Integer status = IngestUnion.IngestStatus.CLEAN.ordinal() + 1;
                String statusDesc = IngestUnion.IngestStatus.CLEAN.name();
                Timestamp start = (Timestamp) new Date();
                
                // TODO: check whether ts, topUUID has already been inserted, and its status is not eq. ingested
                Long jobNo = ingestGraph.newIngestStatus(jobName, ts, topUUID, status, statusDesc, start, null).jobNo;
                data.setIngestJobId(jobNo);
                ingestGraph.logIngest(new Long(jobNo), ts, ingestParams.pi);
            
                Thing parent = topItem;
                int relOrder = 1;
                for (IngestEntry entry : entries) {
                    if (entry.parentEntry() != null) {
                        parent = Thing.findByPI.byId(entry.parentEntry().pi());
                    }
                    Thing work = data.createJellyItem(parent, entry, relOrder);
                    relOrder++;
                }
            }
            
            Ebean.commitTransaction();
        } catch (NoSuchAlgorithmException ex) {
            throw new IngestException("Fail to ingest " + ingestParams.pi, ex);
        } catch (IOException ex) {
            throw new IngestException("Fail to ingest " + ingestParams.pi, ex);
        }
    }
}
