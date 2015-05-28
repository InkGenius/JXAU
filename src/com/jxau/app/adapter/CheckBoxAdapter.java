/**
 * 
 */
package com.jxau.app.adapter;

import java.util.HashMap;
import java.util.List;

import com.jxau.app.bean.ViewHolder;
import com.zero.jxauapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Title: CheckBoxAdapter Description:
 * @author DaiS
 * @date 2014-1-1
 */
public class CheckBoxAdapter extends BaseAdapter {

	public static HashMap<Integer, Boolean> isSelected;
	private Context context = null;
	private LayoutInflater inflater = null;
	private List<HashMap<String, Object>> list = null;
	private String keyString[] = null;
	private String itemString = null; 
	private int idValue[] = null;

	public CheckBoxAdapter(Context context, List<HashMap<String, Object>> list,
			int resource, String[] from, int[] to) {
		this.context = context;
		this.list = list;
		keyString = new String[from.length];
		idValue = new int[to.length];
		System.arraycopy(from, 0, keyString, 0, from.length);
		System.arraycopy(to, 0, idValue, 0, to.length);
		inflater = LayoutInflater.from(context);
		init();
	}

	// 初始化 设置所有checkbox都为未选择
	public void init() {
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			isSelected.put(i, false);
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;

	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder = null;
//		if (holder == null) {
			holder = new ViewHolder();
			if (view == null) {
				view = inflater.inflate(R.layout.bus_checkbox_list_item,
						null);
				holder.tv = (TextView) view
						.findViewById(R.id.checkbox_list_textView_item);
				holder.cb = (CheckBox) view
						.findViewById(R.id.checkbox_list_checkbox_item);
				view.setTag(holder);
			}else {
				holder = (ViewHolder) view.getTag();
			}
//		} 

		HashMap<String, Object> map = list.get(position);
		if (map != null) {
			itemString = (String) map.get(keyString[0]);
			holder.tv.setText(itemString);
		}
		holder.cb.setChecked(isSelected.get(position));
		return view;
	}
}