package com.example.nikirun;


import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class RunSetSlidePageActivity extends FragmentActivity implements OnItemClickListener,OnClickListener{
	/**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private RunSetPagerAdapter mRunSetAdapter;
    private Button mBeginRunButton;
    private String Data[] = {"音乐","地点","方向","加油打气","跑步设置"};
   
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runset_screenslide_pager);
        setTitle("跑步设定");
         
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item1,Data);
        ListView listView = (ListView) findViewById(R.id.runset_listview);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        
        // Instantiate a ViewPager and a PagerAdapter.
        
        mRunSetAdapter = new RunSetPagerAdapter(this);
        mPager = (ViewPager) findViewById(R.id.runset_pager);
//        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mRunSetAdapter); 
        
        mBeginRunButton = (Button) findViewById(R.id.begin_run);
        mBeginRunButton.setOnClickListener(this);
        
    }

    
    @Override
    public void onBackPressed() {
//        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
//        } else {
//            // Otherwise, select the previous step.
//            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
//             
//        }
    }


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(RunSetSlidePageActivity.this,CounDownActivity.class);
//		Intent intent = new Intent(RunSetSlidePageActivity.this,RunningActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		finish();
	}
}
