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

import pagetypes.InstructablePage;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class InstructableCrawler extends WebCrawler {
	
	private enum TypeOfPage{OTHER, ALLSTEPS, AUTHOR};
	
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" 
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf" 
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    
    private String categoryOfPage="food";

/**
* You should implement this function to specify whether
* the given url should be crawled or not (based on your
* crawling logic).
*/
@Override
	public boolean shouldVisit(WebURL url) 
	{
		String href=url.toString();

		if(href.endsWith("?ALLSTEPS"))
		{	
			System.out.println("returning true for url "+href);
			return true;
		
		}
		else
		{	if(href.endsWith("/") && (href.indexOf("?lang=")==-1) && !FILTERS.matcher(href).matches())
				return true;
			return false;
		}
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
			switch(findTypeOfPage(page))
			{
				case ALLSTEPS:
					
					HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
					String text = htmlParseData.getText();
					String html = htmlParseData.getHtml();
					this.getAllCategoryFields(html);
					break;
				
				case AUTHOR:
					//TODO: Add all the author fields;
					break;
				default:
					break;
			}
			
			
			//System.out.println(text);
			
		}
	}
	public void getAllCategoryFields(String html)
	{
		//Here we are getting all the required crawling data from the html string presented to us.
		
		/*List of fields
		 * Name of the instructable
		 * Url
		 * Author
		 * Number of steps
		 * category
		 * Views
		 * Favorites
		 * Tags
		 * Author Followers
		 * Instructions with images replaced by URLS
		 * Comments
		 */
		
		Document doc = Jsoup.parse(html);
		String title=doc.getElementsByTag("title").text();
		System.out.println("Title of the page is "+title);
		//System.out.println(html);
		System.out.println("HTML size is "+html.length());
		InstructablePage i=new InstructablePage(html);
	}
	
	public TypeOfPage findTypeOfPage(Page page)
	{
		//In this method we will perform additional tests to check if we want to crawl this web page further
		//The common cases include not crawling pages we have already crawled. Finding if the page is to be crawled by categorizing 
		//if it is an author page or allstep page. Additional tests to be added.
		//At this point we would like to visit the author and the allsteps page only.
		
		
		TypeOfPage t;
		String url=page.getWebURL().toString();
		if(url.endsWith("?ALLSTEPS") && pageCategory(page, categoryOfPage))
			t=TypeOfPage.ALLSTEPS;
		else
		{
			t=TypeOfPage.OTHER;
		}
		return t;
	}
	public boolean pageCategory(Page page, String category)
	{
		String str="googletag.pubads().setTargeting(\"category\", \""+category+"\")";
		//System.out.println("Google ads string is "+str);
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		String text = htmlParseData.getText();
		String html = htmlParseData.getHtml();
		if(html.indexOf(str)!=-1)
		{	
			
			//System.out.println("pageCategory returning true for url "+page.getWebURL().toString());
			return true;
		
		}
		else
			return false;
				
	}

}
