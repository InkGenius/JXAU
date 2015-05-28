package com.jxau.app.common;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.jxau.app.widget.CustomOverlay;
import com.jxau.app.widget.CustomTextOverlay;

import android.graphics.Bitmap;
import android.view.View;

public class MapUtil {
    	
	/**
	 * 从view 得到图片
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
	}
	
	/**
	 * 删除自定义文字覆盖层
	 */
	public static void removeCustomTextOverlay(MapView mapView) {
		List<Overlay> delList = new ArrayList<Overlay>();
		for (Overlay overlay : mapView.getOverlays()) {
			if (overlay instanceof CustomTextOverlay) {
				delList.add(overlay);
			}
		}
		mapView.getOverlays().removeAll(delList);
	}

	/**
	 * 删除不是自定义（文字、图标、图片）覆盖层
	 */
	public static void removeNotCustomOverlay(MapView mapView) {
		List<Overlay> delList = new ArrayList<Overlay>();
		for (Overlay overlay : mapView.getOverlays()) {
			if (!(overlay instanceof CustomOverlay)) {
				delList.add(overlay);
			}
		}
		mapView.getOverlays().removeAll(delList);
	}
}
