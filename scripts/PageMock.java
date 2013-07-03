package utils.ingest;

import java.util.ArrayList;
import java.util.List;

public class DlirSolrPageIndexMock extends DlirSolrIndex {
    public enum PageOCRContainer {
        PageContainArticle, PageWithoutArticle
    }
    
    public enum TestItem {
        Page1, Page2
    }

    private PageOCRContainer container;
    private TestItem[] pages;
    private String[] ocrOfPages = {"", ""};
    private String[] ocrOfPagesWithArticles = {"", ""};
    private String[] ocrTokensForVerify = {"", ""};
    
    public DlirSolrPageIndexMock(PageOCRContainer container, TestItem[] items) {
        this.container = container;
        pages = items;
    }
    
    @Override
    public String readDataFromUrl(String urlStr) throws Exception {
        // return a page's ocr data.
        return (this.container == PageOCRContainer.PageContainArticle)? construct(ocrOfPagesWithArticles) : construct(ocrOfPages);
    }
    
    public List<String> verifyTokens() throws RuntimeException {
        if (pages == null) 
            throw new IllegalArgumentException("");
        
        List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < pages.length; i++) {
            tokens.add(ocrTokensForVerify[i]);
        }
        return tokens;
    }
    
    private String construct(String[] ocrItems) throws Exception {
        if (ocrItems == null) return "";
        String ocrData = "";
        for (String item : ocrItems) {
            ocrData += item;
        }
        return ocrData;
    }
    
}
