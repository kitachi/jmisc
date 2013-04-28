package models;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.map.ObjectMapper;

public class ThingDescription {
    private final static ObjectMapper jsonMapper = new ObjectMapper();
    private final Map<String, Object> fields = new HashMap<String, Object>();

    public static ThingDescription fromJson(byte[] description) {
        try {
            return jsonMapper.readValue(description, ThingDescription.class);
        } catch (IOException e) {
            throw new RuntimeException("failed parsing JSON", e);
        }
    }

    public static ThingDescription fromJson(String description) {
        try {
            return jsonMapper.readValue(description, ThingDescription.class);
        } catch (IOException e) {
            throw new RuntimeException("failed parsing JSON", e);
        }
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='metadata'>");
        for (Entry<String, Object> e : fields.entrySet()) {
            if (e.getValue() != null) {
                sb.append("<em>");
                sb.append(escapeHtml(e.getKey()));
                sb.append(":</em> ");
                sb.append(escapeHtml(prettyPrint(e.getValue())));
                sb.append("<br>");
            }
        }
        sb.append("</div>");
        return sb.toString();
    }

    private String prettyPrint(Object o) {
        if (o instanceof String) {
            return (String) o;
        } else {
            try {
                return jsonMapper.writeValueAsString(o);
            } catch (IOException e1) {
                return "<unprintable>";
            }
        }
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        fields.put(key, value);
    }

    public Object get(String property) {
        return fields.get(property);
    }

    public String getTitle() {
        return (String) fields.get("title");
    }

    public Integer getWidth() {
        return (Integer) fields.get("width");
    }

    public Integer getHeight() {
        return (Integer) fields.get("height");
    }

    public String getSubUnitType() {
        return (String) fields.get("subUnitType");
    }
}
