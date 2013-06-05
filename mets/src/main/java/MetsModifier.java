import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import edu.harvard.hul.ois.mets.AmdSec;
import edu.harvard.hul.ois.mets.DmdSec;
import edu.harvard.hul.ois.mets.DigiprovMD;
import edu.harvard.hul.ois.mets.FileSec;
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
    
    private static void includeArticles(Mets mets, String[] articles, DmdSec sec) {
        String dmdId = sec.getID();
        for (String article : articles) {
            if (article.equals(dmdId)) {
                mets.getContent().add(sec);
            }
        }
    }
    
    private static void includePages(Mets mets, String[] pages, MetsElement sec, MetsWriter mw) throws ValidityException, ParsingException, IOException, MetsException {
        // sec.write (new MetsWriter (System.out));
        List<MetsElement> elems = ((MetsElement) sec).getContent();
        for (MetsElement elem : elems) {
            // elem.write(mw);
            if (elem instanceof TechMD) {
                elem.write(mw);
                TechMD md = (TechMD) elem;
                if (md.getContent().get(0) instanceof MdWrap) {
                    //System.out.println("techMD contains MdWrap");
                    MdWrap mdWrap = (MdWrap) md.getContent().get(0);
                    if (mdWrap.getContent().get(0) instanceof XmlData) {
                        // System.out.println("MdWrap contains XmlData.");
                        XmlData mdXml = (XmlData) mdWrap.getContent().get(0);
                        Any mdAnyLvl1 = (Any) mdXml.getContent().get(0);
                        PCData mdAnyLvl2 = (PCData) mdAnyLvl1.getContent().get(0);
                        // mdAnyLvl2.write(new MetsWriter(System.out));    
                    // System.out.println("mdAny: " + mdAnyLvl2.write(new MetsWriter(System.out)));
                    }
                }

                // System.out.println("techMD element: " + new String(bos.toByteArray()));
                /*Document mdXml = getXmlDocument((XmlData) (((MdWrap) md.getContent().get(0)).getContent().get(0)));
                Nodes premisObjIdVals = mdXml.query("//premis:objectIdentifierValue"); 
                String premisObjId = premisObjIdVals.get(0).getValue();
                System.out.println("premisObjId is " + premisObjId); */
            } else if (elem instanceof DigiprovMD) {
                elem.write(mw);
                /*
                DigiprovMD md = (DigiprovMD) elem;
                XmlData mdXml = (XmlData) (((MdWrap) md.getContent().get(0)).getContent().get(0));
                List<MetsElement> els = mdXml.getContent();
                for (MetsElement el : els) {
                    System.out.println("digiprovMD element: " + el.getClass().getName());
                } */
                /*Document mdXml = getXmlDocument((XmlData) (((MdWrap) md.getContent().get(0)).getContent().get(0)));
                Nodes premisObjIdVals = mdXml.query("//premis:eventIdentifierValue"); 
                String premisObjId = premisObjIdVals.get(0).getValue();
                System.out.println("premisEventId is " + premisObjId); */
            }
        }
    }
    
    private static Document getXmlDocument(XmlData xmlData) throws ValidityException, ParsingException, IOException {
        Builder builder = new Builder();
        return builder.build(xmlData.toString());
    }

    private static void includePages(Mets mets, String[] pages, FileSec sec) {
        
    }
    
    private static void includeArticlesPages(Mets mets, String[] articles, String[] pages, StructMap sec) {
        
    }
    
    public static Mets parseMETS(String fileName) throws MetsException, IOException {
        FileInputStream in = new FileInputStream(fileName);
        Mets mets = Mets.reader(new MetsReader(in));
        in.close();
        
        mets.validate(new MetsValidator ());
        return mets;
    }
    
    public static Mets modMETS(Mets mets, String[] articles, String[] pages) throws ValidityException, ParsingException, IOException, MetsException {
        Mets newMets = new Mets();
        MetsWriter mw = new MetsWriter(System.out);
        
        List<MetsElement> elems = mets.getContent();
        for (MetsElement elem : elems) {
            if (elem instanceof MetsHdr) {
                newMets.getContent().add(elem);
            }/* else if (elem instanceof DmdSec) {
                includeArticles(newMets, articles, (DmdSec) elem);
            } */else if (elem instanceof AmdSec) {
               /* List<MetsElement> els = elem.getContent();
                for (MetsElement el : els) {
                    el.write(mw);
                } */
                includePages(newMets, pages, elem, mw);
            } else if (elem instanceof FileSec) {
                includePages(newMets, pages, (FileSec) elem);
            } else if (elem instanceof StructMap) {
                includeArticlesPages(newMets, articles, pages, (StructMap) elem);
            }
        }
        return newMets;
    }
    
    public static void writeMets(Mets mets) {
        
    }
    
    public static void main (String[] args) {
        try {
            String[] articles = { "issue-nla.aus-issn00050458_19500225", "modarticle1", "modarticle2", "modarticle3", "modarticle4" };
            String[] pages = { "nlaImageSeq-4310810-c", "nlaImageSeq-4310811-c", "nlaImageSeq-4310812-c", "nlaImageSeq-4310813-c" };
            Mets mets = parseMETS("src/main/resources/mets-issue-nla.aus-issn00050458_19500225.xml");
            // mets.write (new MetsWriter (System.out));
            /*
            List<MetsElement> elems = mets.getContent();
            for (MetsElement elem : elems) {
                elem.write (new MetsWriter (System.out));
                // System.out.println("elem class is " + elem.getClass().getName());
            }
            */
            Mets newMets = modMETS(mets, articles, pages);
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
