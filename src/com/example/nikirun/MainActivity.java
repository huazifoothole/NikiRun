package com.example.nikirun;

import android.app.Activity;

import java.util.List;

import com.baidu.mapapi.SDKInitializer;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
 
 

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private static CharSequence mTitle;
	
	private HomePageFragment mhomePageFragment;
	
	private RunHistoryDataFragment mRunHistoryDataFragment;
	
	private LoginFragment mLoginFragment;
	
	private FriendsList mFriendsList;
	 
	
	/**
     * entity标识
     */
    protected static String entityName = "MyRunTrace";
    public static final String TAG = "RunTraceLog";
    
    
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SDKInitializer.initialize(getApplicationContext());
		
		  
		setContentView(R.layout.activity_main);
		
		Bmob.initialize(this,"a7dbaff012922213ea36d42a7aca4196");
		 
		//This does the magic! 隐藏actionbar左侧图标  transparent透明的
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getString(R.string.title_section1);
	 
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		
		SharedPreferences sharedPreferences = getSharedPreferences(LoginFragment.USER_INFO, MODE_PRIVATE);
		Boolean login = sharedPreferences.getBoolean(LoginFragment.LOGIN_STATUS, false);
//		if(login)
//			queryMyRunData();
		 
	}
	
	 
	
	private void queryMyRunData() {
		RunUser runner = BmobUser.getCurrentUser(RunUser.class);
		BmobQuery<UserRunData> query = new BmobQuery<UserRunData>();
		query.addWhereEqualTo("runner", runner);
		query.include("runner");
		query.findObjects(new FindListener<UserRunData>() {
			
			@Override
			public void done(List<UserRunData> arg0, BmobException e) {
				// TODO Auto-generated method stub
					if(e==null){
						Log.i(TAG, "queryMyRunData success");
						
					}else{
						Log.i(TAG, "queryMyRunData fail " + e.getMessage());
					}
			}
		});
		
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
	
		FragmentManager fragmentManager = getFragmentManager();
		android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		switch (position) {
		case 0:
			if(mLoginFragment == null)
				mLoginFragment = new LoginFragment();
			
			SharedPreferences sharedPreferences = getSharedPreferences(LoginFragment.USER_INFO, MODE_PRIVATE);
			boolean islogin = sharedPreferences.getBoolean(LoginFragment.LOGIN_STATUS, false);
			if(islogin){
				Intent intent = new Intent(this,UserInfoActivity.class);
				startActivity(intent);
				fragmentTransaction.addToBackStack(null);
			}else
			{
				fragmentTransaction.replace(R.id.container, mLoginFragment);
				fragmentTransaction.addToBackStack(null);
			}
			
			mTitle = getString(R.string.title_section0);
			restoreActionBar();
			break;
		case 1:
			if(mhomePageFragment == null)
			mhomePageFragment = new HomePageFragment();
			
			fragmentTransaction.replace(R.id.container, mhomePageFragment);
			fragmentTransaction.addToBackStack(null);
			 
			mTitle = getString(R.string.title_section1);
			restoreActionBar();
			break;
		case 2:
			 if(mRunHistoryDataFragment == null){
				 mRunHistoryDataFragment = new RunHistoryDataFragment();
			 }
			 
			 fragmentTransaction.replace(R.id.container, mRunHistoryDataFragment);
			 fragmentTransaction.addToBackStack(null);
			 
			 mTitle = getString(R.string.title_section2);
			 restoreActionBar();
			break;
		case 3:
			if(mFriendsList == null){
				mFriendsList = new FriendsList();
			}
			
			fragmentTransaction.replace(R.id.container, mFriendsList);
			fragmentTransaction.addToBackStack(null);
			
			mTitle = getString(R.string.title_section3);
			restoreActionBar();
			break;
		default:
			fragmentTransaction.replace(R.id.container, PlaceholderFragment.newInstance(position + 1));
			fragmentTransaction.addToBackStack(null);
			break;
		}
		
		fragmentTransaction.commit();
		
	}

	public void onSectionAttached(int number) {
		 
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section0);
			break;
		case 2:
			mTitle = getString(R.string.title_section1);
			break;
		case 3:
			mTitle = getString(R.string.title_section2);
			break;
		case 4:
			mTitle = getString(R.string.title_section3);
			break;
		case 5:
			mTitle = getString(R.string.title_section4);
			break;
		case 6:
			mTitle = getString(R.string.title_section5);
			break;
		case 7:
			mTitle = getString(R.string.title_section6);
			break;
		case 8:
			mTitle = getString(R.string.title_section7);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar  actionBar =   getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
//			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
		
		

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

	public static void setTitle(String title) {
		mTitle = title;
	}
	
	

}
