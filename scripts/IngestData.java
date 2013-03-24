package models;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngestData implements Iterator<IngestData.IngestEntry> {
	public class IngestEntry {
		public String acFileName;
		public String ocFileName;
		public String atFileName;
		
		public String getFilePath(String baseDir, String copyRole) {
			if ((copyRole == null) || (copyRole.isEmpty())) return "";
			String path = baseDir;
			if (copyRole.equalsIgnoreCase("ac"))
				return baseDir + "images/" + acFileName;
			if (copyRole.equalsIgnoreCase("at"))
				return baseDir + "alto/" + atFileName;
			if (copyRole.equalsIgnoreCase("oc"))
				return baseDir + "json/" + atFileName;
			return "";
		}
		
		public int width() {
			// TODO: current default to this, change later.
			//       to lookup in METS file later maybe.
			return 2265;
		}
		
		public int height() {
			// TODO: current default to this, change later.
			//       to lookup in METS file later maybe.
			return 512;
		}
		
		public int order() {
			// TODO: current default to the number in acFileName,
			//       to lookup in METS file later maybe.
			Pattern pattern = Pattern.compile("\\s*(\\d+)\\.jp2");
			Matcher matcher = pattern.matcher(acFileName);
			return ((matcher.find())? new Integer(matcher.group(1)) : -1);
		}
	}
	
	public String topPI = "";
	public String dlirStore = "";
	public String acSrcDir = "";
	public String ocSrcDir = "";
	public String atSrcDir = "";
	private Enumeration entryIterator;
	
	public IngestData(String dlirStoragePath, String topPI, String acSrcDir, String ocSrcDir, String atSrcDir) {
		this .topPI = topPI;
		this.dlirStore = dlirStoragePath;
		this.acSrcDir = acSrcDir;
		this.ocSrcDir = ocSrcDir;
		this.atSrcDir = atSrcDir;
	}
	
	public void validateIngestData() throws Exception {
		// TODO: check ac and at directory has the same amount of files
		//       and each ac file has a corresponding at file 
		//       if not, throw Exception
		//
		//       Note: not to worry about the oc file at the moment
		//             as these will be generated after ac and at files
		//             are copied to dlir file storage.
		
		// generate ingest entry for each image file
		FileFilter jp2Filter = new FileFilter() {
			public boolean accept(File file) {
				String fileExt = ".jp2";
				if (file.getName().toLowerCase().endsWith(fileExt)) return true;
				return false;
			}
		};
		
		File imgDir = new File(acSrcDir);
		File[] files = imgDir.listFiles(jp2Filter);
		if ((files == null) || (files.length == 0)) return;
		List<IngestEntry> entries = new ArrayList<IngestEntry>();
		entryIterator = Collections.enumeration(entries);
		for (File file : files) {
			IngestEntry entry = new IngestEntry();
			entry.acFileName = file.getName();
			entry.atFileName = file.getName().replace(".jp2", ".xml");
			entry.ocFileName = file.getName().replace(".jp2", ".json.gz");
			entries.add(entry);
		}
	}

	@Override
	public boolean hasNext() {
		return (entryIterator == null)? false : entryIterator.hasMoreElements();
	}

	@Override
	public IngestEntry next() {
		return (IngestEntry) ((entryIterator == null)? null : entryIterator.nextElement());
	}

	@Override
	public void remove() {}
}
