package com.jxau.app.widget;

import com.zero.jxauapp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListHeaderView extends LinearLayout {

	private static final String TAG = "ListHeaderView";
	private Context context;
	private TextView textView;
	
	public ListHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public ListHeaderView(Context context) {
		super(context);
		initialize(context);
	}

	private void initialize(Context context) {
		this.context = context;
		View view = LayoutInflater.from(this.context).inflate(
				R.layout.news_list_item_header_view, null);
		textView = (TextView) view.findViewById(R.id.headerTextView);
		addView(view);
	}

	public void setTextView(String text) {
		textView.setText(text);
	}
}