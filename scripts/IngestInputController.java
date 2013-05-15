package controllers;

import controllers.helpers.IngestUnion;
import models.IngestParams;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.FolderViewHelper;
import views.html.ingest;
import views.html.ingestsubmit;

public class IngestInputController extends Controller {
	private static String workingLocation = Play.application().configuration().getString("dlirFSWorking");
	private static String masterLocation = Play.application().configuration().getString("dlirFSMaster");
	private static String deriveLocation = Play.application().configuration().getString("dlirFSDerivative");
	
    public static Result save() {
    	String workigAreaView = FolderViewHelper.getFolderTreeView(workingLocation);
    	String mdAreaView = FolderViewHelper.getFolderTreeView(masterLocation) + FolderViewHelper.getFolderTreeView(deriveLocation);
    	
        Form<IngestParams> filledForm = form(IngestParams.class)
                .bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(ingest.render(filledForm, workigAreaView, mdAreaView, FolderViewHelper.getWorkingLocParent()));
        } else {
            IngestParams req= filledForm.get();
            req.ts = IngestUnion.getTimestamp();

            return ok(ingestsubmit.render(req.ts, filledForm.get().pi));
        }
    }

    public static Result index() {
    	
    	String workigAreaView = FolderViewHelper.getFolderTreeView(workingLocation);
    	String mdAreaView = FolderViewHelper.getFolderTreeView(masterLocation) + FolderViewHelper.getFolderTreeView(deriveLocation);

        return ok(ingest.render(form(IngestParams.class), workigAreaView, mdAreaView, FolderViewHelper.getWorkingLocParent()));
    }
    
     
}
