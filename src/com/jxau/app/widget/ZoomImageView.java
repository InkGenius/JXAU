package com.jxau.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * �Զ����ImageView���ƣ��ɶ�ͼƬ���ж�㴥�����ź��϶�
 */
public class ZoomImageView extends View {

	public static final int STATUS_INIT = 1;
	public static final int STATUS_ZOOM_OUT = 2;
	public static final int STATUS_ZOOM_IN = 3;
	public static final int STATUS_MOVE = 4;
	private Matrix matrix = new Matrix();
	private Bitmap sourceBitmap;
	private int currentStatus;
	private int width;
	private int height;
	private float centerPointX;
	private float centerPointY;
	private float currentBitmapWidth;
	private float currentBitmapHeight;
	private float lastXMove = -1;
	private float lastYMove = -1;
	private float movedDistanceX;
	private float movedDistanceY;
	private float totalTranslateX;
	private float totalTranslateY;
	private float totalRatio;
	private float scaledRatio;
	private float initRatio;
	private double lastFingerDis;
	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		currentStatus = STATUS_INIT;
	}
	public void setImageBitmap(Bitmap bitmap) {
		sourceBitmap = bitmap;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			// �ֱ��ȡ��ZoomImageView�Ŀ�Ⱥ͸߶�
			width = getWidth();
			height = getHeight();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (initRatio == totalRatio) {
			getParent().requestDisallowInterceptTouchEvent(false);
		} else {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_POINTER_DOWN:
			if (event.getPointerCount() == 2) {
				// ����������ָ������Ļ��ʱ��������ָ֮��ľ���
				lastFingerDis = distanceBetweenFingers(event);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_MOVE:
			if (event.getPointerCount() == 1) {
				// ֻ�е�ָ������Ļ���ƶ�ʱ��Ϊ�϶�״̬
				float xMove = event.getX();
				float yMove = event.getY();
				if (lastXMove == -1 && lastYMove == -1) {
					lastXMove = xMove;
					lastYMove = yMove;
				}
				currentStatus = STATUS_MOVE;
				movedDistanceX = xMove - lastXMove;
				movedDistanceY = yMove - lastYMove;
				// ���б߽��飬������ͼƬ�ϳ��߽�
				if (totalTranslateX + movedDistanceX > 0) {
					movedDistanceX = 0;
				} else if (width - (totalTranslateX + movedDistanceX) > currentBitmapWidth) {
					movedDistanceX = 0;
				}
				if (totalTranslateY + movedDistanceY > 0) {
					movedDistanceY = 0;
				} else if (height - (totalTranslateY + movedDistanceY) > currentBitmapHeight) {
					movedDistanceY = 0;
				}
				// ����onDraw()��������ͼƬ
				invalidate();
				lastXMove = xMove;
				lastYMove = yMove;
			} else if (event.getPointerCount() == 2) {
				// ��������ָ������Ļ���ƶ�ʱ��Ϊ����״̬
				centerPointBetweenFingers(event);
				double fingerDis = distanceBetweenFingers(event);
				if (fingerDis > lastFingerDis) {
					currentStatus = STATUS_ZOOM_OUT;
				} else {
					currentStatus = STATUS_ZOOM_IN;
				}
				// �������ű�����飬���ֻ����ͼƬ�Ŵ�4������С������С����ʼ������
				if ((currentStatus == STATUS_ZOOM_OUT && totalRatio < 4 * initRatio)
						|| (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio)) {
					scaledRatio = (float) (fingerDis / lastFingerDis);
					totalRatio = totalRatio * scaledRatio;
					if (totalRatio > 4 * initRatio) {
						totalRatio = 4 * initRatio;
					} else if (totalRatio < initRatio) {
						totalRatio = initRatio;
					}
					// ����onDraw()��������ͼƬ
					invalidate();
					lastFingerDis = fingerDis;
				}
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if (event.getPointerCount() == 2) {
				// ��ָ�뿪��Ļʱ����ʱֵ��ԭ
				lastXMove = -1;
				lastYMove = -1;
			}
			break;
		case MotionEvent.ACTION_UP:
			// ��ָ�뿪��Ļʱ����ʱֵ��ԭ
			lastXMove = -1;
			lastYMove = -1;
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * ����currentStatus��ֵ��������ͼƬ����ʲô���Ļ��Ʋ�����
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		switch (currentStatus) {
		case STATUS_ZOOM_OUT:
		case STATUS_ZOOM_IN:
			zoom(canvas);
			break;
		case STATUS_MOVE:
			move(canvas);
			break;
		case STATUS_INIT:
			initBitmap(canvas);
		default:
			if (sourceBitmap != null) {
				canvas.drawBitmap(sourceBitmap, matrix, null);
			}
			break;
		}
	}

	/**
	 * ��ͼƬ�������Ŵ���
	 * 
	 * @param canvas
	 */
	private void zoom(Canvas canvas) {
		matrix.reset();
		// ��ͼƬ�������ű�����������
		matrix.postScale(totalRatio, totalRatio);
		float scaledWidth = sourceBitmap.getWidth() * totalRatio;
		float scaledHeight = sourceBitmap.getHeight() * totalRatio;
		float translateX = 0f;
		float translateY = 0f;
		// �����ǰͼƬ���С����Ļ��ȣ�����Ļ���ĵĺ��������ˮƽ���š�������ָ�����ĵ�ĺ��������ˮƽ����
		if (currentBitmapWidth < width) {
			translateX = (width - scaledWidth) / 2f;
		} else {
			translateX = totalTranslateX * scaledRatio + centerPointX
					* (1 - scaledRatio);
			// ���б߽��飬��֤ͼƬ���ź���ˮƽ�����ϲ���ƫ�Ƴ���Ļ
			if (translateX > 0) {
				translateX = 0;
			} else if (width - translateX > scaledWidth) {
				translateX = width - scaledWidth;
			}
		}
		// �����ǰͼƬ�߶�С����Ļ�߶ȣ�����Ļ���ĵ���������д�ֱ���š�������ָ�����ĵ����������д�ֱ����
		if (currentBitmapHeight < height) {
			translateY = (height - scaledHeight) / 2f;
		} else {
			translateY = totalTranslateY * scaledRatio + centerPointY
					* (1 - scaledRatio);
			// ���б߽��飬��֤ͼƬ���ź��ڴ�ֱ�����ϲ���ƫ�Ƴ���Ļ
			if (translateY > 0) {
				translateY = 0;
			} else if (height - translateY > scaledHeight) {
				translateY = height - scaledHeight;
			}
		}
		// ���ź��ͼƬ����ƫ�ƣ��Ա�֤���ź����ĵ�λ�ò���
		matrix.postTranslate(translateX, translateY);
		totalTranslateX = translateX;
		totalTranslateY = translateY;
		currentBitmapWidth = scaledWidth;
		currentBitmapHeight = scaledHeight;
		canvas.drawBitmap(sourceBitmap, matrix, null);
	}

	/**
	 * ��ͼƬ����ƽ�ƴ���
	 * 
	 * @param canvas
	 */
	private void move(Canvas canvas) {
		matrix.reset();
		// ������ָ�ƶ��ľ���������ƫ��ֵ
		float translateX = totalTranslateX + movedDistanceX;
		float translateY = totalTranslateY + movedDistanceY;
		// �Ȱ������е����ű�����ͼƬ��������
		matrix.postScale(totalRatio, totalRatio);
		// �ٸ����ƶ��������ƫ��
		matrix.postTranslate(translateX, translateY);
		totalTranslateX = translateX;
		totalTranslateY = translateY;
		canvas.drawBitmap(sourceBitmap, matrix, null);
	}

	/**
	 * ��ͼƬ���г�ʼ��������������ͼƬ���У��Լ���ͼƬ������Ļ���ʱ��ͼƬ����ѹ����
	 * 
	 * @param canvas
	 */
	private void initBitmap(Canvas canvas) {
		if (sourceBitmap != null) {
			matrix.reset();
			int bitmapWidth = sourceBitmap.getWidth();
			int bitmapHeight = sourceBitmap.getHeight();
			if (bitmapWidth > width || bitmapHeight > height) {
				if (bitmapWidth - width > bitmapHeight - height) {
					// ��ͼƬ��ȴ�����Ļ���ʱ����ͼƬ�ȱ���ѹ����ʹ��������ȫ��ʾ����
					float ratio = width / (bitmapWidth * 1.0f);
					matrix.postScale(ratio, ratio);
					float translateY = (height - (bitmapHeight * ratio)) / 2f;
					// �������귽���Ͻ���ƫ�ƣ��Ա�֤ͼƬ������ʾ
					matrix.postTranslate(0, translateY);
					totalTranslateY = translateY;
					totalRatio = initRatio = ratio;
				} else {
					// ��ͼƬ�߶ȴ�����Ļ�߶�ʱ����ͼƬ�ȱ���ѹ����ʹ��������ȫ��ʾ����
					float ratio = height / (bitmapHeight * 1.0f);
					matrix.postScale(ratio, ratio);
					float translateX = (width - (bitmapWidth * ratio)) / 2f;
					// �ں����귽���Ͻ���ƫ�ƣ��Ա�֤ͼƬ������ʾ
					matrix.postTranslate(translateX, 0);
					totalTranslateX = translateX;
					totalRatio = initRatio = ratio;
				}
				currentBitmapWidth = bitmapWidth * initRatio;
				currentBitmapHeight = bitmapHeight * initRatio;
			} else {
				// ��ͼƬ�Ŀ�߶�С����Ļ���ʱ��ֱ����ͼƬ������ʾ
				float translateX = (width - sourceBitmap.getWidth()) / 2f;
				float translateY = (height - sourceBitmap.getHeight()) / 2f;
				matrix.postTranslate(translateX, translateY);
				totalTranslateX = translateX;
				totalTranslateY = translateY;
				totalRatio = initRatio = 1f;
				currentBitmapWidth = bitmapWidth;
				currentBitmapHeight = bitmapHeight;
			}
			canvas.drawBitmap(sourceBitmap, matrix, null);
		}
	}

	/**
	 * ����������ָ֮��ľ��롣
	 * 
	 * @param event
	 * @return ������ָ֮��ľ���
	 */
	private double distanceBetweenFingers(MotionEvent event) {
		float disX = Math.abs(event.getX(0) - event.getX(1));
		float disY = Math.abs(event.getY(0) - event.getY(1));
		return Math.sqrt(disX * disX + disY * disY);
	}

	/**
	 * ����������ָ֮�����ĵ�����ꡣ
	 * 
	 * @param event
	 */
	private void centerPointBetweenFingers(MotionEvent event) {
		float xPoint0 = event.getX(0);
		float yPoint0 = event.getY(0);
		float xPoint1 = event.getX(1);
		float yPoint1 = event.getY(1);
		centerPointX = (xPoint0 + xPoint1) / 2;
		centerPointY = (yPoint0 + yPoint1) / 2;
	}
}
