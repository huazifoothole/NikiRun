package com.example.nikirun;

import java.util.List;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class HomePageFragment extends Fragment implements OnClickListener{
	
	private static View view;
	private Button mRunButton;
	private TextView mRunCount;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			
		}
		
		try {
			
			view = inflater.inflate(R.layout.home_page, container, false);
			mRunCount =(TextView) view.findViewById(R.id.runcount_view);
			mRunButton = (Button) view.findViewById(R.id.beginRun_bt);
			mRunButton.setOnClickListener(this);
			findCurrentUserData();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return view;
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(),RunSetSlidePageActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(MainActivity.TAG, "HomePage onResume");
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.title_section1);
//		MainActivity.setTitle(getString(R.string.title_section1));;
	}
	 
	 
	public void findCurrentUserData() {
		
		RunUser runUser = BmobUser.getCurrentUser(RunUser.class);
		BmobQuery<UserRunData> query = new BmobQuery<UserRunData>();
		query.addWhereEqualTo("runUser", runUser);
		query.order("-updatedAt");
		query.include("runUser");
		query.findObjects(new FindListener<UserRunData>() {
			
			@Override
			public void done(List<UserRunData> runDataList, BmobException e) {
				// TODO Auto-generated method stub
				if(e == null){
					Log.i(MainActivity.TAG, "findCurrentUserData ok");
					 int runCount = runDataList.size();
					 mRunCount.setText(runCount+"");
				}
			}
		});
		   
	}
}
