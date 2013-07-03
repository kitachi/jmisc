package ingest;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Thing;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import play.Play;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;
import utils.ingest.ALTO2JSON;
import utils.ingest.DlirSolrIndex;
import utils.ingest.DlirSolrPageIndexMock;
import utils.ingest.OCRHelper;
import junit.framework.TestCase;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static utils.ingest.DlirSolrPageIndexMock.TestItem;
import static utils.ingest.DlirSolrPageIndexMock.PageOCRContainer;

public class TestOCR extends TestCase {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    private TestItem[] spec = { TestItem.Page1, TestItem.Page2 };;
    private DlirSolrPageIndexMock dao;

    @BeforeClass
    public void setup() throws IOException {
        folder.create();
        dao = new DlirSolrPageIndexMock(PageOCRContainer.PageContainArticle, spec); 
    }

    @AfterClass
    public void cleanup() {
        folder.delete();
    }

    @Test
    public void testOCR() throws Exception {

        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                File metsFile = new File(
                        "/doss-devel/dlir/testdata/journals/aww-1950-02-25/mets-issue-nla.aus-issn00050458_19500225.xml");
                File altoFolder = new File("/doss-devel/dlir/testdata/journals/aww-1950-02-25/alto");
                File ocrFolder = new File("/doss-devel/dlir/testdata/journals/aww-1950-02-25/ocr");
                /*
                 * assertTrue("METS file exists", metsFile.exists());
                 * assertTrue("ALTO folder exists", altoFolder.exists());
                 * assertTrue("OCR folder exists", ocrFolder.exists());
                 */
                Map<String, models.Thing> articleIdMapping = new HashMap<String, models.Thing>();
                Thing thing1 = new Thing();
                thing1.id = 123L;
                Thing thing2 = new Thing();
                thing2.id = 124L;
                Thing thing3 = new Thing();
                thing3.id = 125L;

                articleIdMapping.put("divarticle1", thing1);
                articleIdMapping.put("divarticle2", thing2);
                articleIdMapping.put("divarticle3", thing3);

                ALTO2JSON a2j = new ALTO2JSON();
                /*
                 * try { System.out.println(altoFolder.getPath());
                 * a2j.convert(altoFolder.getPath(), metsFile.getPath(),
                 * articleIdMapping, ocrFolder.getPath(), 400); } catch
                 * (Exception e1) { System.out.println("Exception" +
                 * e1.getMessage()); e1.printStackTrace(); assertFalse(true); }
                 */

            }
        });

    }

    @Test
    public void testOCRIdxing() throws Exception {
        running(fakeApplication(), new Runnable() {

            @Override
            public void run() {
                // TODO: change id to be indexed by solr for article and page
                // by pre-pend a prefix.

                // TODO: test index a page by calling DlirSolrPageIndexMock

                // TODO: test index an article by calling
                // DlirSolrArticleIndexMock

            }

        });
    }

    @Test
    public void testGetOCRForArticle() throws Exception {
        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                try {
                    Thing work = createWorkWithArticlesOCR();
                    List<String> chkTokens = dao.verifyTokens();
                    Result result = callAction(controllers.routes.ref.ItemNodesController.showOCR(work.pi));
                    assertThat(status(result) == OK);
                    String _result = contentAsString(result);
                    
                    for (String chkToken : chkTokens) {
                        assertThat(contentAsString(result).contains(chkToken));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private Thing createWorkWithArticlesOCR() throws Exception {
        Thing w = new Thing();
        File f = createOCRJsonDocWithArticles(); 
        
        // TODO: create copy, file to link with Thing w.
        return w;
    }
    
    private File createOCRJsonDocWithArticles() throws Exception {
        try {
            File ocrJsonDoc = folder.newFile("ocrJsonDoc.json");
            FileUtils.write(ocrJsonDoc, dao.readDataFromUrl("test data source"));
            return ocrJsonDoc;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
