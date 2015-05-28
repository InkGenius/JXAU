package com.jxau.app.bean;

import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.TextItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.jxau.app.widget.CustomTextItem;

/**
 * Title: CustomItem
 * Description:�Զ����ͼ���ǵ㣬����һ�����֣�CustomTextItem����ͼƬ������OverlayItem��
 * 
 * @author DaiS
 * @date 2013-12-15
 */
public class CustomItem {
	//���ǵ����������
	private CustomTextItem tItem;
	//���ǵ��ͼ���ʾ
	private OverlayItem oItem;

	/**
	 * ����ͼ��Ĺ�����
	 * @param mLat
	 * @param mLon
	 * @param title
	 */
	public CustomItem(int mLat, int mLon, String title) {
		oItem = new OverlayItem(new GeoPoint(mLat, mLon), title, title);
		tItem = new CustomTextItem(mLat, mLon, title);
	}

	public TextItem getTextItem() {
		return tItem;
	}
	public void setTextItem(CustomTextItem tItem) {
		this.tItem=tItem;
	}
	public OverlayItem getOverlayItem() {
		return oItem;
	}
	public void setTextItem(OverlayItem oItem) {
		this.oItem=oItem;
	}
	
}
