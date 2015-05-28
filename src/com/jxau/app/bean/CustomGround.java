/**
 * 
 */
package com.jxau.app.bean;

import com.baidu.platform.comapi.basestruct.GeoPoint;

 /**   
 * Title: CustomGround
 * Description:�Զ����Ground�࣬��������ȡ��Ÿ������xml�����ݵ����������ڳ�ʼ�������档
 * @author DaiS
 * @version 1.0
 * @date 2013-12-18
 */

public class CustomGround {
	
	private GeoPoint LBPoint;
	private GeoPoint RTPoint;
	
	public CustomGround(int lbLa,int lbLo,int rtLa,int rtLo){
		LBPoint=new GeoPoint(lbLa,lbLo);
		RTPoint=new GeoPoint(rtLa,rtLo);
	}
	public GeoPoint getLBPoint(){
		return LBPoint;
	}
	
	public void setLBPoint(GeoPoint LBPoint){
		this.LBPoint=LBPoint;
	}
	public GeoPoint getRTPoint(){
		return RTPoint;
	}
	public void setRTPoint(GeoPoint RTPoint){
		this.RTPoint=RTPoint;
	}
}
