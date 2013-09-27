package graph.domain.ingest;

import graph.domain.classes.Copy;
import graph.domain.classes.File;
import graph.domain.classes.Work;

public abstract class ExportSchema {
    public abstract IngestedEntity exportWork(Work work);
    public abstract IngestedEntity exportCopy(Copy copy);
    public abstract IngestedEntity exportFile(File file);
}
