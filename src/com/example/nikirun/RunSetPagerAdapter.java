package com.example.nikirun;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

public class RunSetPagerAdapter extends PagerAdapter {
	
	private List<View> mListViewPager;
	private List<String> mListViewPagerTitle;
	private Context mContext;
	
	public RunSetPagerAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		
		View v1 = LayoutInflater.from(mContext).inflate(R.layout.runset_basicrun, null);
		View v2 = LayoutInflater.from(mContext).inflate(R.layout.runset_setdistance, null);
		View V3 = LayoutInflater.from(mContext).inflate(R.layout.runset_settime, null);
		
		mListViewPager = new ArrayList<View>();
		mListViewPager.add(v1);
		mListViewPager.add(v2);
		mListViewPager.add(V3);
		
	}
	
	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		((ViewPager)container).addView(mListViewPager.get(position));
		return mListViewPager.get(position);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListViewPager.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == object;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager)container).removeView(mListViewPager.get(position));
	}

}
