package com.beiluoshimen.securityguard.antitheft;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.lock.LockService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BrBoot extends BroadcastReceiver{
	private static final String TAG = "BrBoot";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "The phone has restart.");
		
		//check weather the sim card protection is on?
		SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_config), Context.MODE_PRIVATE);
		boolean protect = sp.getBoolean(context.getString(R.string.sp_sim_protect_on), false);
		if (protect) {
			String safenumber = sp.getString(context.getString(R.string.sp_safenumber), "");
			String mySIM = sp.getString(context.getString(R.string.sp_simserial), "");
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String thiefSIM = tm.getSimSerialNumber();
			if (thiefSIM == null) { // the thief's sim card can be reconginzed . but we can still set it as = ""; 
				thiefSIM = "";
			}
//			TODO IF WE NEED TO TEST THIS FUNCTION, Just comment out the code one line below.
			if (!thiefSIM.equals(mySIM) && safenumber != "") { // sim serail differ 
				//send the alarming message that this cell phone has been stolen.
				Log.i(TAG, "SEND THIEF MESSAGE");
				SmsManager smsManager = SmsManager.getDefault();
				//and we can get the thief cell phone number!.
				smsManager.sendTextMessage(safenumber, null,"SIM card changed.To clear your phone,send your anti-theft password to thief's number.", null, null);
			}
		}
		
		
		//AUTO TURN ON THE LOCK-APP SERVICE (RECOMMEND USE SP TO CHECK)
		boolean dog = sp.getBoolean(context.getString(R.string.sp_auto_lock_on), false);
		if (dog) {
			Intent lockIntent = new Intent(context,LockService.class);
			context.startService(lockIntent);
		}	
	}
	

}
