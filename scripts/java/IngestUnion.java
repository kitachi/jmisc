package controllers.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import play.Play;
import play.libs.Json;

import utils.DbUtil;

import com.avaje.ebean.SqlRow;

import models.Thing;
import models.ingest.IngestData;
import models.ingest.IngestEntry;
import models.IngestParams;

/**
 * IngestUnion: provides APIs to generate sql scripts to ingest metadata
 * associated with a work item and its sub items.
 * 
 * limitation: this utilities can only run in single user mode currently, will
 * not generate correct job no. or metadata internal ids for con-current ingest
 * requests.
 * 
 * TODO: factor the parsing of template and processing of ingest entry to two
 * other separate classes.
 */
public class IngestUnion {
    public enum IngestStatus {
        /*
         * some metadata from the ingest exist in db already, but not all the
         * metadata. this may happen during concurrent ingest.
         */
        INCONSISTENT,
        /*
         * no metadata from the ingest exist in db yet.
         */
        CLEAN,
        /*
         * all metadata from the ingest exist in db.
         */
        INGESTED
    }

    public enum ScriptType {
        INGEST, UNDO
    }

    // TODO: change DLIR_FS_BASE to the approp. value
    // public static final String DLIR_FS_BASE = "/doss-devel/dlir";
    // public static final String DLIR_FS_WORKING = DLIR_FS_BASE + "/working";
    // public static final String DLIR_FS_MASTER = DLIR_FS_BASE + "/master";
    // public static final String DLIR_FS_DERIVATIVE = DLIR_FS_BASE
    // + "/derivative";
    // protected static final String SCRIPT_TEMPLATE_DIR = "scripts/";
    // protected static final String INGEST_TEMPLATE = "ingest_template.sql";
    // private static final String UNDO_TEMPLATE = "undo_ingest_template.sql";
    // private static final String JOB_LOCATION = "conf/evolutions/default/";

    public static final String CHECKSUM_SHA1 = "SHA1";
    public static String DLIR_FS_BASE;
    public static String DLIR_FS_WORKING;
    public static String DLIR_FS_MASTER;
    public static String DLIR_FS_DERIVATIVE;
    protected static String SCRIPT_TEMPLATE_DIR;
    protected static String INGEST_TEMPLATE;
    protected static String UNDO_TEMPLATE;
    protected static String JOB_LOCATION;

    private static final String TOP_ITEM_TAG = "# -- Top level item";
    private static final String CHILD_TAG = "# -- Child element";
    protected static final String SUB_ITEM_TAG = "# -- Subitem";
    private static final String AC_COPY_TAG = "# -- Access Copy";
    private static final String OC_COPY_TAG = "# -- OCR JSON Copy";
    private static final String AT_COPY_TAG = "# -- OCR ALTO Copy";
    private static final String AC_FILE_TAG = "# -- Access File";
    private static final String OC_FILE_TAG = "# -- OCR JSON File";
    private static final String AT_FILE_TAG = "# -- OCR ALTO File";
    private static final String LOG_JOB_TAG = "# -- log ingest job status";
    protected static final String JOB_TS_TAG = "<jobTS>";
    protected static final String TOP_UUID_TAG = "<topUUID>";
    protected static final String ITEM_PI_TAG = "<itemPI>";
    private static final String ITEM_TITLE_TAG = "<itemTitle>";
    private static final String ITEM_CREATOR_TAG = "<itemCreator>";
    private static final String ITEM_BIBID_TAG = "<itemBibId>";
    // =================================================================
    private static final String SEPARATOR = "# =======================================================================================================================";
    private static final String PAGE_ORDER_TAG = "<pageOrder>";
    private static final String FILE_NAME_TAG = "<fileName>";
    private static final String FILE_PATH_TAG = "<filePath>";
    private static final String FILE_SIZE_TAG = "<fileSize>";
    private static final String CHECKSUM_TAG = "<checkSum>";
    private static final String IMAGE_HEIGHT = "<imgHeight>";
    private static final String IMAGE_WIDTH = "<imgWidth>";
    private static final String INGEST_JOB_ID = "<ingestJobId>";
    private static final String UNDO_JOB_ID = "<undoJobId>";
    private static DbUtil du = new DbUtil();

    static {
        DLIR_FS_BASE = Play.application().configuration()
                .getString("dlirFSBase");
        DLIR_FS_WORKING = Play.application().configuration()
                .getString("dlirFSWorking");
        DLIR_FS_MASTER = Play.application().configuration()
                .getString("dlirFSMaster");
        DLIR_FS_DERIVATIVE = Play.application().configuration()
                .getString("dlirFSDerivative");
        SCRIPT_TEMPLATE_DIR = Play.application().configuration()
                .getString("ingestTemplateDir");
        INGEST_TEMPLATE = Play.application().configuration()
                .getString("ingestTemplate");
        UNDO_TEMPLATE = Play.application().configuration()
                .getString("undoTemplate");
        JOB_LOCATION = Play.application().configuration()
                .getString("jobLocation");
    }

    /**
     * activateIngestJob copy the ingest script to the conf/evolutions/default
     * dir as <jobNo>.sql with jobNo being the next number after the maximum
     * number in existing *.sql.
     * 
     * precond: will check ingestSatus, and only activate IngestJob is
     * ingestStatus is clean.
     * 
     * @param jobNo
     * @return the assigned job number, or -1 if fail to activate the ingest
     *         job.
     */
    public synchronized static int activateIngestJob(String ts,
            IngestParams ingestParams) {
        if (checkIngestStatus(ingestParams.pi, ts) != IngestStatus.CLEAN)
            return -1;

        int jobNo = getJobNo();
        try {
            String[] tags = { INGEST_JOB_ID };
            String[] vals = { "" + jobNo };
            replaceInFile(tags, vals, new File(SCRIPT_TEMPLATE_DIR + "job_"
                    + ingestParams.pi + "_" + ts + ".spt"), new File(
                    JOB_LOCATION + jobNo + ".sql"));
            return jobNo;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * activateUndoJob copy the undo script to the conf/evolutions/default dir
     * as <jobNo>.sql with jobNo being the next number after the maximum number
     * in existing *.sql.
     * 
     * precond: will check ingestStatus, and only activate UndoJob is
     * ingestStatus is not clean.
     * 
     * @param ingestJobNo
     * @return the assigned job number, or -1 if fail to activate the undo job.
     */
    public synchronized static int activateUndoJob(int ingestJobId, String ts,
            IngestParams ingestParams) throws Exception {
        if (checkIngestStatus(ingestJobId) == IngestStatus.CLEAN)
            throw new Exception("The ingest job " + ingestJobId
                    + " is cleaned out of the repository.");

        int undoJobNo = getJobNo();
        if (existJob(undoJobNo))
            throw new Exception("The undo job " + undoJobNo + " already exist.");

        try {
            String[] tags = { INGEST_JOB_ID, UNDO_JOB_ID };
            String[] vals = { "" + ingestJobId, "" + undoJobNo };
            replaceInFile(tags, vals, new File(SCRIPT_TEMPLATE_DIR
                    + "undo_job_" + ingestParams.pi + "_" + ts + ".spt"),
                    new File(JOB_LOCATION + undoJobNo + ".sql"));
            return undoJobNo;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized static boolean existJob(int jobNo) {
        String qry = "select count(jobNo) as existJob from dlir.dlIngestStatus where jobNo = ?";
        List<SqlRow> rs = du.executeQuery(qry, jobNo);
        if ((rs == null) || (rs.isEmpty()))
            return false;
        int existJob = rs.get(0).getInteger("existJob");
        return (existJob > 0) ? true : false;
    }

    /**
     * checkIngestStatus
     * 
     * @param ts
     *            the timestamp for the ingest request
     * @return either INCONSISTENT, CLEAN or INGESTED.
     */
    public synchronized static IngestStatus checkIngestStatus(String pi,
            String ts) {
        // status IngestStatus.INGESTED (all metadata from the ingest exist in
        // db.)
        // status IngestStatus.CLEAN (no metadata from the ingest exist in db
        // yet.)
        // status IngestStatus.INCONSISTENT (may happen during concurrent
        // ingest)
        String qry = "select status from dlIngestStatus where topUUID = ? and jobTS = ? order by status desc";
        List<SqlRow> rs = du.executeQuery(qry, pi, ts);
        if ((rs == null) || (rs.isEmpty()))
            return IngestStatus.CLEAN;
        int status = rs.get(0).getInteger("status");
        switch (status) {
        case 1:
            return IngestStatus.INGESTED;
        case 2:
            return IngestStatus.CLEAN;
        default:
            return IngestStatus.INCONSISTENT;
        }
    }

    /**
     * checkIngestStatus
     * 
     * @param jobNo
     * @return either INCONSISTENT, CLEAN or INGESTED.
     */
    public synchronized static IngestStatus checkIngestStatus(int jobNo) {
        // status IngestStatus.INGESTED (all metadata from the ingest exist in
        // db.)
        // status IngestStatus.CLEAN (no metadata from the ingest exist in db
        // yet.)
        // status IngestStatus.INCONSISTENT (may happen during concurrent
        // ingest)
        String qry = "select status from dlIngestStatus where jobNo = ?";
        List<SqlRow> rs = du.executeQuery(qry, jobNo);
        if ((rs == null) || (rs.isEmpty()))
            return IngestStatus.CLEAN;
        int status = rs.get(0).getInteger("status");
        switch (status) {
        case 1:
            return IngestStatus.INGESTED;
        case 2:
            return IngestStatus.CLEAN;
        default:
            return IngestStatus.INCONSISTENT;
        }
    }

    public synchronized static String getTimestamp() {
        Date now = new java.util.Date();
        SimpleDateFormat fmt = new SimpleDateFormat();
        fmt.applyPattern("yyyyMMdd'T'HHmmss");
        return fmt.format(now);
    }

    /**
     * createIngestScripts Write the sql ingest script and undo script using the
     * ingest params to scripts/ directory. The ingest script will be activated
     * and run as part of the IngestWorkflow.
     * 
     * The undo script will be called by IngestWorkflow if the ingested files
     * were failed to be moved to the dlir file storage area.
     * 
     * @param ts
     *            : timestame for the ingest request.
     * @param ingestParams
     *            : parameters from the ingest form.
     * @return true if successful or fale if unable to create the scripts.
     */
    public synchronized static boolean createIngestScripts(
            IngestParams ingestParams, String ts) {
        // create ingest script
        if (createIngestScript(ingestParams, ts)) {
            // create undo ingest script
            if (createUndoScript(ingestParams, ts)) {
                // return timestamp if the ingest scripts are created properly.
                return true;
            }
        }

        // return null timestamp if failed to create ingest scripts.
        return false;
    }

    /*
     * getFileChecksum
     * 
     * @param fileName: the file name for the checksum.
     * 
     * @precondition: assume the fileName is unique. Currently ingested files
     * contains the associated work item pi.
     */
    public synchronized static String getFileChecksum(String fileName) {
        if ((fileName == null) || (fileName.isEmpty()))
            return null;

        String sql = "select checkSum from dlThingCopyFile where fileName = ?";
        List<SqlRow> lst = du.executeQuery(sql, fileName);
        return ((lst == null) || (lst.isEmpty())) ? null : lst.get(0)
                .getString("checkSum");
    }

    /**
     * Write the sql ingest script using the ingest params to
     * conf/evolutions/default. The script will be run as part of the
     * IngestWorkflow.
     * 
     * @param ingestParams
     *            : parameters specifying
     * @return the job number, with -1 indicates
     */
    protected synchronized static boolean createIngestScript(
            IngestParams ingestParams, String ts) {
        File script = new File(JOB_LOCATION + "ingestScript.spt");
        try {
            // Collection<String> scriptLines = FileUtils.readLines(new
            // File(SCRIPT_TEMPLATE_DIR + INGEST_TEMPLATE));
            java.util.Hashtable<String, String> scriptLines = parseIngestTemplate(new File(
                    SCRIPT_TEMPLATE_DIR + INGEST_TEMPLATE));
            scriptHeaderFooter(script, ts, ingestParams,
                    scriptLines.get("header"));
            scriptTopItem(script, ts, ingestParams,
                    scriptLines.get(TOP_ITEM_TAG));

            IngestData data = new IngestData(DLIR_FS_BASE + ingestParams.pi
                    + "/", ingestParams.pi, ingestParams, CHECKSUM_SHA1, ts);

            // System.out.println(DLIR_FS_BASE + ingestParams.pi + "/");
            // System.out.println(Json.toJson(ingestParams));
            List<IngestEntry> entries = data.validateIngestData();
            if (entries == null)
                return false;

            for (IngestEntry entry : entries) {
                addSubItem(script, ts, entry, ingestParams, scriptLines);
            }

            scriptHeaderFooter(script, ts, ingestParams,
                    scriptLines.get("footer"));

            // for recreating an ingest script, check and delete if file exists
            // already
            File ingestScript = new File(SCRIPT_TEMPLATE_DIR + "job_"
                    + ingestParams.pi + "_" + ts + ".spt");
            if (ingestScript.exists())
                FileUtils.deleteQuietly(ingestScript);

            // NOTE: the tag <jobId> will not be substituted until at the
            // activation of
            // the ingest script time, at which time, we can get more accurate
            // jobId.
            //
            scriptHeaderFooter(script, ts, ingestParams,
                    scriptLines.get("footer"));
            FileUtils.moveFile(script, ingestScript);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            FileUtils.deleteQuietly(script);
            return false;
        }
    }

    protected synchronized static java.util.Hashtable<String, String> parseIngestTemplate(
            File template) {
        try {
            java.util.Hashtable<String, String> scriptLines = new java.util.Hashtable<String, String>();
            Scanner sc = new Scanner(template);
            sc.useDelimiter("\\s*" + SEPARATOR + "\\s*");
            boolean atHeader = true;

            while (sc.hasNext()) {
                String scriptPart = sc.next();
                if (scriptPart != null) {
                    if (scriptPart.startsWith(TOP_ITEM_TAG)) {
                        atHeader = false;
                        scriptLines.put(TOP_ITEM_TAG, scriptPart);
                    } else if (scriptPart.startsWith(SUB_ITEM_TAG)) {
                        scriptLines.put(SUB_ITEM_TAG, scriptPart);
                    } else if (scriptPart.startsWith(AC_COPY_TAG)) {
                        scriptLines.put(AC_COPY_TAG, scriptPart);
                    } else if (scriptPart.startsWith(OC_COPY_TAG)) {
                        scriptLines.put(OC_COPY_TAG, scriptPart);
                    } else if (scriptPart.startsWith(AT_COPY_TAG)) {
                        scriptLines.put(AT_COPY_TAG, scriptPart);
                    } else if (scriptPart.startsWith(AC_FILE_TAG)) {
                        scriptLines.put(AC_FILE_TAG, scriptPart);
                    } else if (scriptPart.startsWith(OC_FILE_TAG)) {
                        scriptLines.put(OC_FILE_TAG, scriptPart);
                    } else if (scriptPart.startsWith(AT_FILE_TAG)) {
                        scriptLines.put(AT_FILE_TAG, scriptPart);
                    } else if (atHeader) {
                        scriptLines.put("header", scriptPart);
                    } else {
                        scriptLines.put("footer", scriptPart);
                    }
                }

            }
            sc.close();
            return scriptLines;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected synchronized static void scriptHeaderFooter(File script,
            String ts, IngestParams ingestParams, String scriptPart)
            throws IOException {
        String headerFooter = scriptPart.replaceAll(JOB_TS_TAG, ts).replaceAll(
                TOP_UUID_TAG, ingestParams.pi);
        // System.out.println(headerFooter);
        FileUtils.write(script, headerFooter + "\n", true);
    }

    protected synchronized static void scriptTopItem(File script, String ts,
            IngestParams ingestParams, String scriptPart) throws IOException {

        String topItem = scriptPart.replaceAll(ITEM_PI_TAG, ingestParams.pi);
        topItem = topItem.replaceAll(ITEM_TITLE_TAG, ingestParams.title);
        topItem = topItem.replaceAll(ITEM_CREATOR_TAG, ingestParams.creator);
        topItem = topItem.replaceAll(ITEM_BIBID_TAG, ingestParams.bibId);
        topItem = topItem.replaceAll(JOB_TS_TAG, ts);
        topItem = topItem.replaceAll(TOP_UUID_TAG, ingestParams.pi);
        // System.out.println(topItem);
        FileUtils.write(script, topItem + "\n", true);
    }

    protected synchronized static void addSubItem(File script, String ts,
            IngestEntry entry, IngestParams ingestParams,
            java.util.Hashtable<String, String> scriptLines) throws IOException {
        if (entry == null)
            return;

        // replace tags in the following scriptParts
        // with data from IngestEntry.

        // SubItem
        String subItemPart = scriptLines.get(SUB_ITEM_TAG);
        subItemPart = subItemPart
                .replaceAll(PAGE_ORDER_TAG, "" + entry.order());
        subItemPart = subItemPart.replaceAll(ITEM_PI_TAG, entry.pi());
        subItemPart = subItemPart.replaceAll(JOB_TS_TAG, ts);
        subItemPart = subItemPart.replaceAll(TOP_UUID_TAG, ingestParams.pi);
        // System.out.println(subItemPart);
        FileUtils.write(script, subItemPart + "\n", true);

        // AC Copy
        String acCopyPart = scriptLines.get(AC_COPY_TAG);
        acCopyPart = acCopyPart.replaceAll(ITEM_PI_TAG, entry.pi());
        acCopyPart = acCopyPart.replaceAll(JOB_TS_TAG, ts);
        acCopyPart = acCopyPart.replaceAll(TOP_UUID_TAG, ingestParams.pi);
        // System.out.println(acCopyPart);
        FileUtils.write(script, acCopyPart + "\n", true);

        // OC Copy
        String ocCopyPart = scriptLines.get(OC_COPY_TAG);
        ;
        ocCopyPart = ocCopyPart.replaceAll(ITEM_PI_TAG, entry.pi());
        ocCopyPart = ocCopyPart.replaceAll(JOB_TS_TAG, ts);
        ocCopyPart = ocCopyPart.replaceAll(TOP_UUID_TAG, ingestParams.pi);
        // System.out.println(ocCopyPart);
        FileUtils.write(script, ocCopyPart + "\n", true);

        // AT Copy
        String atCopyPart = scriptLines.get(AT_COPY_TAG);
        atCopyPart = atCopyPart.replaceAll(ITEM_PI_TAG, entry.pi());
        atCopyPart = atCopyPart.replaceAll(JOB_TS_TAG, ts);
        atCopyPart = atCopyPart.replaceAll(TOP_UUID_TAG, ingestParams.pi);
        // System.out.println(atCopyPart);
        FileUtils.write(script, atCopyPart + "\n", true);

        // AC Copy File
        String acFilePart = scriptLines.get(AC_FILE_TAG);
        acFilePart = acFilePart
                .replaceAll(FILE_NAME_TAG, entry.getACFileName());
        acFilePart = acFilePart.replaceAll(FILE_PATH_TAG,
                entry.getFilePath(DLIR_FS_DERIVATIVE, ingestParams.pi, Thing.CopyRole.ACCESS_COPY));
        acFilePart = acFilePart.replaceAll(FILE_SIZE_TAG,
                "" + entry.getFileSize(Thing.CopyRole.ACCESS_COPY));
        acFilePart = acFilePart.replaceAll(CHECKSUM_TAG,
                entry.getFileChecksum(Thing.CopyRole.ACCESS_COPY));
        acFilePart = acFilePart.replaceAll(IMAGE_HEIGHT, "" + entry.height());
        acFilePart = acFilePart.replaceAll(IMAGE_WIDTH, "" + entry.width());
        acFilePart = acFilePart.replaceAll(JOB_TS_TAG, ts);
        acFilePart = acFilePart.replaceAll(TOP_UUID_TAG, ingestParams.pi);
        // System.out.println(acFilePart);
        FileUtils.write(script, acFilePart + "\n", true);

        // OC Copy File
        String ocFilePart = scriptLines.get(OC_FILE_TAG);
        ocFilePart = ocFilePart
                .replaceAll(FILE_NAME_TAG, entry.getOCFileName());
        ocFilePart = ocFilePart.replaceAll(FILE_PATH_TAG,
                entry.getFilePath(DLIR_FS_DERIVATIVE, ingestParams.pi, Thing.CopyRole.OCR_JSON_COPY));
        ocFilePart = ocFilePart.replaceAll(FILE_SIZE_TAG,
                "" + entry.getFileSize(Thing.CopyRole.OCR_JSON_COPY));
        ocFilePart = ocFilePart.replaceAll(CHECKSUM_TAG,
                entry.getFileChecksum(Thing.CopyRole.OCR_JSON_COPY));
        ocFilePart = ocFilePart.replaceAll(JOB_TS_TAG, ts);
        ocFilePart = ocFilePart.replaceAll(TOP_UUID_TAG, ingestParams.pi);
        // System.out.println(ocFilePart);
        FileUtils.write(script, ocFilePart + "\n", true);

        // AT Copy File
        String atFilePart = scriptLines.get(AT_FILE_TAG);
        atFilePart = atFilePart
                .replaceAll(FILE_NAME_TAG, entry.getATFileName());
        atFilePart = atFilePart.replaceAll(FILE_PATH_TAG,
                entry.getFilePath(DLIR_FS_MASTER, ingestParams.pi, Thing.CopyRole.OCR_ALTO_COPY));
        atFilePart = atFilePart.replaceAll(FILE_SIZE_TAG,
                "" + entry.getFileSize(Thing.CopyRole.OCR_ALTO_COPY));
        atFilePart = atFilePart.replaceAll(CHECKSUM_TAG,
                entry.getFileChecksum(Thing.CopyRole.OCR_ALTO_COPY));
        atFilePart = atFilePart.replaceAll(JOB_TS_TAG, ts);
        atFilePart = atFilePart.replaceAll(TOP_UUID_TAG, ingestParams.pi);
        // System.out.println(atFilePart);
        FileUtils.write(script, atFilePart + "\n", true);
    }

    /**
     * createUndoScript
     * 
     * @param jobNo
     * @param ingestParams
     * @return
     */
    protected synchronized static boolean createUndoScript(
            IngestParams ingestParams, String ts) {
        File undoTemplate = new File(SCRIPT_TEMPLATE_DIR + UNDO_TEMPLATE);
        File script = new File(SCRIPT_TEMPLATE_DIR + "undo_job_"
                + ingestParams.pi + "_" + ts + ".spt");
        try {
            String[] tags = { JOB_TS_TAG, TOP_UUID_TAG };
            String[] vals = { ts, ingestParams.pi };
            replaceInFile(tags, vals, undoTemplate, script);
        } catch (IOException e) {
            FileUtils.deleteQuietly(script);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected synchronized static int getJobNo() {
        // get the next evolution number
        FileFilter evolutionFilter = new FileFilter() {
            public boolean accept(File file) {
                String fileName = file.getName().toLowerCase();
                Pattern pattern = Pattern.compile("^\\d+\\.sql");
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find())
                    return true;
                return false;
            }
        };

        File jobDir = new File(JOB_LOCATION);
        File[] files = jobDir.listFiles(evolutionFilter);
        if ((files == null) || (files.length == 0))
            return 1;
        List<Integer> jobNos = new ArrayList<Integer>();
        for (File file : files) {
            Integer jobNo = new Integer(file.getName().replace(".sql", ""));
            jobNos.add(jobNo);
        }
        Collections.sort(jobNos);
        return jobNos.get(jobNos.size() - 1) + 1;
    }

    protected synchronized static String replaceInText(String text,
            String[] tags, String[] vals) {
        if ((tags == null) || (tags.length == 0))
            return text;
        if ((vals == null) || (vals.length == 0))
            return text;
        if ((text == null) || (text.isEmpty()))
            return text;

        for (int i = 0; i < tags.length; i++) {
            text = text.replaceAll(tags[i], vals[i]);
        }
        return text;
    }

    protected synchronized static void replaceInFile(String[] tags,
            String[] vals, File in, File out) throws IOException {
        if ((tags == null) || (tags.length == 0))
            return;
        if ((vals == null) || (vals.length == 0))
            return;

        BufferedReader reader = new BufferedReader(new FileReader(in));
        PrintWriter writer = new PrintWriter(new FileWriter(out));
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = replaceInText(line, tags, vals);
            writer.println(line);
        }
        reader.close();
        writer.close();
    }

}
