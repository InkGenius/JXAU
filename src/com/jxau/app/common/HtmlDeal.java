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

import com.jxau.app.bean.SubBusInfo;

class HtmlDeal {
	
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
			return result;
		} catch (Exception e) {
			System.out.println("NetHelper" + "______________读取数据失败"
					+ e.toString() + "_____________");
			return "";
		}
	}

	public static String getDivContentByJsoup_LineDeal(String content) {
		Document doc = Jsoup.parse(content);
		Elements div2 = doc.getElementsByClass("cmode");
		String s = null;
		for (Element e : div2) {
			s = e.text();
		}
		return s;
	}

	public static String getDivContentByJsoup_Line(String content) {
		Document doc = Jsoup.parse(content);
		Elements div2 = doc.getElementsByClass("apps_main");
		String s = null;
		for (Element e : div2) {
			s = e.getElementsByTag("a").text();
		}
		return s;
	}

	public static String getDivContentByJsoup(String content) {
		Document doc = Jsoup.parse(content);
		Elements div2 = doc.getElementsByClass("apps_main");
		String s = null;
		for (Element e : div2) {
			s = e.getElementsByClass("list-bus-station").text();
		}
		return s;
	}

	public static String getDivContentByJsoup_station(String content) {
		Document doc = Jsoup.parse(content);
		Elements div2 = doc.getElementsByClass("apps_main");
		String s = null;
		for (Element e : div2) {
			s = e.getElementsByClass("list").text();
		}
		return s;
	}

	public static int getPages(String info) {
		Document doc = Jsoup.parse(info);
		Elements div = doc.getElementsByClass("apps_page");
		String s = null;
		for (Element e : div) {
			s = e.getElementsByClass("apps_page").text();
		}
		char ch[] = s.toCharArray();
		String num = "";
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == '/') {
				for (int j = i + 1; j < ch.length; j++) {
					if (ch[j] >= '0' && ch[j] <= '9') {
						num += ch[j];
					} else
						break;
				}
				break;
			}
		}
		return Integer.parseInt(num);
	}

	public static List<SubBusInfo> SolveCase(String s, String location) {
		String str1 = "";
		// 站点名称和号码
		if (s != "" && s != null) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			List<SubBusInfo> list = new ArrayList<SubBusInfo>();
			char data[] = s.toCharArray();
			for (int i = 0; i < data.length; i++) {
				if (isChinese(data[i]) || Character.isDigit(data[i])) {
					str1 += data[i];
				} else if (data[i] == '>') {
					AddBus(str1, map, list);
					str1 = "";
				}
			}

			int num = map.get(location);
			removeListElement3(list, num, map);
			return list;
		} else
			return null;
	}

	public static void removeListElement3(List<SubBusInfo> list, int num,
			Map<String, Integer> map) {
		int size;

		for (int i = 0; i < list.size(); i++) {
			SubBusInfo subBusInfo = list.get(i);
			if ((size = map.get(subBusInfo.getStation())) > num) {
				list.remove(i);
				--i;
			} else {
				int dis = num - size;
				if (dis == 0)
					dis++;
				subBusInfo.setDistance(dis);
			}
		}

	}

	/*
	 * 处理逻辑： 无论当前站点是否有车辆到来，都将其前部的编号和名称拆解，添加到map， 如果当前 站点有车辆到来，那么就将其添加到List
	 */
	public static void AddBus(String str, Map<String, Integer> map,
			List<SubBusInfo> list) {
		if (str == "" || str == null)
			return;
		if (str.indexOf("开往") >= 0 || str.indexOf("到达") >= 0) {
			String station = "", stationNumber = "";
			String count = "";// 开往或者到达的车辆数量
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

			SubBusInfo info = new SubBusInfo();
			info.setStation(station);
			info.setNumber(c);

			if (flag)
				info.setArrived("前往");
			else
				info.setArrived("到达");

			list.add(info);
		} else {
			/* 将当前线路所经过的所有站点信息添加到map, 以便做过滤 */
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
