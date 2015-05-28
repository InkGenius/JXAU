package com.jxau.app.bean;

import java.io.Serializable;

 /**   
 * Title: NewsBean
 * Description:
 * @author DaiS
 * @version 1.0
 * @date 2013-12-28
 */   
public class NewsBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String title;
	private String author;
	private String pubDate;
	private int id;
	private String Url ;

	public NewsBean(String title,String author,String pubDate,String Url ){
		this.pubDate = pubDate;
		this.title = title;
		this.author = author;
		this.Url=Url;
	}	
	
	public String getPubDate() {
		return this.pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getId() {
		return id;
	}
	public String getUrl() {
		return this.Url;
	}
	public void setUrl(String Url) {
		this.Url = Url;
	}
}
