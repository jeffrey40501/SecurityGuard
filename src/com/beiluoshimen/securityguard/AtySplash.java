package com.beiluoshimen.securityguard;

import com.beiluoshimen.securityguard.market.MarketActivity;
import com.beiluoshimen.securityguard.moe.MoeApplication;
import com.beiluoshimen.securityguard.settings.AtySettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * This activity do the following things
 * 1.Show the logo (brand!)
 * 2.Initialize the app (database,dada copy,share preference)
 * 3.connect to server to check update.
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月12日下午2:11:30
 */
public class AtySplash extends Activity{
	private TextView tv_version;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		set as no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		set as full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);		
//		DEBUG:setcontentview must be called before findviewbyid!!!!!!
		setContentView(R.layout.aty_splash);
		tv_version = (TextView) findViewById(R.id.tv_splash_veison);
		tv_version.setText("Version:"+getVersion());

		//test
		//and we can get the thief cell phone number!.
//		smsManager.sendTextMessage("0978519680", null,"SIM card has changed.", null, null);
		
		startActivity(new Intent(AtySplash.this, MoeApplication.class));
		finish();
	}
	/**
	 * return the current version of this app
	 * @return
	 */
	private String getVersion(){
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			
			//Retrieve overall information about an application package that is installed on the system.
			//Return the name of this application's package.

			info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	
}
