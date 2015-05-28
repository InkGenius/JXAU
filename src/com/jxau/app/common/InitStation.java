package com.jxau.app.common;

import java.util.ArrayList;
import java.util.List;


import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

class InitStation extends AsyncTask<Void, Void, List<String>> {
	private String station;
	private Handler handler;

	InitStation(String station, Handler handler) {
		this.station = station;
		this.handler = handler;
	}

	@Override
	protected List<String> doInBackground(Void... params) {
		final String url = "http://mybus.jx139.com/StationLineQuery?station="
				+ station + "&";
		String info = HtmlDeal.GetContentFromUrl(url);
		String mainInfo = HtmlDeal.getDivContentByJsoup_station(info);
		List<String> list = SolveCase(mainInfo);
		if (!list.isEmpty()){
			// �Ƴ���ͬ���ִ�
			removeDuplicate(list);
		}
		// �õ�ҳ����
		int pages = HtmlDeal.getPages(info);
		for(int i=2; i <= pages; i++)
		{
			String url2 = "http://mybus.jx139.com/StationLineQuery?station="
					+ station + "&page="+i+"&";		
			String info2 = HtmlDeal.GetContentFromUrl(url2);
			String mainInfo2 = HtmlDeal.getDivContentByJsoup_station(info2);
			list.addAll(SolveCase(mainInfo2));
			removeDuplicate(list);
		}
		return list;
	}

	protected  List<String> removeDuplicate(List<String> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).equals(list.get(i))) {
					list.remove(j);
				}
			}
		}
		return list;
	}

	protected List<String> SolveCase(String info) {
		if (info == null)
			return null;
		List<String> list = new ArrayList<String>();
		char data[] = info.toCharArray();
		String line = "";
		for (int i = 0; i < data.length; i++) {
			if (data[i] == '.') {
				int j = i + 1;

				// �õ���·
				while (!HtmlDeal.isChinese(data[j])) {
					if (data[j] >= '0' && data[j] <= '9')
						line += data[j];
					j++;
				}
				if (line != null)
					list.add(line);
			}
			line = "";
		}
		return list;
	}

	protected void onPostExecute(List<String> result) {
		super.onPostExecute(result);
		// deal message
		Message msg = new Message();
		msg.obj = result;
		handler.sendMessage(msg);
	}
}
