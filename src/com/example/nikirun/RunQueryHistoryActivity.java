package com.example.nikirun;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnTrackListener;
import com.example.nikirun.HistoryTrackData.Points;
import com.google.gson.Gson;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class RunQueryHistoryActivity extends Activity {
	
	
	private int queryStartTime = 0;
    private int queryEndTime = 0;
    
    public static String startRunTime="",finishRunTime="";
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
	
	private static Gson gson;
	
	public static boolean isUpdateRunData = false;
	
	 
	
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
        
        // 不显示地图缩放控件（按钮控制栏）  
//        bMapView.showZoomControls(false);
        
        Intent intent = getIntent();
        String historyTrace = intent.getStringExtra(RunHistoryDataFragment.HISTORY_TRACE);
        boolean isOnlyQuery = intent.getBooleanExtra(RunHistoryDataFragment.ISONLYQUERY, false);
        if(isOnlyQuery && historyTrace != null){
        	showHistoryTrack(historyTrace);
        }
        else{
        	initOnTrackListener();
    		
    		setQueryTime();
    		
    		queryHistoryTrack();
    		
    		mDbHelper = new RunDatabaseHelper(getApplicationContext());
        }
		
		
		
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
		
		HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack, 
				HistoryTrackData.class);

		List<Points> pointList = new ArrayList<HistoryTrackData.Points>();
		List<LatLng> latLngList  =new ArrayList<LatLng>();
		if(historyTrackData != null && historyTrackData.getStatus() == 0 ){
			if(historyTrackData.getListPoints() != null)
			latLngList.addAll(historyTrackData.getListPoints());
			pointList.addAll(historyTrackData.getPoints());
		}
		
		getTracePoint(pointList);
		
		// 绘制历史轨迹
        drawHistoryTrack(latLngList,historyTrackData.distance);
        
        
        //若只是在RunHistoryDataFragment 中查看则不更新数据库
        if(isUpdateRunData){
        	 SQLiteDatabase database = mDbHelper.getWritableDatabase();
             ContentValues contentValues = new ContentValues();
//             startRunTime = "2016-08-12 14:20:12";
//             finishRunTime = "2016-8-12 15:10:10";
             contentValues.put(COLUMN_RUN_START_DATE, startRunTime);
             contentValues.put(COLUMN_RUN_END_DATE, finishRunTime);
             contentValues.put(COLUMN_RUN_TRACK_DATA, historyTrack);
             database.insert(TABLE_RUN, null, contentValues);
        }
       
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
			finishRunTime = points0.create_time;
	      
			String eTime = DateUtils.getInterval(startRunTime, finishRunTime);
			Log.i(MainActivity.TAG,"finished Time=="+finishRunTime+"eTime ="+eTime);
			
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

	
	 
}
