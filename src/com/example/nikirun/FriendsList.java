package com.example.nikirun;

import java.util.ArrayList;
import java.util.List;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FriendsList extends Fragment {
	
	private static View view;
	private List<String> mRunUserList = new ArrayList<String>();
	private ListView mFriListView;
	ArrayAdapter<String> mAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
		}
		try {
			view = inflater.inflate(R.layout.friendslist, container, false);
			mFriListView = (ListView) view.findViewById(R.id.friends_listview);
			mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mRunUserList);
			mFriListView.setAdapter(mAdapter);
			
			if(mRunUserList.size() == 0)
				setFriendsList(Friends_RangeList.friendsList);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		setHasOptionsMenu(true);
		
		return view;
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.addfriend, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_addfrid:
			Intent intent = new Intent(getActivity(),SearchFriends.class);
			startActivity(intent);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setFriendsList(List<RunUser> mFriList) {
		if(mFriList.size()>0){
			RunUser runUser;
			for(int i=0;i<mFriList.size();i++){
				runUser = mFriList.get(i);
				mRunUserList.add(runUser.getUsername());
			}
		}
		mAdapter.notifyDataSetChanged();
		return;
	}
	
	
}
