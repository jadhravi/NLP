package pagetypes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;

public class InstructablePage extends BasicPage {
	
	private String name;
	private String purl;
	private String views;
	private String favorites;
	private String numberOfSteps;
	private String category;
	private String author;
	private String authorFollowers;
	private String tags;
	private String instructionsWithoutHTML;
	private String instructionsWithHTML;
	private String numberOfComments;
	private String comments;
	Document doc;
	public InstructablePage(Page page)
	{
		HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		String html = htmlParseData.getHtml();
		System.out.println("Crawling page "+page.getWebURL().toString());
		doc=Jsoup.parse(html);
		this.name=extractName();
		this.purl=page.getWebURL().toString();
		this.views=extractViews();
		this.favorites=extractFavorites();
		this.numberOfSteps=extractNumberOfSteps();
		this.author=extractAuthor();
		this.authorFollowers=extractAuthorFollowers();
		this.tags=extractTags();
		this.instructionsWithoutHTML=extractInstructions(0);
		this.instructionsWithHTML=extractInstructions(1);
		this.numberOfComments=extractNumberOfComments();
		this.comments=extractComments();
		this.writeToFile(0);
		this.writeToFile(1);
		//System.out.println("title is "+this.extractName());
	}
	public InstructablePage(String html, String url)
	{
		
		System.out.println("Crawling page "+url);
		doc=Jsoup.parse(html);
		this.name=extractName();
		this.purl=url;
		this.views=extractViews();
		this.favorites=extractFavorites();
		this.numberOfSteps=extractNumberOfSteps();
		this.author=extractAuthor();
		this.authorFollowers=extractAuthorFollowers();
		this.tags=extractTags();
		this.instructionsWithoutHTML=extractInstructions(0);
		this.instructionsWithHTML=extractInstructions(1);
		this.numberOfComments=extractNumberOfComments();
		this.comments=extractComments();
		this.writeToFile(0);
		this.writeToFile(1);
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
	public String extractTags()
	{
		System.out.println("Tags are ");
		Elements ele=doc.getElementsByClass("ible-tags");
		Elements s=new Elements();
		for(Element e:ele)
		{
			s=e.getElementsByTag("a");
			break;
		}
		String str=new String();
		int i=0;
		for(Element e:s)
		{
			if(i==s.size() || i==0)
			str=str+e.text();
			else
				str=str+","+e.text();	
			i++;
		}
	
		System.out.println("Tags are"+str);
		return str;
	}
	public String extractInstructions(int withHtml)
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
					if(withHtml==0)
						retval=retval+"\n"+h.text();
					else
						retval=retval+"\n"+h.html();
				}
				else if(h.tagName().equalsIgnoreCase("h2"))
				{
					retval=retval+"\n"+h.text();
				}
			}
		}
		return retval;

	}
	public String extractNumberOfComments()
	{
		Elements ele=doc.getElementsByClass("comments-container");
		int count=0;
		for(Element e:ele)
		{
			Elements comments=e.getAllElements();
			for(Element c:comments)
			{
				if(c.className().startsWith("comment-entry clearfix"))
					count++;
			}
		}
		System.out.println("Number of comments are "+count);
		return Integer.toString(count);
	}
	public String extractComments()
	{
		String allComments=new String();
		
		Elements ele=doc.getElementsByClass("comments-container");
		for(Element e:ele)
		{
			Elements comments=e.getAllElements();
			for(Element c:comments)
			{
				if(c.className().startsWith("comment-entry clearfix"))
				{
					Elements all=c.getAllElements();
					for(Element a:all)
					{
						if(a.className().equalsIgnoreCase("author-link"))
						{
							allComments+="\nFrom: "+a.text();
						}
						if(a.className().equalsIgnoreCase("author-link pro"))
						{
							allComments+="\nFrom: <Pro Author>"+a.text();
						}
						if(a.className().equalsIgnoreCase("in-reply-to"))
						{
							allComments+="\nTo: "+a.text();
						}
						if(a.className().equalsIgnoreCase("comment-date"))
						{
							String str=a.text();
							str=str.substring(0,(str.length()-5));
							allComments+="\nGivenDate: "+str;
							allComments+="\nApproxDate: "+getDateFromString(str);
							//System.out.println("Approximate date is "+getDateFromString(str));
						}
						if(a.className().equalsIgnoreCase("txt comment-txt"))
						{
							allComments+="\nContent: "+a.text();
						}
						if(a.className().equalsIgnoreCase("spotThumbs spot-Images"))
						{
							Elements cImages=a.getElementsByClass("fancybox-thumb");
							for(Element cI:cImages)
							{
								allComments+="\nCommentImages: "+cI.attr("src");
							}
						}
					}
					
				}
			}
		}
		System.out.println("comments are "+allComments);
		return allComments;
	}
	public String getDateFromString(String str)
	{
		String dt[]=str.split(" ");
		Date d=new Date();
		if(dt.length==3 && dt[2].equalsIgnoreCase("ago"))
		{
			if(dt[1].equalsIgnoreCase("seconds"))
			{
				d=new Date(d.getTime()-(Integer.parseInt(dt[0])*1000));
				
			}
			if(dt[1].equalsIgnoreCase("minutes"))
			{
				d=new Date(d.getTime()-(Integer.parseInt(dt[0])*60*1000));
				
			}
			if(dt[1].equalsIgnoreCase("hours"))
			{
				d=new Date(d.getTime()-(Integer.parseInt(dt[0])*60*60*1000));
				
			}
			if(dt[1].equalsIgnoreCase("days"))
			{
				d=new Date(d.getTime()-(Integer.parseInt(dt[0])*24*60*60*1000));
				
			}
			if(dt[1].equalsIgnoreCase("days"))
			{
				d=new Date(d.getTime()-(Integer.parseInt(dt[0])*365*24*60*60*1000));
				
			}
		}
		else
		{
			if(str.equalsIgnoreCase("yesterday"))
			{
				d=new Date(d.getTime()-(24*60*60*1000));
			}
				
		}
		if(d==null)
			return str;
		else
			return d.toString();
	}
	public void writeToFile(int withhtml)
	{
		String content=new String("");
		content+="Title: "+this.name+"\n";
		content+="Url: "+this.purl+"\n";
		content+="CrawledOn: "+new Date().toString()+"\n";
		content+="Views: "+this.views+"\n";
		content+="Favourites: "+this.favorites+"\n";
		content+="NumberOfSteps: "+this.numberOfSteps+"\n";
		content+="Author: "+this.author+"\n";
		content+="AuthorFollowers: "+this.authorFollowers+"\n";
		content+="Tags: "+this.tags+"\n";
		content+="NumberOfComments: "+this.numberOfComments+"\n";
		if(withhtml==1)
		content+="Instructions: "+this.instructionsWithHTML+"\n";
		else
			content+="Instructions: "+this.instructionsWithoutHTML+"\n";	
		content+="Comments: "+this.comments;
		String fname=this.name;
		fname=fname.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
		try {
			 
			//String content = "This is the content to write into file";
				File file = new File("C://Users/rjadhav/Instructable/InstructionsFiles/"+fname+".txt");
			
				if(withhtml==1)
					file = new File("C://Users/rjadhav/Instructable/InstructionsFilesWithHTML/"+fname+".txt");	
			
			
			if (!file.exists()) {
				file.createNewFile();
			
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			}
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
