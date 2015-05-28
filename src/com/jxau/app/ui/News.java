package com.jxau.app.ui;

import com.jxau.app.adapter.NewsListAdapter;
import com.jxau.app.api.AppContext;
import com.jxau.app.bean.NewsBean;
import com.jxau.app.common.InternetDataCatcher;
import com.jxau.app.widget.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.zero.jxauapp.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class News extends Activity {

	AppContext mAppContext;
	private Button framebtn_School_News;
	private Button framebtn_School_Notice;

	private PullToRefreshListView lvNews;
	private PullToRefreshListView lvNoticle;

	private NewsListAdapter adapter1;
	private NewsListAdapter adapter2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		mAppContext = (AppContext) getApplicationContext();
		mAppContext.showLoading(News.this);
		init();
	}

	private Runnable getFirstDataRunable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			InternetDataCatcher.getNewsListDate();
			if (mAppContext.newsResultList.size() == 0) {
				mAppContext.newsResultList = InternetDataCatcher.getNewsList();
				mAppContext.noticleResultList = InternetDataCatcher
						.getNoticleList();
			}
			handler.sendEmptyMessage(0);
		}
	};
	private Runnable getDataRunable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			InternetDataCatcher.getNewsListDate();
			mAppContext.newsResultList = InternetDataCatcher.getNewsList();
			mAppContext.noticleResultList = InternetDataCatcher
					.getNoticleList();
			handler.sendEmptyMessage(0);
		}
	};

	public void init() {
		framebtn_School_News = (Button) findViewById(R.id.frame_btn_news_lastest);
		framebtn_School_Notice = (Button) findViewById(R.id.frame_btn_news_blog);

		framebtn_School_News.setEnabled(false);
		framebtn_School_News
				.setOnClickListener(frameNewsBtnClick(framebtn_School_News));
		framebtn_School_Notice
				.setOnClickListener(frameNewsBtnClick(framebtn_School_Notice));

		lvNews = (PullToRefreshListView) findViewById(R.id.frame_listview_news);
		lvNoticle = (PullToRefreshListView) findViewById(R.id.frame_listview_noticle);
		new Thread(getFirstDataRunable).start();
		setListItemOnClick();
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mAppContext.hideLoading();
				if (mAppContext.noticleResultList.size() == 0
						|| mAppContext.newsResultList.size() == 0) {
					Toast.makeText(getApplicationContext(), "请检查网络！",
							Toast.LENGTH_SHORT).show();
				}
				adapter1 = new NewsListAdapter(News.this,
						mAppContext.newsResultList);
				adapter2 = new NewsListAdapter(News.this,
						mAppContext.noticleResultList);

				lvNews.setAdapter(adapter1);
				lvNoticle.setAdapter(adapter2);
				lvNews.onRefreshComplete();
				lvNoticle.onRefreshComplete();
				break;
			}
		};
	};

	public void setListItemOnClick() {
		lvNews.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				new Thread(getDataRunable).start();
			}
		});
		lvNoticle
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						new Thread(getDataRunable).start();
					}
				});
		lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ListView lv = (ListView) parent;
				NewsBean news = (NewsBean) lv.getItemAtPosition(position);
				Intent intent = new Intent(News.this, NewsDetails.class);
				intent.putExtra("NEWS_URL", news.getUrl());
				startActivity(intent);
			}
		});
		
		lvNoticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ListView lv = (ListView) parent;
				NewsBean news = (NewsBean) lv.getItemAtPosition(position);
				Intent intent = new Intent(News.this, NewsDetails.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("NOTICLE_NEWS", news);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private OnClickListener frameNewsBtnClick(final Button btn) {
		return new OnClickListener() {
			public void onClick(View v) {
				if (btn == framebtn_School_News) {
					framebtn_School_News.setEnabled(false);
				} else {
					framebtn_School_News.setEnabled(true);
				}
				if (btn == framebtn_School_Notice) {
					framebtn_School_Notice.setEnabled(false);
				} else {
					framebtn_School_Notice.setEnabled(true);
				}

				if (btn == framebtn_School_News) {
					lvNews.setVisibility(View.VISIBLE);
					lvNoticle.setVisibility(View.GONE);

				} else {
					lvNews.setVisibility(View.GONE);
					lvNoticle.setVisibility(View.VISIBLE);
				}
			}
		};
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
