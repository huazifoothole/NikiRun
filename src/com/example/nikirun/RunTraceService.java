package com.example.nikirun;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class RunTraceService extends Service {
	
	private static final String TAG = "RunTraceLog";
	
	//轨迹服务
	protected static Trace trace = null;
	
	public static final long serviceId = 119442;
	
	// 轨迹服务类型
    //0 : 不建立socket长连接， 
    //1 : 建立socket长连接但不上传位置数据
    //2 : 建立socket长连接并上传位置数据）
	private int traceType = 2;
	
	//轨迹服务客户端
	public static LBSTraceClient client;
	
	// Entity监听器
	public static OnEntityListener entityListener = null;
	
	// 开启轨迹服务监听器
	protected static OnStartTraceListener startTraceListener = null;
	
	protected static OnStopTraceListener stopTraceListener = null;
	
	private static OnTrackListener trackListener = null;
	
	//采集周期 s
	private int gatherInterval = 10;
	
	//打包周期
	private int packInterval = 20;
	
	protected static boolean isTraceStart = false;
	
	public static double firstLatitude ,firstLongitude;
	
	boolean isOriginLn = true;
	
	// 手机IMEI号设置为唯一轨迹标记号,只要该值唯一,就可以作为轨迹的标识号,使用相同的标识将导致轨迹混乱
    private String entityName= MainActivity.entityName;
    
    private TraceLocation mTraceLocation;

    private TraceBinder mTraceBinder = new TraceBinder();
   
    class TraceBinder extends Binder{
    	public void startRun() {
    		startTrace();
		}
    	
    	public TraceLocation getRealtimeRunData(){
    		return mTraceLocation;
    	}
    }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mTraceBinder;
	
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		init();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
 
		return super.onStartCommand(intent, START_STICKY, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopTrace();
	}
	
	private void init() {
//		imei = getImei(getApplicationContext())
		//初始化轨迹客户端
		client = new LBSTraceClient(this);
		
		//设置定位模式
		client.setLocationMode(LocationMode.High_Accuracy);
		
		// 初始化轨迹服务
		trace = new Trace(this, serviceId, entityName ,traceType);
		
		//采集周期，上传周期
		client.setInterval(gatherInterval, packInterval);
		
		//设置http协议类型 0：http 1：https
		client.setProtocolType(0);
		
		//初始化监听器
		initListener();
		
	}
	
	//开启轨迹服务
	private void startTrace() {
		// 通过轨迹服务客户端client开启轨迹服务
		client.startTrace(trace,startTraceListener);
		
	}
	
	//停止轨迹服务
	private void stopTrace(){
		// 通过轨迹服务客户端client停止轨迹服务
		Log.i(TAG, "stopTrace(), isTraceStart : " + isTraceStart);
		
			client.stopTrace(trace, stopTraceListener);
		 
	}
	
	//初始化监听器
	private void initListener() {
		
		initOnEntityListener();
		
		// 初始化开启轨迹服务监听器
		initOnStartTraceListener();
		
		//初始化停止轨迹服务监听器
		initOnStopTraceListener();
		
		client.queryRealtimeLoc(serviceId, entityListener);
		
	}

	
	private void initOnStartTraceListener() {
		// 初始化startTraceListener
		startTraceListener = new OnStartTraceListener() {
			
			// 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTracePushCallback(byte arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i(TAG, "轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]");
			}
			
			// 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTraceCallback(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i(TAG, "开启轨迹回调接口 [消息编码 : " + arg0 + "，消息内容 : " + arg1 + "]");
				if(0 == arg0 || 10006 ==arg0){
					isTraceStart = true;
				}
			}
			
		};
	}
	
	private void initOnStopTraceListener() {
		stopTraceListener = new OnStopTraceListener() {
			
			@Override
			public void onStopTraceSuccess() {
				// TODO Auto-generated method stub
				Log.i(TAG, "停止轨迹服务成功");
				isTraceStart =false;
				stopSelf();
			}
			
			 // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onStopTraceFailed(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i(TAG, "停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]");
			}
		};
	}
	
	private void initOnEntityListener() {
		entityListener = new OnEntityListener() {
			
			//请求失败回调接口
			@Override
			public void onRequestFailedCallback(String arg0) {
				// TODO Auto-generated method stub
				Looper.prepare();
				Log.i(TAG, "entity请求失败回调接口消息 : " + arg0);
				Toast.makeText(getApplicationContext(), "entity请求失败回调接口消息 : "+arg0,Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
			
			// 添加entity回调接口
			@Override
			public void onAddEntityCallback(String arg0) {
				// TODO Auto-generated method stub
				Looper.prepare();
                Log.i(TAG, "添加entity回调接口消息 : " + arg0);
                Toast.makeText(getApplicationContext(), "添加entity回调接口消息 : " + arg0, Toast.LENGTH_SHORT).show();
                Looper.loop();
				super.onAddEntityCallback(arg0);
			}
			
			@Override
			public void onQueryEntityListCallback(String message) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onQueryEntityListCallback : " + message);
				super.onQueryEntityListCallback(message);
			}
			
			@Override
			public void onReceiveLocation(TraceLocation arg0) {
				// TODO Auto-generated method stub
				 
//				getRunnigData(arg0);
				mTraceLocation = arg0;
				if(isOriginLn){
					firstLatitude = mTraceLocation.getLatitude();
					firstLongitude = mTraceLocation.getLongitude();
					isOriginLn = false;
				}
				
				super.onReceiveLocation(arg0);
			}
		};
		
		
	}
	 
	
	 
	/**
     * 获取设备IMEI码
     * 
     * @param context
     * @return
     */
    protected static String getImei(Context context) {
        String mImei = "NULL";
        try {
            mImei = ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            System.out.println("获取IMEI码失败");
            mImei = "NULL";
        }
        return mImei;
    }
	  
	
}
