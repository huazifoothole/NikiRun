package com.example.nikirun;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.trace.TraceLocation;
import com.example.nikirun.RunTraceService.TraceBinder;

import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class RunningActivity extends Activity implements OnClickListener ,OnTouchListener{
	
	private ImageButton mRecoverRunBt;
	private ImageButton mFinishedRunBt;
	private ImageButton mPauseRunBt;
	private Button mLockButton;
	
	private TextView mAverSpeedView;
	private TextView mRunTimeView;
	private TextView mRunDistance;
	private TextView mlongPreView;
	
	private RunTraceService.TraceBinder traceBinder;
	private TraceLocation mLocation;
	
	private Timer timer;
	private TimerTask timerTask;
	final int runWhat = 108;
	
	final Handler takeTimeHandler = new Handler();
	Runnable takeTimeRunnable;
	Boolean isStartTimer = true;
	LatLng L1 , L2;
	double afterLatitude,afterLongitude;
	boolean isOriginLatLng = false;
	boolean isLockScreen = false;
	private CirclePgBar mCirclePgBar;
	private static Handler handler = new Handler();
	
	 
	 
	
	private ServiceConnection  connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		//绑定时自动调用
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			traceBinder = (TraceBinder) service;
			traceBinder.startRun();
			mLocation = traceBinder.getRealtimeRunData();
		}
	}; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		EventBus.getDefault().register(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.running);
		
		initComponent();
		
		Intent intent = new Intent(RunningActivity.this,RunTraceService.class);
		bindService(intent, connection, BIND_AUTO_CREATE);
		
		 
		getRunData();
		
		countTakeTime();
		
		RunQueryHistoryActivity.startRunTime = getCurrentTime();
		
	}
	
	 
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		finish();
	}
	
	private void initComponent() {
		mPauseRunBt = (ImageButton) findViewById(R.id.pause_run_button);
		mFinishedRunBt = (ImageButton) findViewById(R.id.finish_run_button);
		mRecoverRunBt = (ImageButton) findViewById(R.id.recovery_run_button);
		
		mFinishedRunBt.setVisibility(View.INVISIBLE);
		mRecoverRunBt.setVisibility(View.INVISIBLE);
		
		mPauseRunBt.setOnClickListener(this);
		mRecoverRunBt.setOnClickListener(this);
		mFinishedRunBt.setOnClickListener(this);
		mFinishedRunBt.setOnTouchListener(this);
		
		mAverSpeedView = (TextView) findViewById(R.id.aver_speed_view);
		mRunTimeView = (TextView) findViewById(R.id.time_view);
		mRunDistance = (TextView) findViewById(R.id.run_distance);
		
		mLockButton = (Button) findViewById(R.id.lockscreen_button);
		
		mCirclePgBar = (CirclePgBar) findViewById(R.id.circleBar);
		mCirclePgBar.setVisibility(View.INVISIBLE);
		
		mlongPreView = (TextView) findViewById(R.id.textView_longpress);
		mlongPreView.setVisibility(View.INVISIBLE);
	}
	@Override
	public void onClick(View v) {
		
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pause_run_button:
			mPauseRunBt.setVisibility(View.INVISIBLE);
			
			mFinishedRunBt.setVisibility(View.VISIBLE);
			mRecoverRunBt.setVisibility(View.VISIBLE);
			
			isStartTimer = false;
//			mCirclePgBar.setVisibility(View.VISIBLE);
//			mCirclePgBar.beginDraw();
			break;
		case R.id.recovery_run_button:
			mPauseRunBt.setVisibility(View.VISIBLE);
			mCirclePgBar.setVisibility(View.INVISIBLE);
			
			mFinishedRunBt.setVisibility(View.INVISIBLE);
			mRecoverRunBt.setVisibility(View.INVISIBLE);
			
			isStartTimer = true;
			takeTimeHandler.postDelayed(takeTimeRunnable, 1000);
			
			
			break;
//		case R.id.finish_run_button:
			//注意释放服务连接
//			if(connection != null){
//				unbindService(connection);
//			}
//			
//			Intent intent2 = new Intent(RunningActivity.this,RunQueryHistoryActivity.class);
//			startActivity(intent2);
//			//停止计时器
//			takeTimeHandler.removeCallbacks(takeTimeRunnable);
//			RunQueryHistoryActivity.finishRunTime = getCurrentTime();
//			onDestroy();
//			break;
		case R.id.lockscreen_button:
			if(!isLockScreen){
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				mLockButton.setBackground(getResources().getDrawable(R.drawable.lock_screen));
			}
			else {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				mLockButton.setBackground(getResources().getDrawable(R.drawable.unlocked_screen));
			}
	 
			
		default:
			break;
		}
		 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
		
	}
	
	private void getRunData() {
		
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				 switch (msg.what) {
				case runWhat:
					mLocation = (TraceLocation) msg.obj;
				 
					if(mLocation!=null){
//						 mAverSpeedView.setText(String.valueOf(mLocation.getSpeed()/60));
						 mAverSpeedView.setText(String.format("%4.2f", mLocation.getSpeed()/60));	

						 afterLatitude = mLocation.getLatitude();
						 afterLongitude = mLocation.getLongitude();
					
						 L1 = new LatLng(RunTraceService.firstLatitude, RunTraceService.firstLongitude);
						 L2 = new LatLng(afterLatitude, afterLongitude);
						 double runDistance = DistanceUtil.getDistance(L1, L2);
						 String distance = String.format("%4.2f", runDistance);
						 mRunDistance.setText(distance);
					}
//						 
					break;

				default:
					break;
				}
			}
		};
			
		
		timerTask = new TimerTask() {
			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(RunTraceService.isTraceStart){
						Message message = new Message();
						message.what = runWhat;
						message.obj = traceBinder.getRealtimeRunData();
						handler.sendMessage(message);
					}
					
				}
			};
		
			//4000，延时4秒后执行
			//5000,每隔5秒执行一次task
			timer = new Timer();
			timer.schedule(timerTask, 4000, 5000);
		}
	

	
	
	private void countTakeTime(){
		
		
		takeTimeRunnable = new Runnable() {
			int mSecond =0;
			int mMiuntes = 0;
			int mHour = 0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(!isStartTimer)
					return;
				
				takeTimeHandler.postDelayed(this, 1000);
				mSecond++ ;
				while(mSecond >= 60){
					mSecond = 0;
					mMiuntes++;
				}
				while(mMiuntes >= 60){
					mMiuntes = 0;
					mHour++;
				}
				String takeTime;
				
				if(mHour == 0){
					takeTime = String.format("%02d:%02d",mMiuntes,mSecond);
				}else {
					takeTime = String.format("%02d:%02d:%02d",mHour,mMiuntes,mSecond);
				}
				 
				mRunTimeView.setText(takeTime);
				
			}
		}; 
		
		takeTimeHandler.postDelayed(takeTimeRunnable, 1000);
		 
	}
	
	private String getCurrentTime(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date(System.currentTimeMillis());
		String  str = format.format(currentDate); 
		return str;
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			
			mCirclePgBar.setVisibility(View.VISIBLE);
			mlongPreView.setVisibility(View.VISIBLE);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mCirclePgBar.beginDraw(true);
						}
					});
					
				}
			}).start();
			
//			if(mCirclePgBar.getProgress() == 100){
//				if(connection != null){
//					unbindService(connection);
//				}
//				
//				Intent intent2 = new Intent(RunningActivity.this,RunQueryHistoryActivity.class);
//				startActivity(intent2);
//				//停止计时器
//				takeTimeHandler.removeCallbacks(takeTimeRunnable);
//				RunQueryHistoryActivity.finishRunTime = getCurrentTime();
//				onDestroy();
//			}
//			
		 
			break;
		case MotionEvent.ACTION_UP:
			if(mCirclePgBar.getProgress() != 100){
				mCirclePgBar.reDraw();
			}
			mlongPreView.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
	
		return true;
	}
	
	public void finishRun() {
		if(connection != null){
			unbindService(connection);
		}
		
		Intent intent2 = new Intent(RunningActivity.this,RunQueryHistoryActivity.class);
		startActivity(intent2);
		//停止计时器
		takeTimeHandler.removeCallbacks(takeTimeRunnable);
		RunQueryHistoryActivity.finishRunTime = getCurrentTime();
		onDestroy();
	}
	
	@Subscribe
	public void finishEventBus(String string){
		if(string.equals("finishRun")){
			if(connection != null){
				unbindService(connection);
			}
			
			Intent intent2 = new Intent(RunningActivity.this,RunQueryHistoryActivity.class);
			startActivity(intent2);
			//停止计时器
			takeTimeHandler.removeCallbacks(takeTimeRunnable);
			RunQueryHistoryActivity.finishRunTime = getCurrentTime();
			onDestroy();
		}
	}

	 
}
