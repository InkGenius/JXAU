package com.jxau.app.common;

import java.util.ArrayList;
import java.util.List;

import com.jxau.app.bean.BusInfo;
import com.jxau.app.bean.GoOutBean;
import com.jxau.app.bean.SubBusInfo;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class GoOut {

	public List<BusInfo> getBusInfo(GoOutBean goOutInfo) {
		int dire = 0;

		if (goOutInfo.isDirection())
			dire = 1;
		else
			dire = 2;

		String location = goOutInfo.getLocation();
		List<BusInfo> list = new ArrayList<BusInfo>();
		LineHelp lineHelp = new LineHelp();
		for (int i = 0; i < goOutInfo.getListLine().size(); i++) {
			String line = goOutInfo.getListLine().get(i);
			List<String> listRealName = lineHelp.getCurrentLine(line);
			for (int j = 0; j < listRealName.size(); j++) {
				getBus(listRealName.get(j), location, dire, list);
			}
		}

		return list;
	}

	private boolean getBus(String lineName, String location, int dire,
			List<BusInfo> list) {
		// TODO Auto-generated method stub
		int realDire;
		if (dire == 1)
			realDire = 2;
		else
			realDire = 1;
		String url3 = "http://mybus.jx139.com/LineDetailQuery?lineId="
				+ lineName + "&direction=" + realDire + "&";
		try {
			String info3 = HtmlDeal.GetContentFromUrl(url3);
			if (info3 != "") {
				String mainInfo = HtmlDeal.getDivContentByJsoup(info3);
				if (mainInfo != "") {
					List<SubBusInfo> subBusInfo = HtmlDeal.SolveCase(mainInfo,
							location);
					if (subBusInfo.size() != 0) {
						BusInfo busInfo = new BusInfo();

						busInfo.setCurrentLine(lineName);
						busInfo.setListSubBusInfo(subBusInfo);
						if (dire == 1)
							busInfo.setDire(true);
						else
							busInfo.setDire(false);

						list.add(busInfo);
						return true;
					}
				}
			}
		} catch (NullPointerException e) {

		}
		return false;
	}
}