/**
 * 
 */
package com.jxau.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Title: s Description:
 * 
 * @author DaiS
 * @version 1.0
 * @date 2014-1-2
 */

public class ParentListView extends ListView {

	public ParentListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}

	public ParentListView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

		// TODO Auto-generated constructor stub

	}

	public ParentListView(Context context, AttributeSet attrs) {

		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// 将 onInterceptTouchEvent的返回值设置为false，取消其对触摸事件的处理，将事件分发给子view
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return false;
	}
}