package rss.utils;

import java.io.FileOutputStream;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import rss.model.FeedEntry;

@SuppressWarnings("restriction")
public class RSSFeedWriter {
  public static void write(List<FeedEntry> rssfeed, String outputFile) throws Exception {

    // Create a XMLOutputFactory
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

    // Create XMLEventWriter
    XMLEventWriter eventWriter = outputFactory
        .createXMLEventWriter(new FileOutputStream(outputFile));

    // Create a EventFactory

    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent end = eventFactory.createDTD("\n");

    // Create and write Start Tag

    StartDocument startDocument = eventFactory.createStartDocument();

    eventWriter.add(startDocument);

    // Create open tag
    eventWriter.add(end);

    StartElement rssStart = eventFactory.createStartElement("", "", "rss");
    eventWriter.add(rssStart);
    eventWriter.add(eventFactory.createAttribute("version", "2.0"));
    eventWriter.add(end);

    eventWriter.add(eventFactory.createStartElement("", "", "channel"));
    eventWriter.add(end);

    // Write the rss feed
    for (FeedEntry entry : rssfeed) {
      eventWriter.add(eventFactory.createStartElement("", "", "item"));
      eventWriter.add(end);
      createNode(eventWriter, "title", entry.title);
      createNode(eventWriter, "description", entry.description);
      createNode(eventWriter, "link", entry.url);
      //TODO: convert entry.createdDate to the pubDate string
      createNode(eventWriter, "pubDate", "23 Jul 2013 12:30:21");
      eventWriter.add(end);
      eventWriter.add(eventFactory.createEndElement("", "", "item"));
      eventWriter.add(end);

    }

    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndElement("", "", "channel"));
    eventWriter.add(end);
    eventWriter.add(eventFactory.createEndElement("", "", "rss"));

    eventWriter.add(end);

    eventWriter.add(eventFactory.createEndDocument());

    eventWriter.close();
  }

    private static void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // Create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // Create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // Create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }
}
