package pagetypes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InstructablePage extends BasicPage {
	
	private String name;
	private String purl;
	private String views;
	private String favorites;
	private String numberOfSteps;
	private String category;
	private String author;
	private String authorFollowers;
	private String[] tags;
	private String instructions;
	private String[] comments;
	Document doc;
	public InstructablePage(String html)
	{
		System.out.println("Creating instructable page "+html.length());
		doc=Jsoup.parse(html);
		this.name=extractName();
		this.views=extractViews();
		this.favorites=extractFavorites();
		this.numberOfSteps=extractNumberOfSteps();
		this.author=extractAuthor();
		this.authorFollowers=extractAuthorFollowers();
		this.tags=extractTags();
		//System.out.println("title is "+this.extractName());
	}
	public String extractName()
	{
		Elements ele= doc.getElementsByTag("title");
		return ele.text();
	}
	public String extractViews()
	{
		Elements ele=doc.getElementsByClass("total-hits");
		String str[]=ele.text().split(" ");
		str[0]=str[0].replaceAll(",", "");
		//System.out.println("Number of views are "+str[0].replaceAll(",", ""));
		//System.out.println("Number of views are "+str[0]);
		return str[0];
		
	}
	public String extractFavorites()
	{
		Elements ele=doc.getElementsByClass("favorites");
		String str[]=ele.text().split(" ");
		str[0]=str[0].replaceAll(",", "");
		//System.out.println("Favorites are "+str[0]);
		return str[0];
	}
	public String extractNumberOfSteps()
	{
		Element ele=doc.getElementById("jump-to-step-btn");
		String str[]=ele.text().split(" ");
		str[0]=str[0].replaceAll(",", "");
		//System.out.println("Number of steps  "+str[0]);
		return str[0];
	}
	public String extractAuthor()
	{
		Elements ele=doc.getElementsByClass("author");
		String str=ele.text();
		//System.out.println("Author is "+ele.text());
		return str;
	}
	public String extractAuthorFollowers()
	{
		Elements ele=doc.getElementsByClass("callout");
		String str=ele.text();
		//System.out.println("Number of followers for Author is "+str);
		return str;
	}
	public String[] extractTags()
	{
		System.out.println("Tags are ");
		Elements ele=doc.getElementsByClass("ible-tags");
		Elements s=new Elements();
		for(Element e:ele)
		{
			s=e.getElementsByTag("a");
			break;
		}
		String str[]=new String[s.size()];
		int i=0;
		for(Element e:s)
		{
			str[i++]=e.text();
			
		}
		for(String tag:str)
		{
			System.out.println(tag);
		}
		return str;
	}
	public String extractInstructions()
	{
		Elements ele=doc.getElementsByClass("step-container");
		String retval="";
		for(Element e:ele)
		{
			Elements s=e.getAllElements();
			for(Element h:s)
			{
				if(h.className().equalsIgnoreCase("photo-container"))
				{
					Elements links=h.getElementsByTag("img");
					for (Element link : links)
					{
						String str=link.attr("src");
						if(str.startsWith("http://cdn.instructables.com/"))
							retval=retval+"\n"+"Image:"+str;						
					}
				}
				else if(h.className().equalsIgnoreCase("txt step-body"))
				{
					retval=retval+"\n"+h.text();
				}
				else if(h.tagName().equalsIgnoreCase("h2"))
				{
					retval=retval+"\n"+h.text();
				}
			}
		}
		return retval;

	}
	
}
