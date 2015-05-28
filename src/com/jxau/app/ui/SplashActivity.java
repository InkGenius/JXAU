package com.jxau.app.ui;

import java.io.IOException;

import com.jxau.app.api.AppContext;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.zero.jxauapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 程序欢迎界面
 **/

public class SplashActivity extends Activity implements AnimationListener {
	
	private AppContext mAppContext;
	private TextView textView;
	private RelativeLayout welcome;
	private Animation alphaAnimation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// 设置为全屏模式
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_appstart);
		mAppContext = (AppContext) getApplicationContext();
		
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this.getApplicationContext());
		
		welcome = (RelativeLayout) findViewById(R.id.welcome); //指定动画的textView的ID
		
		alphaAnimation = AnimationUtils.loadAnimation(SplashActivity.this,
				R.anim.myanim);// 指定动画效果
		
		alphaAnimation.setFillEnabled(true); // 启动Fill保持
		alphaAnimation.setFillAfter(true); // 设置动画的最后一帧是保持在View上面
		
		welcome.setAnimation(alphaAnimation);// 为RelativeLayout实现指定动画效果
		alphaAnimation.setAnimationListener(SplashActivity.this); // 为动画设置监听
		textView = (TextView) findViewById(R.id.welcome_textView);
		textView.setText("加载数据...");
		mAppContext.mApiClient.readPointListInXML();
		mAppContext.mApiClient.readPointMapInXML();
		mAppContext.mApiClient.downloadPictureUrls();

		if (mAppContext.mApiClient.isFristStart()) {
			if (mAppContext.mApiClient.checkDataBase()) {
				mAppContext.mApiClient.deleteDataBase();
				System.out.println("删除数据库成功！");
			}
			try {
				mAppContext.mApiClient.createDatabase();
				System.out.println("数据库创建成功！");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
	}

	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * 动画结束时结束欢迎界面并转到软件的主界面
	 */
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		startActivity(new Intent(SplashActivity.this, Main.class));
		// 如果不关闭当前的会出现好多个页面
		finish();
	}

	/**
	 * 在欢迎界面屏蔽back键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}