package models.ingest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import models.Thing;

public class IngestMETS extends IngestData {

    protected List<IngestEntry> entries;
    private Path metsPath;
    private String tiffSrcDir;
    private String metsJsonSrcDir;
    
    public IngestMETS(Path dlirStoragePath, String topPI, Path metsPath, String ts) {
        super(dlirStoragePath, topPI, ts);
        
        // todo validate metsPath is not null or empty using GUAVA, otherwise throw exception
        this.metsPath = metsPath;
    }

    public List<IngestEntry> validateIngestData(IngestEntry.ThingSubType topEntryType) throws IOException,
    NoSuchAlgorithmException {
        File mets = metsPath.toFile();
        if (!(mets.exists()))
            throw new FileNotFoundException(metsPath.toString() + " does not exist.");
       
        srcDirs[Thing.CopyRole.OCR_METS_COPY.idx()] = mets.getParent();
        srcDirs[Thing.CopyRole.ACCESS_COPY.idx()] = Paths.get(mets.getParent()).resolve("jp2").toString();
        srcDirs[Thing.CopyRole.MASTER_COPY.idx()] = Paths.get(mets.getParent()).resolve("tiff").toString();
        srcDirs[Thing.CopyRole.METS_JSON_COPY.idx()] = Paths.get(mets.getParent()).resolve("oc").toString();
        srcDirs[Thing.CopyRole.OCR_JSON_COPY.idx()] = Paths.get(mets.getParent()).resolve("oc").toString();
        srcDirs[Thing.CopyRole.OCR_ALTO_COPY.idx()] = Paths.get(mets.getParent()).resolve("alto").toString();
        
        entries = new ArrayList<IngestEntry>();
        
        // add mets file as an ingest entry
        IngestEntry metsEntry = new IngestEntry();
        metsEntry.ts = super.ts();
        metsEntry.entryType = topEntryType;
        metsEntry.pi = topPI;
        IngestFile metsManifest = new IngestFile();
        metsManifest.fileName = mets.getName();
        metsManifest.fileSize = mets.length();
        metsManifest.checkSum = super.checksum(mets);
        metsEntry.entryData.put(Thing.CopyRole.OCR_METS_COPY, metsManifest);
        
        String metsName = mets.getName();
        File metsJson = Paths.get(srcDirs[Thing.CopyRole.METS_JSON_COPY.idx()]).resolve(metsName.replace(".xml", ".json.gz")).toFile();
        if (metsJson.exists()) {
            IngestFile metsJsonCopy = new IngestFile();
            metsJsonCopy.fileName = metsJson.getName();
            metsJsonCopy.fileSize = metsJson.length();
            metsJsonCopy.checkSum = checksum(metsJson);
            metsEntry.entryData.put(Thing.CopyRole.METS_JSON_COPY, metsJsonCopy);
        }
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
        return entries;
    }
    
    protected String getWorkingArea(Thing.CopyRole copyRole) {   
        return srcDirs[copyRole.idx()];
    }
}
