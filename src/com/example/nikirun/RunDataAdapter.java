package com.example.nikirun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.nikirun.HistoryTrackData.Points;
import com.example.nikirun.RunHistoryDataFragment.MessageItem;
import com.example.nikirun.RunHistoryDataFragment.ViewHolder;
import com.example.nikirun.SlideView.OnSlideListener;

import android.content.Context;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Toast;

public class RunDataAdapter extends ArrayAdapter<UserRunData> implements SectionIndexer,OnScrollListener,OnSlideListener{

	private int resourceId;
	private List<UserRunData> listData;
	SlideView slideView;
	public static List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
	
	public RunDataAdapter(Context context, int resource, List<UserRunData> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resourceId = resource;
		listData = objects;
		for(int i=0;i<objects.size();i++){
			MessageItem messageItem = new MessageItem();
			mMessageItems.add(messageItem);
			 
		}
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		UserRunData userRunData = getItem(position);
		ViewHolder viewHolder;
		View view;
		slideView = (SlideView) convertView;
		if(slideView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
			
			slideView = new SlideView(getContext());
			slideView.setContentView(view);
			slideView.setOnSlideListener(RunDataAdapter.this);
			viewHolder = new ViewHolder(slideView);
			slideView.setTag(viewHolder);
 
		}else{
			viewHolder = (ViewHolder) slideView.getTag();
			
		}
		
		final MessageItem item =  mMessageItems.get(position);
		item.slideView = slideView;
		item.slideView.shrink();
		mMessageItems.set(position, item);
		
		String startDate = userRunData.getRunDate();
		String expendTime = userRunData.getRunTime() ;
		String historyDataString = userRunData.getRunData();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		String week = null;
		try {
			 date =  format.parse(startDate);
			 week = "星期" + DateUtils.getWeek(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(historyDataString != ""){
			HistoryTrackData historyTrackData = GsonService.parseJson(historyDataString, 
					HistoryTrackData.class);
			int distance = (int) historyTrackData.distance;
			List<Points> pointList = new ArrayList<HistoryTrackData.Points>();
			pointList.addAll(historyTrackData.getPoints());
			Points points = pointList.get(0);
			
			String runDateStr = startDate.substring(0,10);
			viewHolder.runDistance.setText(Integer.toString(distance)+"m");
			viewHolder.runDate.setText(runDateStr);
			viewHolder.runSpeed.setText((long)(points.getSpeed()/3600) + "m/s");
			viewHolder.runExpendTime.setText(expendTime);
			if(week != null){
				viewHolder.runDayOfWeek.setText(week);
			}
			
			int month =Integer.parseInt(runDateStr.substring(5, 7));
			if(position == getPositionForSection(month)){
				viewHolder.sortMonthLayout.setVisibility(View.VISIBLE);
				viewHolder.runDataLayout.setMinimumHeight(60);
				int totalDistance = getMonthDistance(month);
				int year = Integer.parseInt(runDateStr.substring(0, 4));
				viewHolder.runSortTitle.setText(year+"年"+month + "月        " + totalDistance + "m");
			}else{
				viewHolder.sortMonthLayout.setVisibility(View.GONE);
			}
			
			viewHolder.deleteHodler.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View view) {
					// TODO Auto-generated method stub
					if(view.getId() == R.id.holder){
						if(position != ListView.INVALID_POSITION){
							UserRunData userRunData = listData.get(position);
							userRunData.delete(new UpdateListener() {
								
								@Override
								public void done(BmobException e) {
									// TODO Auto-generated method stub
									if(e==null){
										Log.i(MainActivity.TAG, "删除数据成功");
										listData.remove(position);
										notifyDataSetChanged();
									}else{
										Log.i(MainActivity.TAG, "删除数据失败");
										Toast.makeText(getContext(), "删除数据失败 err:"+e.getMessage(), Toast.LENGTH_SHORT).show();
									}
								}
							});
							 					 
						}
					Log.i(MainActivity.TAG, "onClick position="+position);
						
					}
				}
			});
		}
		return slideView;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		// TODO Auto-generated method stub
		 
		for(int i = 0; i<listData.size();i++){
			String startDateStr = listData.get(i).getRunDate();
			 String strKey = startDateStr.substring(5,7);
			 if (sectionIndex == Integer.parseInt(strKey)) {
				return i;
			}
		}
		
		return 0;
		 
	}
	

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getMonthDistance(int month) {
		int mdistance = 0;
		
		for(int i = 0;i<listData.size();i++){
			String startDateStr = listData.get(i).getRunDate();
			String m = startDateStr.substring(5,7);
//			Log.i(MainActivity.TAG, "month str="+startDateStr+"5-7:"+m+" month="+month+"parse= "+Integer.parseInt(m));
			int curDistance = 0;
			if(Integer.parseInt(m) == month){
				 String historyData = listData.get(i).getRunData();
				 if(historyData != null){
					 HistoryTrackData historyTrackData = GsonService.parseJson(historyData, 
								HistoryTrackData.class);
					 curDistance = (int) historyTrackData.distance;
				 }
				 mdistance += curDistance;
			 }
		}
		
		return mdistance;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSlide(View view, int status) {
		// TODO Auto-generated method stub
		 
	}

	@Override
	public UserRunData getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}
 
	

}
