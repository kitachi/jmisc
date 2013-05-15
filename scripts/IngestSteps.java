package controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.IngestParams;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import scala.actors.threadpool.Arrays;
import utils.FolderViewHelper;
import views.html.ingest;
import views.html.ingestsubmit;
import views.html.ingests.status;
import views.html.ingests.steps;

public class IngestSteps extends Controller {
	
	private static String workingLocation = Play.application().configuration().getString("dlirFSWorking");
	private static String masterLocation = Play.application().configuration().getString("dlirFSMaster");
    private static String[] normalWFSteps = { "ConvertOCRToJson",
            "CreateMetadataIngestScripts", "RunMetadataIngestScript",
            "CopyManifestationFiles", "EmailNotification" };
    private static String[] rollbackWFSteps = { "UndoCopyFiles",
            "UndoMetadataIngest", "UndoOCRToJson" };
    private static Map<String, String> bfrLinks = new LinkedHashMap<String, String>();
    private static Map<String, String> runLinks = new LinkedHashMap<String, String>();
    private static Map<String, String> otherLinks = new LinkedHashMap<String, String>();
    private static final int BEFORE = 0;
    private static final int AFTER = 1;

    static {
        String[] ocrToJsonLinks = { "/Ingest/BeforeOCRToJson",
                "/Ingest/RunOCRToJson" };
        bfrLinks.put("ConvertOCRToJson", ocrToJsonLinks[BEFORE]);
        runLinks.put("ConvertOCRToJson", ocrToJsonLinks[AFTER]);

        String[] createMDScptsLinks = { "/Ingest/BeforeCrMDScpts",
                "/Ingest/CrMDScpts" };
        bfrLinks.put("CreateMetadataIngestScripts", createMDScptsLinks[BEFORE]);
        runLinks.put("CreateMetadataIngestScripts", createMDScptsLinks[AFTER]);

        String[] runMDScptLinks = { "/Ingest/BeforeRunMDScpt",
                "/Ingest/RunMDScpt" };
        bfrLinks.put("RunMetadataIngestScript", runMDScptLinks[BEFORE]);
        runLinks.put("RunMetadataIngestScript", runMDScptLinks[AFTER]);

        String[] copyFilesLinks = { "/Ingest/BeforeCopyFiles",
                "/Ingest/CopyFiles" };
        bfrLinks.put("CopyManifestationFiles", copyFilesLinks[BEFORE]);
        runLinks.put("CopyManifestationFiles", copyFilesLinks[AFTER]);

        String[] emailNotifyLinks = { "/NotApplicable", "/Ingest/EmailNotify" };
        bfrLinks.put("EmailNotification", emailNotifyLinks[BEFORE]);
        runLinks.put("EmailNotification", emailNotifyLinks[AFTER]);

        String[] undoCopyFilesLinks = { "/Ingest/BeforeUndoCopyFiles",
                "/Ingest/UndoCopyFiles" };
        bfrLinks.put("UndoCopyFiles", undoCopyFilesLinks[BEFORE]);
        runLinks.put("UndoCopyFiles", undoCopyFilesLinks[AFTER]);

        String[] undoMetadataLinks = { "/Ingest/BeforeUndoMetadata",
                "/Ingest/UndoMetadata" };
        bfrLinks.put("UndoMetadata", undoMetadataLinks[BEFORE]);
        runLinks.put("UndoMetadata", undoMetadataLinks[AFTER]);

        String[] undoOCRToJsonLinks = { "/Ingest/BeforeUndoOCRJson",
                "/Ingest/UndoOCRJson" };
        bfrLinks.put("UndoOCRJson", undoOCRToJsonLinks[BEFORE]);
        runLinks.put("UndoOCRJson", undoOCRToJsonLinks[AFTER]);

        otherLinks.put("List All Jobs", "/Ingest/Status");
        otherLinks.put("Repository Browse Item", "/Repository/Item/179720619");
        otherLinks.put("Show Top Item", "/Repository/Items/nla.aus-f02678?&type=page&relationship=isPartOf");
        otherLinks.put("Show Bib Data", "/Repository/BibData?pi=nla.aus-f02678");
        otherLinks.put("Show first page OCR", "/Repository/OCR/nla.aus-f02678-0001");
        otherLinks.put("Show first page Image", "/Repository/ImageReferrer/display/nla.aus-f02678-0001/2365");
        // otherLinks.put("Show Image For Page: ", "");
    }

    public static Result cannedInput(String uuid) {
    	String workigAreaView = FolderViewHelper.getFolderTreeView(workingLocation);
    	String masterAreaView = FolderViewHelper.getFolderTreeView(masterLocation);
        if ((uuid == null) || (uuid.isEmpty()))
            return ok(ingest.render(form(IngestParams.class), workigAreaView, masterAreaView, FolderViewHelper.getWorkingLocParent()));

        // System.out.println("uuid is " + uuid);
        String dlirFSWorking = System.getenv("DLIR_FS_WORKING");
        IngestParams params = new IngestParams();
        if (uuid.equalsIgnoreCase("nla.aus-vn1232378")) {
            params.pi = "nla.aus-vn1232378";
            params.bibId = "2708312";
            params.creator = "musical creator";
            params.title = "A musical magazine";
            params.derivativeImageDirectory = dlirFSWorking + "nla.mus-vn1232378/jp2/";
            params.ocrAltoDirectory = dlirFSWorking + "nla.mus-vn1232378/xml/";
            params.ocrJsonDirectory = dlirFSWorking + "nla.mus-vn1232378/ocr/";
            params.notifyEmail = "szhou@nla.gov.au";
        } else if (uuid.equalsIgnoreCase("nla.aus-f02678")) {
            params.pi = "nla.aus-f02678";
            params.bibId = "1810632";
            params.creator = "kids book creator";
            params.title = "A Book for Kids'' C.J. Dennis";
            params.derivativeImageDirectory = dlirFSWorking + "nla.aus-f02678/jp2/";
            params.ocrAltoDirectory = dlirFSWorking + "nla.aus-f02678/xml/";
            params.ocrJsonDirectory = dlirFSWorking + "nla.aus-f02678/ocr/";
            params.notifyEmail = "szhou@nla.gov.au";
        } else if (uuid.equalsIgnoreCase("nla.aus-vn4512714")) {
            params.pi = "nla.aus-vn4512714";
            params.bibId = "4511474";
            params.creator = "cookery book creator";
            params.title = "A cookery book";
            params.derivativeImageDirectory = dlirFSWorking + "nla.aus-vn4512714/jp2/";
            params.ocrAltoDirectory = dlirFSWorking + "nla.aus-vn4512714/xml/";
            params.ocrJsonDirectory = dlirFSWorking + "nla.aus-vn4512714/ocr/";
            params.notifyEmail = "szhou@nla.gov.au";
        }
        Form<IngestParams> inputForm = form(IngestParams.class).fill(params);
        return ok(ingest.render(inputForm, workigAreaView, masterAreaView, FolderViewHelper.getWorkingLocParent()));
    }

    public static Result showWFSteps(String uuid, String ts) {
        return ok(views.html.ingests.steps.render(uuid, ts, Arrays.asList(normalWFSteps),
                Arrays.asList(rollbackWFSteps), bfrLinks, runLinks, otherLinks));
    }
    
    public static Result showMDScriptIngest() {
        String SCRIPT_TEMPLATE_DIR = Play.application().configuration()
                .getString("ingestTemplateDir");
        response().setContentType("text/plain; charset=utf-8");
        return ok(new java.io.File(SCRIPT_TEMPLATE_DIR + "/job_nla.aus-f02678_20130412T175023.spt"));
    }
    
    public static Result showMDScriptUndo() {
        String SCRIPT_TEMPLATE_DIR = Play.application().configuration()
                .getString("ingestTemplateDir");
        response().setContentType("text/plain; charset=utf-8");
        return ok(new java.io.File(SCRIPT_TEMPLATE_DIR + "/undo_job_nla.aus-f02678_20130412T175023.spt"));
    }
}
