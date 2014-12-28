package com.beiluoshimen.securityguard.antitheft;

import com.beiluoshimen.securityguard.R;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class AtySetup2 extends Activity implements OnClickListener{
	private ImageButton ib_bind;
	private Button btn_next2,btn_prev2;
	private SharedPreferences sp;
	private boolean sim_protect_on;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_at_setup2);
		//animation!!
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.HORIZION_RIGHT);
		ib_bind = (ImageButton) findViewById(R.id.ibLock);
		btn_next2 = (Button) findViewById(R.id.btnNext2);
		btn_prev2 = (Button) findViewById(R.id.btnPrev2);
		ib_bind.setOnClickListener(this);
		btn_next2.setOnClickListener(this);
		btn_prev2.setOnClickListener(this);
		
		sp = getSharedPreferences(getString(R.string.sp_config), MODE_PRIVATE);
		sim_protect_on = sp.getBoolean(getString(R.string.sp_sim_protect_on), false);
		if (!sim_protect_on) {
			ib_bind.setImageResource(R.drawable.unlock);
		}else {
			ib_bind.setImageResource(R.drawable.lock);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNext2:
			//go to setup 3
			Intent intent3 = new Intent(this, AtySetup4.class);
			startActivity(intent3);
			finish();
			//animation 
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			
			break;
		case R.id.btnPrev2:
			//go back to setup1
			Intent intent1 = new Intent(this, AtySetup3.class);
			startActivity(intent1);
			finish();
			//animation 
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;
		case R.id.ibLock:
			//lock status change...
			String sim;
			Editor editor = sp.edit();
			if (!sim_protect_on) {
				//lock the sim
				sim = getSim();
				System.out.println(sim+"my sim serial num");
//				sim = getGoogleID();
				// splendidusworld 5a68d37dd4484d8a
//				sim = getDeviceID();//IMEI NUMBER !!!
				editor.putString(getString(R.string.sp_simserial), sim);
				if (sim!=null) {
					editor.putBoolean(getString(R.string.sp_sim_protect_on), true);
					System.out.println("sim != null");
					ib_bind.setImageResource(R.drawable.lock);
					sim_protect_on = true;
				}else { //sim == null
					editor.putBoolean(getString(R.string.sp_sim_protect_on), false);
					System.out.println("sim == null");
					Toast.makeText(AtySetup2.this, "Sorry!\nYour phone does not support this function.", Toast.LENGTH_SHORT).show();
				}
				
			}else { // sim protect is on...
				// unlock the sim card
				editor.putString(getString(R.string.sp_simserial), "");
				editor.putBoolean(getString(R.string.sp_sim_protect_on), false);
				sim = "";
				sim_protect_on = false;
				ib_bind.setImageResource(R.drawable.unlock);
			}
			editor.commit();
			break;
		}
	}
	
//	get the sim card serial number, require permission read_phone_state
	private String getSim(){
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		return tm.getSimSerialNumber();
	}
	
	private String getGoogleID(){
		return android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	}
	private String getDeviceID(){
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
}
