package controllers.helpers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import utils.DbUtil;

import com.avaje.ebean.SqlRow;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import models.IngestData;
import models.IngestParams;

/**
 * IngestUnion: provides APIs to generate sql scripts to ingest metadata
 *              associated with a work item and its sub items.
 * 
 * limitation:  this utilities can only run in single user mode currently,
 *              will not generate correct job no. or metadata internal ids
 *              for con-current ingest requests.
 *              
 * TODO:        factor the parsing of template and processing of ingest 
 *              entry to two other separate classes.             
 */
public class IngestUnion {
	public enum IngestStatus {
		/* some metadata from the ingest exist in db already, but not all the metadata.
		 * this may happen during concurrent ingest.
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
	
	private enum ScriptType {
		INGEST, UNDO
	}
	
	// TODO: change DLIR_FS_BASE to the approp. value
	private static final String DLIR_FS_BASE = "/tmp/dlir_file_storage/";
	private static final String SCRIPT_TEMPLATE_DIR = "scripts/";
	private static final String INGEST_TEMPLATE = "ingest_template.sql";
	private static final String UNDO_TEMPLATE   = "undo_ingest_template.sql";
	private static final String JOB_LOCATION = "conf/evolutions/default/";
	private static final String TOP_ITEM_TAG = "# -- Top level item";
	private static final String CHILD_TAG = "# -- Child element";
	private static final String SUB_ITEM_TAG = "# -- Subitem";
	private static final String AC_COPY_TAG = "# -- Access Copy";
	private static final String OC_COPY_TAG = "# -- OCR JSON Copy";
	private static final String AT_COPY_TAG = "# -- OCR ALTO Copy";
	private static final String AC_FILE_TAG = "# -- Access Copy File";
	private static final String OC_FILE_TAG = "# -- OCR JSON File";
	private static final String AT_FILE_TAG = "# -- OCR ALTO File";
	private static final String LOG_JOB_TAG = "# -- log ingest job status";
	private static final String REL_TAG     = "# -- Relationships";
	private static final String IS_PART_OF_TAG = "# -- isPartOf";
	private static final String IS_COPY_OF_TAG = "# -- isCopyOf";
	private static final String IS_FILE_OF_TAG = "# -- isFileOf";
	private static final String ITEM_PI_TAG    = "<itemPI>";
	private static final String ITEM_TITLE_TAG = "<itemTitle>";
	private static final String ITEM_CREATOR_TAG = "<itemCreator>";
	private static final String ITEM_BIBID_TAG = "<itemBibId>";
	// === internal id tags =============================================
	private static final String THING_ID       = "<thingID>";
	private static final String THING_COPY_ID  = "<thingCopyID>";
	private static final String THING_COPY_FILE_ID = "<thingCopyFileID>";
	private static final String FILE_LOCATION_ID   = "<fileLocationID>";
	private static final String RELATIONSHIP_ID    = "<relationshipID>";
	// =================================================================
	private static final java.util.Hashtable<String, String> INGEST_TABLE_MAP = new java.util.Hashtable<String, String>();
	private static final String[] ID_TAGS = { RELATIONSHIP_ID, FILE_LOCATION_ID, THING_COPY_FILE_ID, THING_COPY_ID, THING_ID };
	private static final String SEPARATOR = "# =======================================================================================================================";
	private static DbUtil du = new DbUtil();
	
	static {
		String[] ingestTables = { "dlRelationship", "dlFileLocation", "dlThingCopyFile", "dlThingCopy", "dlThing" };
		int i = 0;
		
		for (i = 0; i < ID_TAGS.length; i++) {
			INGEST_TABLE_MAP.put(ID_TAGS[i], ingestTables[i]);
		}
	};
	
	/**
	 * activateIngestJob
	 * @param jobNo
	 * @return
	 */
	public static boolean activateIngestJob(int jobNo, IngestParams ingestParams) {
		try {
			FileUtils.moveFile(new File(SCRIPT_TEMPLATE_DIR + "job_" + ingestParams.pi + "_" + jobNo + ".spt"),
					new File(JOB_LOCATION + jobNo + ".sql"));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	/**
	 * activateUndoJob
	 * @param jobNo
	 * @return
	 */
	public static boolean activateUndoJob(int jobNo, IngestParams ingestParams) {
		int undoJobNo = jobNo + 1;		
		try {
			FileUtils.moveFile(new File(SCRIPT_TEMPLATE_DIR + "undo_job_" + ingestParams.pi + "_" + jobNo + ".spt"),
					new File(JOB_LOCATION + undoJobNo + ".sql"));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * checkIngestStatus
	 */
	public static IngestStatus checkIngestStatus(int jobNo) {
		//      status IngestStatus.INGESTED  (all metadata from the ingest exist in db.)
		//      status IngestStatus.CLEAN (no metadata from the ingest exist in db yet.)
		//      status IngestStatus.INCONSISTENT (may happen during concurrent ingest)
		String qry = "select status from dlIngestStatus where jobId = ?";
		List<SqlRow> rs = du.executeQuery(qry, jobNo);
		if ((rs == null) || (rs.isEmpty())) return IngestStatus.CLEAN;
		int status = rs.get(0).getInteger("status");
		switch (status) {
		case 1 : return IngestStatus.INGESTED;
		case 2 : return IngestStatus.CLEAN;
		default: return IngestStatus.INCONSISTENT;
		}
	}
	
	/**
	 * createIngestScripts
	 * Write the sql ingest script and undo script using the ingest params
	 * to conf/evolutions/default.  The ingest script will be run as part 
	 * of the IngestWorkflow.  
	 * 
	 * The undo script will be called by IngestWorkflow if the ingested
	 * files were failed to be moved to the dlir file storage area.
	 *  
	 * @param ingestParams: parameters specifying
	 * @return the job number, with -1 indicates failing to create
	 *         the ingest script for the job. 
	 */
	public static int createIngestScripts(IngestParams ingestParams) { 
		int jobNo = getJobNo();
		java.util.Hashtable<String, Stack<Integer>> internalIDs = new java.util.Hashtable<String, Stack<Integer>>();
	    
		// create ingest script
		if (createIngestScript(jobNo, ingestParams, internalIDs)) {
			// create undo ingest script
			if (createUndoScript(jobNo, ingestParams, internalIDs)) {
				// write internalIDs into a file in scripts template directory incl jobNo in fileNo
				logInternalIDs(internalIDs, new File(SCRIPT_TEMPLATE_DIR + "job_" + ingestParams.pi + "_" + jobNo + "_records.lst"));
				return jobNo;
			}
		} 
		
		// return status: failure to create ingest scripts.
		return -1; 
	}

	/**
	 * Write the sql ingest script using the ingest params
	 * to conf/evolutions/default.  The script will be 
	 * run as part of the IngestWorkflow.
	 *  
	 * @param ingestParams: parameters specifying 
	 * @return the job number, with -1 indicates
	 */
	protected static boolean createIngestScript(int jobNo, IngestParams ingestParams, java.util.Hashtable<String, Stack<Integer>> internalIDs) {
		File script = new File(JOB_LOCATION + "ingestScript.spt");
		try {
			// Collection<String> scriptLines = FileUtils.readLines(new File(SCRIPT_TEMPLATE_DIR + INGEST_TEMPLATE));
			java.util.Hashtable<String, String> scriptLines = parseIngestTemplate(new File(SCRIPT_TEMPLATE_DIR + INGEST_TEMPLATE));
			FileUtils.write(script, scriptLines.get("header") + "\n");
			int topItemId = scriptTopItem(script, ingestParams, scriptLines.get(TOP_ITEM_TAG), internalIDs);
			// String dlirStoragePath, String topPI, String acSrcDir, String ocSrcDir, String atSrcDir
			IngestData data = new IngestData(DLIR_FS_BASE + ingestParams.pi + "/",
											 ingestParams.pi,
											 ingestParams.imageDirectory,
											 ingestParams.ocrJsonDirectory,
											 ingestParams.ocrAltoDirectory);
			while (data.hasNext()) { 
				addSubItem(script, topItemId, data.next(), ingestParams, scriptLines, internalIDs);
			}
			FileUtils.write(script, scriptLines.get("footer") + "\n");
			FileUtils.moveFile(script, new File(SCRIPT_TEMPLATE_DIR + "job_" + ingestParams.pi + "_" + jobNo + ".spt"));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			FileUtils.deleteQuietly(script);
			return false;
		}
	}
	
	protected static java.util.Hashtable<String, String> parseIngestTemplate(
			File template) {
		try {
			java.util.Hashtable<String, String> scriptLines = new java.util.Hashtable<String, String>();
			Scanner sc = new Scanner(template).useDelimiter("\\s*" + SEPARATOR + "\\s*");
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
					} else if (scriptPart.startsWith(IS_PART_OF_TAG)) {
						scriptLines.put(IS_PART_OF_TAG, scriptPart);
					} else if (scriptPart.startsWith(IS_COPY_OF_TAG)) {
						scriptLines.put(IS_COPY_OF_TAG, scriptPart);
					} else if (scriptPart.startsWith(IS_FILE_OF_TAG)) {
						scriptLines.put(IS_FILE_OF_TAG, scriptPart);
					} else if (atHeader) {
						scriptLines.put("header", scriptPart);
					} else {
						scriptLines.put("footer", scriptPart);
					}
				}

			}
			return scriptLines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected static int scriptTopItem(File script, IngestParams ingestParams,
			String scriptPart, java.util.Hashtable internalIDs) throws IOException {
		
		String topItem = scriptPart.replaceAll(ITEM_PI_TAG, ingestParams.pi);
		topItem = topItem.replaceAll(ITEM_TITLE_TAG, ingestParams.title);
		topItem = topItem.replaceAll(ITEM_CREATOR_TAG, ingestParams.creator);
		topItem = topItem.replaceAll(ITEM_BIBID_TAG, ingestParams.bibId);
		int thingId = nextInternalId(internalIDs, THING_ID);
		topItem = topItem.replaceAll(THING_ID, "" + thingId);
		FileUtils.write(script, topItem + "\n");
		return thingId;
	}
	
	protected static void addSubItem(File script, int topItemId, IngestData.IngestEntry entry, IngestParams ingestParams, java.util.Hashtable<String, String> scriptLines, java.util.Hashtable internalIDs) {
		if (entry == null) return;

		// TODO: replace tags in the following scriptParts
		//       with data from IngestEntry.
		String subItemPart = scriptLines.get(SUB_ITEM_TAG);
		// TODO: subItemPart.replaceAll(...);
		// TODO: FileUtils.write(...+"\n");
		String acCopyPart = scriptLines.get(AC_COPY_TAG);
		// TODO: acCopyPart.replaceAll(...);
		// TODO: FileUtils.write(...+"\n");
		String ocCopyPart = scriptLines.get(OC_COPY_TAG);
		// TODO: ocCopyPart.replaceAll(...);
		// TODO: FileUtils.write(...+"\n");
		String atCopyPart = scriptLines.get(AT_COPY_TAG);
		// TODO: atCopyPart.replaceAll(...);
		// TODO: FileUtils.write(...+"\n");
		String acFilePart = scriptLines.get(AC_FILE_TAG);
		// TODO: acFilePart.replaceAll(...);
		// TODO: FileUtils.write(...+"\n");
		String ocFilePart = scriptLines.get(OC_FILE_TAG);
		// TODO: ocFilePart.replaceAll(...);
		// TODO: FileUtils.write(...+"\n");
		String atFilePart = scriptLines.get(AT_FILE_TAG);
		// TODO: atFilePart.replaceAll(...);
		// TODO: FileUtils.write(...+"\n");
	}

	/**
	 * createUndoScript
	 * @param jobNo
	 * @param internalIDs
	 * @return
	 */
	protected static boolean createUndoScript(int jobNo, IngestParams ingestParams, java.util.Hashtable<String, Stack<Integer>> internalIDs) {
		File script = new File(JOB_LOCATION + "undoScript.spt");
		try {
			Collection<String> scriptLines = FileUtils.readLines(new File(SCRIPT_TEMPLATE_DIR + UNDO_TEMPLATE));
			
			// Hash undo stmts
			Hashtable stmts = new Hashtable();
			boolean atHeader = true;
			List<String> hdrLines = new ArrayList<String>();
			List<String> ftrLines = new ArrayList<String>();
			
			for (String tag : ID_TAGS) {
				for ( String scriptLine : scriptLines) {
					if (scriptLine.startsWith("delete from ")) {
						stmts.put(tag, scriptLine);
						atHeader = false;
					} else if (atHeader) { 
							hdrLines.add(scriptLine);
					} else {
							ftrLines.add(scriptLine);
					}
				}
			}
			
			// write out the header of the undo script
			for (String scriptLine : hdrLines) {
				FileUtils.write(script, scriptLine + "\n");
			}
			
			// write out the body of the undo script
			for (String tag : ID_TAGS) {
				Stack<Integer> ids = (Stack<Integer>) internalIDs.get(tag);
				String stmt = (String) stmts.get(tag);
				for (Integer id : ids) {
					FileUtils.write(script, stmt.replace(tag, ""+ id) + "\n");
				}
			}
			
			// write out the footer of the undo script
			for (String scriptLine : ftrLines) {
				if (scriptLine.startsWith("values(<jobId>")) {
					FileUtils.write(script, scriptLine.replace("<jobId>", "" + jobNo) + "\n");
				} else {
					FileUtils.write(script, scriptLine + "\n");
				}
			}
			
			// rename the script to be job<No>.sql
			FileUtils.moveFile(script, new File(SCRIPT_TEMPLATE_DIR + "undo_job_" + ingestParams.pi + "_" + jobNo + ".spt"));
			return true;
		} catch (IOException e) {
			FileUtils.deleteQuietly(script);
			e.printStackTrace();
			return false;
		}
	}
	
	protected static int getJobNo() {
		// get the next evolution number
		FileFilter evolutionFilter = new FileFilter() {
			public boolean accept(File file) {
				String fileName = file.getName().toLowerCase();
				Pattern pattern = Pattern.compile("^\\d+\\.sql");
				Matcher matcher = pattern.matcher(fileName);
				if (matcher.find()) return true;
				return false;
			}
		};
		
		File jobDir = new File(JOB_LOCATION);
		File[] files = jobDir.listFiles(evolutionFilter);
		if ((files == null) || (files.length == 0)) return 1;
		List<Integer> jobNos = new ArrayList<Integer>();
		for (File file : files) {
			Integer jobNo = new Integer(file.getName().replace(".sql", ""));
			jobNos.add(jobNo);
		}
		Collections.sort(jobNos);
		return jobNos.get(jobNos.size() - 1) + 1;
	}
	
	protected static void initInternalIDs(java.util.Hashtable<String, Stack<Integer>> internalIDs) {
		if (internalIDs == null) return;
		
		if (internalIDs.isEmpty()) {
			for (String tag : ID_TAGS) {
				Stack<Integer> idStack = new Stack<Integer>();
				String tableName = INGEST_TABLE_MAP.get(tag);
				idStack.push(nextInternalId(tableName));
				internalIDs.put(tag, idStack);
			}
		}
	}
	
	protected static int nextInternalId(java.util.Hashtable<String, Stack<Integer>> internalIDs, String tag) {
		if ((internalIDs == null) || (internalIDs.isEmpty())) {
			initInternalIDs(internalIDs);
			Stack<Integer> ids = internalIDs.get(tag);
			return ((ids == null ) || (ids.isEmpty()))? -1 : ids.peek();
		} else {
			Stack<Integer> ids = internalIDs.get(tag);
			if (ids == null) {
				ids = new Stack<Integer>();
				internalIDs.put(tag, ids);
			}
			if (ids.isEmpty()) {
				ids.push(1);
				return 1;
			}
			else {
				int nextId = ids.peek() + 1;
				ids.push(nextId);
				return nextId;
			}
		}
	}
	
	protected static int nextInternalId(String tableName) {
		String qry = "select max(id) + 1 as maxId from " + tableName;
		List<SqlRow> rs = du.executeQuery(qry);
		if ((rs == null) || (rs.isEmpty())) return 1;
		return rs.get(0).getInteger("maxId");
	}

	protected static void logInternalIDs(
			java.util.Hashtable<String, Stack<Integer>> internalIDs, File logFile) {
		if ((logFile == null) || (internalIDs == null)) return;
		
		Enumeration<String> keys = internalIDs.keys();
		try {
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				Stack<Integer> ids = (Stack<Integer>) internalIDs.get(key);
				FileUtils.write(logFile, "# =============================== \n");
				FileUtils.write(logFile, key + "\n");
				if ((ids != null) && (!ids.isEmpty())) {
					for (Integer id : ids)
						FileUtils.write(logFile, "" + id + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			FileUtils.deleteQuietly(logFile);
		}
	}
}
