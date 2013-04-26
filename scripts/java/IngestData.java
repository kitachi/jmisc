package models.ingest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import models.IngestParams;
import models.Thing;
import models.ThingFile;
import models.Thing.CopyRole;
import models.Thing.ThingRelationship;
import models.Thing.ThingType;
import models.ingest.IngestEntry.ThingSubType;

// TODO:  look at ThingDescription class
// TODO:  look at SDB's img lib jars
public class IngestData {
    public static boolean LOG_INGEST = true;
    public static boolean SKIP_INGEST = false;

    protected Integer jobId;
    protected String collectionArea = "";
    protected String topPI = "";
    protected String dlirStore = "";
    protected String[] srcDirs = new String[Thing.CopyRole.values().length];
    protected String checksumType = "SHA1";
    private Enumeration entryIterator;
    private JellyGraph metadata = new JellyGraph();
    private String ts = "";

    public IngestData(String dlirStoragePath, String topPI, String ts) {
        init(dlirStoragePath, topPI, null, null, ts);
    }
    
    public IngestData(String dlirStoragePath, String topPI,
            String checksumType, String ts) {
        init(dlirStoragePath, topPI, null, checksumType, ts);
    }

    public IngestData(String dlirStoragePath, String topPI,
            IngestParams params, String checksumType, String ts) {
        init(dlirStoragePath, topPI, params, checksumType, ts);
    }

    private void init(String dlirStoragePath, String topPI,
            IngestParams params, String checksumType, String ts) {
        this.topPI = topPI;
        this.dlirStore = dlirStoragePath;

        if (params != null) {
            srcDirs[Thing.CopyRole.ACCESS_COPY.idx()] = params.imageDirectory + "/";
            srcDirs[Thing.CopyRole.OCR_JSON_COPY.idx()] = params.ocrJsonDirectory + "/";
            srcDirs[Thing.CopyRole.OCR_ALTO_COPY.idx()] = params.ocrAltoDirectory + "/";
        }
        Date now = new java.util.Date();
        SimpleDateFormat fmt = new SimpleDateFormat();
        fmt.applyPattern("yyyymmdd'T'HHmmss");
        this.ts = fmt.format(now);
        this.ts = ts;

        if ((checksumType != null) && (!checksumType.isEmpty()))
            this.checksumType = checksumType;

        metadata = new JellyGraph();
    }

    public synchronized Thing createJellyItem(Thing parent, IngestEntry entry, int relOrder) throws NoSuchAlgorithmException, IOException {
        Thing work = createWork(entry, parent, relOrder);
        Map<Thing.CopyRole, IngestFile> copies = entry.entryData;
        for (Thing.CopyRole copyRole : copies.keySet()) {
            Thing copy = createCopy(entry, copyRole, work);
            Thing file = createFile(entry, copyRole, copy);
        }
        return work;
    } 
    
    public synchronized Thing createWork(IngestEntry entry, Thing parent, Integer relOrder) {
        Thing work = metadata.addThing(collectionArea, entry.pi(),
                Thing.ThingType.WORK.name().toLowerCase(), entry.entryType.name()
                        .toLowerCase());
        // TODO: add ThingDescription according to type, subtype

        Relationship r = metadata.addRelationship(work.id,
                Thing.ThingRelationship.ISPARTOF, parent.id, relOrder);
        return work;
    }

    public synchronized Thing createCopy(IngestEntry entry, Thing.CopyRole copyRole,
            Thing parent) {
        Thing copy = metadata.addThing(collectionArea, null,
                Thing.ThingType.COPY.name().toLowerCase(), Thing.ThingType.COPY
                        .name().toLowerCase());
        // TODO: add ThingDescription according to type, subtype

        // Relationship
        Relationship r = metadata.addRelationship(copy.id,
                Thing.ThingRelationship.ISCOPYOF, parent.id, null);

        // Record in ThingCopy
        ThingCopy _copy = new ThingCopy();
        _copy.id = copy.id;
        _copy.copyPid = entry.pi() + "-ac-v1";
        _copy.workPid = entry.pi();

        // TODO: need to query query to set all previous copies ingested with
        // the same copyRole
        // their currentVersion to "n".
        _copy.currentVersion = "y";

        // TODO: version number need to be modified later on too.
        _copy.versionNo = 1;

        _copy.copyType = "d";
        _copy.copyRole = copyRole.code();
        _copy.carrier = "Online";
        _copy.dateCreated = (Timestamp) new Date();
        _copy.sourceCopy = entry.pi() + "-m-v1";
        _copy.accessConditions = "Unrestricted";
        _copy.expiryDate = null;
        _copy.processId = null;
        _copy.save();

        return copy;
    }

    public synchronized Thing createFile(IngestEntry entry, Thing.CopyRole copyRole,
            Thing parent) {
        Thing file = metadata.addThing(collectionArea, entry.pi(),
                Thing.ThingType.FILE.name().toLowerCase(), Thing.ThingType.FILE
                        .name().toLowerCase());
        // TODO: add ThingDescription according to type, subtype

        // TODO: create records in ThingFile
        Relationship r = metadata.addRelationship(file.id,
                Thing.ThingRelationship.ISFILEOF, parent.id, null);

        // Record in ThingFile
        ThingFile _file = new ThingFile();
        _file.id = file.id;
        _file.fileName = entry.getFileName(copyRole);
        _file.fileSize = entry.getFileSize(copyRole);
        _file.checkSum = entry.getFileChecksum(copyRole);
        _file.save();

        return file;
    }

    public JellyGraph getMetadataGraph() {
        return metadata;
    }

    public void setCollectionArea(String collectionArea) {
        this.collectionArea = collectionArea;
    }

    public void setIngestJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String ts() {
        return ts;
    }

    public List<IngestEntry> validateIngestData() throws IOException,
            NoSuchAlgorithmException {
        // TODO: check ac and at directory has the same amount of files
        // and each ac file has a corresponding at file
        // if not, throw Exception
        //
        // Note: not to worry about the oc file at the moment
        // as these will be generated after ac and at files
        // are copied to dlir file storage.

        // generate ingest entry for each image file
        FileFilter jp2Filter = new FileFilter() {
            public boolean accept(File file) {
                String fileExt = ".jp2";
                if (file.getName().toLowerCase().endsWith(fileExt))
                    return true;
                return false;
            }
        };

        File imgDir = new File(srcDirs[Thing.CopyRole.ACCESS_COPY.idx()]);
        if (!imgDir.exists())
            throw new FileNotFoundException("image directory " + imgDir
                    + " does not exist.");

        // System.out.println("imgDir is " + imgDir);
        File[] files = imgDir.listFiles(jp2Filter);
        if ((files == null) || (files.length == 0))
            return null;
        List<IngestEntry> entries = new ArrayList<IngestEntry>();
        // entryIterator = Collections.enumeration(entries);
        for (File file : files) {
            // System.out.println("file: " + file.getName());
            IngestEntry entry = new IngestEntry();
            entry.ts = ts;
            entry.entryType = IngestEntry.ThingSubType.PAGE;

            IngestImg acFile = new IngestImg();
            acFile.fileName = file.getName();
            acFile.fileSize = file.length();
            acFile.checkSum = checksum(file);
            // get image height and width
            BufferedImage source = ImageIO.read(file);
            acFile.width = source.getWidth();
            acFile.height = source.getHeight();
            entry.entryData.put(Thing.CopyRole.ACCESS_COPY, acFile);

            String atSrcDir = srcDirs[Thing.CopyRole.OCR_ALTO_COPY.idx()];
            if ((atSrcDir != null) && (!atSrcDir.isEmpty())) {
                IngestFile atFile = new IngestFile();
                atFile.fileName = file.getName().replace(".jp2", ".xml");
                atFile.fileSize = new File(atSrcDir + "/" + atFile.fileName)
                        .length();
                atFile.checkSum = checksum(new File(atSrcDir + "/"
                        + atFile.fileName));
                entry.entryData.put(Thing.CopyRole.OCR_ALTO_COPY, atFile);
            }

            // ingest pre-req: ocr json file need to be created first
            // in the work area before the ingest.
            String ocSrcDir = srcDirs[Thing.CopyRole.OCR_JSON_COPY.idx()];
            if ((ocSrcDir != null) && (!ocSrcDir.isEmpty())) {
                IngestFile ocFile = new IngestFile();
                ocFile.fileName = file.getName().replace(".jp2", ".json.gz");
                ocFile.fileSize = new File(ocSrcDir + "/" + ocFile.fileName)
                        .length();
                ocFile.checkSum = checksum(new File(ocSrcDir + "/"
                        + ocFile.fileName));
                entry.entryData.put(Thing.CopyRole.OCR_JSON_COPY, ocFile);
            }

            entries.add(entry);
        }
        return entries;
    }

    protected String checksum(File file) throws IOException,
            NoSuchAlgorithmException {
        if ((file == null) || (!file.exists()))
            return null;
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[1024];

        int read = 0;

        MessageDigest md = MessageDigest.getInstance(checksumType);
        while ((read = fis.read(data)) != -1) {
            md.update(data, 0, read);
        }

        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return sb.toString();
    }

    public String getCollectionArea() {
        // TODO Auto-generated method stub
        return collectionArea;
    }
}
