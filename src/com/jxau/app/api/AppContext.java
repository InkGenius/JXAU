package com.jxau.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.jxau.app.bean.CustomItem;
import com.jxau.app.bean.NewsBean;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zero.jxauapp.R;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

public class AppContext extends Application {

	/**
	 * 全局常量
	 */
	public final static String DATABASE_PATH = "/data/data/com.zero.jxauapp/databases/";
	public final static String DATABASE_NAME = "bus.db";
	public final static int TIMEOUT_CONNECTION = 8000;

	/**
	 * 全局生命周期
	 */
	public ApiClient mApiClient;
	public Dialog noticeDialog;
	public Map<String, GeoPoint> pointMap = new HashMap<String, GeoPoint>();
	public ArrayList<String> pictureList = new ArrayList<String>();
	public ArrayList<CustomItem> pointList = new ArrayList<CustomItem>();

	public List<NewsBean> newsResultList = new ArrayList<NewsBean>();
	public List<NewsBean> noticleResultList = new ArrayList<NewsBean>();
	public List<String> lineList;

	@Override
	public void onCreate() {
		super.onCreate();
		mApiClient = new ApiClient(this);
		initImageLoader(this);
		String[] lineArray = getResources().getStringArray(
				R.array.bus_line_array);
		lineList = Arrays.asList(lineArray);
	}

	/** 初始化图片加载类配置信息 **/
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	public void ToastMessage(Context mContext, String msg) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}

	public void showLoading(Context mContext) {

		// TODO Auto-generated method stub
		if (noticeDialog != null && noticeDialog.isShowing()) {
			noticeDialog.dismiss();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(LayoutInflater.from(mContext).inflate(
				R.layout.dialog_loading, null));
		noticeDialog = builder.create();
		noticeDialog.setCanceledOnTouchOutside(false);
		noticeDialog.show();
	}

	public void hideLoading() {
		// TODO Auto-generated method stub
		noticeDialog.dismiss();
	}
}
