package com.jxau.app.ui;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.jxau.app.adapter.ArrayAdapter;
import com.jxau.app.api.AppContext;
import com.jxau.app.bean.CustomItem;
import com.jxau.app.common.MapUtil;
import com.jxau.app.widget.CustomIconOverlay;
import com.jxau.app.widget.CustomTextOverlay;
import com.umeng.analytics.MobclickAgent;
import com.zero.jxauapp.R;

/**
 * Title: jxauMapAct Description:地图页面
 * @author DaiS
 * @version 1.0
 * @date 2013-12-14
 */
public class MapActivity extends Activity implements OnClickListener {

	private AppContext mAppContext;
	private Button routeBtn;
	private BMapManager mBMapManager = null;
	private MapView mMapView = null;
	private MKSearch mSearch = null;

	private LinearLayout linear;
	
	private AutoCompleteTextView editStart;
	private AutoCompleteTextView editEnd;
	private AutoCompleteTextView searchACTV;
	
	private Button searchBtn;
	private Button mBtnDrive = null;
	private Button mBtnTransit = null;
	private Button mBtnWalk = null;
	private Button mbtnLoc = null;
	private LocationClient locationClient = null;
	private Button mBtnHit = null;

	private GeoPoint routeEndPoint;
	private GeoPoint routeStartPoint;

	private RouteOverlay routeOverlay = null;
	private TransitOverlay transitOverlay = null;

	private MyLocationOverlay myLocationOverlay = null;
	private LocationData locData = null;
	private MapController mMapController;
	private ArrayAdapter<String> adapter;
	// 表示输入框的状态
	private final String STATU_END = "地图上的点";
	private final String STATU_START = "当前位置";
	// 出行方式，0表示步行，1表示公交，2表示开车
	private final int VEHICLE_WALK = 0;
	private final int VEHICLE_TRANSIT = 1;
	private final int VEHICLE_DRIVE = 2;
	
	private static final int UPDATE_TIME = 100000;
	// 是否响应屏幕点击
	private boolean enClickAble = false;
	// 浏览路线节点相关
	private View viewCache = null;
	private TextView popupText = null;
	private TextView popleftText = null;
	private TextView poprightText = null;
	private int searchType = -1;
	private int nodeIndex = -2;
	private MKRoute route = null;
	private Button mBtnPre = null;
	private Button mBtnNext = null;
	private PopupOverlay pop = null;
	// 百度地图的key
	private final String baiDuMapKey = "lVHeadDQWp9Y18FdCmbORMZs";
	private CustomIconOverlay itemOverlay;
	private CustomTextOverlay textOverlay;

	boolean isFrist;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mBMapManager = new BMapManager(getApplication());
		mBMapManager.init(baiDuMapKey, null);
		isFrist = true;
		setContentView(R.layout.map);
		mAppContext = (AppContext) getApplicationContext();
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		mMapController.setCompassMargin(20, 110);
		GeoPoint point = new GeoPoint((int) (28.768217 * 1E6),
				(int) (115.839311 * 1E6));
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(18);// 设置地图zoom级别
		mMapController.enableClick(false);
		mMapView.setSatellite(true);
		mMapView.setTraffic(true);

		mSearch = new MKSearch();
		mSearch.init(mBMapManager, myMKSearchListener);
		// 为MapView添加点击事件监听
		mMapView.regMapTouchListner(MymapTouchListener);
		linear = (LinearLayout) findViewById(R.id.map_linear_layout);
		mBtnDrive = (Button) findViewById(R.id.drive_btn);
		mBtnTransit = (Button) findViewById(R.id.transit_btn);
		mBtnWalk = (Button) findViewById(R.id.walk_btn);
		searchBtn = (Button) findViewById(R.id.search_btn);
		editStart = (AutoCompleteTextView) findViewById(R.id.start_ACTextView);
		editEnd = (AutoCompleteTextView) findViewById(R.id.end_ACTextView);
		searchACTV = (AutoCompleteTextView) findViewById(R.id.search_ACTextView);
		mbtnLoc = (Button) findViewById(R.id.auto_loc_button);
		mBtnHit = (Button) findViewById(R.id.screen_hit_btn);
		routeBtn = (Button) findViewById(R.id.map_route_btn);

		viewCache = getLayoutInflater()
				.inflate(R.layout.custom_text_view, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);

		popleftText = (TextView) viewCache.findViewById(R.id.popleft);
		poprightText = (TextView) viewCache.findViewById(R.id.popright);

		searchBtn.setOnClickListener(this);
		routeBtn.setOnClickListener(this);
		locData = new LocationData();
		mbtnLoc.setOnClickListener(this);
		mBtnHit.setOnClickListener(this);
		mBtnDrive.setOnClickListener(this);
		mBtnTransit.setOnClickListener(this);
		mBtnWalk.setOnClickListener(this);
		initLocationService();
		initAutoCompleteTextView();
		// getCustomInfoFromAssetsXML();
		initNode();
		mMapView.regMapStatusChangeListener(listener);
		Drawable mark = getResources().getDrawable(R.drawable.baidumap_ico);
		itemOverlay = new CustomIconOverlay(mark, mMapView);
		textOverlay = new CustomTextOverlay(mMapView);
		paintOverlay();
		mMapView.getOverlays().add(itemOverlay);
		mMapView.getOverlays().add(textOverlay);
		mMapView.refresh();
	}

	boolean isClear = false;
	int fontSize = 23;

	public int fun(int a) {
		return 2 * a - 8;
	}

	boolean flag;
	MKMapStatusChangeListener listener = new MKMapStatusChangeListener() {
		public void onMapStatusChange(MKMapStatus mapStatus) {
			// 字体跟随地图缩放变化
			// if (mapStatus.zoom >= 17) {
			//
			// if (fun((int) mapStatus.zoom) != fontSize) {
			// fontSize = fun((int) mapStatus.zoom);
			// BMapUtil.removeCustomTextOverlay(mMapView);
			// getCustomInfoFromAssetsXML();
			// isClear = false;
			// mMapView.refresh();
			// } else if (fun((int) mapStatus.zoom) == fontSize && isClear) {
			// getCustomInfoFromAssetsXML();
			// isClear = false;
			// mMapView.refresh();
			// }
			// } else {
			// mMapView.getOverlays().clear();
			// mMapView.refresh();
			// isClear = true;
			// }
			if (mapStatus.zoom < 17) {
				MapUtil.removeCustomTextOverlay(mMapView);
				// textOverlay.removeAll();
				// itemOverlay.removeAll();
				//
				mMapView.getOverlays().clear();
				mMapView.refresh();
				flag = true;
			}
			if (flag && mapStatus.zoom >= 17) {
				flag = false;
				mMapView.getOverlays().add(itemOverlay);
				mMapView.getOverlays().add(textOverlay);
				mMapView.refresh();
			}
		}
	};

	MKMapTouchListener MymapTouchListener = new MKMapTouchListener() {
		@Override
		public void onMapClick(GeoPoint point) {
			// 在此处理地图点击事件
			if (pop != null) {
				pop.hidePop();
			}
			if (enClickAble) {
				editEnd.setText("地图上的点");
				routeEndPoint = point;
				Toast.makeText(getApplicationContext(), point.toString(),
						Toast.LENGTH_LONG).show();
				mMapView.getController().enableClick(false);
				enClickAble = false;
			}
		}

		@Override
		public void onMapDoubleClick(GeoPoint point) {

		}

		@Override
		public void onMapLongClick(GeoPoint point) {

		}
	};
	/**
	 * AutoCompleteTextView监听器
	 */
	TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adapter.clear(); // 清空adapter适配器
			if (s.length() > 0) {
				for (String key : mAppContext.pointMap.keySet()) {
					if (key.indexOf(s.toString().trim()) != -1) {
						adapter.add(key);
					}
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	/**
	 * 初始化，自动补全框的功能
	 */
	public void initAutoCompleteTextView() {

		String[] autoStrings = new String[] { "北区食堂", "南区食堂" };
		ArrayList<String> lst = new ArrayList<String>();
		// 设置ArrayAdapter，并且设定以单行下拉列表风格展示（第二个参数设定）。
		lst.addAll(Arrays.asList(autoStrings));
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, lst);
		// 设置AutoCompleteTextView的Adapter
		editStart.setAdapter(adapter);
		editEnd.setAdapter(adapter);
		searchACTV.setAdapter(adapter);
		// getCustomInfoFromAssetsXML();
		editStart.addTextChangedListener(watcher);
		editEnd.addTextChangedListener(watcher);
		searchACTV.addTextChangedListener(watcher);
	}

	/**
	 * 初始化定位服务
	 */
	public void initLocationService() {

		locationClient = new LocationClient(this);
		// 设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 是否打开GPS
		option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
		option.setPriority(LocationClientOption.NetWorkFirst); // 设置定位优先级
		option.setProdName("com.zero.jaxuapp"); // 设置产品线名称
		option.setScanSpan(UPDATE_TIME); // 设置定时定位的时间间隔。单位毫秒
		locationClient.setLocOption(option);

		// 定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mMapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();
		// 注册位置监听器

		locationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null) {
					return;
				}
				locData.latitude = location.getLatitude();
				locData.longitude = location.getLongitude();
				// 如果不显示定位精度圈，将accuracy赋值为0即可
				locData.accuracy = location.getRadius();
				// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
				locData.direction = location.getDerect();
				// 更新定位数据
				myLocationOverlay.setData(locData);
				mMapView.getController().animateTo(
						new GeoPoint((int) (locData.latitude * 1e6),
								(int) (locData.longitude * 1e6)));
				// 更新图层数据执行刷新后生效
				mMapView.refresh();
				routeStartPoint = new GeoPoint(
						(int) (location.getLatitude() * 1E6), (int) (location
								.getLongitude() * 1E6));
				editStart.setText("当前位置");
				//
				locationClient.stop();
				Toast.makeText(MapActivity.this, "定位成功", Toast.LENGTH_SHORT)
						.show();

			}

			@Override
			public void onReceivePoi(BDLocation location) {
			}
		});
	}

	/**
	 * 显示起点选择对话框
	 * 
	 * @param way
	 * @param startArray
	 * @param endArray
	 */
	public void showStartDialog(final int way, final String[] startArray,
			final String[] endArray) {
		new AlertDialog.Builder(MapActivity.this)
				.setTitle("请选择起点")
				.setSingleChoiceItems(startArray, -1,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// flag为true表示选择起点，false表示选择终点
								editStart.setText(startArray[which]);
							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (!editEnd.getText().toString().equals(STATU_END)) {
							showEndDialog(way, endArray);
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create().show();
	}

	/**
	 * 显示终点选择对话框
	 * 
	 * @param way
	 * @param endArray
	 */
	public void showEndDialog(final int way, final String[] endArray) {
		new AlertDialog.Builder(MapActivity.this)
				.setTitle("请选择终点")
				.setSingleChoiceItems(endArray, -1,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								editEnd.setText(endArray[which]);
							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						searchRoute(way);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).create().show();
	}

	/**
	 * 发起路线规划搜索
	 * 
	 * @param way表示查询的方式
	 */
	public void searchRoute(int way) {
		route = null;
		routeOverlay = null;
		transitOverlay = null;
		// mBtnPre.setVisibility(View.INVISIBLE);
		// mBtnNext.setVisibility(View.INVISIBLE);
		// 发起搜索
		String start = editStart.getText().toString().trim();
		MKPlanNode stNode = new MKPlanNode();
		if (start != null && !start.equals("当前位置")) {
			if (mAppContext.pointMap.containsKey(start))
				stNode.pt = mAppContext.pointMap.get(start);
			else
				stNode.name = start;
		} else if (start.equals("当前位置")) {
			stNode.pt = routeStartPoint;
		} else {
			Toast.makeText(MapActivity.this, "请输入起点", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		String end = editEnd.getText().toString().trim();
		MKPlanNode enNode = new MKPlanNode();

		if (end != null && !end.equals("地图上的点")) {
			if (mAppContext.pointMap.containsKey(end))
				enNode.pt = mAppContext.pointMap.get(end);
			else
				enNode.name = end;

		} else if (end.equals("地图上的点")) {
			enNode.pt = routeEndPoint;
		} else {
			Toast.makeText(MapActivity.this, "请输入终点或在地图上选点", Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (way == VEHICLE_DRIVE) {
			mSearch.drivingSearch("南昌", stNode, "南昌", enNode);
		} else if (way == VEHICLE_TRANSIT) {
			mSearch.transitSearch("南昌", stNode, enNode);
		} else {
			mSearch.walkingSearch("南昌", stNode, "南昌", enNode);
		}
	}

	/**
	 * 处理搜索时起点和终点不唯一的情况下，数据的筛选
	 * 
	 * @param stPois
	 * @param enPois
	 * @param way
	 */
	public void dealWithMKSearchData(ArrayList<MKPoiInfo> stPois,
			ArrayList<MKPoiInfo> enPois, int way) {
		String[] start = new String[stPois.size()];
		for (int i = 0; i < stPois.size(); i++) {
			start[i] = stPois.get(i).name;
		}
		String[] end = new String[enPois.size()];
		for (int i = 0; i < enPois.size(); i++) {
			end[i] = enPois.get(i).name;
		}

		if (!editStart.getText().toString().equals(STATU_START)) {
			showStartDialog(way, start, end);
		} else if (!editEnd.getText().toString().equals(STATU_END)) {
			showEndDialog(way, end);
		} else {
			Toast.makeText(MapActivity.this, "无法找到该地址!", Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * 线路规划、检索POI监听器
	 */
	MKSearchListener myMKSearchListener = new MKSearchListener() {

		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
				ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
				dealWithMKSearchData(stPois, enPois, VEHICLE_DRIVE);
				return;
			}
			// 错误号可参考MKEvent中的定义
			if (error != 0 || res == null) {
				Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			searchType = 0;
			routeOverlay = new RouteOverlay(MapActivity.this, mMapView);
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			// 清除其他图层
			MapUtil.removeNotCustomOverlay(mMapView);
			// 添加路线图层
			mMapView.getOverlays().add(routeOverlay);
			// 执行刷新使生效
			mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			// 移动地图到起点
			mMapView.getController().animateTo(res.getStart().pt);
			route = res.getPlan(0).getRoute(0);
			// 重置路线节点索引，节点浏览时使用
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
		}

		public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			// 起点或终点有歧义，需要选择具体的城市列表或地址列表
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
				ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
				dealWithMKSearchData(stPois, enPois, VEHICLE_TRANSIT);
			}
			if (error != 0 || res == null) {
				Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			searchType = 1;
			transitOverlay = new TransitOverlay(MapActivity.this, mMapView);
			// 此处仅展示一个方案作为示例
			transitOverlay.setData(res.getPlan(0));
			// 清除其他图层
			MapUtil.removeNotCustomOverlay(mMapView);
			// 添加路线图层
			mMapView.getOverlays().add(transitOverlay);
			// 执行刷新使生效
			mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(transitOverlay.getLatSpanE6(),
					transitOverlay.getLonSpanE6());
			// 移动地图到起点
			mMapView.getController().animateTo(res.getStart().pt);
			// 重置路线节点索引，节点浏览时使用
			nodeIndex = 0;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);

		}

		public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			// 起点或终点有歧义，需要选择具体的城市列表或地址列表
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				// 遍历所有地址
				ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
				ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
				dealWithMKSearchData(stPois, enPois, VEHICLE_WALK);
				return;
			}
			if (error != 0 || res == null) {
				Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			searchType = 2;
			routeOverlay = new RouteOverlay(MapActivity.this, mMapView);
			// 此处仅展示一个方案作为示例
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			// 清除其他图层
			MapUtil.removeNotCustomOverlay(mMapView);
			// 添加路线图层
			mMapView.getOverlays().add(routeOverlay);
			// 执行刷新使生效
			mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			// 移动地图到起点
			mMapView.getController().animateTo(res.getStart().pt);
			route = res.getPlan(0).getRoute(0);
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
		}

		public void onGetAddrResult(MKAddrInfo res, int error) {

		}

		public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
		}

		public void onGetBusDetailResult(MKBusLineResult result, int iError) {
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
		}

		@Override
		public void onGetPoiDetailSearchResult(int type, int iError) {
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult result, int type,
				int error) {
		}
	};
	OnClickListener nodeClickListener = new OnClickListener() {
		public void onClick(View v) {
			// 浏览路线节点
			nodeClick(v);
		}
	};

	public void initNode() {
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);
		mBtnPre.setOnClickListener(nodeClickListener);
		mBtnNext.setOnClickListener(nodeClickListener);
		createPaopao();
	}

	public void createPaopao() {

		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				if (index == 0 || index == 2) {
					if (routeBtn.getText().equals("线路")) {
						linear.setVisibility(View.VISIBLE);

						editStart.setText("");
						editEnd.setText("");

						routeBtn.setText("返回");
					} else if (routeBtn.getText().equals("返回")) {
						linear.setVisibility(View.GONE);
						routeBtn.setText("线路");
						// 清楚线路层
						MapUtil.removeNotCustomOverlay(mMapView);
					}
				}
				if (index == 0) {
					editStart.setText(popupText.getText());
				} else if (index == 2) {
					editEnd.setText(popupText.getText());
				}
			}
		};
		pop = new PopupOverlay(mMapView, popListener);
	}

	public void nodeClick(View v) {
		if (searchType == 0 || searchType == 2) {
			// 驾车、步行使用的数据结构相同，因此类型为驾车或步行，节点浏览方法相同
			if (nodeIndex < -1 || route == null
					|| nodeIndex >= route.getNumSteps())
				return;

			if (mBtnPre.equals(v) && nodeIndex > 0) {
				nodeIndex--;
				mMapView.getController().animateTo(
						route.getStep(nodeIndex).getPoint());
				popupText.setBackgroundResource(R.drawable.map_popup);
				popupText.setText(route.getStep(nodeIndex).getContent());
				pop.showPopup(MapUtil.getBitmapFromView(popupText), route
						.getStep(nodeIndex).getPoint(), 5);
			}
			if (mBtnNext.equals(v) && nodeIndex < (route.getNumSteps() - 1)) {
				nodeIndex++;
				mMapView.getController().animateTo(
						route.getStep(nodeIndex).getPoint());
				popupText.setBackgroundResource(R.drawable.map_popup);
				popupText.setText(route.getStep(nodeIndex).getContent());
				pop.showPopup(MapUtil.getBitmapFromView(popupText), route
						.getStep(nodeIndex).getPoint(), 5);
			}
		}
		if (searchType == 1) {
			if (nodeIndex < -1 || transitOverlay == null
					|| nodeIndex >= transitOverlay.getAllItem().size())
				return;

			if (mBtnPre.equals(v) && nodeIndex > 1) {
				nodeIndex--;
				mMapView.getController().animateTo(
						transitOverlay.getItem(nodeIndex).getPoint());
				popupText.setBackgroundResource(R.drawable.map_popup);
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
				pop.showPopup(MapUtil.getBitmapFromView(popupText),
						transitOverlay.getItem(nodeIndex).getPoint(), 5);
			}
			if (mBtnNext.equals(v)
					&& nodeIndex < (transitOverlay.getAllItem().size() - 2)) {
				nodeIndex++;
				mMapView.getController().animateTo(
						transitOverlay.getItem(nodeIndex).getPoint());
				popupText.setBackgroundResource(R.drawable.map_popup);
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle());
				pop.showPopup(MapUtil.getBitmapFromView(popupText),
						transitOverlay.getItem(nodeIndex).getPoint(), 5);
			}
		}
	}

	public void paintOverlay() {
		for (CustomItem c : mAppContext.pointList) {
			c.getTextItem().fontSize = fontSize;
			textOverlay.addText(c.getTextItem());
			itemOverlay.addItem(c.getOverlayItem());
		}
	}

	@Override
	public void onClick(View v) {
		int ItemId = v.getId();// 获取组件的id值
		switch (ItemId) {
		case R.id.map_route_btn:
			if (routeBtn.getText().equals("线路")) {
				linear.setVisibility(View.VISIBLE);
				routeBtn.setText("返回");
			} else if (routeBtn.getText().equals("返回")) {
				linear.setVisibility(View.GONE);
				routeBtn.setText("线路");
				mBtnPre.setVisibility(View.GONE);
				mBtnNext.setVisibility(View.GONE);

				editEnd.setText("");
				editStart.setText("");
				MapUtil.removeNotCustomOverlay(mMapView);
			}
			break;
		case R.id.auto_loc_button:
			if (locationClient == null) {
				return;
			}
			locationClient.start();
			locationClient.requestLocation();
			break;
		case R.id.screen_hit_btn:
			enClickAble = true;
			mMapView.getController().enableClick(true);
			break;
		case R.id.drive_btn:
			searchRoute(VEHICLE_DRIVE);
			break;
		case R.id.transit_btn:
			searchRoute(VEHICLE_TRANSIT);
			break;
		case R.id.walk_btn:
			if (editStart.getText().toString().trim() == null
					|| editEnd.getText().toString().trim() == null) {
				Toast.makeText(getApplicationContext(), "请输入始终点",
						Toast.LENGTH_SHORT).show();
				break;
			} else {
				searchRoute(VEHICLE_WALK);
			}
			break;
		case R.id.search_btn:
			String place = searchACTV.getText().toString().trim();
			if (place == null) {
				Toast.makeText(getApplicationContext(), "请输入搜索地点",
						Toast.LENGTH_SHORT).show();
			} else {
				if (mAppContext.pointMap.get(place) == null) {
					Toast.makeText(getApplicationContext(), "未找到该地点",
							Toast.LENGTH_SHORT).show();
				} else {
					mMapController.setCenter(mAppContext.pointMap.get(place));
					popleftText
							.setBackgroundResource(R.drawable.map_popup_side);
					popleftText.setText("从这出发");
					poprightText
							.setBackgroundResource(R.drawable.map_popup_side);
					poprightText.setText("到这来");
					popupText.setBackgroundResource(R.drawable.map_popup);
					popupText.setText(place);
					Bitmap[] b = new Bitmap[3];
					b[0] = MapUtil.getBitmapFromView(popleftText);
					b[1] = MapUtil.getBitmapFromView(popupText);
					b[2] = MapUtil.getBitmapFromView(poprightText);
					pop.showPopup(b, mAppContext.pointMap.get(place), 5);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		mMapView.destroy();
		mSearch.destory();
		if (locationClient != null)
			locationClient.stop();
		if (mBMapManager != null) {
			mBMapManager.destroy();
			mBMapManager = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.getOverlays().clear();
		mMapView.onPause();
		isFrist = false;
		if (mBMapManager != null) {
			mBMapManager.stop();
		}
		MobclickAgent.onPageEnd("SplashScreen");
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("SplashScreen"); // 统计页面
		mMapView.onResume();
		if (mBMapManager != null) {
			mBMapManager.start();
		}
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

}