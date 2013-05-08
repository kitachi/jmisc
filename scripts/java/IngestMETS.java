package models.ingest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import utils.Predicate;
import models.Thing;
import models.ingest.IngestEntry.ThingSubType;

public class IngestMETS extends IngestData {
    protected List<IngestEntry> entries;
    private Path metsPath;
    private String tiffSrcDir;
    private String metsJsonSrcDir;
    
    public IngestMETS(Path dlirStoragePath, String topPI, Path metsPath, String ts) {
        super(dlirStoragePath, topPI, ts);
        
        // todo validate metsPath is not null or empty using GUAVA, otherwise throw exception
        this.metsPath = metsPath;
    }

    public List<IngestEntry> validateIngestData(IngestEntry.ThingSubType topEntryType, ObjectNode metsRoot) throws IOException,
    NoSuchAlgorithmException {
        File mets = metsPath.toFile();
        if (!(mets.exists()))
            throw new FileNotFoundException(metsPath.toString() + " does not exist.");
       
        initSrcDirs(mets);
        
        entries = new ArrayList<IngestEntry>();
        
        // add mets file as an ingest entry
        IngestEntry metsEntry = new IngestEntry();
        metsEntry.ts = super.ts();
        metsEntry.entryType = topEntryType;
        metsEntry.pi = topPI;
        IngestFile metsManifest = new IngestFile();
        metsManifest.fileName = mets.getName();
        metsManifest.fileSize = mets.length();
        // metsManifest.checkSum = super.checksum(mets);
        metsEntry.entryData.put(Thing.CopyRole.OCR_METS_COPY, metsManifest);
        
        metsEntry.parentEntry = null;
        entries.add(metsEntry);
        
        // TODO: dealing with adding edition, section, supplement etc as ingest entries
        //       by reading the METS file.
        
        List<IngestEntry> pageEntries = super.validateIngestData();
        entries.addAll(pageEntries);
        for (IngestEntry pageEntry : pageEntries) {
            pageEntry.parentEntry = metsEntry;
        }
       
        // TODO: to add each tiff as an ingest entry
        // return entries;
        return validateIngestData(entries, metsRoot);
    }
  
    protected String getWorkingArea(Thing.CopyRole copyRole) {   
        return srcDirs[copyRole.idx()];
    }
    
    private void initSrcDirs(File mets) {
		srcDirs[Thing.CopyRole.OCR_METS_COPY.idx()] = mets.getParent();
        srcDirs[Thing.CopyRole.ACCESS_COPY.idx()] = Paths.get(mets.getParent()).resolve("jp2").toString();
        srcDirs[Thing.CopyRole.MASTER_COPY.idx()] = Paths.get(mets.getParent()).resolve("tiff").toString();
        srcDirs[Thing.CopyRole.OCR_JSON_COPY.idx()] = Paths.get(mets.getParent()).resolve("oc").toString();
        srcDirs[Thing.CopyRole.OCR_ALTO_COPY.idx()] = Paths.get(mets.getParent()).resolve("alto").toString();
	}
    /*
    private void getMetsCopies(Map<JsonNode, JsonNode> nodes, JsonNode parent, Iterator<JsonNode> metsIT) {
    	while ((metsIT != null) && (metsIT.hasNext())) {
    		JsonNode node = metsIT.next();
    		Iterator<String> fields = node.getFieldNames();
    		
    		while ((fields!= null) && (fields.hasNext())) {
				String fieldName = fields.next();
				if (fieldName != null) {
					System.out.println("fieldname is " + fieldName);
					if (fieldName.trim().equals("filename")) {
						nodes.put(node, parent);
					}
					JsonNode children = (JsonNode) node.get(fieldName);
					if (children.isArray()) {
						getMetsCopies(nodes, node, children.getElements());
					}
				}
			}
			
			if (node.isArray()) {
				getMetsCopies(nodes, node, node.getElements());
			}
    	}   	
    }
    */
    
    // TODO: test this
    private void getItemsFromMets(IngestEntry.ThingSubType tag, Map<JsonNode, JsonNode> items, JsonNode parent, Iterator<JsonNode> candidates) {
        while ((candidates != null) && (candidates.hasNext())) {
            JsonNode node = candidates.next();
            Iterator<String> fields = node.getFieldNames();
            
            while ((fields!= null) && (fields.hasNext())) {
                String fieldName = fields.next();
                if (fieldName != null) {
                    JsonNode children = (JsonNode) node.get(fieldName);
                    if ((children != null) && (children.isArray())) {
                        if (fieldName.trim().equals(tag.plural())) {
                            Iterator<JsonNode> tagged = children.getElements();
                            while ((tagged != null) && (tagged.hasNext())) {
                                items.put(tagged.next(), parent);
                            }
                        } else {
                            getItemsFromMets(tag, items, node, children.getElements());
                        }
                    }
                }
            }
            
            if (node.isArray()) {
                getItemsFromMets(tag, items, node, node.getElements());
            }
        }
    }
    
    private List<IngestEntry> validateIngestData(List<IngestEntry> entries, ObjectNode metsRoot) throws NoSuchAlgorithmException, IOException {
    	if (entries == null) {
    		throw new InvalidParameterException("entries list is null.");
    	}
    	
    	// entryMap (filename, entry)
    	Map<String, IngestEntry> entryMap = new HashMap<String, IngestEntry>();

    	for (IngestEntry entry : entries) {
    		entryMap.put(entry.pi(), entry);
    		for (Thing.CopyRole copyRole : Thing.CopyRole.values()) {
    			entryMap.put(entry.getFileName(copyRole), entry);
    		}
    	}
    	
    	// parse metsRoot
    	Map<JsonNode, JsonNode> copies = new HashMap<JsonNode, JsonNode>();
    	Map<JsonNode, JsonNode> zones = new HashMap<JsonNode, JsonNode>();
    	Map<JsonNode, JsonNode> sections = new HashMap<JsonNode, JsonNode>();
    	Map<JsonNode, JsonNode> articles = new HashMap<JsonNode, JsonNode>();
    	getItemsFromMets(ThingSubType.COPY, copies, metsRoot, metsRoot.iterator());
    	getItemsFromMets(ThingSubType.REGION, zones, metsRoot, metsRoot.iterator());
    	getItemsFromMets(ThingSubType.SECTION, sections, metsRoot, metsRoot.iterator());
    	getItemsFromMets(ThingSubType.ARTICLE, sections, metsRoot, metsRoot.iterator());
    	
    	// map from copies
    	for (JsonNode copy : copies.keySet()) {
    		String fileName = copy.get("filename").getTextValue();
    		Thing.CopyRole copyRole = Thing.CopyRole.isCopyRole(copy.get("copyrole").getTextValue());
    		IngestEntry entry = entryMap.get(fileName);
    		IngestFile file = entry.entryData.get(copyRole);
    		
    		// map the entry order
    		JsonNode copyParent = copies.get(copy);
    		if (copyParent.get("order") != null)
    			entry.order = new Integer(copyParent.get("order").getTextValue());
    		
    		// map file checksum
    		JsonNode copyChecksumType = copy.get("checksumType");
    		JsonNode copyChecksum = copy.get("checksum");
    		
    		if (copyChecksum == null) {
    			file.checksumType = super.CHECKSUM_SHA1;
    			File copyFile = Paths.get(srcDirs[copyRole.idx()]).resolve(fileName).toFile();
    			file.checkSum = super.checksum(copyFile);
    		} else {
    			file.checksumType = (copyChecksumType == null)? file.checksumType : copyChecksumType.getTextValue();
    			file.checkSum = copyChecksum.getTextValue();
    		}
    		
    		// map image width, height, measurementunit and resolution
    		if ((copyRole == Thing.CopyRole.MASTER_COPY) && (fileName.endsWith(".tif"))) {
    			IngestImg tifFile = (IngestImg) file;
    			JsonNode copyWidth = copy.get("width");
    			JsonNode copyHeight = copy.get("height");
    			JsonNode copyMsrUnit = copy.get("measurementunit");
    			JsonNode copyXRes = copy.get("xresolution");
    			JsonNode copyYRes = copy.get("yresolution");
    			
    			tifFile.width = (copyWidth == null)? -1: new Integer(copyWidth.getTextValue());
    			tifFile.height = (copyHeight == null)? -1 : new Integer(copyHeight.getTextValue());
    			tifFile.measurementUnit = (copyMsrUnit == null)? "": copyMsrUnit.getTextValue();
    			tifFile.xresolution = (copyXRes == null)? "" : copyXRes.getTextValue();
    			tifFile.yresolution = (copyYRes == null)? "" : copyYRes.getTextValue();
    			
    			if (entry.entryData.get(Thing.CopyRole.ACCESS_COPY) != null) {
    				IngestImg acFile = (IngestImg) entry.entryData.get(Thing.CopyRole.ACCESS_COPY);  
    				acFile.width = (copyWidth == null)? -1: new Integer(copyWidth.getTextValue());
    				acFile.height = (copyHeight == null)? -1 : new Integer(copyHeight.getTextValue());
    			} 
    		}
    		
    		// TODO: check description for all these subtype include everything needed.
    		// TODO: map articles
    		// TODO: relate each article to pages
    		
            // TODO: map sections
    		
            // TODO: map zones/regions under page (??) or is this in the OCR JSON file only
    		
    		// TODO: map editions
    		
    		// TODO: map supplement
    	}    	
    	
    	return entries;
    }
}
