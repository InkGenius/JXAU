package com.jxau.app.common;

import java.util.HashMap;
import java.util.Map;
import com.jxau.app.bean.LineBean;

/*
 * 通过InitLineInfo信息类拿站点名称
 * 返回类型为List<String>, 第一个信息为短班，第二个为长班。
 * 如果线路不区分长短班，则只有一个站点信息
 */

public class InitLine{

	
	public static Map<Integer, String> getAllStation(LineBean lineInfo)
	{
		Map<Integer, String> map = new HashMap<Integer, String>();
			int direction;
			if(lineInfo.isStatus())
				direction=2;
			else direction=1;
			String URL = "http://mybus.jx139.com/LineDetailQuery?lineId="
						+lineInfo.getLine()+"&direction="+direction+"&";
			String info = HtmlDeal.GetContentFromUrl(URL);
			if(info!=""){ 
				String mainInfo = HtmlDeal.getDivContentByJsoup(info);
				if(mainInfo!="")
				{
					InfoDeal(mainInfo,map);
				}
			}
						
		return map;
	}
	private static void InfoDeal(String mainInfo, Map<Integer, String> map) {
		try
		{
		String str1="";
		char data[] = mainInfo.toCharArray();
		for (int i = 0; i < data.length; i++) {
			if (HtmlDeal.isChinese(data[i]) || Character.isDigit(data[i])) {
				str1 += data[i];
			} else if (data[i] == '>') {
				AddBus(str1, map);
				str1 = "";
			}
		}
		}catch(Exception e){}
	}
	private static void AddBus(String str, Map<Integer, String> map) {
		try
		{
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
				if (HtmlDeal.isChinese(str.charAt(i)))
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
			map.put(num, station);
		} else {
			/* 将当前线路所经过的所有站点信息添加到map, 以便做过滤 */
			String station = "", stationNum = "";
			char ch[] = str.toCharArray();
			for (int i = 0; i < ch.length; i++) {
				if (ch[i] >= '0' && ch[i] <= '9')
					stationNum += ch[i];
				else if (HtmlDeal.isChinese(ch[i]))
					station += ch[i];
			}
			int num = Integer.parseInt(stationNum);
			map.put(num, station);
		}
		}catch(Exception e){}
	}
}


