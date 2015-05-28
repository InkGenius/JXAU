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
 * 关于界面
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
		textView_title.setText("关于");
		textView_title_back.setText("返回");
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		btn_right.setVisibility(View.GONE);
		
		TextView text = (TextView) findViewById(R.id.text);
		text.setText(Html.fromHtml("" +
			"农大生活是由软件学院零点工作室戴松、邹金勇出品，为了方便同学们的校园生活而开发的一款Android应用。" +
			"<h3><font color=\"#FF3300\">功能介绍</font></h3>" +
			"本软件包含了农大地图、公交出行、常用电话、新闻通告和校园景观5个模块。你可以在本页面下方中进行软件更新和意见反馈。<br>"+
			"1、农大地图：在百度地图的基础上进行开发，添加了多个校园地标，您可在地图上方的搜索栏中进行搜索。另外还有线路规划功能，您可以自己输入起点和终点，当然也可以通过自动定位确定起点或者通过屏幕取点确定终点。<br>" +
			"2、公交出行：提供了240和704两条线路的公交追踪，实时查看公交位置。<br>"+
			"3、常用电话：提供了学习组织、院系、外卖和快递四种号码类型供您查阅拨打。<br>"+
			"4、新闻通过：从农大官网获取数据，您可以在手机上查看学校的新闻通告。<br>"+
			"5、校园景观：以照片墙的形式展现农大校园景观。<br>"+
			"<h3><font color=\"#FF3300\">常见问题及解答</font></h3>" +
			"<h5><font color=\"#FF3300\">1、地图：</font></h5>" +
			"在搜索框搜索时，如果没有自动匹配的提示出现，则说明我们没有录入该地名或者搜索的是地点别名。 <br>" +
			"<h5><font color=\"#FF3300\">2、公交出行：</font></h5>" +
			"我们提供了三个位置，分别是农大生活区、财大和下罗，出发按钮表示从位置出发前往市区，到达按钮表示从市区方向回到该位置。查询过程中数据获取较慢，请耐心等待。" +
			"超过公交运营时间或者网络不通则没有公交信息，请保证网络开启。 <br>" +
			"<h3><font color=\"#FF3300\">其他相关</font></h3>" +
			"校园景观中展示的图片是从互联网上获取，无渠道联系作者。如您对其中出现的您的作品不满，请您在意见反馈中提示我们。 <br>" +
			"<h5><font color=\"#FF3300\">数据统计</font></h5>" +
			"1、本软件的数据均来源于网络<br>" +
			"2、每次打开软件默认自动同步一次<br>" +
			"3、每次同步最新数据大约会消耗130KB的流量<br>" +
			"4、校园景观图片优先加载本地缓存，缓存路径:SD卡/Android/JxauApp/Pictures<br>" +
			"6、开启GPS定位可以更精确地定位您的位置<br><br>"
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
				            		mAppContext.ToastMessage(AboutActivity.this,"当前版本V" + 
				            				getPackageManager().getPackageInfo("com.zero.jxauapp", 0).versionName + "，已经是最新版");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				                break;
				            case 2: // none wifi
				            	mAppContext.ToastMessage(AboutActivity.this, "没有wifi连接， 只在wifi下更新");
				                break;
				            case 3: // time out
				            	mAppContext.ToastMessage(AboutActivity.this, "超时");
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
	    MobclickAgent.onPageStart("SplashScreen"); //统计页面
	    MobclickAgent.onResume(this);          //统计时长
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("SplashScreen"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
	    MobclickAgent.onPause(this);
	}
}
