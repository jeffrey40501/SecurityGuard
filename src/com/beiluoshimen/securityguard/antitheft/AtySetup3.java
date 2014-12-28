package com.beiluoshimen.securityguard.antitheft;

import com.beiluoshimen.securityguard.R;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AtySetup3 extends Activity implements OnClickListener{
	private EditText et_safe_num;
	private Button btn_select_contacts,btn_next3,btn_prev3;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_at_setup3);
		//animation!!
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.HORIZION_RIGHT);
		
		et_safe_num = (EditText) findViewById(R.id.etTextSafeNum);
		btn_select_contacts = (Button) findViewById(R.id.btnSelectContacts);
		btn_next3 = (Button) findViewById(R.id.btnNext3);
		btn_prev3 = (Button) findViewById(R.id.btnPrev3);
		btn_select_contacts.setOnClickListener(this);
		btn_next3.setOnClickListener(this);
		btn_prev3.setOnClickListener(this);
		sp = getSharedPreferences(getString(R.string.sp_config), MODE_PRIVATE);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSelectContacts:
			//get the contacts of this phone
			Intent intentContacts = new Intent(AtySetup3.this, AtySelectContacts.class);
			startActivityForResult(intentContacts, 0);
			
			
			break;
		case R.id.btnNext3:
			//save the safe number and go to setup4
			if (TextUtils.isEmpty(et_safe_num.getText().toString())) {
				Toast.makeText(AtySetup3.this, R.string.at_this_safe_number_is_empty, Toast.LENGTH_SHORT).show();
			}else {
				String safeNum = et_safe_num.getText().toString();
				Editor editor = sp.edit();
				editor.putString(getString(R.string.sp_safenumber), safeNum);
				editor.commit();
				Toast.makeText(AtySetup3.this,R.string.at_the_safe_number_set_successfully_, Toast.LENGTH_SHORT).show();
			}
			//start setup4 
			
			Intent intent4 = new Intent(AtySetup3.this, AtySetup2.class);
			startActivity(intent4);
			finish();
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			break;
		case R.id.btnPrev3:
			Intent intent2 = new Intent(AtySetup3.this, AtySetup1.class);
			startActivity(intent2);
			finish();
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data!=null) {
			String number = data.getStringExtra("phone");
			if (number != null) {
				et_safe_num.setText(number);
			}
			
		}
	}
}
