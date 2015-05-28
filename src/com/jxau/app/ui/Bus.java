package com.jxau.app.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jxau.app.adapter.BusLineAdapter;
import com.jxau.app.api.AppContext;
import com.jxau.app.bean.BusLineInfo;
import com.jxau.app.bean.BusRequestBean;
import com.jxau.app.bean.BusResultBean;
import com.jxau.app.bean.BusTrackDetail;
import com.jxau.app.bean.BusTrackResult;
import com.jxau.app.common.BusTrackUnit;
import com.jxau.app.db.DBHelper;
import com.jxau.app.widget.ViewTitle;
import com.umeng.analytics.MobclickAgent;
import com.zero.jxauapp.R;

public class Bus extends Activity {
	
	private DBHelper db;
	private BusLineInfo busLineInfo;
	private AppContext mAppContext;
	private ListView listView;
	private BusLineAdapter adapter;
	private TextView FAndLTime;
	private TextView FAndLStation;
	private LinearLayout busImageInit;
	
	private BusRequestBean busRequest; // 接收到的用户请求
	
	private List<String> stations;
	private List<String> states;
	private List<Integer> num;
	
	private ViewTitle mViewTitle;
	private RelativeLayout widget_title;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_result);
		
		db = new DBHelper(Bus.this);
		
		mAppContext = (AppContext) getApplicationContext();
		mViewTitle = new ViewTitle(this, "反向", "公交去哪了", "刷新");
		widget_title = (RelativeLayout) findViewById(R.id.widget_title);
		widget_title.addView(mViewTitle);
		
		mAppContext.showLoading(Bus.this);
		
		listView = (ListView) findViewById(R.id.listView);
		FAndLTime=(TextView) findViewById(R.id.time_textView);
		FAndLStation = (TextView) findViewById(R.id.station_textView);
		busImageInit = (LinearLayout) findViewById(R.id.bus_init);
		
		busRequest = (BusRequestBean) getIntent().getSerializableExtra(
				"BUSTRACKINFO");
		
		new Thread(getDataRunable).start();
		
		mViewTitle.setOnRightListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(getDataRunable).start();
				mAppContext.showLoading(Bus.this);
			}
		});
		mViewTitle.setOnBackListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				busRequest = new BusRequestBean(busRequest.getLine(),
						!busRequest.isDirection());
				new Thread(getDataRunable).start();
				mAppContext.showLoading(Bus.this);
			}
		});
		
	}

	public void setUI() {
		
		busLineInfo = db.select(busRequest.getQuaryNum());
		busImageInit.setVisibility(View.VISIBLE);
		
		FAndLStation.setText("首站:"+busLineInfo.getStartStation()+"  "+"末站:"+busLineInfo.getEndStation());
		FAndLTime.setText("首班:"+busLineInfo.getStartTime()+"  "+"末班:"+busLineInfo.getEndTime());
		String[] station = busLineInfo.getStations().split("\\|");
		mViewTitle.setTitle(busLineInfo.getName()+"去哪了");
		List<String> list = Arrays.asList(station);
		
		adapter = new BusLineAdapter(this, new BusResultBean(list,
					stations, states, num, busRequest.isDirection()));
		
		Log.d("适配数据stations",stations.size()+"");
		Log.d("适配数据states",states.size()+"");
		Log.d("适配数据num",num.size()+"");
		Log.d("适配数据busRequest",busRequest.isDirection()+"");
		
		listView.setAdapter(adapter);
	}
	
	private Runnable getDataRunable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			getBusResult(busRequest);//后台任务
			handler.sendEmptyMessage(0);
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				setUI();//更新UI
				mAppContext.hideLoading();
				break;
			}
		};
	};
	
	/**
	 * 获取公交信息
	 * @param requset
	 */
	public void getBusResult(BusRequestBean requset) {
		// 获取公交所处(或将要到达的站点)，以及其状态(正在前往或已到达),还有公交的数量。
		stations = new ArrayList<String>();
		states = new ArrayList<String>();
		num = new ArrayList<Integer>();
		
		List<BusTrackResult> busList = BusTrackUnit.getBusInfo(requset);
		Log.d("List传递是否有效：",busList.size()+"辆");
		for (BusTrackResult busInfo : busList) {
			String line = busInfo.getCurrentLine();
			List<BusTrackDetail> list = busInfo.getListSubBusInfo();
			if (line.contains(requset.getLine() )) {
				for (BusTrackDetail s : list) {
					stations.add(s.getStation());
					states.add(s.getArrived());
					num.add(s.getNumber());
					Log.d("stations + states+ num: ",stations+"");
				}
			}
		}
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
