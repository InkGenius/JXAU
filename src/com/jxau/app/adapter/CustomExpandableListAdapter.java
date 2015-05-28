/**
 * 
 */
package com.jxau.app.adapter;

import java.util.List;
import java.util.Map;

import com.jxau.app.bean.PhoneListViewHolder;
import com.zero.jxauapp.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Title: CustomExpandableListAdapter Description:
 * @author DaiS
 * @version 1.0
 * @date 2014-1-2
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> listDataHeader;
	private Map<String, List<String>> listDataChild;
	private String number;

	public CustomExpandableListAdapter(Context ctx,
			List<String> listDataHeader, Map<String, List<String>> listDataChild) {
		super();
		this.context = ctx;
		this.listDataHeader = listDataHeader;
		this.listDataChild = listDataChild;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this.listDataChild.get(this.listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
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

		holder.pButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("tel:" + number);
				Intent intent = new Intent(Intent.ACTION_DIAL, uri);
				context.startActivity(intent);
			}
		});
		final String childText = (String) getChild(groupPosition, childPosition);
		int index = childText.indexOf(":");
		String name = childText.substring(0, index + 1);
		number = childText.substring(index + 1, childText.length());
		holder.name.setText(name);
		holder.number.setText(number);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.listDataChild.get(listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String groupText = this.listDataHeader.get(groupPosition);
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.phone_list_group,
					null);
		}

		TextView groupTextView = (TextView) convertView
				.findViewById(R.id.ListHeader);
		groupTextView.setTypeface(null, Typeface.BOLD);
		groupTextView.setText(groupText);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}