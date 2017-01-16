package com.example.nikirun;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends Activity implements OnClickListener{
	
	private Button mRegisterButton;
	private EditText mUserNameText,mEmailText,mPasswordText,mConfirmPWText;
	private Spinner mSexSpinner;
	private String mSex;
	private RunUser mRunUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		mRunUser = new RunUser();
		mUserNameText = (EditText) findViewById(R.id.userNameText);
		mSexSpinner = (Spinner) findViewById(R.id.sexSpinner);
		mEmailText = (EditText) findViewById(R.id.emailText);
		mPasswordText = (EditText) findViewById(R.id.passwordText);
		mConfirmPWText = (EditText) findViewById(R.id.confirmPasswordText);
		
		String[] mItems = getResources().getStringArray(R.array.sex);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSexSpinner.setAdapter(adapter);
		mSexSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String[] sexs = getResources().getStringArray(R.array.sex);
				mSex = sexs[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mRegisterButton = (Button) findViewById(R.id.registerButton);
		mRegisterButton.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.registerButton:
			String userName = mUserNameText.getText().toString().trim();
			String email = mEmailText.getText().toString().trim();
			String password = mPasswordText.getText().toString().trim();
			String password2 = mConfirmPWText.getText().toString().trim();
			
			if(userName == null){
				Toast.makeText(this, "please input username", Toast.LENGTH_SHORT).show();
				mUserNameText.requestFocus();
				break;
			}
			if(email == null){
				Toast.makeText(this, "please input email address", Toast.LENGTH_SHORT).show();
				mEmailText.requestFocus();
				break;
			}
			if(password == null){
				Toast.makeText(this, "please input password", Toast.LENGTH_SHORT).show();
				mPasswordText.requestFocus();
				break;
			}
			if(password2 == null){
				Toast.makeText(this, "please confirm password", Toast.LENGTH_SHORT).show();
				mConfirmPWText.requestFocus();
				break;
			}else if(!password.equals(password2)){
				Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
				mConfirmPWText.requestFocus();
				break;
			}
			
			 
			mRunUser.setUsername(userName);
			mRunUser.setEmail(email);
			mRunUser.setPassword(password);
//			mRunUser.setSex(mSex);
			mRunUser.signUp(new SaveListener<RunUser>() {

				@Override
				public void done(RunUser arg0, BmobException e) {
					// TODO Auto-generated method stub
					if(e == null){
						LoginFragment.isLogin = true;
						
						Toast.makeText(getApplicationContext(), "注册成功 ", Toast.LENGTH_SHORT).show();
			        }else{
			        	Log.i(MainActivity.TAG, "注册失败 err = "+e.getMessage());
			        	Toast.makeText(getApplicationContext(), "注册失败 "+e.getMessage(), Toast.LENGTH_SHORT).show();
			        }
				}
			});
			break;

		default:
			break;
		}
	}
}
