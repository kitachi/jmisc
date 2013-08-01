package rss.model;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Represents one RSS entry
 */
public class FeedEntry {

  public String title;
  public String description;
  public String url;
  public String summary;
  public Date createDate;

  @Override
  public String toString() {
      try {
        return new ObjectMapper().writeValueAsString(this);
    } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        throw new FeedMessageException("Cannot convert feed message to string.", e);
    }
  }

} 
