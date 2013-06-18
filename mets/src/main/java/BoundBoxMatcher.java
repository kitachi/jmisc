import java.util.regex.*;

public class BoundBoxMatcher {
    private String ocrJson;
    private double pixelFactor;

    public BoundBoxMatcher(String ocrJson, double pixelFactor) {
        this.ocrJson = ocrJson;
        this.pixelFactor = pixelFactor;
    }

    public String scaleCoords() {
        Pattern p = Pattern.compile("'b':\\{'(\\d*)', '(\\d*)', '(\\d*)', '(\\d*)'\\}");
        Matcher m = p.matcher(ocrJson);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            // System.out.println(m.group());
            double tCoord1 = pixelFactor * new Integer(m.group(1)).doubleValue();
            double tCoord2 = pixelFactor * new Integer(m.group(2)).doubleValue();
            double tCoord3 = pixelFactor * new Integer(m.group(3)).doubleValue();
            double tCoord4 = pixelFactor * new Integer(m.group(4)).doubleValue();
            String tOrdinate1 = "'" + new Double(tCoord1).intValue() + "'";
            String tOrdinate2 = "'" + new Double(tCoord2).intValue() + "'";
            String tOrdinate3 = "'" + new Double(tCoord3).intValue() + "'";
            String tOrdinate4 = "'" + new Double(tCoord4).intValue() + "'";
            m = m.appendReplacement(sb, "'b':{" + tOrdinate1 + "," + tOrdinate2 + "," + tOrdinate3 + "," + tOrdinate4 + "}");
        }
        m.appendTail(sb);

        return sb.toString();
    }

    public static void main(String[] args) {
        String ocrJson = "{'f': 'prefix', 'g': {'heading'}, 'b':{'123', '234', '345', '234'}, 'a':['b':{'213', '342', '453', '342'}, 'c':{'a':['b':{'213', '342', '453', '342'}, 'd': 'design'], 'e': 'example'}";
        double pixelFactor = 0.5;
        BoundBoxMatcher b = new BoundBoxMatcher(ocrJson, pixelFactor);
        System.out.println(b.scaleCoords());
    }
}
