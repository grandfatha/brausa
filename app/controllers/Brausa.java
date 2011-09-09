package controllers;

import play.*;
import play.cache.Cache;
import play.cache.CacheFor;
import play.libs.WS;
import play.libs.XML;
import play.libs.WS.HttpResponse;
import play.libs.XPath;
import play.mvc.*;
import play.utils.HTML;

import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import models.*;

public class Brausa extends Controller {

    public static void index(String subreddit) {

		String rssurl = getURL(subreddit);
    	String xml = getRSS(rssurl);
    	
    	xml = xml.replaceAll("media:", ""); // get rid of media namespace, broke xpath queries	
    	Document doc = XML.getDocument(xml);
//    	List<Node> links = XPath.selectNodes("//*[contains(text(), '[link]')]", doc.getDocumentElement().getFirstChild());
//		String image = XPath.selectText("tr/td/a[contains(@href, 'imgur')]/@href", html.getDocumentElement());

    	List<Node> items = XPath.selectNodes("//item", doc.getDocumentElement().getFirstChild());
    	List<Map> data = new ArrayList<Map>();
    	
    	for (Node node : items) {
			
    		try {
				String title = XPath.selectText("title", node);
				String url = XPath.selectText("link", node);
				String thumb = XPath.selectText("thumbnail/@url", node);
//    		String image = XPath.selectText("description/table/tr/td/a[contains(@href, 'imgur')]/@href", node);
				Node embeddedhtml = XPath.selectNode("description", node);
				
				Document html = XML.getDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
						"<bs>"+embeddedhtml.getTextContent()+"</bs>");
				String image = XPath.selectText("//*[contains(text(), '[link]')]/@href", html.getDocumentElement());

				// TODO: find a away to extract links to the actual image from flickr
				if(image.contains("www.flickr.com")){
					continue;
				}
				
				// convert to direct link in imgur = append file extension
				if(image.contains("imgur") && !image.endsWith(".jpg")){
					image += ".jpg";
				}
//				Logger.info("image-url: "+image);
				
				Map<String,String> entry = new HashMap<String,String>();
				entry.put("title", title);
				entry.put("url", url);
				entry.put("thumb", thumb);
				entry.put("image", image);
				data.add(entry);
			} catch (Exception e) {
//				Logger.error(e, "Failed to parse rss-item, skipping it.");
			}
    	}
    	
    	render(data);
    }

	private static String getURL(String subreddit) {
		subreddit = (subreddit == null || subreddit.length() == 0) ? "earth" : subreddit;
		String area = key2url.get(subreddit);
		return "http://www.reddit.com/r/"+area+".rss";
	}

	private static String getRSS(String rssurl) {
		
		String xml = Cache.get(rssurl, String.class);
		
		if(xml == null || xml.length() == 0){
			xml = WS.url(rssurl).get().getString();
			Cache.set(rssurl, xml, "2mn");
		}
		 
		return xml;
	}
	
	/**
	 * a little lookup-map that prevents the evil p-word from being in the url...
	 */
	public static Map<String,String> key2url = new HashMap<String, String>(){{
		put("earth", "EarthPorn");
		put("botanical", "BotanicalPorn");
		put("cities", "CityPorn");
		put("animal", "AnimalPorn");
		put("water", "waterporn");
		put("space", "spaceporn");
		put("human", "HumanPorn");
		
		put("machine", "MachinePorn");
		put("destruction", "DestructionPorn");
		put("map", "MapPorn");
		put("geek", "GeekPorn");
		put("room", "RoomPorn");
		put("book", "bookporn");
		put("adrenaline", "AdrenalinePorn");
		put("history", "HistoryPorn");
		put("ad", "AdPorn");
		put("village", "VillagePorn");
		put("albumart", "AlbumArtPorn");
		put("abandoned", "AbandonedPorn");
		put("news", "NewsPorn");
		put("quotes", "QuotesPorn");
		put("design", "DesignPorn");
		put("movieposter", "MoviePosterPorn");
		put("military", "MilitaryPorn");
		put("bestofsfw", "BestOfSFWPorn");
		put("meta", "MetaPorn");
		put("cemetery", "CemeteryPorn");
	}};
}