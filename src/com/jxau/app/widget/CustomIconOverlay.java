package com.jxau.app.widget;

import android.graphics.drawable.Drawable;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

 /**   
 * Title: CustomOverlay
 * Description:�Զ���ͼ�긲�ǲ�
 * @author DaiS
 * @version 1.0
 * @date 2013-12-15
 */   
public class CustomIconOverlay extends ItemizedOverlay<OverlayItem> 
	implements CustomOverlay{  
    //��MapView����ItemizedOverlay  
    public CustomIconOverlay(Drawable mark,MapView mapView ){  
            super(mark,mapView);
          //  addItem(items);
    }  
    protected boolean onTap(int index) {  
        //�ڴ˴���item����¼�  
        System.out.println("item onTap: "+index);  
        return true;  
    }  
  
    public boolean onTap(GeoPoint pt, MapView mapView){  
            //�ڴ˴���MapView�ĵ���¼��������� trueʱ  
            super.onTap(pt,mapView);  
            return false;  
    }  
}          
