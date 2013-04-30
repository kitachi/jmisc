package controllers;

import java.util.List;

// for test only
import models.IngestParams;

import models.Thing;
import models.ThingFile;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import utils.GeneralUtil;
import controllers.helpers.IngestThread;
import controllers.helpers.ItemsJsonHelper;

public class ItemNodesController extends Controller {
    private static GeneralUtil gu = new GeneralUtil();
private static final IngestThread processor = IngestThread.getInstance();

// remove exception for test only
public static Result getVersion(String pi) throws InterruptedException {
        String version = gu.getRequestParam(request(), "version", "");
        if ((version == null) || (version.isEmpty()))
            version = "latest";

processor.queue(new IngestParams());
        return ok("version :" + version);
    }

    /**
     * showItems: processes the request for the structure map of the work item
     * with specified pi.
     * 
     * @param pi
     *            : the pi of the work item.
     * @param token
     *            : the access token for the request.
     * @param type
     *            : the type of children nodes to be returned.
     * @param type
     *            : the relationship btwn the root node (i.e. the work item with
     *            the specified pi) and the children nodes.
     * 
     * @exception: potential exceptions include: - JsonGenerationException -
     *             JsonMappingException - IOException
     * 
     * @return a Json document containing the structure map of the current work
     *         item with respect to the specified type (e.g. page) and
     *         relationship (e.g. isPartOf).
     */
    public static Result showItems(String pi) {
        try {
            if ((pi == null) || (pi.isEmpty()))
                return notFound("missing specifying PI.");

            String token = gu.getRequestParam(request(), "token", "");
            String type;
            String relationship;

            type = getRequestParam(request(), "type");
            relationship = getRequestParam(request(), "relationship");
            Thing thing = Thing.findByPI.byId(pi);
            if (thing == null)
                return notFound("Sorry requested item is not found.");

            List<Thing> items = thing.getLeafChildren(type, relationship);
            JsonNode doc = ItemsJsonHelper.toJson(thing, items);
            String prettyJson = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(doc);
            return ok(prettyJson);
        } catch (Exception e) {
            e.printStackTrace();
            return notFound("Invalid Json document.");
        }
    }

    /**
     * showOCR: processes the request for the ocr text and co-ords of the work
     * item with specified pi.
     * 
     * @precondition: the pi must point to a work item of subtype page.
     * 
     * @param pi
     *            : the pi of the work item.
     * @param token
     *            : the access token for the request.
     * 
     * @exception: potential exceptions include: - JsonGenerationException -
     *             JsonMappingException - IOException
     * 
     * @return a Json document containing the ocr text and co-ords the current
     *         work item.
     */
    public static Result showOCR(String pi) {
        try {
            if ((pi == null) || (pi.isEmpty()))
                return notFound("missing specifying PI.");

            String token = gu.getRequestParam(request(), "token", "");
            Thing thing = Thing.findByPI.byId(pi);
            if (thing == null)
                return notFound("Sorry requested item is not found.");

            if (!thing.getType().contains("page"))
                return notFound("Sorry the requested item is not a page, and ocr is only available for a page.");

            ThingFile ocrCopyFile = ThingFile.findCopyForWork.byId(thing
                    .getCurrentCopy(Thing.OCR_COPY));
            if (ocrCopyFile == null)
                return notFound("Sorry no OCR text is found for this item.");

            response().setContentType("application/json; charset=utf-8");
            response().setHeader("Content-Encoding", "gzip");
            response().setHeader("Access-Control-Allow-Origin", "*");
            return ok(new java.io.File(ocrCopyFile.getFileLocation()));
        } catch (Exception e) {
            e.printStackTrace();
            return notFound("Invalid Json document.");
        }
    }

    private static String getRequestParam(Request request, String name)
            throws Exception {
        String param = gu.getRequestParam(request, name, "");
        if ((param == null) && (param.isEmpty()))
            throw new Exception("missing specifying " + name);

        return param;
    }
}
