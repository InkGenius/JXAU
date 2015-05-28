package com.jxau.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.jxau.app.adapter.RadioButonListAdapter;
import com.jxau.app.api.AppContext;
import com.jxau.app.bean.BusRequestBean;
import com.jxau.app.bean.RadioListViewHolder;
import com.jxau.app.widget.DialogBase;
import com.jxau.app.widget.ViewTitle;
import com.umeng.analytics.MobclickAgent;
import com.zero.jxauapp.R;

/**
 * @author DaiS
 * @date 2013-12-20
 */

public class Main extends Activity implements OnClickListener {
	
	private AppContext mAppContext;
	private ImageView mapImageBtn;
	private ImageView gooutImageBtn;
	private ImageView phoneImageBtn;
	private ImageView campusLandscapeBtn;
	private ImageView newsImageBtn;
	/* popupWindows 对话框中的控件 */
	private PopupWindow mPopupWindow;
	private Button arrivelBtn;
	private Button setOffBtn;
	private ListView radioButtonListView;
	private View popupView;
	private ViewTitle mViewTitle;
	private RelativeLayout widget_title;
	private DialogBase mDialogBase;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mAppContext = (AppContext) getApplicationContext();
		widget_title = (RelativeLayout) findViewById(R.id.widget_title);
		mViewTitle = new ViewTitle(this, null, "农大生活", "关于");
		mViewTitle.setOnRightListener(new OnClickListener() {
			public void onClick(View v) {
			 	// TODO Auto-generated method stub
			 	startActivity(new Intent(Main.this, AboutActivity.class));
		    }
		});  
		     
		widget_title.addView(mViewTitle);
		popupView = getLayoutInflater().inflate(R.layout.main_bus_select, null);
		initWidge();    
	}	                
     
	/**
	 * 初始哈主菜单并为组件添加监听
	 */
	public void initWidge() {
     
		mapImageBtn = (ImageView) findViewById(R.id.map_imageBtn);
		gooutImageBtn = (ImageView) findViewById(R.id.goout_imageBtn);
		phoneImageBtn = (ImageView) findViewById(R.id.phone_imageBtn);
		campusLandscapeBtn = (ImageView) findViewById(R.id.campus_imageBtn);
		newsImageBtn = (ImageView) findViewById(R.id.news_and_noticle_imageBtn);
		
		radioButtonListView = (ListView) popupView
				.findViewById(R.id.bus_line_listView);

		arrivelBtn = (Button) popupView.findViewById(R.id.arrive_btn);
		setOffBtn = (Button) popupView.findViewById(R.id.set_off_btn);

		mapImageBtn.setOnClickListener(this);
		phoneImageBtn.setOnClickListener(this);
		gooutImageBtn.setOnClickListener(this);
		campusLandscapeBtn.setOnClickListener(this);
		newsImageBtn.setOnClickListener(this);

		arrivelBtn.setOnClickListener(new PopMenuBtnListener());
		setOffBtn.setOnClickListener(new PopMenuBtnListener());

	}

	/**
	 * 出行服务弹出菜单
	 */
	public void runPopWindow() {
	    WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);  
	    int width = wm.getDefaultDisplay().getWidth();//屏幕宽度  
	    int height = wm.getDefaultDisplay().getHeight();  
		mPopupWindow = new PopupWindow(popupView, width/2+100, height/3, true);
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		mPopupWindow.update();
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));

		RadioButonListAdapter radioButtonAdapter = new RadioButonListAdapter(
				this, mAppContext.lineList);
		radioButtonListView.setAdapter(radioButtonAdapter);

		radioButtonListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {

				for (int i = 0, j = radioButtonListView.getCount(); i < j; i++) {
					View child = radioButtonListView.getChildAt(i);
					RadioButton rdoBtn = (RadioButton) child
							.findViewById(R.id.radio_btn);
					if (i != position) {
						rdoBtn.setChecked(false);
					} else {
						rdoBtn.setChecked(true);
					}
				}
				RadioListViewHolder holder = (RadioListViewHolder) view
						.getTag();
				holder.rb.toggle();
			}
		});

		int y = gooutImageBtn.getBottom() + 50;
		int x = getWindowManager().getDefaultDisplay().getWidth() / 5;
		mPopupWindow
				.showAtLocation(popupView, Gravity.LEFT | Gravity.TOP, x, y);
	}

	class PopMenuBtnListener implements OnClickListener {

		String line;
		boolean dir;// 方向

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int ItemId = v.getId();
			
			for (int i = 0, j = radioButtonListView.getCount(); i < j; i++) {

				View child = radioButtonListView.getChildAt(i);
				RadioButton rdoBtn = (RadioButton) child
						.findViewById(R.id.radio_btn);
				
				if (rdoBtn.isChecked())
					line = mAppContext.lineList.get(i);
			}

			if (ItemId == R.id.set_off_btn) {
				dir = true;
			} else {
				dir = false;
			}
			
			if (line == null || line.length() == 0) {
				Toast.makeText(getApplicationContext(), "请选择公交线路！",
						Toast.LENGTH_SHORT).show();
				return;
			}

			BusRequestBean info = new BusRequestBean(line, dir);
			Bundle busBundle = new Bundle();
			busBundle.putSerializable("BUSTRACKINFO", info);
			Intent intent = new Intent();
			intent.putExtras(busBundle);
			intent.setClass(Main.this, Bus.class);
			startActivity(intent);
			mPopupWindow.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int ItemId = v.getId();
		switch (ItemId) {
		case R.id.map_imageBtn:
			startActivity(new Intent().setClass(Main.this, MapActivity.class));
			break;
		case R.id.goout_imageBtn:
			runPopWindow();
			break;
		case R.id.phone_imageBtn:
			// 切换到常用电话页面
			startActivity(new Intent().setClass(Main.this, PhoneNumber.class));
			break;
		case R.id.campus_imageBtn:
			startActivity(new Intent().setClass(Main.this, LandScape.class));
			break;
		case R.id.news_and_noticle_imageBtn:
			startActivity(new Intent().setClass(Main.this, News.class));
		default:
			break;
		}
	}

	/**
	 * 菜单按钮点击事件，通过点击ActionBar的Home图标按钮来打开滑动菜单
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mDialogBase = new DialogBase(this, "提示", "您要退出吗？", "确认");
			mDialogBase.setOnConfirmListener(new View.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
					mDialogBase.dismiss();
				}
			});
			mDialogBase.show();
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		// 点击返回键关闭滑动菜单
		super.onBackPressed();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SplashScreen"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SplashScreen"); // 保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}
}
