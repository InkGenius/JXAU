package com.jxau.app.bean;

import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.TextItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.jxau.app.widget.CustomTextItem;

/**
 * Title: CustomItem
 * Description:自定义地图覆盖点，包括一个文字（CustomTextItem）和图片描述（OverlayItem）
 * 
 * @author DaiS
 * @date 2013-12-15
 */
public class CustomItem {
	//覆盖点的文字描述
	private CustomTextItem tItem;
	//覆盖点的图标表示
	private OverlayItem oItem;

	/**
	 * 不带图标的构造器
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
