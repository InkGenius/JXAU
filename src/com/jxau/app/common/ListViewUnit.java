/**
 * 
 */
package com.jxau.app.common;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Title: BusUnit Description:
 * 
 * @author DaiS
 * @version 1.0
 * @date 2014-1-18
 */

public class ListViewUnit {

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) { 
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); 
			totalHeight += listItem.getMeasuredHeight(); 
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

}
