package com.example.nikirun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class Friends_RangeList extends Fragment {
	
	 
	private ListView mRangeListView;
	private ArrayAdapter<UserRunData> madapter = null;
	private RunUser mCurrentRunner;
	private View view;
	public static List<RunUser> friendsList;
	 
	private List<UserRunData> mDataList;
	
 
	@Override
	public View onCreateView( final LayoutInflater inflater,final ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
 
		view =  inflater.inflate(R.layout.homepage_friends_range, container,false);
		mRangeListView =  (ListView) view.findViewById(R.id.homepage_list);
		mCurrentRunner = BmobUser.getCurrentUser(RunUser.class);
		 
		
		new Thread(new Runnable() {
			public void run() {
				Log.i(MainActivity.TAG, "thread run");
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		}).start();
		
		Log.i(MainActivity.TAG, "friends rank onCreatView");
		return mRangeListView;
	}
	
	
	private Handler  handler = new Handler(){
		public void  handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				getFriendRunData();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	
	};
	
	private void getFriendRunData(){
		
		BmobQuery<UserSoicalInfo> query = new BmobQuery<UserSoicalInfo>();
		query.addWhereEqualTo("runner", mCurrentRunner);
		query.order("-updateAt");
		query.include("runner");
		query.findObjects(new FindListener<UserSoicalInfo>() {

			@Override
			public void done(List<UserSoicalInfo> soicalList, BmobException e) {
				// TODO Auto-generated method stub
				if(e==null){
					if(soicalList.size()>0){
						Log.i(MainActivity.TAG, "find soicalinfo ok");
						Toast.makeText(getActivity(), "find runner's soicalinfo ok", Toast.LENGTH_SHORT).show();
						UserSoicalInfo userSoicalInfo = soicalList.get(0);
						//查询多对多关系 找此runner的好友
						getSomeOneUserRunData(userSoicalInfo);
					}
				}
			}
		});
	}
	 
	
	private void getSomeOneUserRunData(UserSoicalInfo userSoicalInfo){
		
			Log.i(MainActivity.TAG, "email="+userSoicalInfo.getRunner().getEmail()+" objectId="+userSoicalInfo.getObjectId());
		 	BmobQuery<RunUser> query = new BmobQuery<RunUser>();
		 	query.addWhereRelatedTo("friends", new BmobPointer(userSoicalInfo));
		 	query.findObjects(new FindListener<RunUser>() {

				@Override
				public void done(List<RunUser> runnerList, BmobException e) {
					// TODO Auto-generated method stub
					if(e==null){
						if(runnerList.size()>0){
							
							friendsList = runnerList;
							
							//将本人信息也放进朋友列表进行比较
							runnerList.add(mCurrentRunner);
							Log.i(MainActivity.TAG, "find runner's friends size ="+runnerList.size());
							RunUser runUser = new RunUser();
							List<String> nameList = new ArrayList<String>();
								//存储昵称 name
								for(int i=0;i<runnerList.size();i++){
									runUser = runnerList.get(i);
									nameList.add(runUser.getUsername());
								}
								//查询朋友们的跑步历史数据(包括自己的)
								BmobQuery<RunUser> innerQuery = new BmobQuery<RunUser>();
								innerQuery.addWhereContainedIn("username", nameList);
								BmobQuery<UserRunData> query = new BmobQuery<UserRunData>();
								query.addWhereMatchesQuery("runUser", "_User", innerQuery);
								query.include("runUser");//查询的同时把跑步者的信息也查询出来
//								query.groupby(new String[]{"runUser"});//分组查询 ，将同一跑者的数据分在一组进行统计
//								query.setHasGroupCount(true); 
								query.findObjects(new FindListener<UserRunData>() {

									@Override
									public void done(List<UserRunData> datasList, BmobException e) {
										// TODO Auto-generated method stub
										if(e==null){
											Log.i(MainActivity.TAG, "find userData ok datalist size=" + datasList.size());
											UserRunData tmpUserRunData = new UserRunData();
											mDataList = new ArrayList<UserRunData>();
											double sumDistance = 0;
											
											List<String> runnerList = new ArrayList<String>();
											for(int i=0;i<datasList.size();i++){
												String name = datasList.get(i).getRunUser().getUsername();
												runnerList.add(name);
											}
											
											//一个人可能有多个跑步记录,处理集合为一个跑步记录，使用个人跑步距离进行比较排序
											Map<String, Integer> map = new HashMap<String, Integer>();
											for(String temp:runnerList){
												Integer count = map.get(temp);
												map.put(temp, (count == null)? 1:count+1);
											}
											List<String> namelist = new ArrayList<String>();
											for(Map.Entry<String, Integer> entry : map.entrySet()){
												namelist.add(entry.getKey());
												Log.i(MainActivity.TAG, "key: " + entry.getKey() + " value: " + entry.getValue());
											}
											 
											HistoryTrackData trackData = new HistoryTrackData();
											UserRunData tmpUserRunData2 = new UserRunData();
											for(int i=0;i<namelist.size();i++){
												for(int j=0;j<datasList.size();j++){
													tmpUserRunData = datasList.get(j);
													String name = tmpUserRunData.getRunUser().getUsername();
													if(name.equals(namelist.get(i))){
														trackData = GsonService.parseJson(tmpUserRunData.getRunData(), 
																HistoryTrackData.class);
														//统计每个人的总公里数
														if(trackData!=null){
															sumDistance += trackData.getDistance();
														}else{
															Log.i(MainActivity.TAG, "trachData = null");
														}
														sumDistance = ((int)(sumDistance*100))/100;
														tmpUserRunData2 = tmpUserRunData;
													}
														
												}
												tmpUserRunData2.setRunDistance(sumDistance);
												//每一个需要比较的跑者统计完后，临时距离归零
												sumDistance = 0;
												mDataList.add(tmpUserRunData2);
											}
										
//											跑步距离排序 降序
											RunDistanceSort(mDataList);
										}
										
									}
									 
								});
								
							
						}
						else{
							Log.i(MainActivity.TAG, "find runner friends size= "+runnerList.size());
						}
					}
					else{
						Log.i(MainActivity.TAG, "find runner's friend fail err="+e.getMessage());
					}
				}
			});
	}
	
	//跑步距离排序 降序
	private void RunDistanceSort(List<UserRunData> listData){
		UserRunData temp = new UserRunData();
		int size = listData.size();
		for(int i = 0;i < size-1; i++){
			for(int j= 0;j < size -1 -i ; j++){
				 
				if(listData.get(j).getRunDistance()<listData.get(j+1).getRunDistance()){
					temp = listData.get(j);
					listData.set(j, mDataList.get(j+1));
					listData.set(j+1, temp);
				}
			}
		}
		
		madapter = new FriDataAdapter(getActivity(), R.layout.friend_rank_item, listData);
		mRangeListView.setAdapter(madapter);
	}
	
	
	private class FriDataAdapter extends ArrayAdapter<UserRunData> {
		
		private Context mContext;
		private List<UserRunData> mDatas =  new ArrayList<UserRunData>();
		private int mResourceId;
		 
		
		public FriDataAdapter(Context context, int textViewResourceId, List<UserRunData> dataList) {
			super(context,textViewResourceId, dataList);
			// TODO Auto-generated constructor stub
			mContext = context;
			mDatas = dataList;
			mResourceId = textViewResourceId;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDatas.size();
		}

		@Override
		public UserRunData getItem(int position) {
			// TODO Auto-generated method stub
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			UserRunData userRunData = getItem(position);
			ViewHodler viewHodler;
			if(view == null){
				viewHodler = new ViewHodler();
				view = LayoutInflater.from(mContext).inflate(mResourceId, parent,false);
				viewHodler.mNameView = (TextView) view.findViewById(R.id.fri_name);
				viewHodler.mImageView = (ImageView) view.findViewById(R.id.fri_img);
				viewHodler.mDistanceView = (TextView) view.findViewById(R.id.fri_distance);
				view.setTag(viewHodler);
			}else{
				viewHodler = (ViewHodler) view.getTag();
			}
			viewHodler.mDistanceView.setText(mDatas.get(position).getRunDistance()+"");
			RunUser runUser = userRunData.getRunUser();
			viewHodler.mNameView.setText(runUser.getUsername());
//			viewHodler.mImageView.setBackgroundDrawable(R.drawable.fri_head);
			return view;
		}
		
	}
	
	class ViewHodler{
		ImageView mImageView;
		TextView mNameView;
		TextView mDistanceView;
		
	}
	
}
