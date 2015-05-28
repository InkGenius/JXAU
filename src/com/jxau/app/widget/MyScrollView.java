package com.jxau.app.widget;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jxau.app.api.AppContext;
import com.jxau.app.common.ImageLoader;
import com.jxau.app.ui.ImageDetails;
import com.zero.jxauapp.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class MyScrollView extends ScrollView implements OnTouchListener {
	
	AppContext mAppContext;
	public static final int PAGE_SIZE = 15;
	private SharedPreferences sharedPreferences; 
	private int page;
	
	private int columnWidth;
	private int firstColumnHeight;
	private int secondColumnHeight;
	private int thirdColumnHeight;
	
	private boolean loadOnce;
	
	private ImageLoader imageLoader;
	
	private LinearLayout firstColumn;
	private LinearLayout secondColumn;
	private LinearLayout thirdColumn;
	
	private static Set<LoadImageTask> taskCollection;
	private static View scrollLayout;
	private static int scrollViewHeight;
	private static int lastScrollY = -1;
	private List<ImageView> imageViewList = new ArrayList<ImageView>();
	private ArrayList<String> list=new ArrayList<String>();
	
	private static Handler handler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			MyScrollView myScrollView = (MyScrollView) msg.obj;
			int scrollY = myScrollView.getScrollY();
			if (scrollY == lastScrollY) {
				if (scrollViewHeight + scrollY >= scrollLayout.getHeight()
						&& taskCollection.isEmpty()) {
					myScrollView.loadMoreImages();
				}
				myScrollView.checkVisibility();
			} else {
				lastScrollY = scrollY;
				Message message = new Message();
				message.obj = myScrollView;
				handler.sendMessageDelayed(message, 5);
			}
		};
	};
	
	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAppContext = (AppContext) context.getApplicationContext();
		getUrls();
		imageLoader = ImageLoader.getInstance();
		taskCollection = new HashSet<LoadImageTask>();
		setOnTouchListener(this);
	}
	  
	public void getUrls(){
		if(mAppContext.pictureList.size() > 0){
			list=mAppContext.pictureList;
		}else{
			 sharedPreferences = getContext().getSharedPreferences("test",Context.MODE_PRIVATE);  
		     int i=1;
		     while(sharedPreferences.getString(""+i, null)!=null ){
		    	 list.add(sharedPreferences.getString(""+i, null));
		    	 i++;
		     }
		}
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed && !loadOnce) {
			scrollViewHeight = getHeight();
			scrollLayout = getChildAt(0);
			firstColumn = (LinearLayout) findViewById(R.id.first_column);
			secondColumn = (LinearLayout) findViewById(R.id.second_column);
			thirdColumn = (LinearLayout) findViewById(R.id.third_column);
			columnWidth = firstColumn.getWidth();
			loadOnce = true;
			loadMoreImages();
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Message message = new Message();
			message.obj = this;
			handler.sendMessageDelayed(message, 5);
		}
		return false;
	}

	public void loadMoreImages() {
		if (hasSDCard()) {
			int startIndex = page * PAGE_SIZE;
			int endIndex = page * PAGE_SIZE + PAGE_SIZE;
			
			if (startIndex < list.size()) {
				Toast.makeText(getContext(), "正在加载...", Toast.LENGTH_SHORT).show();
				if (endIndex > list.size()) {
					endIndex = list.size();
				}
				for (int i = startIndex; i < endIndex; i++) {
					LoadImageTask task = new LoadImageTask();
					taskCollection.add(task);
					task.execute(i);
				}
				page++;
			} else {
				Toast.makeText(getContext(), "已没有更多图片", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getContext(), "未发现SD卡", Toast.LENGTH_SHORT).show();
		}
	}

	public void checkVisibility() {
		
		for (int i = 0; i < imageViewList.size(); i++) {
			
			ImageView imageView = imageViewList.get(i);
			
			int borderTop = (Integer) imageView.getTag(R.string.border_top);
			int borderBottom = (Integer) imageView.getTag(R.string.border_bottom);
			if (borderBottom > getScrollY() && borderTop < getScrollY() + scrollViewHeight) {
				String imageUrl = (String) imageView.getTag(R.string.image_url);
				Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
				} else {
					LoadImageTask task = new LoadImageTask(imageView);
					task.execute(i);
				}
			} else {
				imageView.setImageResource(R.drawable.empty_photo);
			}
		}
	}

	private boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	
	class LoadImageTask extends AsyncTask<Integer, Void, Bitmap> {
		public LoadImageTask(){
		}
		private int mItemPosition;
		private String mImageUrl;
		private ImageView mImageView;
		public LoadImageTask(ImageView imageView) {
			mImageView = imageView;
			
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			
			mItemPosition = params[0];
			mImageUrl = list.get(mItemPosition);
			Bitmap imageBitmap = imageLoader.getBitmapFromMemoryCache(mImageUrl);
			if (imageBitmap == null) {
				imageBitmap = loadImage(mImageUrl);
			}
			return imageBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				double ratio = bitmap.getWidth() / (columnWidth * 1.0);
				int scaledHeight = (int) (bitmap.getHeight() / ratio);
				addImage(bitmap, columnWidth, scaledHeight);
			}
			taskCollection.remove(this);
		}

		/**
		 * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
		 * 
		 * @param imageUrl
		 *            图片的URL地址
		 * @return 加载到内存的图片。
		 */
		private Bitmap loadImage(String imageUrl) {
			File imageFile = new File(getImagePath(imageUrl));
			if (!imageFile.exists()) {
				downloadImage(imageUrl);
			}
			if (imageUrl != null) {
				Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(),
						columnWidth);
				if (bitmap != null) {
					imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
					return bitmap;
				}
			}
			return null;
		}
		private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth,
					imageHeight);
			if (mImageView != null) {
				mImageView.setImageBitmap(bitmap);
			} else {
				ImageView imageView = new ImageView(getContext());
				imageView.setLayoutParams(params);
				imageView.setImageBitmap(bitmap);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setPadding(5, 5, 5, 5);
				imageView.setTag(R.string.image_url, mImageUrl);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getContext(), ImageDetails.class);
						intent.putStringArrayListExtra("image_urls", list);
						intent.putExtra("image_position", mItemPosition);
						getContext().startActivity(intent);
					}
				});
				findColumnToAdd(imageView, imageHeight).addView(imageView);
				imageViewList.add(imageView);
			}
		}

		private LinearLayout findColumnToAdd(ImageView imageView, int imageHeight) {
			if (firstColumnHeight <= secondColumnHeight) {
				if (firstColumnHeight <= thirdColumnHeight) {
					imageView.setTag(R.string.border_top, firstColumnHeight);
					firstColumnHeight += imageHeight;
					imageView.setTag(R.string.border_bottom, firstColumnHeight);
					return firstColumn;
				}
				imageView.setTag(R.string.border_top, thirdColumnHeight);
				thirdColumnHeight += imageHeight;
				imageView.setTag(R.string.border_bottom, thirdColumnHeight);
				return thirdColumn;
			} else {
				if (secondColumnHeight <= thirdColumnHeight) {
					imageView.setTag(R.string.border_top, secondColumnHeight);
					secondColumnHeight += imageHeight;
					imageView.setTag(R.string.border_bottom, secondColumnHeight);
					return secondColumn;
				}
				imageView.setTag(R.string.border_top, thirdColumnHeight);
				thirdColumnHeight += imageHeight;
				imageView.setTag(R.string.border_bottom, thirdColumnHeight);
				return thirdColumn;
			}
		}

		private void downloadImage(String imageUrl) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Log.d("TAG", "monted sdcard");
			} else {
				Log.d("TAG", "has no sdcard");
			}
			HttpURLConnection con = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
			File imageFile = null;
			try {
				URL url = new URL(imageUrl);
				con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(5 * 1000);
				con.setReadTimeout(15 * 1000);
				con.setDoInput(true);
				con.setDoOutput(true);
				bis = new BufferedInputStream(con.getInputStream());
				imageFile = new File(getImagePath(imageUrl));
				fos = new FileOutputStream(imageFile);
				bos = new BufferedOutputStream(fos);
				byte[] b = new byte[1024];
				int length;
				while ((length = bis.read(b)) != -1) {
					bos.write(b, 0, length);
					bos.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bis != null) {
						bis.close();
					}
					if (bos != null) {
						bos.close();
					}
					if (con != null) {
						con.disconnect();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (imageFile != null) {
				Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(),
						columnWidth);
				if (bitmap != null) {
					imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
				}
			}
		}
		private String getImagePath(String imageUrl) {
			int lastSlashIndex = imageUrl.lastIndexOf("/");
			String imageName = imageUrl.substring(lastSlashIndex + 1);
			String imageDir = Environment.getExternalStorageDirectory().getPath()
					+ "/JxauApp/Pictures/";
			File file = new File(imageDir);
			if (!file.exists()) {
				file.mkdirs();
			}
			String imagePath = imageDir + imageName;
			return imagePath;
		}
	}
}