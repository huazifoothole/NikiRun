package com.example.nikirun;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

public class CounDownActivity extends Activity{
	
	private ImageView mCountdownView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.countdown_view);
		
		mCountdownView = (ImageView) findViewById(R.id.countdown_imageview);
		creatAnimationThread();
	}
	
	Handler handler = new Handler(){
		
		final int[] arrayId = {0, R.drawable.count3,R.drawable.count2,R.drawable.count1};   
		
		public void handleMessage(android.os.Message msg) {
			int index = (Integer) msg.obj;
			
			mCountdownView.setImageResource(arrayId[index]);
			mCountdownView.setScaleX(0);
			mCountdownView.setScaleY(0);
			
			ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mCountdownView, "scaleX", 0,1);
			objectAnimator1.setDuration(500);
			
			ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mCountdownView, "scaleY", 0,1);
			objectAnimator2.setDuration(500);
			
			AnimatorSet animationSet = new AnimatorSet();
			animationSet.playTogether(objectAnimator1,objectAnimator2);
			animationSet.start();
		};
	};
	
	public void  creatAnimationThread() {
		Thread thread = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				 for(int i=1;i<4;i++){
					 Message message = handler.obtainMessage();
					 message.obj = i;
					 handler.sendMessage(message);
					 try{
						 sleep(1000);
					 }catch(InterruptedException e){
						 e.printStackTrace();
					 }
				 }
				 Intent intent = new Intent(CounDownActivity.this,RunningActivity.class);
				 startActivity(intent);
				 onDestroy();

			}
		};
		thread.start();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		finish();
	}
}
