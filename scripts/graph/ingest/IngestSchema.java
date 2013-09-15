package graph.domain.ingest;

import graph.domain.Repository;
import graph.domain.classes.Article;
import graph.domain.classes.Copy;
import graph.domain.classes.File;
import graph.domain.classes.Page;
import graph.domain.classes.Work;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class IngestSchema {
    public IngestSchema existsOn(IngestedEntity article, IngestedEntity page, int order) {
        Article _article = Repository.getGraph().frame(article.asVertex(), Article.class);
        Page _page = Repository.getGraph().frame(page.asVertex(), Page.class);
        _article.addNewPage(_page).setRelOrder(order);
        article.addSubEntity(page, order);
        return this;
    }
    
    public IngestSchema isPartOf(IngestedEntity childWk, IngestedEntity parentWk, int order) {
        Work child = Repository.getGraph().frame(childWk.asVertex(), Work.class);
        Work parent = Repository.getGraph().frame(parentWk.asVertex(), Work.class);
        child.addNewParent(parent).setRelOrder(order);
        parentWk.addSubEntity(childWk, order);
        return this;
    }
    
    public IngestSchema isCopyOf(IngestedEntity copy, IngestedEntity work) throws IOException {
        Copy _copy = Repository.getGraph().frame(copy.asVertex(), Copy.class);
        Work _work = Repository.getGraph().frame(work.asVertex(), Work.class);
        _work.addNewCopy(_copy);
        copy.addToDescription("workPid", _work.getPi());
        work.addSubEntity(copy);
        return this;
    }
    
    public IngestSchema isFileOf(IngestedEntity file, IngestedEntity copy) {
        File _file = Repository.getGraph().frame(file.asVertex(), File.class);
        Copy _copy = Repository.getGraph().frame(copy.asVertex(), Copy.class);
        _copy.addNewFile(_file);
        copy.addSubEntity(file);
        return this;
    }
    
    /**
     * Ingest initial version of work
     * @return
     */
    public IngestedEntity ingestWork() {
        // TODO
        return null;
    }
    
    /**
     * Ingest a new version of work
     * @param pi
     * @return
     */
    public IngestedEntity ingestWork(String pi) {
        // TODO
        return null;
    }
    
    /**
     * Ingest initial version of copy
     * @param collectionArea
     * @param copyType
     * @param copyRole
     * @param carrier
     * @param versionNo
     * @param dateCreated
     * @param sourceCopy
     * @param accessConditions
     * @param expiryDate
     * @param processId
     * @return
     * @throws IOException
     */
    
    public IngestedEntity ingestCopy(String collectionArea,
                                   String copyType,
                                   String copyRole,
                                   String carrier,
                                   int versionNo,
                                   Date dateCreated,
                                   String sourceCopy,
                                   String accessConditions,
                                   String expiryDate,
                                   String processId) throws IOException {
        IngestedEntity copy = ((IngestedEntity) new IngestedEntity()
                .addField("collectionArea", collectionArea)
                .addField("tType", "copy").addField("subType", "copy"))
                .addToDescription("copyType", copyType)
                .addToDescription("copyRole", copyRole)
                .addToDescription("carrier", carrier)
                .addToDescription("currentVersion", "y")
                .addToDescription("versionNo", new Integer(versionNo).toString())
                .addToDescription("dateCreated", getTimestamp(dateCreated))
                .addToDescription("sourceCopy", sourceCopy)
                .addToDescription("accessConditions", accessConditions)
                .addToDescription("expiryDate", expiryDate)
                .addToDescription("processId", processId);
        ((IngestedEntity) copy.addField("pi", copy.asVertex().getId()))
                .addToDescription("copyPid", copy.asVertex().getId().toString());
        return copy;
    }
    
    /**
     * Ingest a new version of copy
     * @param collectionArea
     * @param copyType
     * @param copyRole
     * @param carrier
     * @param versionNo
     * @param dateCreated
     * @param sourceCopy
     * @param accessConditions
     * @param expiryDate
     * @param processId
     * @param pi
     * @return
     * @throws IOException
     */
    public IngestedEntity ingestCopy(String collectionArea,
            String copyType,
            String copyRole,
            String carrier,
            int versionNo,
            Date dateCreated,
            String sourceCopy,
            String accessConditions,
            String expiryDate,
            String processId,
            String pi) throws IOException {
// TODO
        return null;
}
    /**
     * Ingest initial version of file
     * @param collectionArea
     * @param fileName
     * @param fileSize
     * @param checkSum
     * @param fileLocation
     * @return
     * @throws IOException
     */
    public IngestedEntity ingestFile(String collectionArea,
                                   String fileName,
                                   String fileSize,
                                   String checkSum,
                                   String fileLocation) throws IOException {
        IngestedEntity file = ((IngestedEntity) new IngestedEntity()
                .addField("collectionArea", collectionArea)
                .addField("tType", "file").addField("subType", "file"))
                .addToDescription("fileName", fileName)
                .addToDescription("fileSize", fileSize)
                .addToDescription("checkSum", checkSum)
                .addToDescription("fileLocation", fileLocation);
        file.addField("pi", file.asVertex().getId());
        return file;
    }
    
    /**
     * Ingest a new version of file
     * @param collectionArea
     * @param fileName
     * @param fileSize
     * @param checkSum
     * @param fileLocation
     * @return
     * @throws IOException
     */
    public IngestedEntity ingestFile(String collectionArea,
            String fileName,
            String fileSize,
            String checkSum,
            String fileLocation,
            String pi) throws IOException {
        // TODO
        return null;
    }
    
    private String getTimestamp(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat();
        fmt.applyPattern("yyyyMMdd'T'HHmmss");
        return fmt.format(date);
    }
}
