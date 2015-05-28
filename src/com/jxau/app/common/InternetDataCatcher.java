/**
 * 
 */
package com.jxau.app.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.widget.Toast;

import com.jxau.app.api.AppConfigue;
import com.jxau.app.bean.NewsBean;
import com.jxau.app.bean.NewsDetailsBean;

/**
 * Title: InternetHelper Description:
 * @author DaiS
 * @version 1.0
 * @date 2013-12-28
 */
public class InternetDataCatcher {

	private static List<String> dateList;

	/**
	 * 获取新闻列表
	 */
	public static List<NewsBean> getNewsList() {
		// getNewsListDate();
		List<NewsBean> newsList = new ArrayList<NewsBean>();
		String abs = AppConfigue.HOST_ABS;
		String url = AppConfigue.NEWS_LIST_URL;

		String content = "";
		try {
			Document doc = Jsoup.connect(url).data("jquery", "java")
					.userAgent("Mozilla").cookie("auth", "token")
					.timeout(50000).get();
			content = doc.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document doc = Jsoup.parse(content);
		Elements divs = doc.getElementsByClass("columnStyle");
		String divContent = divs.toString();

		Document doc2 = Jsoup.parse(divContent, abs);
		Elements linkStrs = doc2.getElementsByTag("a");
		for (Element linkStr : linkStrs) {
			String title = linkStr.getElementsByTag("a").text();
			String news_url = linkStr.getElementsByTag("a").attr("href");
			newsList.add(new NewsBean(title, "农大官网", "之前", abs + news_url));
		}
		if (dateList.size() != 0) {
			for (int i = 0; i < 8; i++) {
				newsList.get(i).setPubDate(dateList.get(i));
			}
		}
		return newsList;
	}

	public static void getNewsListDate() {

		List<String> dateList2 = new ArrayList<String>();
		String url = AppConfigue.HOST_URL;
		String content = "";
		try {
			Document doc = Jsoup.connect(url).data("jquery", "java")
					.userAgent("Mozilla").cookie("auth", "token")
					.timeout(80000).get();
			content = doc.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document doc = Jsoup.parse(content);
		Elements divs = doc.getElementsByTag("div");
		String divContent = divs.toString();
		Document doc2 = Jsoup.parse(divContent);
		Elements linkStrs = doc2.getElementsByAttributeValue("style",
				"white-space:nowrap");
		for (Element linkStr : linkStrs) {
			String date = linkStr.text();
			dateList2.add(date);
		}
		dateList = dateList2;

	}

	/**
	 * 获取新闻详细内容
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static NewsDetailsBean getNewDetails(String url)
			throws UnsupportedEncodingException {
		String title = "";
		String content = "";
		String context = "";
		boolean isSpecial = false;
		ArrayList<String> pictures = new ArrayList<String>();
		try {
			Document doc = Jsoup.connect(url).data("jquery", "java")
					.userAgent("Mozilla").cookie("auth", "token")
					.timeout(50000).get();
			content = doc.toString();
			title = doc.title();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(content);
		Elements linkStrs = doc.getElementsByAttributeValue("class",
				"main_table_bg");
		Document doc2 = Jsoup.parse(linkStrs.toString());
		Elements text = doc2.getElementsByTag("p");
		Elements srcs = doc2.getElementsByTag("img");
		for (Element picture : srcs) {
			pictures.add(AppConfigue.HOST_ABS + picture.attr("src"));
		}
		for (Element linkStr : text) {
			String s = linkStr.text().trim();
			context += "       " + s.replace("?", " ") + "\r\n" + '\n';
		}
		byte[] utfspace = new byte[] { (byte) 0xc2, (byte) 0xa0 };
		String UTFSpace;
		try {
			UTFSpace = new String(utfspace, "UTF-8");
			context = context.replace(UTFSpace, "");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (context.trim().length() == 0) {
			isSpecial = true;
			Elements link = doc.getElementsByAttributeValue("class", "content");
			doc2 = Jsoup.parse(link.toString());
			text = doc2.getElementsByTag("h1");
			for (Element linkStr : text) {
				String s = linkStr.text().trim();
				String s1 = "      " + s.toString();
				context += s1 + '\n';
			}
			try {
				UTFSpace = new String(utfspace, "UTF-8");
				context = context.replace(UTFSpace, " ");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Elements dateStrs = doc.getElementsByTag("div");
		String date = dateStrs.text().substring(5, 15);
		int index1 = -1, index2 = -1;
		for (int i = context.length() - 1; i >= 0; i--) {
			if (context.charAt(i) == '）' || context.charAt(i) == ')') {
				index1 = i;
			}
			if (context.charAt(i) == '（' || context.charAt(i) == '(') {
				index2 = i;
				break;
			}
		}
		int index = context.lastIndexOf("。");
		String origin = null;
		if (index2 > index) {
			origin = context.substring(index2 + 1, index1);
			if (isSpecial) {
				context = context.substring(0, index2)
						+ context.substring(index1 + 1, context.length());
			} else {
				context = context.substring(0, index2);
			}

		}

		return new NewsDetailsBean(title, context, date, origin, pictures);
	}

	/**
	 * 获取通知列表
	 */
	public static List<NewsBean> getNoticleList() {
		List<NewsBean> newsList = new ArrayList<NewsBean>();
		String abs = AppConfigue.HOST_ABS;
		String url = AppConfigue.NOTICLE_LIST_URL;

		String content = "";
		try {
			Document doc = Jsoup.connect(url).data("jquery", "java")
					.userAgent("Mozilla").cookie("auth", "token")
					.timeout(50000).get();
			content = doc.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document doc = Jsoup.parse(content);
		Elements divs = doc.getElementsByTag("div");
		Document doc2 = Jsoup.parse(divs.toString());
		Elements table = doc2.getElementsByAttributeValue("class",
				"columnStyle");
		Document doc3 = Jsoup.parse(table.toString());
		Elements linkStrs = doc3.getElementsByTag("a");
		for (Element linkStr : linkStrs) {
			String title = linkStr.getElementsByTag("a").text();
			String news_url = linkStr.getElementsByTag("a").attr("href");
			int indexEnd1 = -1, indexEnd2 = -1, index = -1;

			indexEnd1 = title.indexOf(":");
			indexEnd2 = title.indexOf("：");
			if (indexEnd1 != -1)
				index = indexEnd1;
			if (indexEnd2 != -1)
				index = indexEnd2;
			if (index != -1) {
				String origin = title.substring(0, index);
				String subTitle = title.substring(index + 1);

				newsList.add(new NewsBean(subTitle, origin, "之前", abs
						+ news_url));
			} else {
				newsList.add(new NewsBean(title, "农大官网", "之前", abs + news_url));
			}
		}
		if (newsList.size() != 0) {
			for (int i = 8; i < dateList.size(); i++) {
				newsList.get(i - 8).setPubDate(dateList.get(i));
			}
		}
		return newsList;
	}

	/**
	 * 获取通知详细内容
	 */
	public static String getNoticleDetails(String url) {
		String content = "";
		String context = "";
		try {
			Document doc = Jsoup.connect(url).data("jquery", "java")
					.userAgent("Mozilla").cookie("auth", "token")
					.timeout(50000).get();
			content = doc.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(content);
		Elements linkStrs = doc.getElementsByTag("p");
		for (Element linkStr : linkStrs) {
			context += "       " + linkStr.text().replace("?", " ") + "\r\n"
					+ '\n';
		}
		return context;
	}
}
