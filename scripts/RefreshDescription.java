package controllers;

import models.Thing;
import com.avaje.ebean.Ebean;

public class MetaDataUpdate extends Controller {

    // TODO: validate pi with the au.gov.nla.PI pkg
    //       todo: validators to enforce input types for all jelly APIs
    public static Result rfshDescriptionForCopyFiles(String pi, String copy) {
        Thing t = Thing.findByPI.byId(pi);
        try {
            updDescriptionForCopyFiles(t, copy);
	    return ok("The file size and checksum in the description(json) is updated to match values in the corresponding metadata fields.");
        } catch (Exception e) {
            e.printStackTrace();
            return notFound("unable to complete refreshing description for copy files of " + pi + ".");
        }
    }
    
    private static void updDescriptionForCopyFiles(Thing t, String copy) throws Exception {
        if (t.isCopy(copy)) {
            updDescriptionForCopyFile(t);
	} else if (t.type.equals("work")) {
            if (t.getCurrentCopy(copy) ! = null) {
                updDescriptionForCopyFile(t.getCurrentCopy(copy));
            }
            List<Thing> children = getChildren();
            for (Thing child : children) {
                updDescriptionForCopyFiles(child, copy);
            }
        }
    }

    private static void updDescriptionForCopyFile(Thing copyFile) throws Exception {
        if (copyFile == null) throw new FileNotFoundException("copyFile not found.");
        ThingFile cf = ThingFile.findCopyForWork.byId(copyFile);
        if (cf == null) throw new FileNotFoundException("cf not found.");

        ObjectNode desc;

        try {
            desc = (ObjectNode) new ObjectMapper().reader().readTree(copyFile.description);
            desc.get("fileSize").setTextValue(cf.fileSize);
            desc.get("checkSum").setTextValue(cf.checkSum);
            Ebeans.startTransaction();
            copyFile.description = desc.toString();       
            copyFile.save();
            Ebeans.commit();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
