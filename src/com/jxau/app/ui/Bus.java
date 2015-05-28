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
	
	private BusRequestBean busRequest; // ���յ����û�����
	
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
		mViewTitle = new ViewTitle(this, "����", "����ȥ����", "ˢ��");
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
		
		FAndLStation.setText("��վ:"+busLineInfo.getStartStation()+"  "+"ĩվ:"+busLineInfo.getEndStation());
		FAndLTime.setText("�װ�:"+busLineInfo.getStartTime()+"  "+"ĩ��:"+busLineInfo.getEndTime());
		String[] station = busLineInfo.getStations().split("\\|");
		mViewTitle.setTitle(busLineInfo.getName()+"ȥ����");
		List<String> list = Arrays.asList(station);
		
		adapter = new BusLineAdapter(this, new BusResultBean(list,
					stations, states, num, busRequest.isDirection()));
		
		Log.d("��������stations",stations.size()+"");
		Log.d("��������states",states.size()+"");
		Log.d("��������num",num.size()+"");
		Log.d("��������busRequest",busRequest.isDirection()+"");
		
		listView.setAdapter(adapter);
	}
	
	private Runnable getDataRunable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			getBusResult(busRequest);//��̨����
			handler.sendEmptyMessage(0);
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				setUI();//����UI
				mAppContext.hideLoading();
				break;
			}
		};
	};
	
	/**
	 * ��ȡ������Ϣ
	 * @param requset
	 */
	public void getBusResult(BusRequestBean requset) {
		// ��ȡ��������(��Ҫ�����վ��)���Լ���״̬(����ǰ�����ѵ���),���й�����������
		stations = new ArrayList<String>();
		states = new ArrayList<String>();
		num = new ArrayList<Integer>();
		
		List<BusTrackResult> busList = BusTrackUnit.getBusInfo(requset);
		Log.d("List�����Ƿ���Ч��",busList.size()+"��");
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
	    MobclickAgent.onPageStart("SplashScreen"); //ͳ��ҳ��
	    MobclickAgent.onResume(this);          //ͳ��ʱ��
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("SplashScreen"); // ��֤ onPageEnd ��onPause ֮ǰ����,��Ϊ onPause �лᱣ����Ϣ 
	    MobclickAgent.onPause(this);
	}
}
