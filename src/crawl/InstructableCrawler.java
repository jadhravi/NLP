package crawl;
import controller.InstructableController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;











import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
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
            + "|png|tiff|mid|mp2|mp3|mp4"
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
			System.out.println("href is "+href);
			//InstructableController.addCustomSeed(href+"=0");
			getAllCategoryFieldsByHttp(href);
			return true;
		
		}
		else
		{	
			if(href.startsWith("http://www.instructables.com/")&&(href.indexOf("?lang=")==-1) && !FILTERS.matcher(href).matches() && isUrlFoodCategory(href))
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
		
		String url = page.getWebURL().toString();
		System.out.println("URL is : " + url);
		if (page.getParseData() instanceof HtmlParseData) 
		{
			//System.out.println("Inside instance of html");
			switch(findTypeOfPage(page))
			{
				case ALLSTEPS:
					
					HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
					String text = htmlParseData.getText();
					String html = htmlParseData.getHtml();
					this.getAllCategoryFields(page);
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
	public void getAllCategoryFieldsByHttp(String url)
	{
		String html=visitPage(url);
		if(isAllStepsPage(html) && pageCategory(html, "food"))
		{	
			InstructablePage i=new InstructablePage(html, url);
		
		}
		
	}
	public void getAllCategoryFields(Page page)
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
		
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		String text = htmlParseData.getText();
		String html = htmlParseData.getHtml();
		Document doc = Jsoup.parse(html);
		String title=doc.getElementsByTag("title").text();
		System.out.println("Title of the page is "+title);
		//System.out.println(html);
		//System.out.println("HTML size is "+html.length());
		InstructablePage i=new InstructablePage(page);
	}
	
	public TypeOfPage findTypeOfPage(Page page)
	{
		//In this method we will perform additional tests to check if we want to crawl this web page further
		//The common cases include not crawling pages we have already crawled. Finding if the page is to be crawled by categorizing 
		//if it is an author page or allstep page. Additional tests to be added.
		//At this point we would like to visit the author and the allsteps page only.
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		
		String html = htmlParseData.getHtml();
		
		TypeOfPage t;
	
		String url=page.getWebURL().toString();
		if(isAllStepsPage(html) && pageCategory(html, categoryOfPage))
			t=TypeOfPage.ALLSTEPS;
		else
		{
			t=TypeOfPage.OTHER;
		}
		//System.out.println("Url is "+url);
		System.out.println(" Type of page is "+t);
		return t;
	}
	public boolean pageCategory(String html, String category)
	{
		String str="googletag.pubads().setTargeting(\"category\", \""+category+"\")";
		//System.out.println("Google ads string is "+str);
		
		if(html.indexOf(str)!=-1)
		{	
			
			System.out.println("pageCategory returning true");
			return true;
		
		}
		else
		{	
			System.out.println("pageCategory returning false");
			return false;
		
		}
				
	}
	public boolean isAllStepsPage(String html)
	{
		
		
		Document doc=Jsoup.parse(html);
		Element e=doc.getElementById("ible-header");
		if(e==null)
		{	
			return false;
		
		}
		else
		{
			String str=e.attr("data-allsteps");
			System.out.println(str);
			if(str.equalsIgnoreCase("false"))
				return false;
			else
				return true;
		}
				
	}
	public String visitPage(String url)
	{
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
	 
		// add request header
		StringBuffer result = new StringBuffer();
		try
		{
			request.addHeader("User-Agent", "");
			HttpResponse response = client.execute(request);
		 
			System.out.println("Response Code : " 
		                + response.getStatusLine().getStatusCode());
		 
			
			
			BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
			
			
			String line = "";
			String res="";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString()+" Exception ");
		}
		return result.toString();
	}
	public boolean isUrlFoodCategory(String url)
	{
		if(url.indexOf("category")!=-1)
		{
			if(url.indexOf("category-food")!=-1)
			{
				return true;
			}
			else
				return false;
		}
		else
		{
			if(url.startsWith("www.instructables.com/group") || url.startsWith("www.instructables.com/contest") || url.startsWith("www.instructables.com/community") || url.startsWith("www.instructables.com/workshop") || url.startsWith("www.instructables.com/about") || url.startsWith("www.instructables.com/member") )
				return false;
			else
				return true;
		}

	}

}
