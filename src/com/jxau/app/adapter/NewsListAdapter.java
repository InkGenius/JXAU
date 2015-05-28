/**
 * 
 */
package com.jxau.app.adapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.jxau.app.bean.ListItemView;
import com.jxau.app.bean.NewsBean;
import com.jxau.app.common.DateTransformer;
import com.zero.jxauapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Title: BusTrackInfoAdapter Description:�Զ�������������Ϣ��䵽ListView��
 * 
 * @author DaiS
 * @version 1.0
 * @date 2013-12-26
 */
public class NewsListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public Context context;
	public List<NewsBean> newsList = new ArrayList<NewsBean>();

	public NewsListAdapter(Context context, List<NewsBean> newsList) {
		this.context = context;
		this.mInflater = LayoutInflater.from(this.context);
		this.newsList = newsList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (newsList != null)
			return newsList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (newsList != null)
			return newsList.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		if (newsList != null)
			return position;
		else
			return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView listItemView = null;

		if (convertView == null) {
			// ��ȡlist_item�����ļ�����ͼ
			convertView = mInflater.inflate(R.layout.news_listitem, null);

			listItemView = new ListItemView();
			// ��ȡ�ؼ�����
			listItemView.title = (TextView) convertView
					.findViewById(R.id.news_listitem_title);
			listItemView.author = (TextView) convertView
					.findViewById(R.id.news_listitem_author);
			listItemView.date = (TextView) convertView
					.findViewById(R.id.news_listitem_date);
			listItemView.flag = (ImageView) convertView
					.findViewById(R.id.news_listitem_flag);

			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		NewsBean news = newsList.get(position);

		listItemView.title.setText(news.getTitle());
		listItemView.title.setTag(news);              //�������ز���(ʵ����)
		listItemView.author.setText(news.getAuthor());
		//listItemView.date.setText(news.getPubDate());
		//Ӧ�Ի�����ƴ������ͺ�������
		listItemView.flag.setVisibility(View.GONE);
		if (news.getPubDate().equals("֮ǰ")) {
			listItemView.date.setText("֮ǰ");
		} else {
			try {
				listItemView.date.setText(DateTransformer.friendly_time(news
						.getPubDate()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (DateTransformer.isToday(news.getPubDate()))
				listItemView.flag.setVisibility(View.VISIBLE);
			else
				listItemView.flag.setVisibility(View.GONE);
		}
		return convertView;
	}

	// �˳�
	public void exit() {
		newsList = null;
		notifyDataSetChanged();
		mInflater = null;
		context = null;
	}
}
