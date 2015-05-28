/**
 * 
 */
package com.jxau.app.adapter;

import java.util.HashMap;
import java.util.List;

import com.jxau.app.bean.RadioListViewHolder;
import com.zero.jxauapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Title: RadioButonListAdapter Description:
 * 
 * @author DaiS
 * @version 1.0
 * @date 2014-1-1
 */
public class RadioButonListAdapter extends BaseAdapter {

	private Context context;
	private List<String> stationList;
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();// ���ڼ�¼ÿ��RadioButton��״̬������ֻ֤��ѡһ��

	public RadioButonListAdapter(Context context, List<String> userList) {
		this.context = context;
		this.stationList = userList;
	}

	@Override
	public int getCount() {
		return stationList.size();
	}

	@Override
	public Object getItem(int position) {
		return stationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void set(int position, RadioButton radio) {
		for (String key : states.keySet()) {
			states.put(key, false);
		}
		states.put(String.valueOf(position), radio.isChecked());
		RadioButonListAdapter.this.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		RadioListViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.bus_radio_list_item, null);
			holder = new RadioListViewHolder();
			holder.background = (LinearLayout) convertView
					.findViewById(R.id.bus_track_radiolist_item);
			holder.tv = (TextView) convertView
					.findViewById(R.id.search_user_name);
			convertView.setTag(holder);
		} else {
			holder = (RadioListViewHolder) convertView.getTag();
		}

		final RadioButton radio = (RadioButton) convertView
				.findViewById(R.id.radio_btn);
		holder.rb = radio;
		holder.tv.setText(stationList.get(position));

		// ��RadioButton��ѡ��ʱ������״̬��¼��States�У�����������RadioButton��״̬ʹ���ǲ���ѡ��
		holder.rb.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// ���ã�ȷ�����ֻ��һ�ѡ��
				for (String key : states.keySet()) {
					states.put(key, false);
				}
				states.put(String.valueOf(position), radio.isChecked());
				RadioButonListAdapter.this.notifyDataSetChanged();
			}
		});

		boolean res = false;
		if (states.get(String.valueOf(position)) == null
				|| states.get(String.valueOf(position)) == false) {
			res = false;
			states.put(String.valueOf(position), false);
		} else
			res = true;

		holder.rb.setChecked(res);

		return convertView;
	}

}
