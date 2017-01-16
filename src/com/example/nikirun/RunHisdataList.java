package com.example.nikirun;

import com.example.nikirun.RunHistoryDataFragment.MessageItem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ListAdapter;

public class RunHisdataList extends ListView  {

	ListView listview;
	Context mContext;
	RunDataAdapter mAdapter;
	SlideView mFocusedItemView;
	
	public RunHisdataList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public RunHisdataList(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public RunHisdataList(Context context) {
		super(context);
		
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
		mAdapter = (RunDataAdapter) getAdapter();
	}
 
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			
			
			int position = pointToPosition(x, y);
			if(position != INVALID_POSITION){
				if(mAdapter.mMessageItems.size()>0){
					MessageItem messageItem = mAdapter.mMessageItems.get(position);
					mFocusedItemView = messageItem.slideView;
				}
				
			}
			break;

		default:
			break;
		}
    	
    	if(mFocusedItemView != null){
    		mFocusedItemView.onRequireTouchEvent(ev);
    	}
		return super.onTouchEvent(ev);
	}
	

}
