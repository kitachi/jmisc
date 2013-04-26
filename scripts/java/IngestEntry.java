package models.ingest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Thing;

public class IngestEntry {        
    public static enum ThingSubType {
        WORK, COPY, FILE, PAGE, VOLUME, BOOK, CHAPTER, TITLE, ISSUE, EDITION, SECTION, SUPPLEMENT, ARTICLE, ARTICLEPART
    }
    
    protected Map<Thing.CopyRole, IngestFile> entryData = new HashMap<Thing.CopyRole, IngestFile>();
    protected ThingSubType entryType;
    protected IngestEntry parentEntry;
    protected String ts;
    
    public IngestEntry parentEntry() {
        return parentEntry;
    }
    
    public String getFileName(Thing.CopyRole copyRole) {
        return (entryData.get(copyRole) == null)? null : entryData.get(copyRole).fileName;
    }
    
    public String getFilePath(String baseDir, String topPI, Thing.CopyRole copyRole) {
        return baseDir + "/" + ts + "-" + topPI + "/" + getFileName(copyRole);
    }

    public long getFileSize(Thing.CopyRole copyRole) {
        return (entryData.get(copyRole) == null)? null : entryData.get(copyRole).fileSize;
    }

    public String getFileChecksum(Thing.CopyRole copyRole) {
        return (entryData.get(copyRole) == null)? null : entryData.get(copyRole).checkSum;
    }

    public int width() {
        return (entryData.get(Thing.CopyRole.ACCESS_COPY) == null)? null : ((IngestImg) entryData.get(Thing.CopyRole.ACCESS_COPY)).width;
    }

    public int height() {
        return (entryData.get(Thing.CopyRole.ACCESS_COPY) == null)? null : ((IngestImg) entryData.get(Thing.CopyRole.ACCESS_COPY)).height;
    }

    public int order() {
        // TODO: current default to the number in acFileName,
        // to lookup in METS file later maybe.
        Pattern pattern = Pattern.compile("\\s*(\\d+)\\.jp2");
        Matcher matcher = pattern.matcher(getFileName(Thing.CopyRole.ACCESS_COPY));
        return ((matcher.find()) ? new Integer(matcher.group(1)) : -1);
    }

    public String pi() {
        // TODO: current default to the prefix excl. number in acFileName,
        // to lookup in METS file later maybe.
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
}
