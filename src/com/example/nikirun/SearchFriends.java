package com.example.nikirun;

import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SearchFriends extends Activity implements OnQueryTextListener,OnClickListener{
	
	private SearchView mSearchView;
	private TextView mSearchText;
	private ListView mlv;
	private ImageButton mAddFriBt;
	RunUser myRunner,mFriRunner;
 
	 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfriends);
		
		mSearchView = (SearchView) findViewById(R.id.searchView1);
		mSearchView.setIconifiedByDefault(true);
		mSearchView.setQueryHint("按姓名或电子邮件搜索");
		mSearchView.setIconified(false);
		mSearchView.setFocusable(false);
		mSearchView.clearFocus();
		mSearchView.requestFocus();
		mSearchView.setOnQueryTextListener(this);
		
	 
		mSearchView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
		 
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Log.i(MainActivity.TAG, "onfocus2---");
				if(hasFocus){
					Log.i(MainActivity.TAG, "onfocus---");
				}else{
					Log.i(MainActivity.TAG, "lost focus---");
				}
			}
		});
		
		
		mlv = (ListView) findViewById(R.id.search_list);
		
		
		mSearchText = (TextView) findViewById(R.id.searchText);
		mSearchText.setOnClickListener(this);
		
		
		
		myRunner =  BmobUser.getCurrentUser(RunUser.class);
		 
	}
	
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		
		return false;
	}
	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		if(!TextUtils.isEmpty(newText)){
			Log.i(MainActivity.TAG, "onQueryTextChange");
			mSearchText.setText("搜索 "+newText);
			mSearchText.setVisibility(View.VISIBLE);
			mlv.setVisibility(View.INVISIBLE);
		}
		else
			mSearchText.setText("");
		return true;
	}
	
	private boolean searchUser(String email) {
		
		RunUser runUser = new RunUser();
		BmobQuery<RunUser> query = new BmobQuery<RunUser>();
		//sad Bmob的模糊查询需要收费
		query.addWhereEqualTo("email", email);
		Log.i(MainActivity.TAG, "email ="+email);
		query.findObjects(new FindListener<RunUser>() {
			
			@Override
			public void done(List<RunUser> object, BmobException e) {
				// TODO Auto-generated method stub
				if(e==null && object.size()>0){
					mSearchText.setVisibility(View.INVISIBLE);
					Log.i(MainActivity.TAG, "查询用户成功 + object.size ="+object.size());
					Toast.makeText(getApplicationContext(), "查询用户成功", Toast.LENGTH_SHORT).show();
					mlv.setVisibility(View.VISIBLE);
					SearchResultAdapter adapter = new SearchResultAdapter(getApplicationContext(), R.layout.userbysearch_email, object);
					mlv.setAdapter(adapter);
				}
				else{
					Toast.makeText(getApplicationContext(), "此用户不存在"+e.getMessage(), Toast.LENGTH_SHORT).show();
					Log.i(MainActivity.TAG, "此用户不存在 error="+e.getMessage());
				}
			}
		});
		
		return true;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String email;
		switch (v.getId()) {
		case R.id.searchText:
			email = mSearchView.getQuery().toString();
			if(email != null)
				searchUser(email);
			break;
		default:
			break;
		}
	}
	
	public class SearchResultAdapter extends ArrayAdapter<RunUser> implements OnClickListener{


		private int resourceId;
		View view;
		ViewHolder viewHolder;

		public SearchResultAdapter(Context context, int resource, List<RunUser> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			resourceId = resource;
		}
	 
		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			 RunUser runUser = getItem(position);
			 Log.i(MainActivity.TAG, "run email = "+runUser.getEmail()+"run name ="+runUser.getUsername());
			 if(convertView == null){
				 Log.i(MainActivity.TAG, "converView == null");
				 view = LayoutInflater.from(getApplicationContext()).inflate(resourceId,null);
				 viewHolder = new ViewHolder(view);
				 String name = runUser.getUsername();
				 if(!TextUtils.isEmpty(name)){
					 viewHolder.userName.setText(runUser.getUsername());
					 viewHolder.headImage.setImageResource(R.drawable.ic_launcher);
					 viewHolder.addBt.setOnClickListener(this);
				 } 
			  }else{
				  Log.i(MainActivity.TAG, "converView != null");
				  view = convertView;
				  viewHolder = (ViewHolder) view.getTag();
			  }
			 	 
				 return view;
		}
		 
		 class ViewHolder{
				ImageView headImage;
				TextView  userName;
				ImageButton addBt;
				
				public ViewHolder(View view) {
					// TODO Auto-generated constructor stub
					headImage = (ImageView) view.findViewById(R.id.head_image);
					userName = (TextView) view.findViewById(R.id.user_name);
					addBt = (ImageButton) view.findViewById(R.id.addfriend_bt);
				}
			}
		 
		 @Override
		public RunUser getItem(int position) {
			// TODO Auto-generated method stub
			return super.getItem(position);
		}
		 
		 
		 
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.addfriend_bt:
				int position = mlv.getPositionForView(view);
				mFriRunner = getItem(position);
				
				RunUser runUser = BmobUser.getCurrentUser(RunUser.class);
				BmobQuery<UserSoicalInfo> query = new BmobQuery<UserSoicalInfo>();
				query.addWhereEqualTo("runner", runUser);
				query.order("-updateAt");
				query.include("runner");
				query.findObjects(new FindListener<UserSoicalInfo>() {

					@Override
					public void done(List<UserSoicalInfo> soicalList, BmobException e) {
						// TODO Auto-generated method stub
						if(e==null){
							Log.i(MainActivity.TAG, "socail size ="+soicalList.size());
							if(soicalList.size()>0){
								Log.i(MainActivity.TAG, "have soicalinfo");
								Toast.makeText(getApplicationContext(), "get soicalinfo ok", Toast.LENGTH_SHORT).show();
								UserSoicalInfo userSoicalInfo = soicalList.get(0);
								//增加朋友 更新多对多关系
								addFriend(userSoicalInfo);
							}else{
								Log.i(MainActivity.TAG, "this runner not have soicalinfo");
								Toast.makeText(getApplicationContext(), "this runner not have soicalinfo", Toast.LENGTH_SHORT).show();
								//建立此runner的soicalUserInfo并增加朋友 
								buildAndAddFriend();
							}
							
						}
					}
				});
				
			 
				Log.i(MainActivity.TAG, "mFriRunner = "+mFriRunner.getEmail());
				
				break;

			default:
				break;
			}
			
			
			
		}
		
		private void buildAndAddFriend() {
			UserSoicalInfo userSoicalInfo = new UserSoicalInfo();
			RunUser runUser = BmobUser.getCurrentUser(RunUser.class);
			userSoicalInfo.setRunner(runUser);//一对一关系
			BmobRelation relation = new BmobRelation();
			relation.add(mFriRunner);
			userSoicalInfo.setFriends(relation);
			userSoicalInfo.save(new SaveListener<String>() {

				@Override
				public void done(String arg0, BmobException e) {
					// TODO Auto-generated method stub
					if(e==null){
						Log.i(MainActivity.TAG, "add friend ok");
						Toast.makeText(getApplicationContext(), "add friend ok", Toast.LENGTH_SHORT).show();
						
					}else{
						Log.i(MainActivity.TAG, "add friend fail err="+e.getMessage());
						Toast.makeText(getApplicationContext(), "add friend fail", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		
		private void addFriend(UserSoicalInfo userSoicalInfo){
			BmobRelation relation = new BmobRelation();
			relation.add(mFriRunner);
			userSoicalInfo.setFriends(relation);
			userSoicalInfo.update(new UpdateListener() {
				
				@Override
				public void done(BmobException e) {
					// TODO Auto-generated method stub
					if(e==null){
						Log.i(MainActivity.TAG, "update soicalFri ok");
					}else{
						Log.i(MainActivity.TAG, "update soicalFri fail");
						}
					}
			});
		}
		
		 
	}



}
