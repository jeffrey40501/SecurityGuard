package com.beiluoshimen.securityguard.settings;

import com.beiluoshimen.securityguard.MainUI;
import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
/**
 * This activity is used to set the settings...
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月12日下午5:01:26
 */
public class AtySettings extends BaseActivity{

public AtySettings(int titleRes) {
		super(R.string.title_settings);
	}
public AtySettings() {
	super(R.string.title_settings);
}

	//	autoupdate setting
	private SharedPreferences sp;
	private TextView tv_autoupdate;
	private CheckBox cb_autoupdate;
//	other settings can be added below....
//	--------------------------
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_settings);
		setSlidingActionBarEnabled(false);
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.ALPHA);

		
//		make ActionBar's image can go back to previous activity
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		settingAutoupdate();
//	other settings can be added below....
//	--------------------------
	}
		
	private void settingAutoupdate() {
		sp =getSharedPreferences(getString(R.string.sp_config), MODE_PRIVATE);
		cb_autoupdate = (CheckBox) findViewById(R.id.cb_settings_autoupdate);
		tv_autoupdate =(TextView) findViewById(R.id.tv_setting_autoupdate);
		//default of autoupdate is turned on 
		boolean autoupdate = sp.getBoolean(getString(R.string.sp_autoupdate), true);
		if (autoupdate) {
			tv_autoupdate.setText(R.string.aty_settings_autoupdate_is_on);
			cb_autoupdate.setChecked(true);
		}else {
			tv_autoupdate.setText(R.string.aty_settings_autoupdate_is_off);
			cb_autoupdate.setChecked(false);
		}
		
		cb_autoupdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor =sp.edit();
				editor.putBoolean(getString(R.string.sp_autoupdate), isChecked);
				editor.commit();
//				update the textview
				if (isChecked) {
					tv_autoupdate.setTextColor(Color.WHITE);
					tv_autoupdate.setText(R.string.aty_settings_autoupdate_is_on);
				}else {
					tv_autoupdate.setTextColor(Color.RED);
					tv_autoupdate.setText(R.string.aty_settings_autoupdate_is_off);
				}
				
			}
		});
		
		
	}
}
