package crawl;
import controller.InstructableController;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;







import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class InstructableCrawler extends WebCrawler {
	
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" 
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf" 
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

/**
* You should implement this function to specify whether
* the given url should be crawled or not (based on your
* crawling logic).
*/
@Override
	public boolean shouldVisit(WebURL url) 
	{
		//System.out.println("Visiting url"+url.toString());
		String href = url.getURL().toLowerCase();
		if(FILTERS.matcher(href).matches())
			return false;
		else if ((href.startsWith("http://www.instructables.com/id/") && href.endsWith("/")) /*|| (href.endsWith("?allsteps")*/)
		{
			System.out.println(" Url is "+href);
			if(!href.endsWith("?allsteps"))
			{
				InstructableController.addCustomSeed(href+"?ALLSTEPS");
				return false;
			}
			return true;
		}
		else
		{	
			return false;
		}
		//return !FILTERS.matcher(href).matches() && href.startsWith("http://www.ics.uci.edu/");
	}

/**
* This function is called when a page is fetched and ready 
* to be processed by your program.
*/
@Override
	public void visit(Page page) 
	{          
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);
		if (page.getParseData() instanceof HtmlParseData) 
		{
			//System.out.println("Inside instance of html");
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			//System.out.println(html);
			//Pattern pattern = Pattern.compile("googletag\\.pubads\\(\\)\\.setTargeting(.?*);");
			
			/*Pattern pattern = Pattern.compile("googletag.cmd.push(function()");
			Matcher matcher = pattern.matcher(html);
			/*List<WebURL> links = htmlParseData.getOutgoingUrls();
			
			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
			System.out.println(matcher.matches());*/
			Document doc = Jsoup.parse(html);
			Element content = doc.getElementById("ible-header-inner");
			Elements links=content.getElementsByTag("h1");
			for(Element link:links)
			{
				System.out.println(link.text());
			}
			
		}
	}

}
