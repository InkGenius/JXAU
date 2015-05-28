package com.jxau.app.widget;

import android.R.color;
import android.graphics.Color;

import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

 /**   
 * Title: CustomTextItem
 * Description:�Զ���TextItem,ͳһ���ֵĴ�С����ɫ���Լ�������ɫ
 * @author DaiS
 * @version 1.0
 * @date 2013-12-17
 */   
public class CustomTextItem extends TextItem{
	private int latOffset=60;//γ��ƫ����60,ʹ���ֲ���ͼ���·�
	//���ִ�С����ɫ���Լ�������ɫ����������
	private  int FONTSIZE=18;
	
//	private final Symbol.Color FONTCOLOR = new Symbol().new Color(255,0,255,0); 
//	private final Symbol.Color BGCOLOR = new Symbol().new Color(150,80,80,80);
//	private final Symbol.Color FONTCOLOR = new Symbol().new Color();
	private final Symbol.Color FONTCOLOR = new Symbol().new Color(0xFFF8F8FF);
	private final Symbol.Color BGCOLOR = new Symbol().new Color(color.holo_orange_light);
	
	public CustomTextItem(int latitude,int longtitude,String name){
		super();
//		this.fontColor=new Symbol().new Color(150,255,0,0);
//		this.bgColor=BGCOLOR; 	#FCFCFC
//		this.fontColor = new Symbol().new Color(255,255,255,255);  
//	    this.bgColor = new Symbol().new Color(150,255,255,255);
		this.fontColor=FONTCOLOR;
		this.pt=new GeoPoint(latitude-latOffset,longtitude);
		this.text=name;
		this.align = TextItem.ALIGN_CENTER;
	}
	
	public int getFontSize(){
		return FONTSIZE;
	}
	
	public void setFontSize(int font){
		this.FONTSIZE=font;
	}

	/**
	 * @return the fONTSIZE
	 */
	public int getFONTSIZE() {
		return FONTSIZE;
	}

	/**
	 * @param fONTSIZE the fONTSIZE to set
	 */
	public void setFONTSIZE(int fONTSIZE) {
		FONTSIZE = fONTSIZE;
	}

	/**
	 * @return the bGCOLOR
	 */
	public Symbol.Color getBGCOLOR() {
		return BGCOLOR;
	}
}
