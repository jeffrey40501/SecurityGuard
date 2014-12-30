package com.beiluoshimen.securityguard.antitheft;

import java.util.Iterator;

import com.beiluoshimen.securityguard.R;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;
import android.util.Log;
/**
 * This class can be used to receive message and according to this message,
 * wipe out the content of this phone.
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月20日下午9:42:05
 */



public class BrSMS extends BroadcastReceiver{
	private static final String TAG = "BrSMS";
	SharedPreferences sp;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "receive message");
		sp = context.getSharedPreferences(context.getString(R.string.sp_config),Context.MODE_PRIVATE);
		
		//once the message is received , the message content is in the array of "pdus"
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		
		//create one instance of the device policy manager
		DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName admin = new ComponentName(context, Admin.class);
		for(Object obj:objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String body = smsMessage.getMessageBody();
			//check if the password match the messagebody
			if (body.equals(sp.getString(context.getString(R.string.sp_password),""))) {
				Log.i(TAG, "WIPE OUT DATA!!");
				
				//check if we have the admin authority to wipe out data on devices....
				if (dpm.isAdminActive(admin)) {
					//TODO  TEST TEST  wipe out all data
//					dpm.wipeData(0);//reboot the device
					System.err.println("i have the authority!!!!");
				}else {
					System.err.println("no active adimin");
				}
				abortBroadcast(); //don't show this wipe out password message . hide it....
			}
		}
		
	}

}
