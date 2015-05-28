package com.jxau.app.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.jxau.app.common.XmlReader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zero.jxauapp.R;

public class ApiClient {

	AppContext mAppContext;
	Context mContext;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	public ApiClient(Context mContext) {
		this.mContext = mContext;
		mAppContext = (AppContext) mContext.getApplicationContext();
		sharedPreferences = mContext.getSharedPreferences("test",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}

	public void get(String url, RequestParams params,
			AsyncHttpResponseHandler handler) {
		AsyncHttpClient oneClient = new AsyncHttpClient();
		oneClient.setTimeout(AppContext.TIMEOUT_CONNECTION);
		oneClient.get(mContext, url, params, handler);
	}

	/**
	 * 解析联网数据并储存到全局变量download里面
	 * 
	 * @param data
	 */
	public void downloadPictureUrls() {
		// TODO Auto-generated method stub
		get("http://blog.csdn.net/u013677570/article/details/19290151", null,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
						if (mAppContext.pictureList.size() == 0) {
							mAppContext.ToastMessage(mContext, "网络不给力！");
						}
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub

						super.onStart();
					}

					@Override
					public void onSuccess(int arg0, String arg1) {
						// TODO Auto-generated method stub
						System.out.println("Apiclient:firstDownlaod下载成功");
						try {
							parseData(arg1);
							System.out.println("Apiclient:UI更新成功");
						} catch (Exception e) {
							e.printStackTrace();
						}
						super.onSuccess(arg0, arg1);
					}
				});
	}

	/**
	 * @param data
	 *            解析数据
	 */

	public void parseData(String data) {
		// TODO Auto-generated method stub
		ArrayList<String> plist = new ArrayList<String>();
		try {
			data = data
					.substring(data.indexOf("<p>"), data.lastIndexOf("</p>"));
			data = data.replaceAll("\r|\n|\t", "");
			String[] s = data.split("</p>");
			for (int i = 0; i < s.length; i++) {
				plist.add(s[i].trim().substring(3));
			}
			// 如果获取的URLS有变化，则将新的URLs存入sharedPreference。
			if (sharedPreferences.getInt("size", 0) != plist.size()) {
				int i = 1;
				editor.clear();
				for (String str : plist) {
					editor.putString("" + i, str);
					i++;
				}
				editor.putInt("size", plist.size());
				editor.commit();
			}
			mAppContext.pictureList = plist;

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Apiclient:解析失败");
		}

	}

	public boolean isFristStart() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"client", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		boolean flag = sharedPreferences.getBoolean("Start", true);

		if (flag) {
			editor.clear();
			editor.putBoolean("Start", false);
			editor.commit();
		}

		return flag;
	}

	public void createDatabase() throws IOException {
		try {
			String databaseFilename = mAppContext.DATABASE_PATH
					+ mAppContext.DATABASE_NAME;
			File dir = new File(mAppContext.DATABASE_PATH);
			if (!dir.exists())
				dir.mkdir();
			if (!(new File(databaseFilename)).exists()) {
				InputStream is = mAppContext.getResources().openRawResource(
						R.raw.bus);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[7168];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
		}
	}

	public void deleteDataBase() {
		String path = mAppContext.DATABASE_PATH + mAppContext.DATABASE_NAME;
		File file = new File(path);
		file.delete();
	}

	public boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		String myPath = mAppContext.DATABASE_PATH + mAppContext.DATABASE_NAME;
		try {
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLiteException e) {
		}
		if (checkDB != null) {
			checkDB.close();
			System.out.println("关闭");
		}
		return checkDB != null ? true : false;
	}

	/**
	 * 读取XML文件中的覆盖点信息，渲染地图
	 */
	public void readPointListInXML() {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(
					mAppContext.getResources().getAssets().open("point_db.xml"),
					null);
			mAppContext.pointList = XmlReader.getCustomItemInfo(xpp);
		} catch (Exception e) {
		}
	}

	/**
	 * 读取xml中的文件，用于模糊匹配用户输入
	 */
	public void readPointMapInXML() {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(
					mAppContext.getResources().getAssets().open("point_db.xml"),
					null);
			mAppContext.pointMap = XmlReader.getPointDB(xpp);
		} catch (Exception e) {
		}
	}
}
