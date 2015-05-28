package com.jxau.app.ui;

import java.io.File;
import java.util.ArrayList;

import com.jxau.app.widget.ZoomImageView;
import com.umeng.analytics.MobclickAgent;
import com.zero.jxauapp.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class ImageDetails extends Activity implements
		OnPageChangeListener {
	private ViewPager viewPager;
	private TextView pageText;
	private ArrayList<String> list=new ArrayList<String>();
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.landscape_details);
		list=getIntent().getStringArrayListExtra("image_urls");
		int imagePosition = getIntent().getIntExtra("image_position", 0);
		pageText = (TextView) findViewById(R.id.page_text);
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		ViewPagerAdapter adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(imagePosition);
		viewPager.setOnPageChangeListener(this);
		viewPager.setEnabled(false);
		pageText.setText((imagePosition + 1) + "/" + list.size());
	}

	class ViewPagerAdapter extends PagerAdapter {
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			String imagePath = getImagePath(list.get(position));
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.empty_photo);
			}
			View view = LayoutInflater.from(ImageDetails.this).inflate(
					R.layout.zoom_image_layout, null);
			ZoomImageView zoomImageView = (ZoomImageView) view
					.findViewById(R.id.zoom_image_view);
			zoomImageView.setImageBitmap(bitmap);
			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}

	}

	private String getImagePath(String imageUrl) {
		
		int lastSlashIndex = imageUrl.lastIndexOf("/");
		String imageName = imageUrl.substring(lastSlashIndex + 1);
		String imageDir = Environment.getExternalStorageDirectory().getPath()
				+ "/JxauApp/Pictures/";
		File file = new File(imageDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imagePath = imageDir + imageName;
		return imagePath;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int currentPage) {
		// 每当页数发生改变时重新设定一遍当前的页数和总页数
		pageText.setText((currentPage + 1) + "/" + list.size());
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