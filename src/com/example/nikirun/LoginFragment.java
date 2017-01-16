package com.example.nikirun;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.b.thing;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginFragment extends Fragment implements OnClickListener{
	  
	private static View view;
	private Button  mLoginButton, mRegisterButton;
	private TextView mUserNameView, mPassword;
	private RunUser mRunUser;
	public static Boolean isLogin = false;
	public static String USER_NAME = "userName" ,LOGIN_STATUS = "login_status",USER_INFO = "user_info";
	private String mUserName;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		view = inflater.inflate(R.layout.login, container,false);
		mRunUser = new RunUser();
		mLoginButton = (Button) view.findViewById(R.id.loginButton);
		mRegisterButton = (Button) view.findViewById(R.id.registerButton);
		mUserNameView = (TextView) view.findViewById(R.id.login_userNameText);
		mPassword = (TextView) view.findViewById(R.id.login_passwordText);
		
		
		mLoginButton.setOnClickListener(this);
		mRegisterButton.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.loginButton:
			mUserName = mUserNameView.getText().toString().trim();
			String password = mPassword.getText().toString().trim();
			if(mUserName == null){
				Toast.makeText(getActivity(), "please input username", Toast.LENGTH_SHORT).show();
				mUserNameView.requestFocus();
				break;
			}
			if(password == null){
				Toast.makeText(getActivity(), "please input password", Toast.LENGTH_SHORT).show();
				mPassword.requestFocus();
				break;
			}
			mRunUser.setUsername(mUserName);
			mRunUser.setPassword(password);
			mRunUser.login(new SaveListener<BmobUser>() {

				@Override
				public void done(BmobUser arg0, BmobException e) {
					// TODO Auto-generated method stub
					if(e == null){
						Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
						isLogin = true;
						SharedPreferences.Editor editor =  getActivity().getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
						editor.putBoolean(LOGIN_STATUS, isLogin);
						editor.putString(USER_NAME, mUserName);
						editor.commit();
						
						Intent intent = new Intent(getActivity(),UserInfoActivity.class);
						startActivity(intent);
						//登录成功后移除登录界面
						getActivity().getFragmentManager().popBackStack();
						
					}else{
						Toast.makeText(getActivity(), "登录失败\n "+e.getMessage(), Toast.LENGTH_SHORT).show();
						Log.i(MainActivity.TAG, "login fail error = "+e.getMessage());
					}
				}
			});
			break;
		case R.id.registerButton:
			 Intent intent = new Intent(getActivity(),RegisterActivity.class);
			 startActivity(intent);
		default:
			break;
		}
	}
}
