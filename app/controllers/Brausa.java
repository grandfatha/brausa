package controllers;

import play.*;
import play.libs.WS;
import play.libs.XML;
import play.libs.WS.HttpResponse;
import play.libs.XPath;
import play.mvc.*;
import play.utils.HTML;

import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import models.*;

public class Brausa extends Controller {

    public static void index() {
        
    	String url = "http://www.reddit.com/r/EarthPorn.rss";
    	String xml = WS.url(url).get().getString();

    	Document doc = XML.getDocument(StringEscapeUtils.unescapeHtml(xml));
//    	List<Node> links = XPath.selectNodes("//*[contains(text(), '[link]')]", doc.getDocumentElement().getFirstChild());
    	List<Node> links = XPath.selectNodes("//a[contains(@href, 'imgur')]", doc.getDocumentElement().getFirstChild());
    	
    	List<String> nodes = new ArrayList<String>();
    	
    	for (Node node : links) {
			String href = node.getAttributes().getNamedItem("href").getTextContent();
			
			// convert to direct link in imgur = append file extension
			if(!href.endsWith(".jpg")){
				href = href + ".jpg";
			}
			nodes.add(href);
		}
    	render(xml, nodes);

    }

}