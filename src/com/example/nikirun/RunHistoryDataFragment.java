package com.example.nikirun;

import java.util.List;

 

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RunHistoryDataFragment extends Fragment implements OnItemClickListener{
	
		private View view;
		public static RunHisdataList mRunHistoryListView;
		public static final String COLUMN_RUN_START_DATE = "start_date";
		public static final String COLUMN_RUN_END_DATE = "end_date";
		private static final String COLUMN_RUN_TRACK_DATA = "track_data";
		public static final String HISTORY_TRACE = "history_trace";
		public static final String ISONLYQUERY = "only_query";
		
		public static boolean isAllowItemClick = false;
		
		RunDataAdapter mRunDataAdapter;
		private List<UserRunData> listRunDatas;
		private PullToRefreshView mPullToRefreshView;
		 public static final int REFRESH_DELAY = 2000;
		 
		@Override
		public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
 
			 Log.i(MainActivity.TAG, "RunHistoryDataFragment onCreateView");
			 view = inflater.inflate(R.layout.rundata_listview, container,false);
			 mRunHistoryListView = (RunHisdataList) view.findViewById(R.id.historydata_listview2);
			 if(mRunDataAdapter != null){
				 mRunHistoryListView.setAdapter(mRunDataAdapter);
			 }else{
				 queryRunData();
			 }
			 mRunHistoryListView.setOnItemClickListener(this);
			 mRunHistoryListView.setOnScrollListener(mRunDataAdapter);
			 
			 mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh_view);
			 mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
				
				@Override
				public void onRefresh() {
					// TODO Auto-generated method stub
					mPullToRefreshView.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							queryRunData();
							mPullToRefreshView.setRefreshing(false);
						}
					}, REFRESH_DELAY);
				}
			});
			 return view;
		}
		
		private void queryRunData(){
			Log.i(MainActivity.TAG, "queryRunData");
			RunUser runUser = BmobUser.getCurrentUser(RunUser.class);
			BmobQuery<UserRunData> query = new BmobQuery<UserRunData>();
			query.addWhereEqualTo("runUser", new BmobPointer(runUser));
			query.order("runDate");//按日期升序显示数据
			query.findObjects(new FindListener<UserRunData>() {

				@Override
				public void done(List<UserRunData> listdata, BmobException e) {
					// TODO Auto-generated method stub
					if(e == null){
						if(listdata.size()>0){
							listRunDatas = listdata;
							mRunDataAdapter = new RunDataAdapter(getActivity(), R.layout.run_list_item, listdata);
							mRunHistoryListView.setAdapter(mRunDataAdapter);
							mRunDataAdapter.notifyDataSetChanged();
						}else
						{
							Log.i(MainActivity.TAG, "listData size = 0");
						}
						
					}else{
						Log.d(MainActivity.TAG, "query Rundata failed err="+e.getMessage());
					}
				}
			});
		}
		
		 

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.i(MainActivity.TAG, "item click position ="+position);
			// TODO Auto-generated method stub
			if(isAllowItemClick){
				if(listRunDatas.size() > 0){
					UserRunData userRunData = listRunDatas.get(position);
					String historyData = userRunData.getRunData();
					String startDate = userRunData.getRunDate();
					String endDate = userRunData.getRunTime();
					Intent intent = new Intent(getActivity(),RunQueryHistoryActivity.class);
					intent.putExtra(HISTORY_TRACE, historyData);
					intent.putExtra(ISONLYQUERY, true);
					intent.putExtra(COLUMN_RUN_START_DATE, startDate);
					intent.putExtra(COLUMN_RUN_END_DATE, endDate);
					startActivity(intent);
				}
			}
		
		}
		
		
		public static class MessageItem {
	        public int iconRes;
	        public String title;
	        public String msg;
	        public String time;
	        public SlideView slideView;
	    }
		
		static class ViewHolder {
			TextView runDistance;
			TextView runDate;
			TextView runSpeed;
			TextView runExpendTime;
			TextView runDayOfWeek;
			LinearLayout sortMonthLayout;
			RelativeLayout runDataLayout;
			TextView runSortTitle;
			ViewGroup deleteHodler;
			
			ViewHolder(View view){
				runDistance = (TextView) view.findViewById(R.id.run_distance);
				runDate = (TextView) view.findViewById(R.id.run_date);
				runDayOfWeek = (TextView) view.findViewById(R.id.day_of_week);
				runSpeed = (TextView) view.findViewById(R.id.run_speed);
				runExpendTime = (TextView) view.findViewById(R.id.expend_time);
				sortMonthLayout = (LinearLayout) view.findViewById(R.id.sort_month_layout);
				runDataLayout = (RelativeLayout) view.findViewById(R.id.runhistory_layout);
				runSortTitle = (TextView) view.findViewById(R.id.sort_month_text);
				deleteHodler = (ViewGroup) view.findViewById(R.id.holder);
			}
		}

 



}
