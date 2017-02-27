package com.example.nikirun;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import de.greenrobot.event.EventBus;
 

public class CirclePgBar extends View {
   

	private Paint mBackPaint;
    private Paint mFrontPaint;
    private Paint mTextPaint;
    private float mStrokeWidth = 5;
    private float mHalfStrokeWidth = mStrokeWidth / 2;
    private float mRadius = 65;
    private RectF mRect;
    private int mProgress = 0;
    //目标值，想改多少就改多少
    private int mTargetProgress = 100;
    private int mMax = 100;
    private int mWidth;
    private int mHeight;
    private Canvas mCanvas;
    private boolean bDrawFlag;
	
	public CirclePgBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
    
	public CirclePgBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
    
    public CirclePgBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}
    
    public int getProgress() {
		return mProgress;
	}
	
	private void init(){
        mBackPaint = new Paint();
        mBackPaint.setColor(0x99EBEBEB);
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.STROKE);
        mBackPaint.setStrokeWidth(mStrokeWidth);

        mFrontPaint = new Paint();
        mFrontPaint.setColor(Color.WHITE);
        mFrontPaint.setAntiAlias(true);
        mFrontPaint.setStyle(Paint.Style.STROKE);
        mFrontPaint.setStrokeWidth(mStrokeWidth);


        mTextPaint = new Paint();
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(80);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        
        mCanvas = new Canvas();
        bDrawFlag = false;
        
       
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getRealSize(widthMeasureSpec);
		mHeight = getRealSize(heightMeasureSpec);
		setMeasuredDimension(mWidth, mHeight);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mCanvas = canvas;
		initRect();
		float angle = mProgress /(float) mMax * 360 ;
		canvas.drawCircle(mWidth/2, mHeight/2, mRadius, mBackPaint);
		canvas.drawArc(mRect, -90, angle, false, mFrontPaint);
//		canvas.drawText(mProgress + "%", mWidth/2 + mHalfStrokeWidth, mHeight/2 +mHalfStrokeWidth, mTextPaint);
		if(bDrawFlag && mProgress < mTargetProgress){
			mProgress += 1;
			invalidate();//让View刷新
			if(mProgress == 100){
				 EventBus.getDefault().post("finishRun");
			}
		}
		 
	}
	
	public void reDraw() {
		initRect();
		mProgress = 0;
		bDrawFlag = false;
		float angle = mProgress /(float) mMax * 360 ;
		mCanvas.drawCircle(mWidth/2, mHeight/2, mRadius, mBackPaint);
		mCanvas.drawArc(mRect, -90, angle, false, mFrontPaint);
		invalidate();
//		canvas.drawText(mProgress + "%", mWidth/2 + mHalfStrokeWidth, mHeight/2 +mHalfStrokeWidth, mTextPaint);
		 
	}
	
	public void beginDraw(boolean flag) {
		
		bDrawFlag = flag;
		invalidate();
	}

	public int getRealSize(int measureSpec) {
        int result = 1;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.UNSPECIFIED) {
            //自己计算
            result = (int) (mRadius * 2 + mStrokeWidth);
        } else {
            result = size;
        }

        return result;
    }
	
	private void initRect(){
	      if (mRect == null) {
	            mRect = new RectF();
	            int viewSize = (int) (mRadius * 2);
	            int left = (mWidth - viewSize) / 2;
	            int top = (mHeight - viewSize) / 2;
	            int right = left + viewSize;
	            int bottom = top + viewSize;
	            mRect.set(left, top, right, bottom);
	        }
	}
}
