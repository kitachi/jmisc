package models.ingest;

import java.util.HashMap;
import java.util.Map;

public class IngestArticle extends IngestEntry {
    protected Map<Integer, IngestEntry> articlePages = new HashMap<Integer, IngestEntry>();
}
