package controllers.helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import models.ingest.IngestData;
import models.ingest.IngestEntry;
import models.ingest.IngestMETS;
import models.IngestParams;
import models.Thing;
import models.ThingFile;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import scala.actors.threadpool.Arrays;
import utils.DbUtil;
import controllers.ImageRefController;
import controllers.helpers.IngestUnion;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class IngestUnionTest {
    private IngestParams ingestParamsVn1232378() {
        return ingestParams("nla.aus-vn1232378");
    }

    private IngestParams ingestParamsF02678() {
        return ingestParams("nla.aus-f02678");
    }

    private IngestParams ingestParamsVn4511474() {
        return ingestParams("nla.aus-vn4512714");
    }

    private IngestParams ingestParams(String piIdx) {
        String basedir = System.getenv("LOCAT_PREFIX")
                + System.getenv("DLIR_FS_WORKING");
        String ts = "";
        IngestParams params = new IngestParams();
        if (piIdx == null)
            return null;
        if (piIdx.equals("nla.aus-vn1232378")) {
            params.pi = "nla.aus-vn1232378";
            params.bibId = "2708312";
            params.creator = "musical creator";
            params.title = "A musical magazine";
            params.imageDirectory = basedir + "nla.mus-vn1232378/jp2/";
            params.ocrAltoDirectory = basedir + "nla.mus-vn1232378/xml/";
            params.ocrJsonDirectory = basedir + "nla.mus-vn1232378/ocr/";
        } else if (piIdx.equals("nla.aus-f02678")) {
            params.pi = "nla.aus-f02678";
            params.bibId = "1810632";
            params.creator = "kids book creator";
            params.title = "A Book for Kids'' C.J. Dennis";
            params.imageDirectory = basedir + "nla.aus-f02678/jp2/";
            params.ocrAltoDirectory = basedir + "nla.aus-f02678/xml/";
            params.ocrJsonDirectory = basedir + "nla.aus-f02678/ocr/";
        } else if (piIdx.equals("nla.aus-vn4512714")) {
            params.pi = "nla.aus-vn4512714";
            params.bibId = "4511474";
            params.creator = "cookery book creator";
            params.title = "A cookery book";
            params.imageDirectory = basedir + "nla.aus-vn4512714/jp2/";
            params.ocrAltoDirectory = basedir + "nla.aus-vn4512714/xml/";
            params.ocrJsonDirectory = basedir + "nla.aus-vn4512714/ocr/";
        } else if (piIdx.equals("nla.aus-an3281107")) {
            ts = "20130417T120058";
            basedir = System.getenv("LOCAL_PREFIX")
                    + System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-an3281107/";
            params.pi = "nla.aus-an3281107";
            params.bibId = "2770260";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.aus-an3819304")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-an3819304/";
            params.pi = "nla.aus-an3819304";
            params.bibId = "814517";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.aus-an4911308")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-an4911308/";
            params.pi = "nla.aus-an4911308";
            params.bibId = "857989";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.aus-an7221679")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-an7221679/";
            params.pi = "nla.aus-an7221679";
            params.bibId = "1810632";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.aus-f2678")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-f2678/";
            params.pi = "nla.aus-f2678";
            params.bibId = "380565";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.aus-f2688")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-f2688/";
            params.pi = "nla.aus-f2688";
            params.bibId = "998720";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.aus-nk1428")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-nk1428/";
            params.pi = "nla.aus-nk1428";
            params.bibId = "1034206";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.aus-nk4232")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-nk4232/";
            params.pi = "nla.aus-nk4232";
            params.bibId = "147281";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.aus-nk5677")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-nk5677/";
            params.pi = "nla.aus-nk5677";
            params.bibId = "392527";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.aus-nk873")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.aus-nk873/";
            params.pi = "nla.aus-nk873";
            params.bibId = "90039";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";

        } else if (piIdx.equals("nla.gen-an6520463s")) {
            ts = "20130417T120058";
            basedir = System.getenv("DERIVATIVE_AREA_CONFIG");
            params.imageDirectory = basedir + ts + "-" + "nla.gen-an6520463/";
            params.pi = "nla.gen-an6520463";
            params.bibId = "87364 ";
            params.creator = "";
            params.title = "";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";
        } else if (piIdx.equals("nla.aus-issn00050458")) {
            ts = IngestUnion.getTimestamp();
            params.imageDirectory = Paths.get("/doss-devel/dlir/working/aww_testdata/1950-02-25/jp2").toString();
            params.pi = "nla.aus-issn00050458";
            params.bibId = "1935438";
            params.creator = "aww team";
            params.title = "The Australian Women's Weekly (1933 - 1982) in Jelly";
            params.ocrAltoDirectory = "";
            params.ocrJsonDirectory = "";
        }
        return params;
    }

    private String getTS() {
        Date now = new java.util.Date();
        SimpleDateFormat fmt = new SimpleDateFormat();
        fmt.applyPattern("yyyyMMdd'T'HHmmss");
        return fmt.format(now);
    }

    private IngestData createIngestData(String pi) {
        try {
            System.out.println("create ingest data for " + pi);

            IngestParams params = null;
            if (pi.startsWith("nla.aus-vn1232378")) {
                params = ingestParamsVn1232378();
            } else if (pi.startsWith("nla.aus-f02678")) {
                params = ingestParamsF02678();
            } else if (pi.startsWith("nla.aus-vn4512714")) {
                params = ingestParamsVn4511474();
            }

            FileFilter jp2Filter = new FileFilter() {
                public boolean accept(File file) {
                    String fileExt = ".jp2";
                    if (file.getName().toLowerCase().endsWith(fileExt))
                        return true;
                    return false;
                }
            };

            File imgDir = new File(params.imageDirectory);
            File[] files = imgDir.listFiles(jp2Filter);
            System.out.println(files.length + " files found.");

            IngestData data = new IngestData(Paths.get(IngestUnion.DLIR_FS_BASE),
                    params.pi, params, "SHA1", getTS());
            List<IngestEntry> entries = data.validateIngestData();
            File out = new File("/tmp/ingest/" + pi + ".txt");

            if ((entries == null) && (entries.isEmpty()))
                System.out.println("there's no entries found for the ingest");

            for (IngestEntry entry : entries) {
                FileUtils.write(out, "acFileName is " + entry.getACFileName()
                        + "\n", true);
                FileUtils.write(out, "ocFileName is " + entry.getOCFileName()
                        + "\n", true);
                FileUtils.write(out, "atFileName is " + entry.getATFileName()
                        + "\n", true);
                FileUtils
                        .write(out,
                                "file path for ac is "
                                        + entry.getFilePath("/tmp/test/",
                                                "nla.aus-vn4512714", Thing.CopyRole.ACCESS_COPY)
                                        + "\n", true);
                FileUtils.write(out, "image width: " + entry.width() + "\n",
                        true);
                FileUtils.write(out, "image height: " + entry.height() + "\n",
                        true);
                FileUtils.write(out, "page order: " + entry.order() + "\n",
                        true);
                FileUtils.write(out, "item pi is " + entry.pi() + "\n", true);
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int createIngestScript(String pi) {
        IngestParams params = null;
        if (pi.startsWith("nla.aus-vn1232378")) {
            params = ingestParamsVn1232378();
        } else if (pi.startsWith("nla.aus-f02678")) {
            params = ingestParamsF02678();
        } else if (pi.startsWith("nla.aus-vn4512714")) {
            params = ingestParamsVn4511474();
        }

        int jobId = 98;
        String ts = getTS();
        boolean status = IngestUnion.createIngestScript(params, ts);
        if (status) {
            System.out.println("Ingest script is created successfully.");
        } else {
            System.out.println("Ingest script fail to be created.");
        }
        return jobId;
    }

    private String createIngestScripts(String pi) {
        IngestParams params = null;
        if (pi.startsWith("nla.aus-vn1232378")) {
            params = ingestParamsVn1232378();
        } else if (pi.startsWith("nla.aus-f02678")) {
            params = ingestParamsF02678();
        } else if (pi.startsWith("nla.aus-vn4512714")) {
            params = ingestParamsVn4511474();
        }

        String ts = IngestUnion.getTimestamp();
        if ((ts != null) && (!ts.isEmpty())) {
            IngestUnion.createIngestScripts(params, ts);
            System.out.println("Ingest script for pi " + params.pi
                    + " is created successfully at " + ts);
        } else {
            System.out.println("Ingest script for pi " + params.pi
                    + " fail to be created.");
        }

        return ts;
    }

    private String createIngestAccessCopyForPages(String pi) {
        String ts = "20130417T120058";

        IngestParams params = ingestParams(pi);
        // IngestAccessCopy.createIngestScripts(params, ts);
        return ts;
    }
    
    @Ignore
    // @Test
    public void testUpdateJournalTitle() {
        running(fakeApplication(), new Runnable() {
           @Override
           public void run() {
               String pi = "nla.aww-title112";
               String title = "The Australian Women's Weekly (1933 - 1982) in Jelly";
               try {
                   JournalIngestHelper.updateJournalTitle(pi, title);
               } catch (IngestException ex) {
                   ex.printStackTrace();
               }              
           }
        });
    }
    
    @Ignore
    //@Test
    public void testValidateJournalIssueEntries() {
        running(fakeApplication(), new Runnable() {
           @Override
           public void run() {
               // String ts = IngestUnion.getTimestamp();
               String ts = "20130429T120058";
               String metspath = Paths.get("/doss-devel/dlir/working/aww_testdata/1950-02-25/mets-issue-nla.aus-issn00050458_19500225.xml").toString();
               IngestParams params = ingestParams("nla.aus-issn00050458");
               try {
                System.out.println(new ObjectMapper().writeValueAsString(params));
                Path metsPath = Paths.get(metspath);
                Path dlirStoragePath = Paths.get(IngestUnion.DLIR_FS_BASE);
                IngestMETS data = new IngestMETS(dlirStoragePath, params.pi, metsPath, ts);
                data.setCollectionArea("nla.news");

                List<IngestEntry> entries = data.validateIngestData(IngestEntry.ThingSubType.ISSUE);
                System.out.println("\n");
                for (IngestEntry entry : entries) {
                    ObjectNode node = entry.toJson("/doss-devel/dlir/master", "/doss-devel/dlir/derivative", "nla.aus-issn00050458");
                    System.out.println(new ObjectMapper().writeValueAsString(node) + "\n\n");
                }
            } catch (NoSuchAlgorithmException|IOException|InvalidPathException e) {
                e.printStackTrace();
            }
           }
        });
    }
    
    // @Ignore
    @Test
    public void testIngestJournalIssue() {
        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                String titlePI = "nla.aww-title112";
                String ts = IngestUnion.getTimestamp();
                String metsPath = Paths.get("/doss-devel/dlir/working/aww_testdata/1950-02-25/mets-issue-nla.aus-issn00050458_19500225.xml").toString();
                IngestParams params = ingestParams("nla.aus-issn00050458");
                try {
                    JournalIngestHelper.ingest(titlePI, metsPath, params);
                } catch (NoSuchAlgorithmException | IngestException
                        | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Ignore
    // @Test
    public void testCreateIngestAccessCopyForPagesScripts() {
        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                // String[] topPIs = { "nla.aus-an3819304" };
                // Note: nla.aus-an3281107 (BBill) already has access copies for
                // its
                // pages.
                String[] topPIs = { "nla.aus-an4911308", "nla.aus-f2678",
                        "nla.aus-f2688", "nla.gen-an6520463",
                        "nla.aus-an7221679", "nla.aus-nk1428",
                        "nla.aus-nk4232", "nla.aus-nk5677", "nla.aus-nk873" };

                String ts = "";
                int jobNo = 0;
                for (String topPI : topPIs) {
                    IngestParams params = ingestParams(topPI);
                    ts = createIngestAccessCopyForPages(topPI);
                    /*jobNo = IngestAccessCopy.activateIngestJob(ts,
                            ingestParams(topPI));
                    System.out.println("topPI: " + topPI + " ts: " + ts
                            + "jobNo: " + jobNo);*/
                }
            }
        });

    }

    @Ignore
    // @Test
    public void testGetBibId() {
        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                String[] topPIs = { "nla.aus-an3819304", "nla.aus-an4911308",
                        "nla.aus-f2678", "nla.aus-f2688", "nla.gen-an6520463",
                        "nla.aus-an7221679", "nla.aus-nk1428",
                        "nla.aus-nk4232", "nla.aus-nk5677", "nla.aus-nk873" };

                for (String topPI : topPIs) {
                    Thing t = Thing.findByPI.byId(topPI);
                    System.out.println(t.link);
                }
            }
        });

    }

    private int createUndoIngestScript(String pi) {
        IngestParams params = null;
        if (pi.startsWith("nla.aus-vn1232378")) {
            params = ingestParamsVn1232378();
        } else if (pi.startsWith("nla.aus-f02678")) {
            params = ingestParamsF02678();
        } else if (pi.startsWith("nla.aus-vn4512714")) {
            params = ingestParamsVn4511474();
        }

        int jobId = 99;
        boolean status = IngestUnion.createUndoScript(params, getTS());
        if (status) {
            System.out.println("Undo ingest script created successfully.");
        } else {
            System.out.println("Undo ingest script fail to be created.");
        }
        return jobId;
    }

    @Ignore
    // @Test
    public void testIngestConfigurations() {
        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                assertEquals(IngestUnion.DLIR_FS_BASE,
                        "/doss-devel/dlir/");
                assertEquals(IngestUnion.DLIR_FS_WORKING,
                        "/doss-devel/dlir/working/ingest_testdata/");
                assertEquals(IngestUnion.DLIR_FS_MASTER,
                        "/doss-devel/dlir/master/");
                assertEquals(IngestUnion.DLIR_FS_DERIVATIVE,
                        "/doss-devel/dlir/derivative/");
                assertEquals(IngestUnion.SCRIPT_TEMPLATE_DIR,
                        "/Users/szhou/git/jelly-sz-ingest-script/scripts/");
                assertEquals(IngestUnion.INGEST_TEMPLATE, "ingest_template.sql");
                assertEquals(IngestUnion.UNDO_TEMPLATE,
                        "undo_ingest_template.sql");
                assertEquals(IngestUnion.JOB_LOCATION,
                        "/Users/szhou/git/jelly-sz-ingest-script/conf/evolutions/default/");
            }
        });
    }

    @Ignore
    // @Test
    public void testCreateIngestData() {
        createIngestData("nla.aus-vn1232378");
        createIngestData("nla.aus-f02678");
        createIngestData("nla.aus-vn4512714");
    }

    @Ignore
    // @Test
    public void testCreateIngestScript() {
        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                // createIngestScript("nla.aus-vn1232378");
                createIngestScript("nla.aus-f02678");
                createIngestScript("nla.aus-vn4512714");
            }

        });
    }

    @Ignore
    // @Test
    public void testCreateIngestScripts() {
        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                // createIngestScript("nla.aus-vn1232378");
                createIngestScripts("nla.aus-f02678");
                createIngestScripts("nla.aus-vn4512714");
            }

        });
    }

    @Ignore
    // @Test
    public void testActivateIngestAndUndoScripts() {
        running(fakeApplication(), new Runnable() {

            @Override
            public void run() {
                String ts = createIngestScripts("nla.aus-f02678");
                IngestParams params;
                DbUtil du = new DbUtil();

                String delete = "delete from dlIngestStatus where (jobNo = "
                        + IngestUnion.getJobNo() + " or jobNo=-1)";
                System.out.println("delete stmt: " + delete);
                du.executeUpdate(delete);

                params = ingestParamsF02678();
                int ingestId = IngestUnion.activateIngestJob(ts, params);
                System.out.println("Ingest id is " + ingestId);

                String insert = "insert into dlIngestStatus(jobNo, jobName, jobTS, topUUID, status, statusDescription, startDate, endDate) "
                        + "select "
                        + ingestId
                        + ", 'ingest for nla.aus-f02678 at 20130412T170933', '20130412T170933', 'nla.aus-f02678', 1, 'Ingested.', @startdate, sysdate()";

                du.executeUpdate(insert);
                int undoId = -1;
                try {
                    undoId = IngestUnion.activateUndoJob(ingestId, ts, params);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("Undo ingest id is " + undoId);
                // params = ingestParamsVn4511474();
                // IngestUnion.activateIngestJob(ts, params);
            }

        });
    }

    @Ignore
    // @Test
    public void testAddSubItem() {
        running(fakeApplication(), new Runnable() {

            @Override
            public void run() {
                try {
                    IngestParams params = ingestParamsVn1232378();
                    Hashtable<String, Stack<Integer>> ids = new Hashtable<String, Stack<Integer>>();
                    IngestData data = createIngestData("nla.aus-vn1232378");
                    List<IngestEntry> entries = data.validateIngestData();
                    if ((entries == null) || (entries.isEmpty())) {
                        System.out
                                .println("No entries found for nla.aus-vn1232378.");
                        return;
                    }

                    Hashtable<String, String> scriptLines = IngestUnion
                            .parseIngestTemplate(new File(
                                    IngestUnion.SCRIPT_TEMPLATE_DIR
                                            + IngestUnion.INGEST_TEMPLATE));
                    String subItemPart = scriptLines
                            .get(IngestUnion.SUB_ITEM_TAG);
                    System.out.println("pi is " + entries.get(0).pi());
                    subItemPart = subItemPart.replaceAll(
                            IngestUnion.ITEM_PI_TAG, entries.get(0).pi());
                    subItemPart = subItemPart.replaceAll(
                            IngestUnion.JOB_TS_TAG, getTS());
                    subItemPart = subItemPart.replaceAll(
                            IngestUnion.TOP_UUID_TAG, params.pi);
                    System.out.println("subItemPart is " + subItemPart);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Ignore
    // @Test
    public void testCheckIngestStatus() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                String insertSql = "insert into dlIngestStatus(jobNo, status, statusDescription) values(98, 1, 'aaaa')";
                String deleteSql = "delete from dlIngestStatus where jobNo = 98";
                DbUtil du = new DbUtil();
                du.executeUpdate(deleteSql);
                du.executeUpdate(insertSql);
                IngestUnion.IngestStatus status = IngestUnion
                        .checkIngestStatus(98);
                System.out.println("status is " + status);
                du.executeUpdate(deleteSql);
            }
        });
    }

    // TODO: testIngestStatusClean()
    // TODO: testIngestStatusIngested()
    // TODO: testIngestStatusInconsistent()
    // TODO: logInternalIDs(...)

    @Ignore
    // @Test
    public void testCreateUndoScript() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                // createUndoIngestScript("nla.aus-vn1232378");
                createUndoIngestScript("nla.aus-f02678");
                createUndoIngestScript("nla.aus-vn4512714");
            }
        });
    }

    @Ignore
    // @Test
    public void testParseIngestTemplate() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                String fileName = "scripts/ingest_template.sql";
                Hashtable<String, String> map = IngestUnion
                        .parseIngestTemplate(new File(fileName));
                Enumeration<String> keys = map.keys();

                System.out.println("test parsing ingest template: ");
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    System.out.println("key is " + key);
                    System.out.println("data: " + map.get(key));
                }
            }
        });
    }

    private void sizeImage(File imgFile) {
        BufferedImage source;
        try {
            source = ImageIO.read(imgFile);

            int width = source.getWidth();
            int height = source.getHeight();

            System.out.println("!==================================");
            System.out.println(imgFile.getName() + " has width: " + width
                    + " height: " + height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sizeJP2(File imgFile) {
        BufferedImage source;

        Iterator<ImageReader> it = ImageIO
                .getImageReadersByMIMEType("image/jpeg2000");
        if ((it == null) || (!it.hasNext())) {
            System.out.println("No jpeg2000 image reader is found");
        } else {
            System.out.println("Jpeg2000 image reader is found");
        }

        String[] readFormats = ImageIO.getReaderMIMETypes();
        System.out.println("can read format: " + Arrays.asList(readFormats));
    }

    @Ignore
    // @Test
    public void testGetImgWidthHeight() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                File imgFile = new File(
                        "/Volumes/OUT/NLA-Books-Fast/NLA-Books/nla.mus-vn1232378/nla.mus-vn1232378-v/nla.mus-vn1232378-0045-v.jpg");
                sizeImage(imgFile);
                sizeJP2(imgFile);
                imgFile = new File(
                        "/Volumes/OUT/NLA-Books-Fast/NLA-Books/nla.mus-vn1232378/nla.mus-vn1232378-c/nla.mus-vn1232378-0052-c.tif");
                sizeImage(imgFile);
                imgFile = new File(
                        "/Volumes/doss-devel/dlir/working/ingest_testdata/nla.mus-vn1232378/jp2/nla.mus-vn1232378-0045.jp2");
                sizeImage(imgFile);
            }
        });

    }

    @Ignore
    // @Test
    public void testGetImagePath() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                System.out.println("image path for nla.aus-an3819304-s15 is"
                        + ImageRefController
                                .getImagePath("nla.aus-an3819304-s15"));
            }
        });
    }

    /**
     * List BiBill fileId, file pathe
     */
    @Ignore
    // @Test
    public void testListBBillFileIDnPath() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Thing bbill = Thing.find.byId(179720227L);
                List<Thing> pages = bbill.getLeafChildren("page", "isPartOf");
                File list = new File("/tmp/bbillFilePathList.txt");
                for (Thing page : pages) {
                    try {
                            Thing acCopy = page.getCurrentCopy(Thing.ACCESS_COPY);
                            ThingFile acFile = ThingFile.findCopyForWork.byId(acCopy);
                            String filePath = "/doss-devel/dlir/derivative/20130402T145925-nla.mus-vn1232378/"
                                    + acFile.fileName;
                            filePath = filePath.replace("-ac.jp2", ".jp2");
                            // FileUtils.write(list, filePath + "," + acFile.id + "\n", true);
                            FileUtils.write(list, acFile.id + "\n", true);
                            Thing ocCopy = page.getCurrentCopy(Thing.OCR_COPY);
                            ThingFile ocFile = ThingFile.findCopyForWork.byId(ocCopy);
                            filePath = "/doss-devel/dlir/derivative/20130402T145925-nla.mus-vn1232378/" + ocFile.fileName;
                            // FileUtils.write(list, filePath + "," + ocFile.id + "\n", true);
                            FileUtils.write(list, ocFile.id + "\n", true);
        
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }      }
            }
        });
    }

    /**
     * List BBill checksum, file size
     */
    @Ignore
    // @Test
    public void testListBBileACChksumnSize() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Thing bbill = Thing.find.byId(179720227L);
                List<Thing> pages = bbill.getLeafChildren("page", "isPartOf");
                File list = new File("/tmp/bbillFileChksumSizeAC.txt");
                for (Thing page : pages) {
                    Thing acCopy = page.getCurrentCopy(Thing.ACCESS_COPY);
                    ThingFile acFile = ThingFile.findCopyForWork.byId(acCopy);
                    String filePath = "/doss-devel/dlir/derivative/20130402T145925-nla.mus-vn1232378/" + acFile.fileName;
                    filePath = filePath.replace("-ac.jp2", ".jp2");
                    File _acFile = new File(filePath);
                    try {
                        FileUtils.write(list, _acFile.length() + "," + checksum(_acFile) + ",# " + acFile.id + "\n", true);
                    } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }                
            }
        });
    }
    
    @Ignore
    // @Test
    public void testListBBileOCChksumnSize() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Thing bbill = Thing.find.byId(179720227L);
                List<Thing> pages = bbill.getLeafChildren("page", "isPartOf");
                File list = new File("/tmp/bbillFileChecksumSizeOC.txt");
                for (Thing page : pages) {
                    Thing ocCopy = page.getCurrentCopy(Thing.OCR_COPY);
                    ThingFile ocFile = ThingFile.findCopyForWork.byId(ocCopy);
                    String filePath = "/doss-devel/dlir/derivative/20130402T145925-nla.mus-vn1232378/" + ocFile.fileName;
                    File _ocFile = new File(filePath);
                    try {
                        FileUtils.write(list, _ocFile.length() + "," + checksum(_ocFile) + ",# " + ocFile.id + "\n", true);
                    } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }                
            }
        });
    }

    private String checksum(File file) throws IOException,
            NoSuchAlgorithmException {
        if ((file == null) || (!file.exists()))
            return null;
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[1024];

        int read = 0;

        MessageDigest md = MessageDigest.getInstance("SHA1");
        while ((read = fis.read(data)) != -1) {
            md.update(data, 0, read);
        }

        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return sb.toString();
    }
}
