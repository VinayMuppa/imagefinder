package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet(
    name = "ImageFinder",
    urlPatterns = {"/main"}
)
public class ImageFinder extends HttpServlet{
	private static final long serialVersionUID = 1L;

	protected static final Gson GSON = new GsonBuilder().create();

	//This is just a test array
	public static final String[] testImages = {
//			"https://images.pexels.com/photos/545063/pexels-photo-545063.jpeg?auto=compress&format=tiny",
//			"https://images.pexels.com/photos/464664/pexels-photo-464664.jpeg?auto=compress&format=tiny",
//			"https://images.pexels.com/photos/406014/pexels-photo-406014.jpeg?auto=compress&format=tiny",
//			"https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg?auto=compress&format=tiny"
  };

	ArrayList<String> result=new ArrayList<>();

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/json");
		String path = req.getServletPath();
		String url = req.getParameter("url");
		System.out.println("Got request of:" + path + " with query param:" + url);
		crawl(0,url,new ArrayList<>());
		System.out.println("done");
		resp.getWriter().print(GSON.toJson(result));
	}

	private void crawl(int level, String url, ArrayList<String> visited){
		if(level<=0){
			Document doc=request(url,visited);
			if(doc!=null){
				for(Element link:doc.select("a[href]")){
					String next_link=link.absUrl("href");
					if(isSameDomain(url,next_link)&&visited.contains(next_link)==false){
						crawl(level++,next_link,visited);
					}
				}
			}
		}
	}

	private boolean isSameDomain(String baseUrl, String url) {
		try {
			URL base = new URL(baseUrl);
			URL next = new URL(url);
			return base.getHost().equalsIgnoreCase(next.getHost());
		} catch (MalformedURLException e) {
			// Handle malformed URL exception
			return false;
		}
	}



	private Document request(String url, ArrayList<String> visited){
		try{
			Connection con= Jsoup.connect(url);
			Document doc=con.get();
			//System.out.println(doc);
			if(con.response().statusCode() == 200){
				Elements images = doc.select("img");
				//ArrayList<String> imageUrls = new ArrayList<>();
				for (Element image : images) {
					String src = image.attr("src");
					//System.out.println(src);
					result.add(src);
//					if(result.size()<=10){
//						result.add(src);
//					}
//					else{
//						break;
//					}

				}
				//testImages = re.toArray(new String[0]);
				visited.add(url);
				return doc;
			}
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}
