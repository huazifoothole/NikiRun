package com.example.nikirun;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Friends_RangeList extends Fragment {
	
	private String[] data = { "张三", "李四", "王五","蒋六"};
	private ListView mRangeListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mRangeListView = (ListView) inflater.inflate(R.layout.homepage_friends_range, container,false);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,data);
		mRangeListView.setAdapter(adapter);
		
		return mRangeListView;
	}
}
