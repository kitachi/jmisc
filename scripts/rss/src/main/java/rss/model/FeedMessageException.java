package rss.model;

public class FeedMessageException extends RuntimeException {

    public FeedMessageException(String msg, Exception e) {
        super(msg, e);
    }
}
