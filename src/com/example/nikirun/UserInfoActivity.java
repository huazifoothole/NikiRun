package com.example.nikirun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobUser;

public class UserInfoActivity extends Activity implements OnClickListener{
	
	private ImageView mHeadImage;
	private TextView mUserInfoName,mUserInfoEmail;
	private Button mLogoutButton;
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo);
		
		mUserInfoName = (TextView) findViewById(R.id.userInfoName);
		mUserInfoEmail = (TextView) findViewById(R.id.userInfoEmail);
		
//		SharedPreferences sharedPreferences = getSharedPreferences(LoginFragment.USER_INFO, MODE_PRIVATE);
//		String name = sharedPreferences.getString(LoginFragment.USER_NAME, "runner");
		String email = (String) BmobUser.getObjectByKey("email");
		String name = (String) BmobUser.getObjectByKey("username");
		mUserInfoName.setText(name);	 
		mUserInfoEmail.setText(email);
		
		mLogoutButton = (Button) findViewById(R.id.logOutbutton);
		mLogoutButton.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.logOutbutton:
			BmobUser.logOut();
			BmobUser currentUser = BmobUser.getCurrentUser();
			if(currentUser == null){
				Toast.makeText(getApplicationContext(), "退出登录成功", Toast.LENGTH_SHORT).show();
				LoginFragment.isLogin = false;
				SharedPreferences.Editor editor =  getApplicationContext().getSharedPreferences(LoginFragment.USER_INFO, Context.MODE_PRIVATE).edit();
				editor.putBoolean(LoginFragment.LOGIN_STATUS, LoginFragment.isLogin);
				editor.commit();
				Intent intent = new Intent(this,MainActivity.class);
				startActivity(intent);
			}else {
				Toast.makeText(getApplicationContext(), "退出登录失败", Toast.LENGTH_SHORT).show();

			}
			break;

		default:
			break;
		}
	}
}
