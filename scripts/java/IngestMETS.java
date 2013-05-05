package models.ingest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import models.Thing;
import models.ingest.IngestEntry.ThingSubType;

public class IngestMETS extends IngestData {

    protected List<IngestEntry> entries;
    private File metsFile;
    private String tiffSrcDir;
    private String metsJsonSrcDir;
    
    public IngestMETS(Path dlirStoragePath, String topPI, Path metsPath, String ts) throws FileNotFoundException {
        super(dlirStoragePath, topPI, ts);
        
        // todo validate metsPath is not null or empty using GUAVA, otherwise throw exception
        File mets = metsPath.toFile();
        if (!(mets.exists()))
            throw new FileNotFoundException(metsPath.toString() + " does not exist.");
    }
    
    /*
     * need to run after validateIngestData(IngestEntry.ThingSubType topEntryType)
     */
    private List<IngestEntry> validateIngestData(List<IngestEntry> entries, ObjectNode metsRoot) {
        Map<String, IngestEntry> entryMap = new HashMap<String, IngestEntry>();
        Map<String, Thing.CopyRole> copyRoleMap = new HashMap<String, Thing.CopyRole>();
        
        if (entries != null) {
            for (IngestEntry entry : entries) {
                // TODO: Map entryMap
                // TODO: Map copyRoleMap
            }
        }
        
        // TODO: traverse metsRoot and populate order, height and width in each entry
        
        return entries;
    }
    
/*    public List<IngestEntry> validateIngestData(ObjectNode metsRoot) {
        initSrcDirs(metsFile);
        entries = new ArrayList<IngestEntry>();        
        IngestEntry metsEntry = new IngestEntry();
        entries.add(metsEntry);
        
        metsEntry.ts = super.ts();
        int entryLevel = 0;
        metsEntry.entryType = mapType(metsRoot.get("type"), entryLevel, null);
        metsEntry.pi = topPI;
        metsEntry.order = (metsRoot.get("order") == null)?null:new Integer(metsRoot.get("order").getTextValue());
        metsEntry = mapManifest(metsEntry, metsRoot.get("copies"), entryLevel, null); 
                
        return entries;
    }*/

    private IngestEntry.ThingSubType mapSubType(JsonNode node, int level, String pi) {
        // TODO
        return null;
    }
    
    private Integer mapOrder(JsonNode node, int level, String pi, String fileName) {
        // TODO
        return null;
    }
    
    private IngestEntry mapManifest(IngestEntry entry, JsonNode copies, int level, Integer order, String pi) {
        if (copies == null) {
            if (order == null)
                throw new InvalidParameterException("Cannot map copies at level " + level + ", copies is null.");
            else
                throw new InvalidParameterException("Cannot map copies " + order + " at level " + level + ", copies is null.");  
        }
        
        ArrayNode _copies = (ArrayNode) copies;
        for (JsonNode copy : copies) {
            String fileName = copy.get("filename").getTextValue();
            IngestFile mf;
            // TODO: check width and height is in pixels
            if (fileName.endsWith(".tif")) {
                mf = new IngestImg();
                JsonNode techMD = copy.get("technicalmetadata");
                if (techMD != null) {
                    ((IngestImg) mf).height = new Integer(techMD.get("height").getTextValue());
                    ((IngestImg) mf).width = new Integer(techMD.get("width").getTextValue());
                }
            } else {
                mf = new IngestFile();
            }
            mf.fileName = copy.get("filename").getTextValue();
            // mf.fileSize = 0; // TODO
            // mf.checkSum = null; //TODO
            
            Thing.CopyRole copyRole = Thing.CopyRole.isCopyRole(copy.get("copyrole").getTextValue());
            entry.entryData.put(copyRole, mf);
        }
        return entry;
    }

    private ThingSubType mapType(JsonNode entry, int level, Integer order, String pi) {
        if (entry == null) {
            if (order == null)
                throw new InvalidParameterException("Cannot map entry at level " + level + ", entry is null.");
            else
                throw new InvalidParameterException("Cannot map entry " + order + " at level " + level + ", entry is null.");
        }
                
        return IngestEntry.ThingSubType.isType(entry.getTextValue());
    }

    public synchronized List<IngestEntry> validateIngestData(IngestEntry.ThingSubType topEntryType, ObjectNode metsRoot) throws IOException,
    NoSuchAlgorithmException {
        initSrcDirs(metsFile);        
        entries = new ArrayList<IngestEntry>();
        
        // add mets file as an ingest entry
        IngestEntry metsEntry = new IngestEntry();
        metsEntry.ts = super.ts();
        metsEntry.entryType = topEntryType;
        metsEntry.pi = topPI;
        IngestFile metsManifest = new IngestFile();
        metsManifest.fileName = metsFile.getName();
        metsManifest.fileSize = metsFile.length();
        metsManifest.checkSum = super.checksum(metsFile);
        metsEntry.entryData.put(Thing.CopyRole.OCR_METS_COPY, metsManifest);
        
        metsEntry.parentEntry = null;
        entries.add(metsEntry);
        
        // TODO: dealing with adding edition, section, supplement etc as ingest entries
        //       by reading the METS file.
        
        List<IngestEntry> pageEntries = super.validateIngestData();
        entries.addAll(pageEntries);
        for (IngestEntry pageEntry : pageEntries) {
            pageEntry.parentEntry = metsEntry;
        }
       
        // TODO: to add each tiff as an ingest entry
        return validateIngestData(entries, metsRoot);
    }
    
    protected String getWorkingArea(Thing.CopyRole copyRole) {   
        return srcDirs[copyRole.idx()];
    }
    
    private void initSrcDirs(File mets) {
        srcDirs[Thing.CopyRole.OCR_METS_COPY.idx()] = mets.getParent();
        srcDirs[Thing.CopyRole.ACCESS_COPY.idx()] = Paths.get(mets.getParent()).resolve("jp2").toString();
        srcDirs[Thing.CopyRole.MASTER_COPY.idx()] = Paths.get(mets.getParent()).resolve("tiff").toString();
        srcDirs[Thing.CopyRole.OCR_JSON_COPY.idx()] = Paths.get(mets.getParent()).resolve("oc").toString();
        srcDirs[Thing.CopyRole.OCR_ALTO_COPY.idx()] = Paths.get(mets.getParent()).resolve("alto").toString();
    }
}
