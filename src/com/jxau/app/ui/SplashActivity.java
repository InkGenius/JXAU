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
 * ����ӭ����
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
		// ����Ϊȫ��ģʽ
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_appstart);
		mAppContext = (AppContext) getApplicationContext();
		
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this.getApplicationContext());
		
		welcome = (RelativeLayout) findViewById(R.id.welcome); //ָ��������textView��ID
		
		alphaAnimation = AnimationUtils.loadAnimation(SplashActivity.this,
				R.anim.myanim);// ָ������Ч��
		
		alphaAnimation.setFillEnabled(true); // ����Fill����
		alphaAnimation.setFillAfter(true); // ���ö��������һ֡�Ǳ�����View����
		
		welcome.setAnimation(alphaAnimation);// ΪRelativeLayoutʵ��ָ������Ч��
		alphaAnimation.setAnimationListener(SplashActivity.this); // Ϊ�������ü���
		textView = (TextView) findViewById(R.id.welcome_textView);
		textView.setText("��������...");
		mAppContext.mApiClient.readPointListInXML();
		mAppContext.mApiClient.readPointMapInXML();
		mAppContext.mApiClient.downloadPictureUrls();

		if (mAppContext.mApiClient.isFristStart()) {
			if (mAppContext.mApiClient.checkDataBase()) {
				mAppContext.mApiClient.deleteDataBase();
				System.out.println("ɾ�����ݿ�ɹ���");
			}
			try {
				mAppContext.mApiClient.createDatabase();
				System.out.println("���ݿⴴ���ɹ���");
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
	 * ��������ʱ������ӭ���沢ת�������������
	 */
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		startActivity(new Intent(SplashActivity.this, Main.class));
		// ������رյ�ǰ�Ļ���ֺö��ҳ��
		finish();
	}

	/**
	 * �ڻ�ӭ��������back��
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
		MobclickAgent.onPageStart("SplashScreen"); // ͳ��ҳ��
		MobclickAgent.onResume(this); // ͳ��ʱ��
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SplashScreen"); // ��֤ onPageEnd ��onPause
													// ֮ǰ����,��Ϊ onPause �лᱣ����Ϣ
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}