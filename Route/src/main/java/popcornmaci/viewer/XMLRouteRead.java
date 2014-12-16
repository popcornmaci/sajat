package popcornmaci.viewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jxmapviewer.viewer.GeoPosition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLRouteRead {

   
    Document dom;
    private ArrayList<GeoPosition> locations = new ArrayList<>();

    public ArrayList<GeoPosition> getLocations() {
		return locations;
	}
    
    public void parseXmlFile(InputStream inputstr){
    	//get the factory
		  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
        	//Using factory get an instance of document builder
		DocumentBuilder db = dbf.newDocumentBuilder();
		//parse using builder to get DOM representation of the XML file
		dom = db.parse(inputstr);

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
    	locations.clear();
    	 //get the root element  
        Element docEle = dom.getDocumentElement();
        //get a nodelist of  elements  
        NodeList nl = docEle.getElementsByTagName("Location");
        if(nl != null && nl.getLength() > 0) {
            for(int i = 0 ; i < nl.getLength();i++) {
            	//get the employee element  
            	Element node = (Element)nl.item(i);
                Double lat = Double.parseDouble(node.getAttribute("lat"));
                Double lon = Double.parseDouble(node.getAttribute("lon"));
                //add it to list  
                 GeoPosition loc = new GeoPosition(lat,lon);
                locations.add(loc);
            }
        }
    }
    
}
