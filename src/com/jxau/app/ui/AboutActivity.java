package com.jxau.app.ui;

import com.jxau.app.api.AppContext;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.zero.jxauapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * ���ڽ���
 **/
public class AboutActivity extends Activity{
	AppContext mAppContext;
	
	TextView textView_title_back;
	TextView textView_title;
	TextView textView_title_right;
	
	LinearLayout btn_back;
	LinearLayout btn_right;
	
	LinearLayout btn_version;
	LinearLayout btn_feedback;
	
	TextView textView_version;
	ImageView imageView_update;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        mAppContext = (AppContext) getApplicationContext();
		textView_title_back = (TextView) findViewById(R.id.textview_widget_title_back);
		textView_title = (TextView) findViewById(R.id.textview_widget_title);
		textView_title_right = (TextView) findViewById(R.id.textview_widget_title_right);
		btn_back = (LinearLayout) findViewById(R.id.btn_widget_title_back);
		btn_right = (LinearLayout) findViewById(R.id.btn_widget_title_right);
		textView_title.setText("����");
		textView_title_back.setText("����");
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		btn_right.setVisibility(View.GONE);
		
		TextView text = (TextView) findViewById(R.id.text);
		text.setText(Html.fromHtml("" +
			"ũ�������������ѧԺ��㹤���Ҵ��ɡ��޽��³�Ʒ��Ϊ�˷���ͬѧ�ǵ�У԰�����������һ��AndroidӦ�á�" +
			"<h3><font color=\"#FF3300\">���ܽ���</font></h3>" +
			"�����������ũ���ͼ���������С����õ绰������ͨ���У԰����5��ģ�顣������ڱ�ҳ���·��н���������º����������<br>"+
			"1��ũ���ͼ���ڰٶȵ�ͼ�Ļ����Ͻ��п���������˶��У԰�ر꣬�����ڵ�ͼ�Ϸ����������н������������⻹����·�滮���ܣ��������Լ����������յ㣬��ȻҲ����ͨ���Զ���λȷ��������ͨ����Ļȡ��ȷ���յ㡣<br>" +
			"2���������У��ṩ��240��704������·�Ĺ���׷�٣�ʵʱ�鿴����λ�á�<br>"+
			"3�����õ绰���ṩ��ѧϰ��֯��Ժϵ�������Ϳ�����ֺ������͹������Ĳ���<br>"+
			"4������ͨ������ũ�������ȡ���ݣ����������ֻ��ϲ鿴ѧУ������ͨ�档<br>"+
			"5��У԰���ۣ�����Ƭǽ����ʽչ��ũ��У԰���ۡ�<br>"+
			"<h3><font color=\"#FF3300\">�������⼰���</font></h3>" +
			"<h5><font color=\"#FF3300\">1����ͼ��</font></h5>" +
			"������������ʱ�����û���Զ�ƥ�����ʾ���֣���˵������û��¼��õ��������������ǵص������ <br>" +
			"<h5><font color=\"#FF3300\">2���������У�</font></h5>" +
			"�����ṩ������λ�ã��ֱ���ũ�����������ƴ�����ޣ�������ť��ʾ��λ�ó���ǰ�����������ﰴť��ʾ����������ص���λ�á���ѯ���������ݻ�ȡ�����������ĵȴ���" +
			"����������Ӫʱ��������粻ͨ��û�й�����Ϣ���뱣֤���翪���� <br>" +
			"<h3><font color=\"#FF3300\">�������</font></h3>" +
			"У԰������չʾ��ͼƬ�Ǵӻ������ϻ�ȡ����������ϵ���ߡ����������г��ֵ�������Ʒ�����������������������ʾ���ǡ� <br>" +
			"<h5><font color=\"#FF3300\">����ͳ��</font></h5>" +
			"1������������ݾ���Դ������<br>" +
			"2��ÿ�δ����Ĭ���Զ�ͬ��һ��<br>" +
			"3��ÿ��ͬ���������ݴ�Լ������130KB������<br>" +
			"4��У԰����ͼƬ���ȼ��ر��ػ��棬����·��:SD��/Android/JxauApp/Pictures<br>" +
			"6������GPS��λ���Ը���ȷ�ض�λ����λ��<br><br>"
			));
		
		btn_version = (LinearLayout) findViewById(R.id.btn_setting_version);
		btn_feedback = (LinearLayout) findViewById(R.id.btn_setting_feedback);
		
		btn_version.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mAppContext.showLoading(AboutActivity.this);
				btn_version.setClickable(false);
				
				UmengUpdateAgent.forceUpdate(AboutActivity.this);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				        @Override
				        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
				        	mAppContext.hideLoading();
							btn_version.setClickable(true);
				            switch (updateStatus) {
				            case 0: // has update
				                UmengUpdateAgent.showUpdateDialog(AboutActivity.this, updateInfo);
				                break;
				            case 1: // has no update
				            	try {
				            		mAppContext.ToastMessage(AboutActivity.this,"��ǰ�汾V" + 
				            				getPackageManager().getPackageInfo("com.zero.jxauapp", 0).versionName + "���Ѿ������°�");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				                break;
				            case 2: // none wifi
				            	mAppContext.ToastMessage(AboutActivity.this, "û��wifi���ӣ� ֻ��wifi�¸���");
				                break;
				            case 3: // time out
				            	mAppContext.ToastMessage(AboutActivity.this, "��ʱ");
				                break;
				            }
				        }
				});
			}
		});
		btn_feedback.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FeedbackAgent agent = new FeedbackAgent(AboutActivity.this);
			    agent.startFeedbackActivity();
			}
		});
		textView_version = (TextView) findViewById(R.id.textView_setting_version);
		textView_version.setText("");
		imageView_update = (ImageView) findViewById(R.id.imageview_setting_update);
    }
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("SplashScreen"); //ͳ��ҳ��
	    MobclickAgent.onResume(this);          //ͳ��ʱ��
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("SplashScreen"); // ��֤ onPageEnd ��onPause ֮ǰ����,��Ϊ onPause �лᱣ����Ϣ 
	    MobclickAgent.onPause(this);
	}
}
