import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;



public class IngestMetsHelper {

	private static final String MODS = "http://www.loc.gov/mods/v3";
	private static final String METS = "http://www.loc.gov/METS/";
	private static final String XLINK = "http://www.w3.org/1999/xlink";
	private static final String ALTO = "http://schema.ccs-gmbh.com/ALTO";;
	private static final int TIFF_XRESOLUTION_TAG = 282;
	private static final int TIFF_YRESOLUTION_TAG = 283;
	private static final int TIFF_HEIGHT_TAG = 257;
	private static final int TIFF_WIDTH_TAG = 256;

	public static Document getMetsDocument(File file) throws ValidityException,
			ParsingException, IOException {
		Builder builder = new Builder();
		FileInputStream metsIns = new FileInputStream(file);
		Document metsDoc = builder.build(metsIns);
		return metsDoc;
	}

	/**
	 * Parses mets alto file into JSON structure as an input for ingest
	 * 
	 * @param metsLocation
	 * @param altoFolderLocation
	 * @return
	 * @throws Exception
	 */
	public static ObjectNode parsMetadata(Path metsLocation,
			Path altoFolderLocation) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode resultNode = mapper.createObjectNode();

		Document metsDoc = getMetsDocument(metsLocation.toFile());
		XPathContext xc = getMetsXPathContext();

		String title = getTitle(metsDoc, xc);
		resultNode.put("title", title);

		String type = getGenre(metsDoc, xc);
		resultNode.put("type", type);

		String identifier = getIdentifier(metsDoc, xc);

		String volume = getVolume(metsDoc, xc);

		String issue = getIssue(metsDoc, xc);

		includeDescription(mapper, resultNode, identifier, volume, issue);

		includeMetsCopy(metsLocation, mapper, resultNode);

		getChildren(altoFolderLocation, mapper, resultNode, metsDoc, xc);

		return resultNode;

	}

	/**
	 * Includes mets as a copy on the root level of the document
	 * 
	 * @param metsLocation
	 * @param mapper
	 * @param resultNode
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private static void includeMetsCopy(Path metsLocation, ObjectMapper mapper,
			ObjectNode resultNode) throws NoSuchAlgorithmException, IOException {
		ArrayNode copies = mapper.createArrayNode();
		ObjectNode metsCopy = mapper.createObjectNode();
		String fileName = metsLocation.getFileName().toString();
		String parentFolder = metsLocation.getParent().getFileName().toString();
		String location = parentFolder + "/" + fileName;
		metsCopy.put("filename", fileName);
		metsCopy.put("location", location);
		metsCopy.put("copyrole", "m");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String sourceHash = "adfasdfbasdvsdfasf";
		metsCopy.put("checksumType", "MD5");
		metsCopy.put("checksum", sourceHash);
		copies.add(metsCopy);
		resultNode.put("copies", copies);
	}

	/**
	 * Includes description on the root level of the document
	 * 
	 * @param mapper
	 * @param resultNode
	 * @param identifier
	 * @param volume
	 * @param issue
	 */
	private static void includeDescription(ObjectMapper mapper,
			ObjectNode resultNode, String identifier, String volume,
			String issue) {
		ObjectNode description = mapper.createObjectNode();
		description.put("identifier", identifier);
		description.put("volume", volume);
		description.put("issue", issue);
		resultNode.put("description", description);
	}

	/**
	 * Uses namespaces from mets document to create XPathContext
	 * 
	 * @return
	 */
	public static XPathContext getMetsXPathContext() {
		XPathContext xc = new XPathContext();
		xc.addNamespace("mets", METS);
		xc.addNamespace("mods", MODS);
		xc.addNamespace("xlink", XLINK);
		xc.addNamespace("alto", ALTO);
		return xc;
	}

	/**
	 * Retieves article information from mets logical struct map
	 * 
	 * @param mapper
	 * @param metsDoc
	 * @param resultNode
	 * @param xc
	 */
	private static void getArticles(ObjectMapper mapper, Document metsDoc,
			ObjectNode resultNode, XPathContext xc) {

		Nodes logicalStructMapNodes = metsDoc
				.query("//mets:structMap[@TYPE='logical']//mets:div[@TYPE='article']",
						xc);

		if (logicalStructMapNodes != null && logicalStructMapNodes.size() > 0) {
			ArrayNode children = mapper.createArrayNode();
			resultNode.put("articles", children);
			for (int i = 0; i < logicalStructMapNodes.size(); i++) {
				Node node = logicalStructMapNodes.get(i);
				Attribute articleIdAttr = ((Element) node).getAttribute("ID");

				String articleId = articleIdAttr.getValue();

				ObjectNode articleNode = mapper.createObjectNode();
				children.add(articleNode);
				articleNode.put("id", articleId);

				articleNode.put("subtype", "article");
				Attribute articleDmdidAttr = ((Element) node)
						.getAttribute("DMDID");
				String atricleDmdid = articleDmdidAttr.getValue();

				includeTitle(metsDoc, xc, articleNode, atricleDmdid);

				includeNamePartAndRole(metsDoc, xc, articleNode, atricleDmdid);

				includeCategory(metsDoc, xc, articleNode, atricleDmdid);

				// get pages that article exists on
				Nodes articlePartNodes = getArticlePartNodes(metsDoc, xc,
						articleId);
				if (articlePartNodes != null && articlePartNodes.size() > 0) {
					ArrayNode existsOnArayNode = mapper.createArrayNode();
					articleNode.put("existson", existsOnArayNode);

					for (int j = 0; j < articlePartNodes.size(); j++) {

						ObjectNode pageNode = mapper.createObjectNode();
						Node articlePartNode = articlePartNodes.get(j);
						Attribute orderAttr = ((Element) articlePartNode)
								.getAttribute("ORDER");
						String order = orderAttr.getValue();
						Attribute idAttr = ((Element) articlePartNode)
								.getAttribute("ID");
						String articlePartId = idAttr.getValue();
						// Search for the page id
						Nodes pageIdNodes = getPageIdNodes(metsDoc, xc,
								articleId, articlePartId);

						includePageId(pageNode, pageIdNodes);
						pageNode.put("order", order);
						existsOnArayNode.add(pageNode);

					}
				}

			}
		}
	}

	/**
	 * Include pageId in the article
	 * 
	 * @param pageNode
	 * @param pageIdNodes
	 */
	private static void includePageId(ObjectNode pageNode, Nodes pageIdNodes) {
		if (pageIdNodes != null && pageIdNodes.size() > 0) {
			Node pageIdNode = pageIdNodes.get(0);
			Attribute pageIdAttr = ((Element) pageIdNode)
					.getAttribute("FILEID");
			String pageId = pageIdAttr.getValue();
			int lastSep = pageId.lastIndexOf(".");
			pageId = pageId.substring(0, lastSep);
			pageNode.put("page", pageId);
		}
	}

	/**
	 * Gets page nodes
	 * 
	 * @param metsDoc
	 * @param xc
	 * @param articleId
	 * @param articlePartId
	 * @return
	 */
	private static Nodes getPageIdNodes(Document metsDoc, XPathContext xc,
			String articleId, String articlePartId) {
		Nodes pageIdNodes = metsDoc.query(
				"//mets:structMap[@TYPE='logical']//mets:div[@ID='" + articleId
						+ "']//mets:div[@ID='" + articlePartId
						+ "']//mets:area", xc);
		return pageIdNodes;
	}

	/**
	 * @param metsDoc
	 * @param xc
	 * @param articleId
	 * @return
	 */
	private static Nodes getArticlePartNodes(Document metsDoc, XPathContext xc,
			String articleId) {
		Nodes articlePartNodes = metsDoc.query(
				"//mets:structMap[@TYPE='logical']//mets:div[@ID='" + articleId
						+ "']//mets:div[@TYPE='article-part']", xc);
		return articlePartNodes;
	}

	/**
	 * Includes the title of the article on the article node
	 * 
	 * @param metsDoc
	 * @param xc
	 * @param articleNode
	 * @param atricleDmdid
	 */
	private static void includeTitle(Document metsDoc, XPathContext xc,
			ObjectNode articleNode, String atricleDmdid) {
		Nodes titleNodes = metsDoc.query("//mets:dmdSec[@ID='" + atricleDmdid
				+ "']//mods:title", xc);
		if (titleNodes != null && titleNodes.size() > 0) {
			Node titleNode = titleNodes.get(0);
			String articleTitle = titleNode.getValue();
			articleNode.put("title", articleTitle);

		}
	}

	/**
	 * Includes NamePart and Role on the article node
	 * 
	 * @param metsDoc
	 * @param xc
	 * @param articleNode
	 * @param atricleDmdid
	 */
	private static void includeNamePartAndRole(Document metsDoc,
			XPathContext xc, ObjectNode articleNode, String atricleDmdid) {
		Nodes namePartNodes = metsDoc.query("//mets:dmdSec[@ID='"
				+ atricleDmdid
				+ "']//mods:name[@type='personal']/mods:namePart", xc);
		String namePart = null;
		if (namePartNodes != null && namePartNodes.size() > 0) {
			Node namePartNode = namePartNodes.get(0);
			namePart = namePartNode.getValue();

		}
		// get role
		String role = null;
		Nodes roleNodes = metsDoc.query("//mets:dmdSec[@ID='" + atricleDmdid
				+ "']//mods:role/mods:roleTerm[@type='text']", xc);
		if (roleNodes != null && roleNodes.size() > 0) {
			Node roleNode = roleNodes.get(0);
			role = roleNode.getValue();

		}
		if (role != null && namePart != null && role.equals("creator")) {
			articleNode.put("creator", namePart);
		}
	}

	/**
	 * Includes category on the article node
	 * 
	 * @param metsDoc
	 * @param xc
	 * @param articleNode
	 * @param atricleDmdid
	 */
	private static void includeCategory(Document metsDoc, XPathContext xc,
			ObjectNode articleNode, String atricleDmdid) {
		Nodes categoryNodes = metsDoc.query("//mets:dmdSec[@ID='"
				+ atricleDmdid + "']//mods:genre[@type='articleCategory']", xc);
		if (categoryNodes != null && categoryNodes.size() > 0) {
			Node categoryNode = categoryNodes.get(0);
			String category = categoryNode.getValue();
			articleNode.put("category", category);

		}
	}

	/**
	 * Gets pages, sections, supplements and articles
	 * 
	 * @param altoFolderLocation
	 * @param builder
	 * @param mapper
	 * @param resultNode
	 * @param metsDoc
	 * @throws Exception
	 */
	private static void getChildren(Path altoFolderLocation,
			ObjectMapper mapper, ObjectNode resultNode, Document metsDoc,
			XPathContext xc) throws Exception {

		Nodes physicalStructMap = metsDoc.query(
				"//mets:structMap[@TYPE='physical']", xc);
		Node n1 = physicalStructMap.get(0);
		Elements els = ((Element) n1).getChildElements();
		Element divIssue = els.get(0);
		Attribute dmdType = divIssue.getAttribute("DMDID");
		String dmdId = dmdType.getValue();
		resultNode.put("dmdid", dmdId);

		Nodes files = divIssue.query("//mets:div[@TYPE='page']", xc);

		// get pages
		getPages(altoFolderLocation, mapper, metsDoc, files, resultNode, xc);
		// include sections
		getSections(mapper, metsDoc, resultNode, xc);
		// include supplements
		getSupplements(mapper, metsDoc, resultNode, xc);

		getArticles(mapper, metsDoc, resultNode, xc);

	}

	/**
	 * Gets Pages
	 * 
	 * @param altoFolderLocation
	 * @param mapper
	 * @param metsDoc
	 * @param files
	 * @param resultNode
	 * @param xc
	 * @throws ParsingException
	 * @throws ValidityException
	 * @throws IOException
	 * @throws Exception
	 */
	private static void getPages(Path altoFolderLocation, ObjectMapper mapper,
			Document metsDoc, Nodes files, ObjectNode resultNode,
			XPathContext xc) throws ParsingException, ValidityException,
			IOException, Exception {

		ArrayNode children = mapper.createArrayNode();
		resultNode.put("pages", children);
		for (int i = 0; i < files.size(); i++) {

			ObjectNode childNode = mapper.createObjectNode();
			Node currNode = files.get(i);
			String physType = ((Element) currNode).getAttributeValue("TYPE");
			childNode.put("subType", physType);
			String divPageOrder = ((Element) currNode).getAttributeValue("ID");
			String order = ((Element) currNode).getAttributeValue("ORDER");
			// validate this
			if (divPageOrder != null) {
				String[] orderDtls = divPageOrder.split("divpage");
				if (orderDtls.length > 0) {
					childNode.put("order", orderDtls[1]);
				} else {
					childNode.put("order", order);
				}
			}

			Nodes fls = currNode.query("mets:fptr", xc);
			// create an array for copies
			ArrayNode copies = null;
			if (fls.size() > 0) {
				copies = mapper.createArrayNode();
			}
			childNode.put("copies", copies);
			// get copies

			for (int y = 0; y < fls.size(); y++) {
				ObjectNode copyNode = mapper.createObjectNode();
				String copyName = ((Element) fls.get(y))
						.getAttributeValue("FILEID");

				Document altoFileDoc = null;
				copyNode.put("filename", copyName);

				// find the file location
				Nodes locs = metsDoc.query("//mets:file[@ID='" + copyName
						+ "']/mets:FLocat", xc);
				String location = null;
				if (locs.size() > 0) {
					Node loc = locs.get(0);
					Element locEl = (Element) loc;
					Nodes locNds = locEl.query("@xlink:href", xc);
					if (locNds.size() > 0) {
						Node locationNode = locNds.get(0);
						location = locationNode.getValue();
						copyNode.put("location", location);
					}

					includeAlto(metsDoc, xc, copies, copyNode, copyName);
					includeTiff(altoFolderLocation, mapper, xc, copies,
							copyNode, copyName, location);
					// placeholder for jp2
					if (copyName.endsWith(".jp2")) {
						copyNode.put("copyrole",
								"ac");
					}

				}
			}
			children.add(childNode);

		}
	}

	/**
	 * Includes ALTO file information
	 * 
	 * @param metsDoc
	 * @param xc
	 * @param copies
	 * @param copyNode
	 * @param copyName
	 */
	private static void includeAlto(Document metsDoc, XPathContext xc,
			ArrayNode copies, ObjectNode copyNode, String copyName) {
		if (copyName.endsWith(".xml")) {

			copyNode.put("copyrole", "at");
			copies.add(copyNode);
			// find checksum
			Nodes fileNodes = metsDoc.query("//mets:file[@ID='" + copyName
					+ "']", xc);
			Node fileNode = fileNodes.get(0);
			String checksumType = ((Element) fileNode)
					.getAttributeValue("CHECKSUMTYPE");
			String checksum = ((Element) fileNode)
					.getAttributeValue("CHECKSUM");
			copyNode.put("checksumType", checksumType);
			copyNode.put("checksum", checksum);

		}
	}

	/**
	 * Include tiff information, this is curenly hardcoded as tiff information
	 * is not included in mets
	 * 
	 * @param altoFolderLocation
	 * @param mapper
	 * @param xc
	 * @param copies
	 * @param copyNode
	 * @param copyName
	 * @param location
	 * @throws ParsingException
	 * @throws ValidityException
	 * @throws IOException
	 * @throws Exception
	 */
	private static void includeTiff(Path altoFolderLocation,
			ObjectMapper mapper, XPathContext xc, ArrayNode copies,
			ObjectNode copyNode, String copyName, String location)
			throws ParsingException, ValidityException, IOException, Exception {
		Document altoFileDoc;
		if (copyName.endsWith(".tif")) {
			copyNode.put("copyrole", "mt");
			String altoName = copyName.replace(".tif", ".xml");
			Path altoPath = altoFolderLocation.resolve(altoName);
			Path imagePath = altoPath.getParent().getParent().resolve(location);
			Builder builder = new Builder();
			altoFileDoc = builder.build(altoPath.toFile());
			Nodes measurementUnitNodes = altoFileDoc.query(
					"//alto:MeasurementUnit", xc);
			String measurementUnit = measurementUnitNodes.get(0).getValue();

			Nodes ocrPageNodes = altoFileDoc.query("//alto:Page", xc);
			copyNode.put("measurementunit", measurementUnit);
			// get Resolution
			String XResolution = "XRES:400";
			String YResolution = "YRES:400";
			String height = "29132";
			String width = "30492";
			copyNode.put("xresolution", XResolution);
			copyNode.put("yresolution", YResolution);
			includeOcrAndJp2(mapper, copies, copyNode, copyName, ocrPageNodes,
					height, width);

		}
	}

	/**
	 * OCR and jp2 are both hardcoded as they are not included in mets file
	 * 
	 * @param mapper
	 * @param copies
	 * @param copyNode
	 * @param copyName
	 * @param ocrPageNodes
	 * @param height
	 * @param width
	 */
	private static void includeOcrAndJp2(ObjectMapper mapper, ArrayNode copies,
			ObjectNode copyNode, String copyName, Nodes ocrPageNodes,
			String height, String width) {
		if (ocrPageNodes.size() > 0) {
			Node ocrPageNode = ocrPageNodes.get(0);
			ObjectNode tmObjectNode = mapper.createObjectNode();
			tmObjectNode.put("width", width);
			tmObjectNode.put("height", height);
			copyNode.put("technicalmetadata", tmObjectNode);
			copies.add(copyNode);
			// hardcode jp2 stuff currentlly not in mets
			ObjectNode jp2CopyNode = mapper.createObjectNode();
			String jp2Name = copyName.replace(".tif", ".jp2");
			jp2CopyNode.put("copyrole", "ac");
			jp2CopyNode.put("technicalmetadata", tmObjectNode);
			jp2CopyNode.put("filename", jp2Name);
			jp2CopyNode.put("location", "jp2/" + jp2Name);
			copies.add(jp2CopyNode);
			// hardcode ocr stuff
			ObjectNode ocrCopyNode = mapper.createObjectNode();
			String ocrName = copyName.replace(".tif", ".json.gz");
			ocrCopyNode.put("copyrole", "oc");
			ocrCopyNode.put("filename", ocrName);
			ocrCopyNode.put("location", "ocr/" + ocrName);
			copies.add(ocrCopyNode);

		}
	}

	/**
	 * Gets supplements
	 * 
	 * @param mapper
	 * @param metsDoc
	 * @param resultNode
	 * @param xc
	 */
	private static void getSupplements(ObjectMapper mapper, Document metsDoc,
			ObjectNode resultNode, XPathContext xc) {
		Nodes supplements = metsDoc
				.query("//mets:structMap[@TYPE='physical']/*/mets:div[@TYPE='supplement']",
						xc);
		ArrayNode children = mapper.createArrayNode();
		resultNode.put("sections", children);
		if (supplements != null && supplements.size() > 0) {

			for (int i = 0; i < supplements.size(); i++) {
				ObjectNode supplementNode = mapper.createObjectNode();
				Node currNode = supplements.get(i);
				String physType = ((Element) currNode)
						.getAttributeValue("TYPE");
				supplementNode.put("subType", physType);
				String supplementOrder = ((Element) currNode)
						.getAttributeValue("DMDID");
				String order = ((Element) currNode).getAttributeValue("ORDER");
				// validate this
				if (supplementOrder != null) {
					String[] orderDtls = supplementOrder
							.split("modssupplement");

					if (orderDtls.length == 2) {
						supplementNode.put("order", orderDtls[1]);
					} else {
						supplementNode.put("order", order);
					}

					children.add(supplementNode);

				}

			}

		}
	}

	/**
	 * Gets Sections
	 * 
	 * @param mapper
	 * @param metsDoc
	 * @param resultNode
	 * @param xc
	 */
	private static void getSections(ObjectMapper mapper, Document metsDoc,
			ObjectNode resultNode, XPathContext xc) {
		Nodes sections = metsDoc
				.query("//mets:structMap[@TYPE='physical']/*/mets:div[@TYPE='section']",
						xc);

		ArrayNode children = mapper.createArrayNode();
		resultNode.put("sections", children);

		if (sections != null && sections.size() > 0) {

			for (int i = 0; i < sections.size(); i++) {
				ObjectNode sectionNode = mapper.createObjectNode();
				Node currNode = sections.get(i);
				String physType = ((Element) currNode)
						.getAttributeValue("TYPE");
				sectionNode.put("subType", physType);
				String sectionOrder = ((Element) currNode)
						.getAttributeValue("DMDID");
				String order = ((Element) currNode).getAttributeValue("ORDER");
				// validate this
				if (sectionOrder != null) {
					String[] orderDtls = sectionOrder.split("modssection");

					if (orderDtls.length == 2) {
						sectionNode.put("order", orderDtls[1]);
					} else {
						sectionNode.put("order", order);
					}

					children.add(sectionNode);

				}

			}

		}
	}

	/**
	 * Gets issue
	 * 
	 * @param metsDoc
	 * @param xc
	 * @return
	 */
	private static String getIssue(Document metsDoc, XPathContext xc) {
		Nodes issueNodes = metsDoc.query(
				"//mods:detail[@type='issue']/mods:number", xc);
		String issue = issueNodes.get(0).getValue();
		return issue;
	}

	/**
	 * Gets volume
	 * 
	 * @param metsDoc
	 * @param xc
	 * @return
	 */
	private static String getVolume(Document metsDoc, XPathContext xc) {
		Nodes volumeNodes = metsDoc.query(
				"//mods:detail[@type='volume']/mods:number", xc);
		String volume = volumeNodes.get(0).getValue();
		return volume;
	}

	/**
	 * Gets identifier
	 * 
	 * @param metsDoc
	 * @param xc
	 * @return
	 */
	private static String getIdentifier(Document metsDoc, XPathContext xc) {
		Nodes identifierNodes = metsDoc.query("//mods:identifier", xc);
		String identifier = identifierNodes.get(0).getValue();
		return identifier;
	}

	/**
	 * Gets genre
	 * 
	 * @param metsDoc
	 * @param xc
	 * @return
	 */
	private static String getGenre(Document metsDoc, XPathContext xc) {
		Nodes genreNodes = metsDoc.query("//mods:genre", xc);
		String genre = genreNodes.get(0).getValue();
		return genre;
	}

	/**
	 * Gets title
	 * 
	 * @param metsDoc
	 * @param xc
	 * @return
	 */
	private static String getTitle(Document metsDoc, XPathContext xc) {
		Nodes titleNodes = metsDoc.query("//mods:title", xc);
		String title = titleNodes.get(0).getValue();
		return title;
	}

}
