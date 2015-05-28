package com.jxau.app.common;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

/**
 * 对图片进行管理的工具类。
 */
@SuppressLint("NewApi")
public class ImageLoader {
	
	private static LruCache<String, Bitmap> mMemoryCache;
	private static ImageLoader mImageLoader;

	private ImageLoader() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
	}
	public static ImageLoader getInstance() {
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader();
		}
		return mImageLoader;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth) {
		// 源图片的宽度
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (width > reqWidth) {
			// 计算出实际宽度和目标宽度的比率
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = widthRatio;
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

}
