/**
 * 
 */
package com.jxau.app.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.jxau.app.api.AppConfigue;
import com.jxau.app.api.AppContext;
import com.jxau.app.bean.NewsBean;
import com.jxau.app.bean.NewsDetailsBean;
import com.jxau.app.common.ListViewUnit;
import com.jxau.app.common.InternetDataCatcher;
import com.jxau.app.widget.AbsListViewBaseActivity;
import com.jxau.app.widget.ListHeaderView;
import com.jxau.app.widget.ViewTitle;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.zero.jxauapp.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

 /**   
 * Title: TestNewDetailsAct
 * Description:
 * @author DaiS
 * @version 1.0
 * @date 2013-12-28
 */

public class NewsDetails extends AbsListViewBaseActivity{
	AppContext mAppContext;
	DisplayImageOptions options; //配置图片加载及显示选项
	
	private TextView mTitle;
	private TextView mAuthor;
	private TextView mPubDate;
	private TextView mContext;
	private ImageView picture;
	
	private String newsUrl;
	private NewsDetailsBean newsInfo;
	private String noticleContext;
	private boolean isNews=true;
	private NewsBean noticle;
	public static ViewTitle mViewTitle;
	RelativeLayout widget_title;
	private ScrollView scrollView;
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_detail);
		//配置图片加载及显示选项
		mAppContext = (AppContext) getApplicationContext();
		mViewTitle = new ViewTitle(this, null, "新闻正文", null);
		widget_title = (RelativeLayout) findViewById(R.id.widget_title);
		widget_title.addView(mViewTitle);
		
		mAppContext.showLoading(NewsDetails.this);
		
		options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(true)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300))
		
		.showStubImage(R.drawable.ic_stub)    //在ImageView加载过程中显示图片
		.showImageForEmptyUri(R.drawable.ic_empty)  //image连接地址为空时
		.showImageOnFail(R.drawable.ic_error)  //image加载失败
		.cacheInMemory(true)  //加载图片时会在内存中加载缓存
		.cacheOnDisc(true)   //加载图片时会在磁盘中加载缓存
		//.displayer(new RoundedBitmapDisplayer(100))  //设置用户加载图片task(这里是圆角图片显示)
		.build();
		
		listView = (ListView) findViewById(android.R.id.list);
		//绑定适配器
		newsUrl = getIntent().getStringExtra("NEWS_URL");
		noticle= (NewsBean) getIntent().getSerializableExtra("NOTICLE_NEWS");  
		if(newsUrl==null){
			newsUrl=noticle.getUrl();
			isNews=false;
		}
		initView();
		new Thread(){
	          @Override  
	          public void run() {  
	              // TODO Auto-generated method stub  
	              super.run(); 
	              if(isNews){
	            	  try {
						newsInfo=InternetDataCatcher.getNewDetails(newsUrl);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	              }else{
	            	  noticleContext=InternetDataCatcher.getNoticleDetails(newsUrl);
	              }
	             
	              handler.sendEmptyMessage(0); 
	          }  
	      }.start(); 
	}
	private Handler handler = new Handler() {  
		  
		public void handleMessage(Message msg) { 
	      	  
	          switch (msg.what) {  
	          case 0:  
	        	  if(isNews){
		        	mTitle.setText(newsInfo.getTitle());
		        	mPubDate.setText(newsInfo.getDate());
		        	mAuthor.setText(newsInfo.getAuthor());
		        	if(newsInfo.getPictures().size()>0){
		        		listView.setAdapter(new ItemAdapter(newsInfo.getPictures()));
		        	}else{
		        		listView.setVisibility(View.GONE);
		        		scrollView.setVisibility(View.VISIBLE);
		        		mContext.setText(newsInfo.getContext());
		        	}
	        	  }else{
	        		  mTitle.setText(noticle.getTitle());
			          mPubDate.setText(noticle.getPubDate());
			          mAuthor.setText(noticle.getAuthor());
	        		  listView.setVisibility(View.GONE);
	        		  scrollView.setVisibility(View.VISIBLE);
	        		  mContext.setText(noticleContext);
	        	  }
	        	mAppContext.hideLoading();
	        	break;  
	          }
	      };  
	  }; 
	  public static Bitmap returnBitMap(String path) {  
	        URL url = null;  
	        Bitmap bitmap = null;  
	        try {  
	            url = new URL(path);  
	        } catch (MalformedURLException e) {  
	            e.printStackTrace();  
	        }  
	        try {  
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//利用HttpURLConnection对象,我们可以从网络中获取网页数据.  
	            conn.setDoInput(true);  
	            conn.connect();  
	            InputStream is = conn.getInputStream(); //得到网络返回的输入流  
	            bitmap = BitmapFactory.decodeStream(is);  
	            is.close();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return bitmap;  
	    }  
	private void initView() {
		
		mTitle = (TextView) findViewById(R.id.news_detail_title);
		mAuthor = (TextView) findViewById(R.id.news_detail_author);
		mPubDate = (TextView) findViewById(R.id.news_detail_date);
		mContext=(TextView) findViewById(R.id.news_detail_context_2);
		picture=(ImageView) findViewById(R.id.imageView1);
		scrollView = (ScrollView) findViewById(R.id.news_detail_scrollview);
	}

	/**自定义图片适配器**/
	class ItemAdapter extends BaseAdapter {
		
		public List<String> list;
		public ItemAdapter(List<String> list){
			this.list=list;
		}
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView context;
			public ImageView image;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			convertView = null;//禁用缓存机制
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.context=(TextView) view.findViewById(R.id.news_detail_context);
				holder.image = (ImageView) view.findViewById(R.id.imageView1);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			if(position==0){
				holder.context.setText(newsInfo.getContext());
			}else{
				holder.context.setText("");
				holder.context.setVisibility(View.GONE);
			}
			//Adds display image task to execution pool. Image will be set to ImageView when it's turn.
			imageLoader.init(ImageLoaderConfiguration.createDefault(NewsDetails.this));
			imageLoader.displayImage(list.get(position), holder.image, options, animateFirstListener);
			
			return view;
		}
	}

	/**图片加载监听事件**/
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500); //设置image隐藏动画500ms
					displayedImages.add(imageUri); //将图片uri添加到集合中
				}
			}
		}
	}
	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
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
