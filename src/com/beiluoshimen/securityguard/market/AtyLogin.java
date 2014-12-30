package com.beiluoshimen.securityguard.market;



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

public class AtyLogin extends BaseActivity implements OnClickListener{

	public AtyLogin(int titleRes) {
		super(R.string.title_login);
	}
	
	public AtyLogin() {
		super(R.string.title_login);
	}

	public static final int LOGIN_SUCCEED = 10;
	public static final int LOGIN_FAIL = 11;
	
	
	//UI
	private ProgressBar pb_login;
	private EditText et_phone,et_pass;
	private Button btn_sign_up,btn_sign_in;
	
	
	//Acommunication Service
	private AccountSvcApi accountSvc;
	
	private String username;
	private String password;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//we're share that the accountSvc is implemented in MarketActivity.
		accountSvc = MarketActivity.accountService;
		
		setContentView(R.layout.aty_login);
		pb_login = (ProgressBar) findViewById(R.id.pb_login);
		et_phone = (EditText) findViewById(R.id.et_login_phone_num);
		et_pass = (EditText) findViewById(R.id.et_login_password);
		btn_sign_up = (Button) findViewById(R.id.btn_login_login);
		btn_sign_in = (Button) findViewById(R.id.btn_login_register);
		
		btn_sign_in.setOnClickListener(this);
		btn_sign_up.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login_login:
			if ( (!TextUtils.isEmpty(et_phone.getText().toString())) &&
			     (!TextUtils.isEmpty(et_pass.getText().toString())) ) {
				
				pb_login.setVisibility(View.VISIBLE);
				login();
			}else {
				Toast.makeText(this, "Phone or password can't be empty.", Toast.LENGTH_SHORT).show();;
			}
			break;
		case R.id.btn_login_register:
			Intent intent = new Intent(AtyLogin.this,AtyRegister.class);
			startActivityForResult(intent, 0);
			break;
		}
	}
	
	
	private Handler handler  = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOGIN_SUCCEED:
				System.out.println("handler login success");
				pb_login.setVisibility(View.GONE);
				
				Intent intent = new Intent();
				intent.putExtra("username", username);
				intent.putExtra("password", password);
				setResult(LOGIN_SUCCEED, intent);
				Toast.makeText(AtyLogin.this, "Login succeed !", Toast.LENGTH_LONG).show();
				finish();
				break;

			case LOGIN_FAIL :
				System.out.println("handler login fail");
				pb_login.setVisibility(View.GONE);
				setResult(LOGIN_FAIL);
				Toast.makeText(AtyLogin.this, "Login failed, please try to login later", Toast.LENGTH_LONG).show();
				finish();
				break;
			}
			
			
		};
		
		
	};
	
	
	private void login(){
		new Thread(){
			public void run() {
				Message msg = Message.obtain();
				try {
					username = et_phone.getText().toString();
					password = et_pass.getText().toString();
					accountSvc.login(username, password);
					msg.what = LOGIN_SUCCEED;
//					System.out.println("login success 2.");
					
				} catch (Exception e) {
					msg.what = LOGIN_FAIL;
//					System.out.println("login failed. 3..");	
				}
				handler.sendMessage(msg);
			};		
		}.start();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == AtyRegister.REGISTER_SUCCEED_CODE) {
			et_phone.setText(data.getStringExtra("username"));
			et_pass.setText(data.getStringExtra("password"));
		}
		
	}
	
	
}
