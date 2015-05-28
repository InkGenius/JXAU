/**
 * 
 */
package com.jxau.app.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

 /**   
 * Title: DateTransformer
 * Description:
 * @author DaiS
 * @version 1.0
 * @date 2013-12-30
 */

public class DateTransformer{
	public static String friendly_time(String sdate) throws ParseException {
		String ftime = "";
		SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		
		long lt = df.parse(sdate).getTime()/86400000+1;
		long ct = cal.getTimeInMillis()/86400000;
		int days = (int)(ct - lt);
		System.out.println(days);
		if(days == 0){
			ftime = "今天";
		}
		else if(days == 1){
			ftime = "昨天";
		}
		else if(days == 2){
			ftime = "前天";
		}
		else if(days > 2 && days <= 10){ 
			ftime = days+"天前";			
		}
		else if(days > 10){			
			ftime = sdate;
		}
		return ftime;
	}
	
	/**
	 * 判断给定字符串时间是否为今日
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate){
		Date today = new Date();
		SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd");
		String dDate=df.format(today);
		System.out.println(dDate);
		if(sdate.equals(dDate)){
			return true;
		}
		return false;
	}
}

