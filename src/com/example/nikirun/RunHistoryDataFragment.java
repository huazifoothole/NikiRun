package com.example.nikirun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.nikirun.HistoryTrackData.Points;
import com.example.nikirun.PinnedHeaderListView.PinnedHeaderAdapter;
import com.example.nikirun.RunDatabaseHelper.RunCursor;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class RunHistoryDataFragment extends Fragment implements OnItemClickListener{
	
		private View view;
		private ListView mRunHistoryListView;
		private static RunCursor mRunCursor=null;
		private RunDatabaseHelper mHelper;
		private static final String COLUMN_RUN_START_DATE = "start_date";
		private static final String COLUMN_RUN_END_DATE = "end_date";
		private static final String COLUMN_RUN_TRACK_DATA = "track_data";
		public static final String HISTORY_TRACE = "history_trace";
		public static final String ISONLYQUERY = "only_query";
		 
		//git test 10
		public RunHistoryDataFragment() {
			// TODO Auto-generated constructor stub
			 
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
			 view =  inflater.inflate(R.layout.runhistory_listview, container,false);
			 mRunHistoryListView = (ListView) view.findViewById(R.id.historydata_listview);
			 mHelper = new RunDatabaseHelper(getActivity());
			 if(mRunCursor == null || RunQueryHistoryActivity.isUpdateRunData == true){
				 mRunCursor = mHelper.queryRunsCursor();
				 RunQueryHistoryActivity.isUpdateRunData = false;
			 }
			 RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), mRunCursor);
			 mRunHistoryListView.setAdapter(adapter);
			 mRunHistoryListView.setOnScrollListener(adapter);
			 mRunHistoryListView.setOnItemClickListener(this);
			 return view;
		}
		
		 
		private static class RunCursorAdapter extends CursorAdapter implements SectionIndexer, PinnedHeaderAdapter, OnScrollListener{

			public RunCursorAdapter(Context context, RunCursor cursor) {
				super(context, cursor, 0);
				// TODO Auto-generated constructor stub
				mCursor = cursor;
			}

			@Override
			public View newView(Context context, Cursor cursor, ViewGroup parent) {
				// TODO Auto-generated method stub
				ViewHolder viewHolder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.run_list_item, parent,false);
//				View view = LayoutInflater.from(context).inflate(R.layout.run_list_item, parent,false);
				viewHolder.runDistance = (TextView) view.findViewById(R.id.run_distance);
				viewHolder.runDate = (TextView) view.findViewById(R.id.run_date);
				viewHolder.runDayOfWeek = (TextView) view.findViewById(R.id.day_of_week);
				viewHolder.runSpeed = (TextView) view.findViewById(R.id.run_speed);
				viewHolder.runExpendTime = (TextView) view.findViewById(R.id.expend_time);
				viewHolder.sortMonthLayout = (LinearLayout) view.findViewById(R.id.sort_month_layout);
				viewHolder.runDataLayout = (RelativeLayout) view.findViewById(R.id.runhistory_layout);
				viewHolder.runSortTitle = (TextView) view.findViewById(R.id.sort_month_text);
				view.setTag(viewHolder);
				return view;
			}

			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				// TODO Auto-generated method stub
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				
				String startDate = cursor.getString(cursor.getColumnIndex(COLUMN_RUN_START_DATE));
				String endDate = cursor.getString(cursor.getColumnIndex(COLUMN_RUN_END_DATE));
				String expendTime = DateUtils.getInterval(startDate, endDate);
				String historyDataString = cursor.getString(cursor.getColumnIndex(COLUMN_RUN_TRACK_DATA));
				
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
					if(cursor.getPosition() == getPositionForSection(month)){
						viewHolder.sortMonthLayout.setVisibility(View.VISIBLE);
						viewHolder.runDataLayout.setMinimumHeight(60);
						int totalDistance = getMonthDistance(month, distance);
						viewHolder.runSortTitle.setText(month + "月        " + totalDistance + "m");
					}else{
						viewHolder.sortMonthLayout.setVisibility(View.GONE);
					}
					
				}
				
				
			}

		class ViewHolder {
			TextView runDistance;
			TextView runDate;
			TextView runSpeed;
			TextView runExpendTime;
			TextView runDayOfWeek;
			LinearLayout sortMonthLayout;
			RelativeLayout runDataLayout;
			TextView runSortTitle;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			if(view instanceof PinnedHeaderListView){
				((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
			}
		}

		@Override
		public int getPinnedHeaderState(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void configurePinnedHeader(View header, int position, int alpha) {
			// TODO Auto-generated method stub
			int realPosition = position;
			int section = getSectionForPosition(realPosition);
			String title = String.valueOf(section) + "月";
			((TextView)header.findViewById(R.id.sort_month_text)).setText(title);
					
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getPositionForSection(int sectionIndex) {
			// TODO Auto-generated method stub
			mRunCursor.moveToFirst();
			
			do {
				 String startDateStr = mRunCursor.getWrappedCursor().getString(mRunCursor.getWrappedCursor()
						 .getColumnIndex(COLUMN_RUN_START_DATE));
				 String strKey = startDateStr.substring(5,7);
				 if (sectionIndex == Integer.parseInt(strKey)) {
					 
					return mRunCursor.getPosition();
				}
			} while (mRunCursor.moveToNext());
			
			return 0;
		}
		
		public int getMonthDistance(int month,int distance) {
			mRunCursor.moveToFirst();
			int mdistance = distance;
			do {
				String startDateStr = mRunCursor.getWrappedCursor().getString(mRunCursor.getWrappedCursor()
						 .getColumnIndex(COLUMN_RUN_START_DATE));
				
				 String m = startDateStr.substring(5,7);
				 int curDistance = 0;
				 if(Integer.parseInt(m) == month){
					 String historyData = mRunCursor.getString(mRunCursor.getColumnIndex(COLUMN_RUN_TRACK_DATA));
					 if(historyData != null){
						 HistoryTrackData historyTrackData = GsonService.parseJson(historyData, 
									HistoryTrackData.class);
						 curDistance = (int) historyTrackData.distance;
					 }
					 mdistance += curDistance;
				 }
			} while (mRunCursor.moveToNext());
			
			return mdistance;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			if(position < 0 || position >= getCount()){
				return -1;
			}
			mRunCursor.moveToPosition(position);
			 
			String startDateStr = mRunCursor.getWrappedCursor().getString(mRunCursor.getWrappedCursor()
							 .getColumnIndex(COLUMN_RUN_START_DATE));
			int section = Integer.parseInt(startDateStr.substring(5,7));
			return section;
		}
			
		}


		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			Log.i(MainActivity.TAG, "ItemClick position =" + position);
			if(mRunCursor != null){
				mRunCursor.moveToPosition(position);
				String historyData = mRunCursor.getWrappedCursor().getString(mRunCursor.getWrappedCursor()
						 .getColumnIndex(COLUMN_RUN_TRACK_DATA));
				Intent intent = new Intent(getActivity(),RunQueryHistoryActivity.class);
				intent.putExtra(HISTORY_TRACE, historyData);
				intent.putExtra(ISONLYQUERY, true);
				startActivity(intent);
			}
		
		}
		
		
		 
}
