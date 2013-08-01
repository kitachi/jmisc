package rss.utils;

import junit.framework.TestCase;

public class RSSFeedTest extends TestCase {

    @Test
    public void testReadRSS() {
       // might need to mock the feed for this
    RSSFeedParser parser = new RSSFeedParser("http://www.vogella.de/article.rss");
    Feed feed = parser.readFeed();
    System.out.println(feed);
    for (FeedMessage message : feed.getMessages()) {
      System.out.println(message);

    }

    }

    @Test
    public void testWriteRSS() {
    // Create the rss feed
    String copyright = "Copyright hold by Lars Vogel";
    String title = "Eclipse and Java Information";
    String description = "Eclipse and Java Information";
    String language = "en";
    String link = "http://www.vogella.de";
    Calendar cal = new GregorianCalendar();
    Date creationDate = cal.getTime();
    SimpleDateFormat date_format = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
    String pubdate = date_format.format(creationDate);
    Feed rssFeeder = new Feed(title, link, description, language,
        copyright, pubdate);

    // Now add one example entry
    FeedMessage feed = new FeedMessage();
    feed.setTitle("RSSFeed");
    feed.setDescription("This is a description");
    feed.setAuthor("nonsense@somewhere.de (Lars Vogel)");
    feed.setGuid("http://www.vogella.de/articles/RSSFeed/article.html");
    feed.setLink("http://www.vogella.de/articles/RSSFeed/article.html");
    rssFeeder.getMessages().add(feed);

    // Now write the file
    RSSFeedWriter writer = new RSSFeedWriter(rssFeeder, "articles.rss");
    try {
      writer.write();
    } catch (Exception e) {
      e.printStackTrace();
    }
    }
}
