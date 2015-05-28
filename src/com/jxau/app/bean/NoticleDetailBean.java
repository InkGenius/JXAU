/**
 * 
 */
package com.jxau.app.bean;

 /**   
 * Title: NoticleBean
 * Description:
 * @author DaiS
 * @version 1.0
 * @date 2013-12-30
 */

public class NoticleDetailBean {
	
		private String title;
		private String context;
		private String author;
		private String date;
		private String origin;
		
		public NoticleDetailBean(String title,String context,String date){
			this.context=context;
			this.title=title;
			this.date=date;
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
}
