package controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.nio.file.Path;
import java.nio.file.Paths;

import controllers.helpers.IngestThread;
import controllers.helpers.IngestUnion;
import models.IngestParams;
import models.ingest.JobStatus;
import utils.FolderViewHelper;
import views.html.ingest;
import views.html.ingestsubmit;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class IngestWorkflow extends Controller {
	
	private static String workingLocation = Play.application().configuration().getString("dlirFSWorking");
	private static String masterLocation = Play.application().configuration().getString("dlirFSMaster");
    protected static Map<String, IngestParams> ingestRequests = new HashMap<String, IngestParams>();
    protected static Map<String, List<String>> jobLog = new LinkedHashMap<String, List<String>>();
    private static final IngestThread processor = IngestThread.getInstance();

    public static Result listJobs() {
        List<JobStatus> jobs = JobStatus.find.all();
        return ok(views.html.ingests.status.render(jobs));
    }

    public static Result showJobLog(String uuid, String ts) {
        // TODO:
        return ok();
    }

    public static Result index() {
    	
    	String workigAreaView = FolderViewHelper.getFolderTreeView(workingLocation);
    	String masterAreaView = FolderViewHelper.getFolderTreeView(masterLocation);
        return ok(ingest.render(form(IngestParams.class), workigAreaView, masterLocation, FolderViewHelper.getWorkingLocParent()));
    }

    public static Result ingest() throws InterruptedException {
    	
    	String workigAreaView = FolderViewHelper.getFolderTreeView(workingLocation);
    	String masterAreaView = FolderViewHelper.getFolderTreeView(masterLocation);
        Form<IngestParams> filledForm = form(IngestParams.class)
                .bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(ingest.render(filledForm, workigAreaView, masterLocation, FolderViewHelper.getWorkingLocParent()));
        } else {
            IngestParams req = filledForm.get();
            System.out.println(Json.toJson(req));

            req.ts = IngestUnion.getTimestamp();
            System.out.println("Queueing ingest req at " + req.ts);
            processor.queue(req);
            return ok(ingestsubmit.render(req.ts, filledForm.get().pi));
        }
    }

    private synchronized static void scheduleRequest(String ts,
            IngestParams params) {
        if ((params == null) || (params.pi == null) || (params.pi.isEmpty()))
            return;
        ingestRequests.put(params.pi + "_" + ts, params);
    }

    private synchronized static void completeRequest(String uuid, String ts) {
        if ((uuid == null) || (uuid.isEmpty()))
            return;
        if ((ts == null) || (ts.isEmpty()))
            return;
        ingestRequests.remove(uuid + "_" + ts);
        // TODO: insert update dlJobStatus Table
    }

    private synchronized static void stepConvertOCRToJson(String ts,
            IngestParams params) throws Exception {
        Path altoFolder = Paths.get(params.ocrAltoDirectory);
        Path workOCRFolder = altoFolder.getParent().resolve("ocr");
        // OCRHelper.createOCRJsonFromAlto(altoFolder, workOCRFolder, 400);
        // OCRHelper.gzipFilesFromFolder(workOCRFolder, true, "json");

        List<String> jlog = jobLog.get(params.pi + "_" + ts);
        jlog.add("Converted OCR to Json.");
    }

    private synchronized static void stepDeleteOCRJson(String ts,
            IngestParams params) throws Exception {

    }

    /*
     * @param ts: timestamp
     * 
     * @param ingestParam : IngestParams
     */
    public static Result runIngest(String uuid, String ts) {
        try {
            IngestParams params = ingestRequests.get(uuid + "_" + ts);
            stepConvertOCRToJson(ts, params);

            runStepCreateIngestSQLScripts();
            runStepRunIngestSQLScript(); // should get the ingest jobNo
            runStepCopyFiles();
            runStepEmailNotification();
            completeRequest(uuid, ts);
            return ok("Ingest completed");
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    // TODO: create a view with all workflow steps (input params: uuid and ts)
    // TODO:
    public static Result bfrStepConvertOCRToJson() {
        return ok("TODO");
    }

    /*
     * @param ts: timestamp
     */
    public static Result runStepConvertOCRToJson(String ts, IngestParams params) {
        try {
            // TODO
            stepConvertOCRToJson(ts, params);

            // TODO: show working area, verify OCR Json is there
            return ok("Ingest scheduled");
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    /*
     * @param ts: timestamp
     */
    public static Result runStepCreateIngestSQLScripts() {
        // TODO
        return ok("Ingest metatadata scripts created.");
    }

    /*
     * @param ts: timestamp
     */
    public static Result runStepRunIngestSQLScript() {
        // TODO
        return ok("Ingested metadata.");
    }

    /*
     * @param ts: timestamp
     */
    public static Result runStepCopyFiles() {
        // TODO
        return ok("copied files.");
    }

    /*
     * @param ts: timestamp
     */
    public static Result runStepEmailNotification() {
        // TODO
        return ok("sent notification email.");
    }

    public static Result undoIngest() {
        runUndoIngestSQLScript();
        undoCopyFiles();
        undoConvertOCRToJson();
        return ok("TODO");
    }

    public static Result undoConvertOCRToJson() {
        return ok("TODO");
    }

    public static Result runUndoIngestSQLScript() {
        return ok("TODO");
    }

    public static Result undoCopyFiles() {
        return ok("TODO");
    }

    // display dlJobStatus
    public static Result showJobStatus() {
        return ok("TODO");
    }
}
