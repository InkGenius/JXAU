package com.jxau.app.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.jxau.app.bean.BusRequestBean;
import com.jxau.app.bean.BusTrackDetail;
import com.jxau.app.bean.BusTrackResult;

public class BusTrackUnit {

	static List<BusTrackResult> list;
	
	public static List<BusTrackResult> getBusInfo(BusRequestBean request) {
		
		list = new ArrayList<BusTrackResult>();
		
		int dire = 0;
		if (request.isDirection())
			dire = 1;
		else
			dire = 2;
		
		if (request.getLine().equals("240")) {
			getBus("1240", dire);
			getBus("2240", dire);
		} else {
			getBus(request.getLine(), dire);
		}
		//Log.d("最终返回结果，List是否有效：",list.size()+"辆");
		return list;
	}

	private static void getBus(String lineName, int dire) {
		// TODO Auto-generated method stub
		int realDire;
		
		if (dire == 1)
			realDire = 2;
		else
			realDire = 1;
		
		String url3 = "http://mybus.jx139.com/LineDetailQuery?lineId="
				+ lineName + "&direction=" + realDire + "&";
		
		try {
			String info3 = GetContentFromUrl(url3);
			
			if (info3 != "") {
				
				String mainInfo = getDivContentByJsoup(info3);
				
				if (mainInfo != "") {
					
					List<BusTrackDetail> subBusInfo = SolveCase(mainInfo);
					
					if (subBusInfo.size() != 0) {
						list.add(new BusTrackResult(subBusInfo, lineName));
//						for(BusTrackResult b:list){
//							Log.d("case", list.size()+"");
//						}
					}
				}
				}
			} catch (NullPointerException e) {
		}
	}
	
	public static String GetContentFromUrl(String url) {
		
		String result = "";
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest req = new HttpGet(url);
			HttpResponse resp = client.execute(req);
			HttpEntity ent = resp.getEntity();

			int status = resp.getStatusLine().getStatusCode();
			
			if (status == HttpStatus.SC_OK) {
				result = EntityUtils.toString(ent);
			}
			client.getConnectionManager().shutdown();
//			Log.d("DefaultHttpClient返回结果", result);
			return result;
		} catch (Exception e) {
			return "";	
		}
	}

	public static String getDivContentByJsoup(String content) {
		Document doc = Jsoup.parse(content);
		Elements div2 = doc.getElementsByClass("apps_main");
		String s = null;
		for (Element e : div2) {
			s = e.getElementsByClass("list-bus-station").text();
		}
//		Log.d("Jsoup返回结果", s);
		return s;
	}

	public static List<BusTrackDetail> SolveCase(String s) {
		
		String str1 = "";
		
		if (s != "" && s != null) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			
			List<BusTrackDetail> list = new ArrayList<BusTrackDetail>();
			
			char data[] = s.toCharArray();
			
			for (int i = 0; i < data.length; i++) {
				if (isChinese(data[i]) || Character.isDigit(data[i])) {
					
					str1 += data[i];
					
				} else if (data[i] == '>') {
					
					addBus(str1, map, list);
					
					str1 = "";
				}
			}
//			for(BusTrackDetail b:list){
//				Log.d("SolveCase返回结果", "站点："+b.getStation()+" 是否到达："+b.getArrived()+" 几辆车："+b.getNumber()+" ");
//			}
			return list;
		} else
			return null;
	}

	/*	
	 * 处理逻辑： 无论当前站点是否有车辆到来，都将其前部的编号和名称拆解，添加到map， 如果当前 站点有车辆到来，那么就将其添加到List
	 */	
	public static void addBus(String str, Map<String, Integer> map,
			List<BusTrackDetail> list) {
		
		if (str == "" || str == null)
			return;
		
		if (str.indexOf("开往") >= 0 || str.indexOf("到达") >= 0) {
			
			String station = "", stationNumber = "";
			
			String count = "";							// 开往或者到达的车辆数量
			
			boolean flag = false;
			// 得到站台的编号
			while (true) {
				if (Character.isDigit(str.charAt(0)))
					stationNumber += str.charAt(0);
				else
					break;
				str = str.substring(1);
			}
			// 得到站台的名称
			int i = 0;
			while (true) {
				if (isChinese(str.charAt(i)))
					station += str.charAt(i);
				else
					break;
				i++;
			}
			// 得到前往或者到达当前站台的车辆数量
			while (Character.isDigit(str.charAt(i))) {
				count += str.charAt(i);
				i++;
			}
			
			if (str.indexOf("到达") >= 0)
				flag = true;
			
			int num = Integer.parseInt(stationNumber);
			map.put(station, num);
			int c = Integer.parseInt(count);

			BusTrackDetail info = new BusTrackDetail();
			info.setStation(station);
			info.setNumber(c);

			if (flag)
				info.setArrived("前往");
			else
				info.setArrived("到达");

			list.add(info);
			
		} else {
			/*将当前线路所经过的所有站点信息添加到map, 以便做过滤 */
			String station = "", stationNum = "";
			char ch[] = str.toCharArray();
			for (int i = 0; i < ch.length; i++) {
				if (ch[i] >= '0' && ch[i] <= '9')
					stationNum += ch[i];
				else if (isChinese(ch[i]))
					station += ch[i];
			}
			int num = Integer.parseInt(stationNum);
			map.put(station, num);
		}
	}
	
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}
}


