/**
 * 
 */
package com.jxau.app.bean;

import java.util.List;

 /**   
 * Title: NewsDetailsBean
 * Description:
 * @author DaiS
 * @version 1.0
 * @date 2013-12-30
 */

public class NewsDetailsBean {
	
	private String title;
	private String context;
	private String author;
	private String date;
	private String origin;
	private List<String> pictures;
	
	public NewsDetailsBean(String title,String context,String date,String author,List<String> pictures){
		this.context=context;
		this.title=title;
		this.date=date;
		this.author=author;
		this.pictures=pictures;
	}
	public String getOrigin(){
		return this.origin;
	}
	public void setOrigin(String origin){
		this.origin=origin;
	}
	
	public String getDate(){
		return this.date;
	}
	public void setDate(String date){
		this.date=date;
	}
	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title=title;
	}
	public String getContext(){
		return this.context;
	}
	public void setContext(String context){
		this.context=context;
	}
	
	public String getAuthor(){
		return this.author;
	}
	public void setAuthor(String author){
		this.author=author;
	}
	public List<String> getPictures() {
		return pictures;
	}
	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}
}
