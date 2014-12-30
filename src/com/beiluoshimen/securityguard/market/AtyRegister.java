package com.beiluoshimen.securityguard.market;


import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;
import com.beiluoshimen.securityguard.tools.UtilTools;

public class AtyRegister extends BaseActivity implements OnClickListener{

	private ProgressBar pb_register;
	private Button btn_confirm,btn_cancel;
	private EditText et_phone,et_pass,et_pass2;
	
	private String pass1,pass2,phone;
	
	public AtyRegister(int titleRes) {
		super(R.string.title_register);
	}
	
	public AtyRegister(){
		super(R.string.title_register);
	}
	
	protected static final int  REGISTER_SUCCEED_CODE = 4;
	protected static final int  REGISTER_FAIL_CODE = 5;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_register);
		btn_confirm = (Button) findViewById(R.id.btn_register_confirm);
		btn_cancel = (Button) findViewById(R.id.btn_register_cancel);
		btn_confirm.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		pb_register = (ProgressBar) findViewById(R.id.pb_register);
		et_pass = (EditText) findViewById(R.id.et_register_password);
		et_pass2 = (EditText) findViewById(R.id.et_register_password2);
		et_phone = (EditText) findViewById(R.id.et_register_phone_num);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register_confirm:
			
			pass1 = et_pass.getText().toString();
			pass2 = et_pass2.getText().toString();
			phone = et_phone.getText().toString();
			
			if (!pass1.equals(pass2)) {
				Toast.makeText(AtyRegister.this, "The password doesn't match.", Toast.LENGTH_LONG).show();
				break;
			}else if (TextUtils.isEmpty(pass1) || TextUtils.isEmpty(phone)) {
				Toast.makeText(AtyRegister.this, "Can't be empty.", Toast.LENGTH_LONG).show();
				break;
			}else {
				pb_register.setVisibility(View.VISIBLE);
				registerAccount();
			}
			break;

		case R.id.btn_register_cancel:
			setResult(REGISTER_FAIL_CODE);
			finish();
			break;
		}
		
	}
	
	void registerAccount(){
		new Thread(){
			public void run() {
				try {
					boolean status = MarketActivity.accountService.addAccount(new Account(phone, pass1, 0,new ArrayList<Integer>()));
					if (status) {
						Message msg = Message.obtain();
						msg.what = REGISTER_SUCCEED_CODE;
						handler.sendMessage(msg);
					}else { //i.e. duplicate name 
						Message msg = Message.obtain();
						msg.what = REGISTER_FAIL_CODE;
						handler.sendMessage(msg);
					}
				} catch (Exception e) { //like sever is shut down.
					Message msg = Message.obtain();
					msg.what = REGISTER_FAIL_CODE;
					handler.sendMessage(msg);
				}	
			};
			
		}.start();
		
		
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == REGISTER_SUCCEED_CODE) {
				Toast.makeText(AtyRegister.this, "Register Succeed.", Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.putExtra("username", phone);
				intent.putExtra("password", pass1);
				setResult(REGISTER_SUCCEED_CODE, intent);
				finish();
				
			}else if (msg.what == REGISTER_FAIL_CODE) {
				Toast.makeText(AtyRegister.this, "Register Failed.", Toast.LENGTH_LONG).show();
				pb_register.setVisibility(View.GONE);
				
			}
			
		};
		
	};
	

}
