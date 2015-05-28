/**
 * 
 */
package com.jxau.app.adapter;

import java.util.List;

import com.jxau.app.bean.PhoneListViewHolder;
import com.zero.jxauapp.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Title: CustomListAdapter Description:
 * 
 * @author DaiS
 * @version 1.0
 * @date 2014-1-2
 */

public class CustomListAdapter extends BaseAdapter {

	private Context context;
	private List<String> numbers;

	public CustomListAdapter(Context context, List<String> numbers) {
		this.context = context;
		this.numbers = numbers;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return numbers.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return numbers.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	String number;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		PhoneListViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater
					.inflate(R.layout.phone_list_item, null);
			holder = new PhoneListViewHolder();
			holder.pButton = (ImageButton) convertView
					.findViewById(R.id.phone_call_imageButton);
			holder.name = (TextView) convertView
					.findViewById(R.id.phone_name_textView);
			holder.number = (TextView) convertView
					.findViewById(R.id.phone_number_textView);
			convertView.setTag(holder);
		} else {
			holder = (PhoneListViewHolder) convertView.getTag();
		}
		final String childText = (String) getItem(position);
		int index = childText.indexOf(":");
		String name = childText.substring(0, index + 1);
		number = childText.substring(index + 1, childText.length());
		holder.pButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("tel:" + number);
				Intent intent = new Intent(Intent.ACTION_DIAL, uri);
				context.startActivity(intent);
			}
		});

		holder.name.setText(name);
		holder.number.setText(number);
		return convertView;
	}
}
