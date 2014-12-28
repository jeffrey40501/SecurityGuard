package com.beiluoshimen.securityguard.antitheft;

import com.beiluoshimen.securityguard.R;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AtySetup4 extends Activity implements OnClickListener{
	private Button btn_remote_wipeout,btn_next4,btn_prev4;
	private SharedPreferences sp;
	private boolean wipeout; // indicated weather the wipeout is on or off.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_at_setup4);
		//animation!!
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.HORIZION_RIGHT);
		
		btn_remote_wipeout = (Button) findViewById(R.id.btnWipeOut);
		btn_next4 = (Button) findViewById(R.id.btnNext4);
		btn_prev4 = (Button) findViewById(R.id.btnPrev4);
		btn_remote_wipeout.setOnClickListener(this);
		btn_next4.setOnClickListener(this);
		btn_prev4.setOnClickListener(this);
		sp = getSharedPreferences(getString(R.string.sp_config), MODE_PRIVATE);
		wipeout = false; //the default setting is false 
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNext4:
			Editor editor = sp.edit();
			if (wipeout == true) {
				//remote wipe out need to be turned on.
				editor.putBoolean(getString(R.string.sp_remote_wipe_out), true);
				editor.commit();
			}else {
				editor.putBoolean(getString(R.string.sp_remote_wipe_out), false);
				editor.commit();
				
			}
			Intent atyAntitheft = new Intent(AtySetup4.this, AtyAntitheft.class);
			startActivity(atyAntitheft);
			finish();
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			break;
			
		case R.id.btnPrev4:
			Intent intent3 = new Intent(AtySetup4.this, AtySetup2.class);
			startActivity(intent3);
			finish();
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			break;
		case R.id.btnWipeOut:
			if (wipeout == false) {
				wipeout = true;
				btn_remote_wipeout.setText(R.string.at_remote_wipe_out_is_on_);
				activeDeviceAdmin();
				
			}else {
				wipeout = false;
				btn_remote_wipeout.setText(R.string.at_remote_wipe_out_is_off_);
			}
			
			break;

		default:
			break;
		}
	}
	
	//get the highest authority.
	private void activeDeviceAdmin(){
		ComponentName admin = new ComponentName(this,Admin.class);
		DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//		check if the component has got the admin authority
		if (!dpm.isAdminActive(admin)) {
			//get the top authority
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
			startActivity(intent);
		}
	}
}
