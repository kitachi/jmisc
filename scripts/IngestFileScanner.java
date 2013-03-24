package controllers.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class IngestFileScanner {
	private static final String separator = "# =======================================================================================================================";
	
	public static void scanTemplate(String[] args) {
		String template = "scripts/ingest_template.sql";
		try {
			Scanner sc = new Scanner(new File(template)).useDelimiter("\\s*" + separator + "\\s*");
			while (sc.hasNext()) {
				System.out.println(sc.next());
				System.out.println("##### The End #####");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean filterFileName(String fileName) {
		Pattern pattern = Pattern.compile("^\\d+\\.sql");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) return true;
		return false;
	}
	
	public static int filterFileNo(String fileName) {
		Pattern pattern = Pattern.compile("\\s*(\\d+)\\.jp2");
		Matcher matcher = pattern.matcher(fileName);
		return ((matcher.find())? new Integer(matcher.group(1)) : -1);
	}
	
	public static int sortIntList() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(9);
		list.add(2);
		list.add(3);

		
		for (int i : list) {
			System.out.println(i);
		}
		
		Collections.sort(list);
		
		for (int i : list) {
			System.out.println(i);
		}
		
		return (list.get(list.size() - 1));
	}
	
	public static void main(String[] args) {
		// scanTemplate(null);
		// System.out.println("1.sql: " + IngestFileScanner.filterFileName("1.sql"));
		// System.out.println("99.sql: " + IngestFileScanner.filterFileName("99.sql"));
		// System.out.println("aa9.sql: " + IngestFileScanner.filterFileName("aa9.sql"));
		// System.out.println("9aa.sql: " + IngestFileScanner.filterFileName("9aa.sql"));
		
		// System.out.println("the max number in the list is " + sortIntList());
		System.out.println(" the file number in nla.aaaa-99.jp2 is " + filterFileNo("nla.aaaa-99.jp2"));
	}
}
