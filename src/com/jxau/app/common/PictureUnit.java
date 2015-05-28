package com.jxau.app.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PictureUnit {
	
	public static ArrayList<String> catchUrls(String url) {
		String content = "";
		ArrayList<String> list=new ArrayList<String>();
		try {
			Document doc = Jsoup.connect(url).data("jquery", "java")
					.userAgent("Mozilla").cookie("auth", "token")
					.timeout(50000).get();
			content = doc.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(content);

		Elements linkStrs = doc.getElementsByAttributeValue("class",
				"article_content");
		Document doc2 = Jsoup.parse(linkStrs.toString());
		Elements text = doc2.getElementsByTag("p");
		for (Element linkStr : text) {
			list.add(linkStr.text().trim());
			//System.out.println(linkStr.text().trim());
		}
		return list;
	}
}
