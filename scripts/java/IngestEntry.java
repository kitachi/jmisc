package models.ingest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.node.ObjectNode;

import play.Play;
import play.libs.Json;

import models.Thing;

public class IngestEntry {        
    public static enum ThingSubType {
        WORK, COPY, FILE, PAGE, VOLUME, BOOK, CHAPTER, TITLE, ISSUE, EDITION, SECTION, SUPPLEMENT, ARTICLE, ARTICLEPART
    }
    
    protected Map<Thing.CopyRole, IngestFile> entryData = new HashMap<Thing.CopyRole, IngestFile>();
    protected ThingSubType entryType;
    protected IngestEntry parentEntry;
    protected String ts;
    protected String pi;
    
    public IngestEntry parentEntry() {
        return parentEntry;
    }
    
    public ThingSubType getEntryType() {
        return entryType;
    }
    
    public String getFileName(Thing.CopyRole copyRole) {
        return (entryData.get(copyRole) == null)? null : entryData.get(copyRole).fileName;
    }
    
//    public String getFilePath(String baseDir, String topPI, Thing.CopyRole copyRole) {
//        return Paths.get(baseDir).resolve(ts + "-" + topPI).resolve(getFileName(copyRole)).toAbsolutePath().toString();
//    }
    
    public String getFilePath(String baseDir, String topPI, Thing.CopyRole copyRole) {
        return Paths.get(baseDir).resolve(getFileName(copyRole)).toAbsolutePath().toString();
    }

    public long getFileSize(Thing.CopyRole copyRole) {
        return (entryData.get(copyRole) == null)? null : entryData.get(copyRole).fileSize;
    }

    public String getFileChecksum(Thing.CopyRole copyRole) {
        return (entryData.get(copyRole) == null)? null : entryData.get(copyRole).checkSum;
    }

    public Integer width() {
        if (entryType != ThingSubType.PAGE) return 0;
        return (entryData.get(Thing.CopyRole.ACCESS_COPY) == null)? 0 : ((IngestImg) entryData.get(Thing.CopyRole.ACCESS_COPY)).width;
    }

    public Integer height() {
        if (entryType != ThingSubType.PAGE) return 0;
        return (entryData.get(Thing.CopyRole.ACCESS_COPY) == null)? 0 : ((IngestImg) entryData.get(Thing.CopyRole.ACCESS_COPY)).height;
    }

    public int order() {
        // TODO: current default to the number in acFileName,
        // to lookup in METS file later maybe.
        if (entryType != ThingSubType.PAGE) return 0;
        
        Pattern pattern = Pattern.compile("\\s*(\\d+)\\.jp2");
        Matcher matcher = pattern.matcher(getFileName(Thing.CopyRole.ACCESS_COPY));
        return ((matcher.find()) ? new Integer(matcher.group(1)) : -1);
    }

    public String pi() {
        // TODO: current default to the prefix excl. number in acFileName,
        // to lookup in METS file later maybe.
        if (pi != null) return pi;
        
        String acFileName = getFileName(Thing.CopyRole.ACCESS_COPY);
        if ((acFileName == null) || (acFileName.isEmpty()))
            return "";
        return acFileName.replace(".jp2", "").replaceAll(
                "(\\D+)(0+)(\\d+)", "$1$3");
    }

    public boolean validate() {
        // TODO: check that acFileName, ocFileName, atFileName, and
        // getFilePath
        // all have the valid file name and file path.

        // TODO: check that pi conforms to the pi format.

        return true;
    }

    public String getACFileName() {
        return getFileName(Thing.CopyRole.ACCESS_COPY);
    }

    public String getOCFileName() {
        return getFileName(Thing.CopyRole.OCR_JSON_COPY);
    }

    public String getATFileName() {
        return getFileName(Thing.CopyRole.OCR_ALTO_COPY);
    }
    
    public ObjectNode toJson(String masterBase, String deriveBase, String topPI) {
        ObjectNode node = Json.newObject();
        node.put("parentEntry", (parentEntry() == null)?"":parentEntry().getEntryType().name());
        node.put("entryType", getEntryType().name());
        node.put("pi/uuid", pi());
        node.put("order", order());
        
        for (Thing.CopyRole copyRole : entryData.keySet()) {
            node.put(copyRole.name() + " fileName", getFileName(copyRole));
            
            if ((copyRole == Thing.CopyRole.ACCESS_COPY) ||
               (copyRole == Thing.CopyRole.METS_JSON_COPY) ||
               (copyRole == Thing.CopyRole.OCR_JSON_COPY)) {
                node.put(copyRole.name() + " filePath", getFilePath(deriveBase, topPI, copyRole));
            } else {
                node.put(copyRole.name() + " filePath", getFilePath(masterBase, topPI, copyRole));
            }
            node.put(copyRole.name() + " fileSize", getFileSize(copyRole));
            node.put(copyRole.name() + " checksum", getFileChecksum(copyRole));
            if (copyRole == Thing.CopyRole.ACCESS_COPY) {
                node.put("ac height", (height() == null)? "" : "" + height());
                node.put("ac width", (width() == null)? "" : "" + width());
            }
        }
       
        return node;
    }
}
