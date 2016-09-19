package com.example.nikirun;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class HomePageFragment extends Fragment implements OnClickListener{
	
	private static View view;
	private Button mRunButton;
	
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
			mRunButton = (Button) view.findViewById(R.id.beginRun_bt);
			mRunButton.setOnClickListener(this);
			
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
}
