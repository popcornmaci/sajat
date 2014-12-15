package popcornmaci.viewer;

import javax.xml.parsers.*;
import java.io.IOException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


public class RouteReader {

    class Location {
        Location(double x, double y) {
            this.x=x;
            this.y=y;
        }
        double x;
        double y;
        void setLatitude(double x) {
            this.x=x;
        }
        void setLongitude(double y) {
            this.y = y;
        }

    };
    Document dom;
    public static ArrayList<Location> locs = new ArrayList<Location>();

    public void parseXmlFile(InputStream is){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(is);

        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch(SAXException se) {
            se.printStackTrace();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
        parseDocument();
    }

    private void parseDocument(){
        locs.clear();
        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("Location");
        if(nl != null && nl.getLength() > 0) {
            for(int i = 0 ; i < nl.getLength();i++) {
                Element node = (Element)nl.item(i);
                Double lat = Double.parseDouble(node.getAttribute("lat"));
                Double lon = Double.parseDouble(node.getAttribute("lon"));
                Location loc = new Location(lat,lon);
                locs.add(loc);
            }
        }
    }
    
}
