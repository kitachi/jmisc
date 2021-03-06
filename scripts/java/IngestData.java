package models.ingest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import models.IngestParams;
import models.Thing;
import models.ThingDescription;
import models.ThingFile;

import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import com.sun.media.imageio.plugins.tiff.TIFFDirectory;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

// TODO:  look at ThingDescription class
// TODO:  look at SDB's img lib jars
public class IngestData {
    public static final boolean LOG_INGEST = true;
    public static final boolean SKIP_INGEST = false;
    public static final String CHECKSUM_SHA1 = "SHA1";

    protected Long jobId;
    protected String collectionArea = "";
    protected String topPI = "";
    protected Path dlirStore;
    protected String[] srcDirs = new String[Thing.CopyRole.values().length];
    protected String checksumType = "SHA1";
    private Enumeration entryIterator;
    private JellyGraph metadata = new JellyGraph();
    private String ts = "";

    public IngestData(Path dlirStoragePath, String topPI, String ts) {
        init(dlirStoragePath, topPI, null, null, ts);
    }

    public IngestData(Path dlirStoragePath, String topPI,
            String checksumType, String ts) {
        init(dlirStoragePath, topPI, null, checksumType, ts);
    }

    public IngestData(Path dlirStoragePath, String topPI,
            IngestParams params, String checksumType, String ts) {
        init(dlirStoragePath, topPI, params, checksumType, ts);
    }

    private void init(Path dlirStoragePath, String topPI,
            IngestParams params, String checksumType, String ts) {
        this.topPI = topPI;
        this.dlirStore = dlirStoragePath;

        if (params != null) {
            srcDirs[Thing.CopyRole.ACCESS_COPY.idx()] = params.imageDirectory;
            srcDirs[Thing.CopyRole.OCR_JSON_COPY.idx()] = params.ocrJsonDirectory;
            srcDirs[Thing.CopyRole.OCR_ALTO_COPY.idx()] = params.ocrAltoDirectory;
            srcDirs[Thing.CopyRole.MASTER_COPY.idx()] = Paths.get(params.imageDirectory).getParent().resolve("tiff").toString();
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

    public synchronized Thing createJellyItem(Thing parent, IngestEntry entry, String link,
            ThingDescription desc, int relOrder) throws JsonGenerationException, JsonMappingException,
            NoSuchAlgorithmException, IOException {
        Thing work = createWork(entry, parent, link, desc, relOrder);
        Map<Thing.CopyRole, IngestFile> copies = entry.entryData;
        for (Thing.CopyRole copyRole : copies.keySet()) {
            Thing copy = createCopy(entry, copyRole, work);
            Thing file = createFile(entry, copyRole, copy);
        }
        return work;
    }
    
    /**
     * @pre-req: pages must be ingested before articles does.
     * @param parent
     * @param article
     * @param link
     * @param desc
     * @param relOrder
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public synchronized Thing createJellyItem(Thing parent, IngestArticle article, String link,
            ThingDescription desc, int relOrder) throws JsonGenerationException, JsonMappingException,
            NoSuchAlgorithmException, IOException {
        // TODO: relate article to pages
    }

    public synchronized Thing createWork(IngestEntry entry, Thing parent, String link,
            ThingDescription desc, Integer relOrder) throws JsonGenerationException, JsonMappingException, IOException {
        Thing work = metadata.addThing(collectionArea, entry.pi(),
                Thing.ThingType.WORK.name().toLowerCase(), entry.entryType
                        .name().toLowerCase());
        work.updateDescription(desc);
        Relationship r = metadata.addRelationship(work.id,
                Thing.ThingRelationship.ISPARTOF, parent.id, relOrder);
        return work;
    }

    public synchronized Thing createCopy(IngestEntry entry,
            Thing.CopyRole copyRole, Thing parent)
            throws JsonGenerationException, JsonMappingException, IOException {
        Thing copy = metadata.addThing(collectionArea, null,
                Thing.ThingType.COPY.name().toLowerCase(), Thing.ThingType.COPY
                        .name().toLowerCase());

        // Relationship
        Relationship r = metadata.addRelationship(copy.id,
                Thing.ThingRelationship.ISCOPYOF, parent.id, null);

        // Record in ThingCopy
        ThingCopy _copy = new ThingCopy();
        _copy.id = copy.id;
        _copy.copyPid = entry.pi() + "-" + copyRole.code() + "-v1";
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
        _copy.dateCreated = null;
        _copy.sourceCopy = entry.pi() + "-m-v1";
        _copy.accessConditions = "Unrestricted";
        _copy.expiryDate = null;
        _copy.processId = null;
        _copy.save();

        // add description
        copy.description = new ObjectMapper().writeValueAsBytes(_copy.toJson());
        copy.save();

        return copy;
    }

    public synchronized Thing createFile(IngestEntry entry,
            Thing.CopyRole copyRole, Thing parent)
            throws JsonGenerationException, JsonMappingException, IOException {
        Thing file = metadata.addThing(collectionArea, entry.pi() + "-" + copyRole + "-f",
                Thing.ThingType.FILE.name().toLowerCase(), Thing.ThingType.FILE
                        .name().toLowerCase());

        Relationship r = metadata.addRelationship(file.id,
                Thing.ThingRelationship.ISFILEOF, parent.id, null);

        // Record in ThingFile
        ThingFile _file = new ThingFile();
        _file.id = file.id;
        _file.fileName = entry.getFileName(copyRole);
        _file.fileSize = entry.getFileSize(copyRole);
        _file.checkSum = entry.getFileChecksum(copyRole);
        _file.save();

        // add description
        ObjectNode node = _file.toJson();
        // node.put("width", entry.width());
        // node.put("height", entry.height());
        node.put("width", "2265");
        node.put("height", "2138");

        file.description = new ObjectMapper().writeValueAsBytes(node);
        file.save();
        
        // fileLocation
        FileLocation location = new FileLocation();
        location.id = file.id;
        
        if ((copyRole == Thing.CopyRole.ACCESS_COPY) || (copyRole == Thing.CopyRole.OCR_JSON_COPY))
            location.fileLocation = entry.getFilePath(dlirStore.resolve("derivative").toString(), topPI, copyRole);
        else 
            location.fileLocation = entry.getFilePath(dlirStore.resolve("master").toString(), topPI, copyRole);  
        location.save();
        
        return file;
    }

    public JellyGraph getMetadataGraph() {
        return metadata;
    }

    public void setCollectionArea(String collectionArea) {
        this.collectionArea = collectionArea;
    }

    public void setIngestJobId(Long jobId) {
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

        File imgDir = Paths.get(srcDirs[Thing.CopyRole.ACCESS_COPY.idx()])
                .toFile();
        if (!imgDir.exists())
            throw new FileNotFoundException("image directory " + imgDir
                    + " does not exist.");

        // System.out.println("imgDir is " + imgDir);
        File[] files = imgDir.listFiles(jp2Filter);
        if ((files == null) || (files.length == 0))
            return null;
        List<IngestEntry> entries = new ArrayList<IngestEntry>();
        for (File file : files) {
            // System.out.println("file: " + file.getName());
            
            //Note: the following values will be extracted from the IngestMetsHelper
        	//       - checksum type and file checksum if exist
            //       - height in pixel, width in pixel, ocr measurement unit, xres, yres for tiff images
        	//       - height in pixel, width in pixel for jp2 images will have the same value as per tiff
            //       - page order
            IngestEntry entry = new IngestEntry();
            entry.ts = ts;
            entry.entryType = IngestEntry.ThingSubType.PAGE;

            IngestImg acFile = new IngestImg();
            acFile.fileName = file.getName();
            acFile.fileSize = file.length();
            // acFile.checkSum = checksum(file);

            entry.entryData.put(Thing.CopyRole.ACCESS_COPY, acFile);
            
            Path masterSrcPath = Paths.get(srcDirs[Thing.CopyRole.MASTER_COPY.idx()]);
            File masterSrcDir = masterSrcPath.toFile();
            if (masterSrcDir.exists()) {
                IngestImg mFile = new IngestImg();
                mFile.fileName = file.getName().replace(".jp2", ".tif");
                mFile.fileSize = masterSrcPath.resolve(mFile.fileName).toFile().length();
                // mFile.checkSum = checksum(masterSrcPath.resolve(mFile.fileName).toFile());
                mFile.width = acFile.width;
                mFile.height = acFile.height;
                entry.entryData.put(Thing.CopyRole.MASTER_COPY, mFile);
            }

            Path atSrcPath = Paths.get(srcDirs[Thing.CopyRole.OCR_ALTO_COPY
                    .idx()]);
            File atSrcDir = atSrcPath.toFile();
            if (atSrcDir.exists()) {
                IngestFile atFile = new IngestFile();
                atFile.fileName = file.getName().replace(".jp2", ".xml");
                atFile.fileSize = atSrcPath.resolve(atFile.fileName).toFile()
                        .length();
                // atFile.checkSum = checksum(atSrcPath.resolve(atFile.fileName)
                //        .toFile());
                entry.entryData.put(Thing.CopyRole.OCR_ALTO_COPY, atFile);
            }

            // ingest pre-req: ocr json file need to be created first
            // in the work area before the ingest.
            Path ocSrcPath = Paths.get(srcDirs[Thing.CopyRole.OCR_JSON_COPY
                    .idx()]);
            File ocSrcDir = ocSrcPath.toFile();
            if (ocSrcDir.exists()) {
                IngestFile ocFile = new IngestFile();
                ocFile.fileName = file.getName().replace(".jp2", ".json.gz");
                ocFile.fileSize = ocSrcPath.resolve(ocFile.fileName).toFile()
                        .length();
                // ocFile.checkSum = checksum(ocSrcPath.resolve(ocFile.fileName)
                //        .toFile());
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
