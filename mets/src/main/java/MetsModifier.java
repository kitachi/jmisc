import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.codehaus.jackson.node.ObjectNode;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;
import edu.harvard.hul.ois.mets.AmdSec;
import edu.harvard.hul.ois.mets.Area;
import edu.harvard.hul.ois.mets.Div;
import edu.harvard.hul.ois.mets.DmdSec;
import edu.harvard.hul.ois.mets.DigiprovMD;
import edu.harvard.hul.ois.mets.File;
import edu.harvard.hul.ois.mets.FileGrp;
import edu.harvard.hul.ois.mets.FileSec;
import edu.harvard.hul.ois.mets.Fptr;
import edu.harvard.hul.ois.mets.MdWrap;
import edu.harvard.hul.ois.mets.Mets;
import edu.harvard.hul.ois.mets.MetsHdr;
import edu.harvard.hul.ois.mets.StructMap;
import edu.harvard.hul.ois.mets.TechMD;
import edu.harvard.hul.ois.mets.XmlData;
import edu.harvard.hul.ois.mets.helper.Any;
import edu.harvard.hul.ois.mets.helper.MetsElement;
import edu.harvard.hul.ois.mets.helper.MetsException;
import edu.harvard.hul.ois.mets.helper.MetsReader;
import edu.harvard.hul.ois.mets.helper.MetsValidator;
import edu.harvard.hul.ois.mets.helper.MetsWriter;
import edu.harvard.hul.ois.mets.helper.PCData;

public class MetsModifier {
    private static final String METS = "http://www.loc.gov/METS/";
    private static final String PREMIS = "http://www.loc.gov/standards/premis/v1";

    private static void includeArticlesInDmdSec(Mets mets, String[] articles, DmdSec sec) {
        String dmdId = sec.getID();
        for (String article : articles) {
            if (article.equals(dmdId)) {
                mets.getContent().add(sec);
            }
        }
    }

    private static void includePagesInAmdSec(Mets mets, String[] pages, AmdSec sec, MetsWriter mw,
            ByteArrayOutputStream bos) throws ValidityException, ParsingException, IOException, MetsException {
        AmdSec newAmdSec = new AmdSec();
        newAmdSec.setSchema("mets", METS);
        mets.getContent().add(newAmdSec);
        List<MetsElement> elems = ((MetsElement) sec).getContent();
        for (MetsElement elem : elems) {
            if (elem instanceof TechMD) {
                TechMD md = (TechMD) elem;
                if (md.getContent().get(0) instanceof MdWrap) {
                    MdWrap mdWrap = (MdWrap) md.getContent().get(0);
                    if (mdWrap.getContent().get(0) instanceof XmlData) {
                        XmlData mdXml = (XmlData) mdWrap.getContent().get(0);
                        Any mdAny = (Any) mdXml.getContent().get(0);
                        mdAny.write(mw);
                        mw.flush();
                        String objIdentifier = qryXmlDocument(bos.toByteArray(), "premis", PREMIS,
                                "//premis:objectIdentifierValue");
                        for (String page : pages) {
                            if (stripExtension(objIdentifier).equals(page)) {
                                newAmdSec.getContent().add(elem);
                            }
                        }
                        bos.reset();
                    }
                }
            } else if (elem instanceof DigiprovMD) {
                DigiprovMD md = (DigiprovMD) elem;
                if (md.getContent().get(0) instanceof MdWrap) {
                    MdWrap mdWrap = (MdWrap) md.getContent().get(0);
                    if (mdWrap.getContent().get(0) instanceof XmlData) {
                        XmlData mdXml = (XmlData) mdWrap.getContent().get(0);
                        Any mdAny = (Any) mdXml.getContent().get(0);
                        mdAny.write(mw);
                        mw.flush();
                        String objIdentifier = qryXmlDocument(bos.toByteArray(), "premis", PREMIS,
                                "//premis:eventIdentifierValue");
                        for (String page : pages) {
                            if (stripExtension(objIdentifier).contains(page)) {
                                newAmdSec.getContent().add(elem);
                            }
                        }
                        bos.reset();
                    }
                }
            }
        }
    }

    private static String stripExtension(String objIdentifier) {
        if (objIdentifier == null || objIdentifier.isEmpty())
            return objIdentifier;
        return objIdentifier.substring(0, objIdentifier.indexOf("."));
    }

    private static String qryXmlDocument(byte[] cdata, String nsprefix, String namespace, String qry)
            throws ValidityException, ParsingException, IOException {
        Document mdDoc = getXmlDocument(cdata);
        XPathContext xc = new XPathContext();
        xc.addNamespace(nsprefix, namespace);
        Nodes nodes = mdDoc.query(qry, xc);
        if (nodes == null)
            System.out.println("no result found for qry: " + qry + ".");
        String resId = (nodes.size() == 0) ? "" : nodes.get(0).getValue();
        System.out.println("resId is " + resId);
        return resId;
    }

    private static Document getXmlDocument(byte[] data) throws ValidityException, ParsingException, IOException {
        Builder builder = new Builder();
        return builder.build(new ByteArrayInputStream(data));
    }

    private static void includePagesInFileSec(Mets mets, String[] pages, FileSec sec) {
        FileSec newFileSec = new FileSec();
        newFileSec.setSchema("mets", METS);
        mets.getContent().add(newFileSec);
        List<MetsElement> elems = sec.getContent();
        for (MetsElement elem : elems) {
            if (elem instanceof FileGrp) {
                FileGrp newFileGrp = new FileGrp();
                boolean addFileGrp = true;
                List<MetsElement> files = elem.getContent();
                for (MetsElement file : files) {
                    if (file instanceof File) {
                        String fileId = ((File) file).getID();
                        System.out.println("fileId is " + fileId);
                        for (String page : pages) {
                            if (stripExtension(fileId).equals(page)) {
                                newFileGrp.getContent().add(file);
                                if (addFileGrp) {
                                    newFileSec.getContent().add(newFileGrp);
                                    addFileGrp = false;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void includePagesInStructMap(Mets mets, String[] pages, StructMap sec) throws IOException {
        StructMap newStruct = new StructMap();
        newStruct.setID("structmap1");
        newStruct.setTYPE("physical");
        mets.getContent().add(newStruct);
        List<MetsElement> elems = sec.getContent();
        for (MetsElement elem : elems) {
            if (elem instanceof Div) {
                if (((Div) elem).getTYPE().equals("issue")) {
                    Div newIssue = new Div();
                    newIssue.setTYPE("issue");
                    newIssue.setDMDID((String) ((Div) elem).getDMDID().keySet().iterator().next());
                    boolean addNewIssue = true;

                    List<MetsElement> divPages = elem.getContent();
                    for (MetsElement divPage : divPages) {
                        if (((Div) divPage).getTYPE().equals("page")) {
                            List<MetsElement> fptrs = divPage.getContent();
                            if (fptrs.get(0) instanceof Fptr) {
                                Object fileId = ((Fptr) fptrs.get(0)).getFILEID().keySet().iterator().next();
                                // System.out.println("fptr file id is " +
                                // fileId);
                                for (String page : pages) {
                                    if (stripExtension((String) fileId).equals(page)) {
                                        newIssue.getContent().add(divPage);
                                        if (addNewIssue) {
                                            newStruct.getContent().add(newIssue);
                                            addNewIssue = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void includeArticlesInStructMap(Mets mets, String[] articles, String[] pages, StructMap sec)
            throws MetsException {
        StructMap newStruct = new StructMap();
        newStruct.setID("structmap2");
        newStruct.setTYPE("logical");
        mets.getContent().add(newStruct);
        List<MetsElement> elems = sec.getContent();
        for (MetsElement elem : elems) {
            if (elem instanceof Div) {
                if (((Div) elem).getTYPE().equals("issue")) {
                    Div newIssue = new Div();
                    newIssue.setTYPE("issue");
                    newIssue.setDMDID((String) ((Div) elem).getDMDID().keySet().iterator().next());
                    boolean addIssue = true;

                    List<MetsElement> divArticles = elem.getContent();
                    for (MetsElement divArticle : divArticles) {

                        if (((Div) divArticle).getTYPE().equals("article")) {
                            String dmdId = (String) ((Div) divArticle).getDMDID().keySet().iterator().next();
                            if (selectedArticle(dmdId, articles)) {
                                Div newArticle = new Div();
                                newArticle.setTYPE("article");
                                newArticle.setID(((Div) divArticle).getID());
                                newArticle.setDMDID((String) ((Div) divArticle).getDMDID().keySet().iterator().next());
                                boolean addArticle = true;

                                List<MetsElement> articleParts = divArticle.getContent();
                                for (MetsElement articlePart : articleParts) {
                                    if (((Div) articlePart).getTYPE().equals("article-part")) {
                                        Div newArticlePart = new Div();
                                        newArticlePart.setTYPE("article-part");
                                        newArticlePart.setID(((Div) articlePart).getID());
                                        newArticlePart.setORDER(((Div) articlePart).getORDER());
                                        boolean addArticlePart = true;
                                        List<MetsElement> articleElems = articlePart.getContent();
                                        for (MetsElement articleElem : articleElems) {
                                            boolean include = false;
                                            if (articleElem instanceof Div) {
                                                // an article zone
                                                include = processArticleZone(newArticlePart, (Div) articleElem, pages);
                                            } else if (articleElem instanceof Fptr) {
                                                include = processFptr(newArticlePart, (Fptr) articleElem, pages);
                                            }
                                            if (include) {
                                                System.out.println("included article " + ((Div) divArticle).getID());
                                                if (addArticlePart) {
                                                    newArticle.getContent().add(newArticlePart);
                                                    addArticlePart = false;
                                                }
                                                if (addArticle) {
                                                    newIssue.getContent().add(newArticle);
                                                    addArticle = false;
                                                }
                                                if (addIssue) {
                                                    newStruct.getContent().add(newIssue);
                                                    addIssue = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private static boolean selectedArticle(String dmdId, String[] articles) {
        System.out.println("article dmdId is " + dmdId);
        for (String article : articles) {
            if (dmdId.equals(article))
                return true;
        }
        return false;
    }

    private static boolean processFptr(Div newArticleDiv, Fptr articleElem, String[] pages) {
        boolean include = false;
        Fptr newFptr = new Fptr();
        boolean addFptr = true;
        List<MetsElement> elems = articleElem.getContent();
        for (MetsElement elem : elems) {
            if (elem instanceof Area) {
                String fileId = (String) ((Area) elem).getFILEID().keySet().iterator().next();
                for (String page : pages) {
                    if (stripExtension(fileId).equals(page)) {
                        Area newArea = new Area();
                        newArea.setFILEID(fileId);
                        if (((Area) elem).getSHAPE() != null)
                            newArea.setSHAPE(((Area) elem).getSHAPE());
                        if (((Area) elem).getCOORDS() != null)
                            newArea.setCOORDS(((Area) elem).getCOORDS());
                        if (((Area) elem).getBETYPE() != null)
                            newArea.setBETYPE(((Area) elem).getBETYPE());
                        if (((Area) elem).getBEGIN() != null)
                            newArea.setBEGIN(((Area) elem).getBEGIN());
                        newFptr.getContent().add(newArea);
                        if (addFptr) {
                            newArticleDiv.getContent().add(newFptr);
                            addFptr = false;
                            include = true;
                        }
                    }
                }
            }
        }
        return include;
    }

    private static boolean processArticleZone(Div newArticleDiv, Div articleElem, String[] pages) {
        boolean include = false;
        Div newZone = new Div();
        newZone.setTYPE(((Div) articleElem).getTYPE());
        newZone.setID(((Div) articleElem).getID());
        boolean addZone = true;

        List<MetsElement> elems = articleElem.getContent();
        for (MetsElement elem : elems) {
            boolean alsoInclude = false;
            if (elem instanceof Fptr) {
                alsoInclude = processFptr(newZone, (Fptr) elem, pages);
            } else if (elem instanceof Div) {
                alsoInclude = processArticleZone(newZone, (Div) elem, pages);
            }
            if (alsoInclude) {
                if (addZone) {
                    newArticleDiv.getContent().add(newZone);
                    addZone = false;
                    include = true;
                }
            }
        }
        return include;
    }

    public static Mets parseMETS(String fileName) throws MetsException, IOException {
        FileInputStream in = new FileInputStream(fileName);
        Mets mets = Mets.reader(new MetsReader(in));
        in.close();

        mets.validate(new MetsValidator());
        return mets;
    }

    public static Mets modMETS(Mets mets, String[] articles, String[] pages) throws ValidityException,
            ParsingException, IOException, MetsException {
        Mets newMets = new Mets();
        // MetsWriter mw = new MetsWriter(System.out);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MetsWriter mw = new MetsWriter(bos);

        List<MetsElement> elems = mets.getContent();
        for (MetsElement elem : elems) {
            if (elem instanceof MetsHdr) {
                newMets.getContent().add(elem);
            } else if (elem instanceof DmdSec) {
                includeArticlesInDmdSec(newMets, articles, (DmdSec) elem);
            } else if (elem instanceof AmdSec) {
                includePagesInAmdSec(newMets, pages, (AmdSec) elem, mw, bos);
            } else if (elem instanceof FileSec) {
                includePagesInFileSec(newMets, pages, (FileSec) elem);
            } else if (elem instanceof StructMap) {
                if (((StructMap) elem).getID().equals("structmap1")) {
                    includePagesInStructMap(newMets, pages, (StructMap) elem);
                } else {
                    includeArticlesInStructMap(newMets, articles, pages, (StructMap) elem);
                }
            }
        }
        return newMets;
    }

    public static void writeMets(Mets mets, String metsFilePath, String xmlHdrPath) throws MetsException, IOException {
        Files.deleteIfExists(Paths.get(metsFilePath));
        Files.createFile(Paths.get(metsFilePath));
        OutputStream os = new FileOutputStream(metsFilePath);
        MetsWriter mw = new MetsWriter(os);
        mets.write(mw);
        mw.flush();
        os.flush();
        os.close();

        // reformat the metsFile written so it can be parsed by the
        // IngestMetsHelper
        List<String> xmlHdr = Files.readAllLines(Paths.get(xmlHdrPath), Charset.defaultCharset());
        List<String> lines = Files.readAllLines(Paths.get(metsFilePath), Charset.defaultCharset());
        for (int i = 0; i < 2; i++) {
            lines.remove(0);
        }
        for (int i = 4; i > -1; i--) {
            lines.add(0, xmlHdr.get(i));
        }

        Files.deleteIfExists(Paths.get(metsFilePath));
        Files.createFile(Paths.get(metsFilePath));
        Files.write(Paths.get(metsFilePath), lines, Charset.defaultCharset());
    }

    public static void main(String[] args) throws Exception {
        try {
            // note: modsarticle4 does not exist in any of these images
            // todo: test remove modsarticle4 from the articles list
            //       to see if the newly created METS will then be consistent.
            String[] articles = { "issue-nla.aus-issn00050458_19500225", "modsarticle1", "modsarticle2", "modsarticle3",
                    "modsarticle4" };
            String[] pages = { "nlaImageSeq-4310810-c", "nlaImageSeq-4310811-c", "nlaImageSeq-4310812-c",
                    "nlaImageSeq-4310813-c" };
            Mets mets = parseMETS("src/main/resources/mets-issue-nla.aus-issn00050458_19500225.xml");
            // mets.write (new MetsWriter (System.out));
            Mets newMets = modMETS(mets, articles, pages);
            writeMets(newMets, "/tmp/newMets.xml", "src/main/resources/mets_xml_ns_hdr.txt");
            ObjectNode node = IngestMetsHelper.parsMetadata(Paths.get("/tmp/newMets.xml"),
                    Paths.get("src/main/resources/alto"));
            
            // TODO: quick pi / uuid workaround for this sprint
            // String uuid = java.util.UUID.randomUUID().toString();
            // System.out.println("uuid: " + uuid + " is of length " + uuid.length());
        } catch (MetsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidityException e) {
            e.printStackTrace();
        } catch (ParsingException e) {
            e.printStackTrace();
        }
    }
}
