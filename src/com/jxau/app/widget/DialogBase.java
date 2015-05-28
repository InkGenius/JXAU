package com.jxau.app.widget;

import com.zero.jxauapp.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
/**
 * ×Ô¶¨ÒåDialog
 */
public class DialogBase extends Dialog {

	Context context;
	public TextView dialog_title;
	public TextView dialog_content;
	public TextView dialog_btn_confirm;
	public TextView dialog_btn_cancel;

	String title;
	String content;
	String confirm;
	View.OnClickListener mListener;
	
	public DialogBase(Context mContext, String title, String content,String confirm) {
		super(mContext, R.style.mDialog);
		// TODO Auto-generated constructor stub
		this.context = mContext;
		this.title = title;
		this.content = content;
		this.confirm = confirm;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_base);

		Window window = getWindow();
		LayoutParams lp = window.getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		window.getWindowManager().getDefaultDisplay().getMetrics(dm);
		lp.width = dm.widthPixels * 4 / 5;
		window.setAttributes(lp);
		
		dialog_title = (TextView) findViewById(R.id.dialog_title);
		dialog_content = (TextView) findViewById(R.id.dialog_content);
		dialog_btn_confirm = (TextView) findViewById(R.id.dialog_btn_confirm);
		dialog_btn_cancel = (TextView) findViewById(R.id.dialog_btn_cancel);

		dialog_title.setText(title);
		dialog_content.setText(content);
		dialog_btn_confirm.setText(confirm);
		dialog_btn_confirm.setOnClickListener(mListener);
		
		dialog_btn_cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	
	public void setOnConfirmListener(View.OnClickListener mListener) {
		// TODO Auto-generated method stub);
		this.mListener=mListener;
	}
}