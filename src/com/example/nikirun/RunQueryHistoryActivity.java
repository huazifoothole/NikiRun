package com.example.nikirun;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.platform.comapi.map.r;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnTrackListener;
import com.example.nikirun.HistoryTrackData.Points;
import com.google.gson.Gson;

import android.R.string;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RunQueryHistoryActivity extends Activity implements OnClickListener{
	
	
	private int queryStartTime = 0;
    private int queryEndTime = 0;
    
    public static String startRunTime="",finishRunTime="",expendTime="",userRunedData="";
    private RunDatabaseHelper mDbHelper;
    
    private static final String TABLE_RUN = "run";
    private static final String COLUMN_RUN_START_DATE = "start_date";
	private static final String COLUMN_RUN_END_DATE = "end_date";
	private static final String COLUMN_RUN_TRACK_DATA = "track_data";
	 
    
    private int year = 0;
    private int month = 0;
    private int day = 0;
	
 // 起点图标覆盖物
    private static MarkerOptions startMarker = null;
 // 终点图标覆盖物
    private static MarkerOptions endMarker = null;
 // 路线图标覆盖物
    private static PolylineOptions polylineOptions = null;
    
    private static MarkerOptions markerOptions = null;
    
    private MapStatusUpdate mapStatusUpdate = null;
    
    //起点图标
    private static BitmapDescriptor bmStart;
    //终点图标
    private static BitmapDescriptor bmEnd;
	
    private OnTrackListener mOnTrackListener;
	
	private LBSTraceClient mLBSTraceClient;
	
	protected static BaiduMap mBaiduMap = null;
	
	protected static MapView bMapView = null;  
	
	public static boolean isUpdateRunData = false;
	
	private TextView mDistanceView;
	
	private TextView mSpeedView;
	
	private TextView mExpendView;
	
	private static boolean isOnlyQuery = false;
	
	private Button submitButton;
	
	private HistoryTrackData mTrackData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.runhistorytrace);
		
		 // 初始化轨迹服务客户端
        mLBSTraceClient = new LBSTraceClient(getApplicationContext());

        // 设置定位模式
        mLBSTraceClient.setLocationMode(LocationMode.High_Accuracy);
        
        
        
        bMapView = (MapView) findViewById(R.id.bdmapview);
        mBaiduMap = bMapView.getMap();
        
        mDistanceView = (TextView) findViewById(R.id.distance_view);
        mSpeedView = (TextView) findViewById(R.id.speed_view);
        mExpendView = (TextView) findViewById(R.id.expandtime_view);
        // 不显示地图缩放控件（按钮控制栏）  
//        bMapView.showZoomControls(false);
        
        Intent intent = getIntent();
        String historyTrace = intent.getStringExtra(RunHistoryDataFragment.HISTORY_TRACE);
        isOnlyQuery = intent.getBooleanExtra(RunHistoryDataFragment.ISONLYQUERY, false);
        if(isOnlyQuery && historyTrace != null){
        	showHistoryTrack(historyTrace);
        }
        else{
        	initOnTrackListener();
    		
    		setQueryTime();
    		
    		queryHistoryTrack();
    		
    		mDbHelper = new RunDatabaseHelper(getApplicationContext());
        }
		
		submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mDbHelper != null)
			mDbHelper.close();
	}
	
	 /**
     * 初始化轨迹监听器OnTrackListener
     */
	
	private void initOnTrackListener() {
		
		mOnTrackListener = new OnTrackListener() {
			
			// 请求失败回调接口
			@Override
			public void onRequestFailedCallback(String arg0) {
				// TODO Auto-generated method stub
				Looper.prepare();
				Toast.makeText(getApplicationContext(), "track请求失败回调接口消息 :"+arg0, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
			
			// 查询历史轨迹回调接口
			@Override
			public void onQueryHistoryTrackCallback(String historyData) {
				// TODO Auto-generated method stub
				super.onQueryHistoryTrackCallback(historyData);
				isUpdateRunData = true;
				showHistoryTrack(historyData);
			}
		};
	}
	
	private void setQueryTime(){
		int[] date = null;
		if(startRunTime == "" || finishRunTime == ""){
			if(year == 0 && month == 0 && day == 0){
				 String curDate = DateUtils.getCurrentDate();
				 date = DateUtils.getYMDArray(curDate, "-");
			}
			
			if(date != null){
				year = date[0];
				month = date[1];
				day = date[2];
			}
			startRunTime = year +"-"+month +"-"+day +" 00:00:00";
			finishRunTime = year +"-"+month +"-"+day +" 23:59:59";
		}
		
		queryStartTime = Integer.parseInt(DateUtils.getTimeToStamp(startRunTime));
		queryEndTime = Integer.parseInt(DateUtils.getTimeToStamp(finishRunTime));
		
	}
	
	//查询历史轨迹
	private void queryHistoryTrack() {
		//entity 标识
		String entityName = MainActivity.entityName;
		 // 是否返回精简的结果（0 : 否，1 : 是）
		 int simpleReturn = 0;
		// 开始时间
	        if (queryStartTime == 0) {
	            queryStartTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
	        }
	        if (queryEndTime == 0) {
	            queryEndTime = (int) (System.currentTimeMillis() / 1000);
	        }
	        // 分页大小
	        int pageSize = 1000;
	        // 分页索引
	        int pageIndex = 1;
	        
	        mLBSTraceClient.setOnTrackListener(mOnTrackListener);
	        
	        mLBSTraceClient.queryHistoryTrack(RunTraceService.serviceId, entityName, simpleReturn, 
	        		queryStartTime, queryEndTime, pageSize, pageIndex, mOnTrackListener);
	         
	}
	
	public void showHistoryTrack(String historyTrack) {
		
		Log.i(MainActivity.TAG, "showHistoryTrack");
		mTrackData = GsonService.parseJson(historyTrack, 
				HistoryTrackData.class);
		userRunedData = historyTrack;
		List<Points> pointList = new ArrayList<HistoryTrackData.Points>();
		List<LatLng> latLngList  =new ArrayList<LatLng>();
		if(mTrackData != null && mTrackData.getStatus() == 0 ){
			if(mTrackData.getListPoints() != null)
			latLngList.addAll(mTrackData.getListPoints());
			pointList.addAll(mTrackData.getPoints());
		}
		
		getTracePoint(pointList);
		
		// 绘制历史轨迹
        drawHistoryTrack(latLngList,mTrackData.distance);

        
        if(isOnlyQuery){
			Intent intent = getIntent();
	        startRunTime = intent.getStringExtra(RunHistoryDataFragment.COLUMN_RUN_START_DATE);
	        finishRunTime = intent.getStringExtra(RunHistoryDataFragment.COLUMN_RUN_END_DATE);
		}else{
			checkUserData();
		}
			
       
		expendTime = DateUtils.getInterval(startRunTime, finishRunTime);
		Log.i(MainActivity.TAG,"start time="+startRunTime+"finished Time=="+finishRunTime+"eTime ="+expendTime);

        
        //若只是在RunHistoryDataFragment 中查看则不更新数据库
//        if(isUpdateRunData){
//        	 SQLiteDatabase database = mDbHelper.getWritableDatabase();
//             ContentValues contentValues = new ContentValues();
////             startRunTime = "2016-08-12 14:20:12";
////             finishRunTime = "2016-8-12 15:10:10";
//             contentValues.put(COLUMN_RUN_START_DATE, startRunTime);
//             contentValues.put(COLUMN_RUN_END_DATE, finishRunTime);
//             contentValues.put(COLUMN_RUN_TRACK_DATA, historyTrack);
//             database.insert(TABLE_RUN, null, contentValues);
//             
//        }
        
        mDistanceView.setText((int)mTrackData.distance + "m");
       
        
        
	}
	
	private void getTracePoint(final List<HistoryTrackData.Points> points){
		//获取位置points信息
		if(points.size() == 1){
			points.add(points.get(0));
		}
		if(points == null || points.size() == 0){
			TrackApplication.showMessage("当前查询无轨迹点");
		}else if(points.size() > 1){
			Points points0 = points.get(0);
		 
			
		}
		
		  
	}
	
	/** 绘制历史轨迹
    * 
    * @param points
    */
	private void drawHistoryTrack(final List<LatLng> points,final double distance) {
		// 绘制新覆盖物前，清空之前的覆盖物
		
		mBaiduMap.clear();
		
		if(points.size() == 1){
			points.add(points.get(0));
		}
		
		if(points == null || points.size() == 0){
			TrackApplication.showMessage("当前查询无轨迹点");
			resetMarker();
		}else if (points.size() > 1) {
			
			LatLng llc = points.get(0);
			LatLng llD = points.get(points.size() - 1);
			LatLngBounds bounds = new LatLngBounds.Builder()
					.include(llc).include(llD).build();
			
			mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);
			 
			//起点图标
			bmStart = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
			bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.icon_end);
			
			//添加起点图标
			startMarker = new MarkerOptions()
					.position(points.get(points.size() - 1)).icon(bmStart)
					.zIndex(9).draggable(true);
			
			//添加终点图标
			endMarker = new MarkerOptions().position(points.get(0))
					.icon(bmEnd).zIndex(9).draggable(true);
			
			//添加路线
			polylineOptions = new PolylineOptions().width(10)
					.color(Color.RED).points(points);
			
			markerOptions = new MarkerOptions();
			markerOptions.flat(true);
			markerOptions.anchor(0.5f, 0.5f);
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.icon_gcoding));
			markerOptions.position(points.get(points.size() - 1));
			
			addMarker();
			
		}
	}
	
	/**
    * 添加覆盖物
    */
	protected void addMarker() {
		
		if(null != mapStatusUpdate){
			MapStatusUpdate ms = MapStatusUpdateFactory.zoomTo(19);
			mBaiduMap.setMapStatus(mapStatusUpdate);
			mBaiduMap.animateMapStatus(ms);
			Log.i(MainActivity.TAG, "zoom level = "+ mBaiduMap.getMaxZoomLevel());
		}
		
		if(null != startMarker){
			mBaiduMap.addOverlay(startMarker);
		}
		
		if(null != endMarker){
			mBaiduMap.addOverlay(endMarker);
		}
		
		if(null != polylineOptions){
			mBaiduMap.addOverlay(polylineOptions);
		}
	}
	
	/**
     * 重置覆盖物
     */
    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        polylineOptions = null;
    }

	private void saveData(){
		Log.i(MainActivity.TAG, "submit---");
		if(startRunTime != "" && expendTime != "" && userRunedData != ""){
			RunUser runner =  BmobUser.getCurrentUser(RunUser.class);
			UserRunData user = new UserRunData();
			user.setRunDate(startRunTime); 
			user.setRunTime(expendTime);
			user.setRunData(userRunedData); 
			HistoryTrackData trackData = GsonService.parseJson(userRunedData, 
					HistoryTrackData.class);
			double distance = ((int)(trackData.distance*100))/100;
			Log.i(MainActivity.TAG, "distance ="+distance);
			user.setRunDistance(distance);
			user.setRunUser(runner);
			user.save(new SaveListener<String>() {

				@Override
				public void done(String arg0, BmobException e) {
					// TODO Auto-generated method stub
					if(e == null){
						Toast.makeText(getApplicationContext(), "save data success", Toast.LENGTH_SHORT).show();
					}else {
						Log.i(MainActivity.TAG,"save 失败："+e.getMessage()+","+e.getErrorCode());
					}
				}
			});
		}
	}
	
	
	private void updateData(UserRunData userData){
		Log.i(MainActivity.TAG, "submit---");
		if(startRunTime != "" && expendTime != "" && userRunedData != ""){
			RunUser runner =  BmobUser.getCurrentUser(RunUser.class);
			userData.setRunDate(startRunTime); 
			userData.setRunTime(expendTime);
			userData.setRunData(userRunedData); 
			double distance = ((int)(mTrackData.distance*100))/100;
			userData.setRunDistance(distance);
			userData.setRunUser(runner);
			Log.i(MainActivity.TAG, "runner name ="+runner.getUsername());
			userData.update(new UpdateListener() {
				
				@Override
				public void done(BmobException e) {
					// TODO Auto-generated method stub
					if(e == null){
						Toast.makeText(getApplicationContext(), "update data success", Toast.LENGTH_SHORT).show();
					}else {
						Log.i(MainActivity.TAG,"update 失败："+e.getMessage()+","+e.getErrorCode());
					}
				}
			});
		}
	}
	
	private void checkUserData(){
		BmobQuery<UserRunData> query = new BmobQuery<UserRunData>();
		query.addWhereEqualTo("runDate", startRunTime);
		query.findObjects(new FindListener<UserRunData>() {
			
			@Override
			public void done(List<UserRunData> dataList, BmobException e) {
				// TODO Auto-generated method stub
				if(e == null){
					//已有数据
					Log.i(MainActivity.TAG, "check data ok");
					if(dataList.size()>0){
						updateData(dataList.get(0));
					}else{
						Log.i(MainActivity.TAG, "check data size = 0");
						saveData();
					}
				}else{
					Log.i(MainActivity.TAG, "can not find");
				}
			}
		});
		 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.submit:
				checkUserData();
			break;
		default:
			break;
		}
		 
	}
	
	 
}
