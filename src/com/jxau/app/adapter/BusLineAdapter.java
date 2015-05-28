package com.jxau.app.adapter;

import com.jxau.app.bean.BusResultBean;
import com.zero.jxauapp.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BusLineAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private BusResultBean bean;

	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public BusLineAdapter(Context context, BusResultBean bean) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mInflater = LayoutInflater.from(this.context);
		this.bean = bean;
		Log.d("适配器为什么不执行？", bean.getBusList().get(0));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (bean.getBusList() != null)
			return bean.getBusList().size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (bean.getBusList() != null)
			return bean.getBusList().get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		if (bean.getBusList() != null)
			return position;
		else
			return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		Holder holder = null;
		
		if (convertView == null) {
			
			convertView = mInflater.inflate(R.layout.bus_unit, parent, false);
			holder = new Holder();

			holder.station = (TextView) convertView
					.findViewById(R.id.textView_station);
			holder.busTo = (ImageView) convertView
					.findViewById(R.id.imageView_bus_to);
			holder.busWay = (ImageView) convertView
					.findViewById(R.id.imageView_bus_way);
			holder.busNum = (TextView) convertView
					.findViewById(R.id.textView_bus_num);

			holder.stationState = (ImageView) convertView
					.findViewById(R.id.imageView_station_state);
			holder.cutLine = (ImageView) convertView
					.findViewById(R.id.imageView_cutline);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		//应对缓存机制
		holder.cutLine.setVisibility(View.VISIBLE);
		holder.cutLine.setImageResource(R.drawable.frame_button_cutline);
		holder.busTo.setVisibility(View.GONE);
		holder.busWay.setVisibility(View.GONE);
		holder.busNum.setText("");
		holder.stationState.setImageResource(R.drawable.presence_offline);
		holder.station.setText(bean.getBusList().get(position));
		
		if (position == 0) {
			holder.cutLine.setVisibility(View.INVISIBLE);
		} else {
			String station;
			
			int index2 = bean.getBusList().get(position).indexOf("(");
			if (index2 != -1) {
				station = bean.getBusList().get(position).substring(0, index2);
			} else {
				station = bean.getBusList().get(position);
			}
			
			Log.d("适配器信息：", bean.getStationList().toString());
			Log.d("适配器信息,匹配吗？",station );
			int leng = station.length();
			String temp = station.substring(0,leng-1);
			if (bean.getStationList().contains(temp)) {
				int index = bean.getStationList().indexOf(temp);
				//Log.d("适配器数据",bean.getStateList().get(index));
				if (bean.getStateList().get(index).equals("前往")) {
					holder.cutLine.setImageResource(R.drawable.road_cutline);
					holder.busWay.setVisibility(View.VISIBLE);
				} else {
					holder.busTo.setVisibility(View.VISIBLE);
				}
				holder.stationState
						.setImageResource(R.drawable.presence_online);
				
				if (bean.getBusNum().get(index) > 1) {
					holder.busNum.setText(bean.getBusNum().get(index) + "辆");
				}
			}
		}
		return convertView;
	}

	class Holder {
		ImageView cutLine;
		ImageView busTo;
		ImageView stationState;
		ImageView busWay;
		TextView station;
		TextView busNum;
	}

	// 退出
	public void exit() {
		bean.getBusList().clear();
		notifyDataSetChanged();
		mInflater = null;
		context = null;
	}
}