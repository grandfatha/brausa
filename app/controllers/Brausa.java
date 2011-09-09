package controllers;

import play.*;
import play.cache.CacheFor;
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

//	@CacheFor(value="2mn")
    public static void index() {
        
    	String rssurl = "http://www.reddit.com/r/EarthPorn.rss";
    	String xml = WS.url(rssurl).get().getString();
    	System.out.println(xml);
    	xml = xml.replaceAll("media:", "");
    	Document doc = XML.getDocument(StringEscapeUtils.unescapeHtml(xml));
//    	List<Node> links = XPath.selectNodes("//*[contains(text(), '[link]')]", doc.getDocumentElement().getFirstChild());
    	
    	List<Node> items = XPath.selectNodes("//item", doc.getDocumentElement().getFirstChild());
    	
    	List<Map> data = new ArrayList<Map>();
    	
    	for (Node node : items) {
			
    		String title = XPath.selectText("title", node);
    		String url = XPath.selectText("link", node);
    		String thumb = XPath.selectText("thumbnail/@url", node);
    		String image = XPath.selectText("description/table/tr/td/a[contains(@href, 'imgur')]/@href", node);
    		
    		// flickr links, etc
    		if(image == null){
    			continue;
    		}
			// convert to direct link in imgur = append file extension
			if(!image.endsWith(".jpg")){
				image += ".jpg";
			}
    		
//    		System.out.println(title);
//    		System.out.println(url);
//    		System.out.println(thumb);
//    		System.out.println(image);
			
			Map<String,String> entry = new HashMap<String,String>();
			entry.put("title", title);
			entry.put("url", url);
			entry.put("thumb", thumb);
			entry.put("image", image);
			data.add(entry);

    	}
    	
    	render(data);

    }

}