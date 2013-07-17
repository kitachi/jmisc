package utils.ingest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.rules.TemporaryFolder;

import utils.Gzipper;
import models.Thing;
import models.ThingFile;
import models.ingest.FileLocation;
import models.ingest.Relationship;
import models.ingest.ThingCopy;

public class OCRItemMock {
    public enum OCRMarkup {
        PageContainArticle, PageWithoutArticle
    }
    
    public enum ArticleType {
        JournalArticle;     
        TestItem[] onPages;
        
        TestItem[] existOnPages() {
            return onPages;
        }
    };
    
    public enum TestItem {
        Page1 {

            @Override
            protected String getOCR(OCRMarkup markupLevel) {
                switch (markupLevel) {
                case PageContainArticle:
                    return "{\"top\":{},\"left\":{},\"right\":{},\"bottom\":{},\"print\":{\"zs\":[{\"id\":\"ART1\",\"b\":\"22,7,2242,3192\",\"zs\":[{\"aid\":99999,\"ao\":1,\"id\":\"ZONE124-1\",\"b\":\"22,7,2242,3192\"},{\"aid\":99999,\"ao\":2,\"id\":\"ZONE124-2\",\"b\":\"34,352,2119,3151\",\"ps\":[{\"id\":\"BLOCK1\",\"b\":\"78,1291,1969,1440\",\"ls\":[{\"id\":\"LINE1\",\"b\":\"78,1291,1969,1440\",\"ws\":[{\"id\":\"S1\",\"b\":\"78,1297,303,1408\",\"w\":\"The\"},{\"id\":\"S2\",\"b\":\"339,1295,643,1410\",\"w\":\"best\"},{\"id\":\"S3\",\"b\":\"669,1292,1030,1439\",\"w\":\"thing\"},{\"id\":\"S4\",\"b\":\"1062,1299,1212,1409\",\"w\":\"to\"},{\"id\":\"S5\",\"b\":\"1248,1291,1786,1440\",\"w\":\"happen\"},{\"id\":\"S6\",\"b\":\"1819,1298,1969,1409\",\"w\":\"to\"}]}]},{\"id\":\"BLOCK2\",\"b\":\"45,1444,1754,1593\",\"ls\":[{\"id\":\"LINE2\",\"b\":\"45,1444,1754,1593\",\"ws\":[{\"id\":\"S7\",\"b\":\"45,1446,889,1592\",\"w\":\"partying\"},{\"id\":\"S8\",\"b\":\"930,1447,1056,1559\",\"w\":\"in\"},{\"id\":\"S9\",\"b\":\"1092,1444,1264,1562\",\"w\":\"25\"},{\"id\":\"S10\",\"b\":\"1296,1445,1754,1593\",\"w\":\"years.'\"}]}]},{\"id\":\"BLOCK3\",\"b\":\"59,1626,597,1678\",\"ls\":[{\"id\":\"LINE3\",\"b\":\"59,1626,597,1678\",\"ws\":[{\"id\":\"S11\",\"b\":\"59,1626,91,1663\",\"w\":\"ob\"},{\"id\":\"S12\",\"b\":\"101,1626,303,1675\",\"w\":\"Armstrong,\"},{\"id\":\"S13\",\"b\":\"317,1629,397,1667\",\"w\":\"with\"},{\"id\":\"S14\",\"b\":\"411,1629,461,1667\",\"w\":\"25\"},{\"id\":\"S15\",\"b\":\"473,1632,597,1678\",\"w\":\"years'\"}]}]},{\"id\":\"BLOCK4\",\"b\":\"28,1677,692,1825\",\"ls\":[{\"id\":\"LINE4\",\"b\":\"30,1677,686,1731\",\"ws\":[{\"id\":\"S16\",\"b\":\"30,1677,216,1723\",\"w\":\"j/perience\"},{\"id\":\"S17\",\"b\":\"228,1687,271,1716\",\"w\":\"as\"},{\"id\":\"S18\",\"b\":\"284,1688,307,1716\",\"w\":\"a\"},{\"id\":\"S19\",\"b\":\"318,1680,517,1728\",\"w\":\"party\"},{\"id\":\"S20\",\"b\":\"529,1681,686,1730\",\"w\":\"goer,\"}]}]}]}]}]}}";
                case PageWithoutArticle:
                    return "{\"top\":{},\"left\":{},\"right\":{},\"bottom\":{},\"print\":{\"ps\":[{\"id\":\"P1_TB00001\",\"b\":\"316,372,1994,575\",\"ls\":[{\"id\":\"P1_TL00001\",\"b\":\"316,372,1994,575\",\"ws\":[{\"id\":\"P1_ST00001\",\"b\":\"316,373,1279,575\",\"w\":\"Blinky\"},{\"id\":\"P1_ST00002\",\"b\":\"1416,372,1994,572\",\"w\":\"Bill\"}]}]}]}}";
                default:
                    return null;
                }
            }

            @Override
            protected String expectedOCRContent(OCRMarkup markupLevel) {
                switch (markupLevel) {
                case PageContainArticle:
                    return "The best thing to happen to partying in 25 years.' ob Armstrong, with 25 years' j/perience as a party goer,";
                case PageWithoutArticle:
                    return "Blinky Bill";
                default:
                    return null;
                }
            }

            @Override
            protected String expectedOCRIdBoxes(OCRMarkup markupLevel) {
                switch (markupLevel) {
                case PageContainArticle:
                    return "S1:78,1297,303,1408 S2:339,1295,643,1410 S3:669,1292,1030,1439 S4:1062,1299,1212,1409 S5:1248,1291,1786,1440 S6:1819,1298,1969,1409 S7:45,1446,889,1592 S8:930,1447,1056,1559 S9:1092,1444,1264,1562 S10:1296,1445,1754,1593 S11:59,1626,91,1663 S12:101,1626,303,1675 S13:317,1629,397,1667 S14:411,1629,461,1667 S15:473,1632,597,1678 S16:30,1677,216,1723 S17:228,1687,271,1716 S18:284,1688,307,1716 S19:318,1680,517,1728 S20:529,1681,686,1730";
                case PageWithoutArticle:
                    return "P1_ST00001:316,373,1279,575 P1_ST00002:1416,372,1994,572";
                default:
                    return null;
                }
            }

            @Override
            public int itemOrder() {
                return 1;
            }
        },
        Page2 {

            @Override
            protected String getOCR(OCRMarkup markupLevel) {
                switch (markupLevel) {
                case PageContainArticle:
                    return "{\"top\":{},\"left\":{},\"right\":{},\"bottom\":{},\"print\":{\"zs\":[{\"id\":\"ART1\",\"b\":\"11,17,3329,4377\",\"zs\":[{\"aid\":99999,\"ao\":3,\"id\":\"ZONE1-1\",\"b\":\"20,17,3329,4377\"},{\"aid\":99999,\"ao\":4,\"id\":\"ZONE1-2\",\"b\":\"450,35,1490,175\"},{\"aid\":123,\"ao\":3,\"id\":\"ZONE1-3\",\"b\":\"485,175,2955,620\"},{\"aid\":99999,\"ao\":5,\"id\":\"ZONE1-4\",\"b\":\"1603,60,3089,140\",\"ps\":[{\"id\":\"BLOCK1\",\"b\":\"1726,84,2889,140\",\"ls\":[{\"id\":\"LINE1\",\"b\":\"1726,84,2889,140\",\"ws\":[{\"id\":\"S1\",\"b\":\"1726,97,1863,140\",\"w\":\"Ovpr\"},{\"id\":\"S2\",\"b\":\"1889,95,2005,140\",\"w\":\"700\"},{\"id\":\"S3\",\"b\":\"2030,92,2147,140\",\"w\":\"OOO\"},{\"id\":\"S4\",\"b\":\"2173,92,2368,140\",\"w\":\"CoDe\"},{\"id\":\"S5\",\"b\":\"2394,89,2515,140\",\"w\":\"Sold\"},{\"id\":\"S6\",\"b\":\"2547,87,2702,140\",\"w\":\"Every\"},{\"id\":\"S7\",\"b\":\"2718,84,2889,133\",\"w\":\"Week\"}]}]}]},{\"aid\":99999,\"ao\":6,\"id\":\"ZONE1-5\",\"b\":\"11,760,563,1297\"}]}]}}";
                case PageWithoutArticle:
                    return "{\"top\":{},\"left\":{\"ps\":[{\"id\":\"P92_TB00001\",\"b\":\"569,1092,572,1102\",\"ls\":[{\"id\":\"P92_TL00001\",\"b\":\"569,1092,572,1102\",\"ws\":[{\"id\":\"P92_ST00001\",\"b\":\"569,1092,572,1102\",\"w\":\"II\"}]}]}]},\"right\":{},\"bottom\":{\"ps\":[{\"id\":\"P92_TB00007\",\"b\":\"2285,2402,2287,2430\"}]},\"print\":{\"ps\":[{\"id\":\"P92_TB00009\",\"b\":\"1240,383,1985,659\",\"ls\":[{\"id\":\"P92_TL00009\",\"b\":\"1340,385,1982,431\",\"ws\":[{\"id\":\"P92_ST00010\",\"b\":\"1340,386,1470,423\",\"w\":\"Wait\"},{\"id\":\"P92_ST00011\",\"b\":\"1501,387,1555,422\",\"w\":\"till\"},{\"id\":\"P92_ST00012\",\"b\":\"1585,386,1601,422\",\"w\":\"I\"},{\"id\":\"P92_ST00013\",\"b\":\"1633,387,1722,423\",\"w\":\"hear\"},{\"id\":\"P92_ST00014\",\"b\":\"1754,386,1811,423\",\"w\":\"his\"},{\"id\":\"P92_ST00015\",\"b\":\"1843,385,1982,431\",\"w\":\"story,\"}]}]}]}}";
                default:
                    return null;
                }
            }

            @Override
            protected String expectedOCRContent(OCRMarkup markupLevel) {
                switch (markupLevel) {
                case PageContainArticle:
                    return "Ovpr 700 OOO CoDe Sold Every Week";
                case PageWithoutArticle:
                    return "Wait till I hear his story,";
                default:
                    return null;
                }
            }

            @Override
            protected String expectedOCRIdBoxes(OCRMarkup markupLevel) {
                switch (markupLevel) {
                case PageContainArticle:
                    return "S1:1726,97,1863,140 S2:1889,95,2005,140 S3:2030,92,2147,140 S4:2173,92,2368,140 S5:2394,89,2515,140 S6:2547,87,2702,140 S7:2718,84,2889,133";
                case PageWithoutArticle:
                    return "P92_ST00010:1340,386,1470,423 P92_ST00011:1501,387,1555,422 P92_ST00012:1585,386,1601,422 P92_ST00013:1633,387,1722,423 P92_ST00014:1754,386,1811,423 P92_ST00015:1843,385,1982,431";
                default:
                    return null;
                }
            }

            @Override
            public int itemOrder() {
                return 2;
            }
        },
        Article1 {            
            @Override
            protected String getOCR(OCRMarkup markupLevel) {
                // TODO: get the print out ocr data from OCRTest.testGetOCRForArticle.ocrData
                return "";
                
                // TODO: also in OCRTest:
                //   - no longer need spec to initialize OCRItemMock...  (ie PageMock chged to OCRItemMock, PageOCRContainer chged to OCRMarkup)
                //  - change dao.getPage(spec[0]) --> TestItem page = TestItem.page1; Thing pageRef = page.getMetaData();
                //  - change ArticleMock.getArticle(...) to:
                //         TestItem article = TestItem.Article1; Thing articleRef = article.getMetadata();
            }

            @Override
            protected String expectedOCRContent(OCRMarkup markupLevel) {
                // TODO
                return "";
            }

            @Override
            protected String expectedOCRIdBoxes(OCRMarkup markupLevel) {
                // TODO
                return "";
            }

            @Override
            public int itemOrder() {
                return 1;
            }
        };
        
        public static TestItem findBy(Thing itemRef) throws JsonProcessingException, IOException {
            String itemId = new ObjectMapper().readTree(itemRef.description).get("testItemName").getTextValue();
            return TestItem.valueOf(itemId);
        }
        
        Thing metadata;
        public Thing getMetadata() {
            return metadata;
        }
        protected void setMetadata(Thing metadata) {
            this.metadata = metadata; 
        }
        
        public abstract int itemOrder();
        protected abstract String getOCR(OCRMarkup markupLevel);
        protected abstract String expectedOCRContent(OCRMarkup markupLevel);
        protected abstract String expectedOCRIdBoxes(OCRMarkup markupLevel);
    }
    
    public File folder = new TemporaryFolder().newFolder("test_ocr_data");
    private OCRMarkup markupLevel;
    private ArticleType articleMarkup;
        
    public OCRItemMock(OCRMarkup markupLevel) throws IOException {
        this.markupLevel = markupLevel;
        TestItem[] pages = { TestItem.Page1, TestItem.Page2 };
        for (TestItem pageItem : pages) {
            pageItem.setMetadata(getPageRef(pageItem));
        }
        if (markupLevel == OCRMarkup.PageContainArticle) {
            this.articleMarkup = ArticleType.JournalArticle;
            this.articleMarkup.onPages = pages;
            TestItem.Article1.setMetadata(getArticleRef(TestItem.Article1));
        }
    }
    
    public String getItemOCR(TestItem item) {
        return item.getOCR(markupLevel);
    }
    public String expectedItemOCRContent(TestItem item) {
        return item.expectedOCRContent(markupLevel);
    }
    public String expectedItemOCRIdBoxes(TestItem item) {
        return item.expectedOCRIdBoxes(markupLevel);
    }
    
    public String readOCRData(Thing itemRef) throws JsonProcessingException, IOException {
        // return a page's ocr data.
         return getItemOCR(TestItem.findBy(itemRef));
    }
    
    private Thing getArticleRef(TestItem articleItem) throws IOException {
        List<Thing> pages = new ArrayList<Thing>();
        for (TestItem pageItem : articleMarkup.existOnPages()) {
            Thing page = pageItem.getMetadata();
            pages.add(page);
        }
        
        // create article
        Thing article = new Thing();
        article.id = 99999L;
        article.collectionArea = "nla.tst";
        article.tType = Thing.ThingType.WORK.name();
        article.subType = "article";
        article.pi = "nla.tst-issn0001-article" + articleItem.itemOrder();
        article.save();
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("title", "nla.tst_article_title" + article.id);
        node.put("creator", "nla.tst_article_creator" + article.id);
        node.put("issn", "nla.tst_article_issn" + article.id);
        node.put("date", "nla.tst_article_issue_date" + article.id);
        node.put("heading", "nla.tst_article_heading" + article.id);
        node.put("subHeadings", "nla.tst_article_sub_headings" + article.id);
        node.put("authors", "nla.tst_article_authors" + article.id);
        node.put("abstract", "nla.tst_article_abstract" + article.id);
        node.put("category", "nla.tst_article_category" + article.id);
        article.description = node.toString().getBytes();
        article.save();
        
        // link up existOn relationship
        for (int i=0; i < pages.size(); i++) {
            Relationship articleOnPage = new Relationship();
            articleOnPage.thing1Id = article.id;
            articleOnPage.thing2Id = pages.get(i).id;
            articleOnPage.relationship = Thing.ThingRelationship.EXISTSON.code();
            articleOnPage.relOrder = i;
            articleOnPage.save();
        }
        
        return article;
    }
    
    private Thing getPageRef(TestItem pageItem) throws IOException {
        Thing page = new Thing();
        page.collectionArea = "nla.tst";
        page.tType = Thing.ThingType.WORK.name();
        page.subType = "page";
        page.pi = java.util.UUID.randomUUID().toString();
        page.save();
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("title", "nla.tst_page_title" + page.id);
        node.put("creator", "nla.tst_page_creator" + page.id);
        node.put("issn", "nla.tst_page_issn" + page.id);
        node.put("date", "nla.tst_page_issue_date" + page.id);
        node.put("heading", "nla.tst_page_heading" + page.id);
        node.put("subHeadings", "nla.tst_page_sub_headings" + page.id);
        node.put("authors", "nla.tst_page_authors" + page.id);
        node.put("abstract", "nla.tst_page_abstract" + page.id);
        node.put("category", "nla.tst_page_category" + page.id);
        node.put("copyRole", "nla.tst_page_copy_role" + page.id);
        node.put("carrier", "nla.tst_page_carrier" + page.id);
        node.put("fileSize", "nla.tst_page_file_size" + page.id);
        node.put("testItemName", pageItem.name());
        page.description = node.toString().getBytes();
        page.save();
        
        Thing ocrCopy = new Thing();
        ocrCopy.collectionArea = "nla.tst";
        ocrCopy.tType = Thing.ThingType.COPY.name();
        ocrCopy.subType = Thing.ThingType.COPY.name();
        ocrCopy.save();
        
        ThingCopy _ocrCopy = new ThingCopy();
        _ocrCopy.id = ocrCopy.id;
        _ocrCopy.copyPid = page.pi + Thing.CopyRole.OCR_JSON_COPY.code() + "v1";
        _ocrCopy.workPid = page.pi;
        _ocrCopy.currentVersion = "y";
        _ocrCopy.versionNo = 1;
        _ocrCopy.copyType = "d";
        _ocrCopy.copyRole = Thing.CopyRole.OCR_JSON_COPY.code();
        _ocrCopy.carrier = "Online";
        _ocrCopy.dateCreated = null;
        _ocrCopy.sourceCopy = page.pi + "mv1";
        _ocrCopy.accessConditions = "Unrestricted";
        _ocrCopy.expiryDate = null;
        _ocrCopy.processId = null;
        _ocrCopy.save();
        
        Thing ocrFile = new Thing();
        ocrFile.collectionArea = "nla.tst";
        ocrFile.tType = Thing.ThingType.FILE.name();
        ocrFile.subType = Thing.ThingType.FILE.name();
        ocrFile.save();
        File ocrDoc = folder.toPath().resolve(pageItem.name() + ".json").toFile();
        File ocrDocGzip = folder.toPath().resolve(pageItem.name() + ".json.gz").toFile();
        String data = getItemOCR(pageItem);
        ocrDoc.delete();
        ocrDocGzip.delete();
        FileUtils.writeByteArrayToFile(ocrDoc, data.getBytes("UTF-8"));
        Gzipper.gzip(ocrDoc, ocrDocGzip);
        
        ThingFile _file = new ThingFile();
        _file.id = ocrFile.id;
        _file.fileName = ocrDocGzip.getName();
        _file.fileSize = (long) data.length();
        _file.save();
        
        FileLocation location = new FileLocation();
        location.id = _file.id;
        location.fileLocation = ocrDocGzip.getAbsolutePath();
        location.save();
        
        // link ocrCopy to page
        Relationship pageCopy = new Relationship();
        pageCopy.thing1Id = ocrCopy.id;
        pageCopy.thing2Id = page.id;
        pageCopy.relationship = Thing.ThingRelationship.ISCOPYOF.code();
        pageCopy.relOrder = 1;
        pageCopy.save();
        
        // link ocrFile to ocrCopy
        Relationship pageFile = new Relationship();
        pageFile.thing1Id = ocrFile.id;
        pageFile.thing2Id = ocrCopy.id;
        pageFile.relationship = Thing.ThingRelationship.ISFILEOF.code();
        pageFile.relOrder = 1;
        pageFile.save();
        
        pageItem.setMetadata(page);
        return page;
    }
    
    protected void finalize() throws Throwable {
        if (folder.exists()) folder.delete();            
    }
}
