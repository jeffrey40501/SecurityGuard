package com.beiluoshimen.securityguard.lock;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class LockService extends Service{
	protected final static String TAG = "LockService";
	// true = LockService is turning on , false = off;
	private boolean flag; 
	// this intent is used to start the UI of password input. which starts when lock-app try to start.
	private Intent passIntent;
	private List<String> lockPackNames;
	private AppLockDB db;
	//use to store the package name of app which do not want to be locked temporarily.
	private List<String> tempStopLockPackNames;
	
	
	// this observer observers the changes in content provider (which store the current apps locked,),
	// once more app should be lock or some app has been removed from lock
	// this observer should update the locklist of this Service.
	// Content Observer
	private MyObserver observer;
	// Receives call backs for changes to content.
	private class MyObserver extends ContentObserver{
		
		public MyObserver(Handler handler) {
			super(handler);
		}
		@Override
		public void onChange(boolean selfChange) {
			// get all the new data from db;
			lockPackNames = db.findAll();
			super.onChange(selfChange);
			
		}
	}
	
	// This is used to clear all the tempStopLockPack names in the list 
	// once the cell phone screen is off,
	// thus next time the cell phone is used, all the apps need lock again to enter.
	// Broadcast Receiver
	private LockScreenReceiver receiver;
	private class LockScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "The screen is close !");
			tempStopLockPackNames.clear();
		}
		
		
	}
	
//Return the communication channel to the service. May return null if clients can not bind to the service. 
//	The returned IBinder is usually for a complex interface , here we only have one method, addtempStopLock
	
	@Override
	public IBinder onBind(Intent intent) {
		binder = new MyBinder();
		return binder;
	}
	private MyBinder binder;
	private class MyBinder extends Binder implements IService{
		
		// the interface IService's method
		@Override
		public void callTempStopLock(String packName) {
			tempStopLockPackNames.add(packName);
			System.err.println("added!!!!");
		}

	}
	
	@Override
	public void onCreate() {
//		Register an observer class that gets callbacks when data identified by a given content URI changes.		
		Uri uri = Uri.parse("content://com.beiluoshimen.securityguard/");
		observer = new MyObserver(new Handler());
		getContentResolver().registerContentObserver(uri, true, observer);
		
//		Register a BroadcastReceiver to be run in the main activity thread. 
//		The receiver will be called with any broadcast Intent that matches filter, in the main application thread.
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		receiver = new LockScreenReceiver();
		registerReceiver(receiver, filter);
		
		super.onCreate();
		db = new AppLockDB(this);
		flag = true;
		tempStopLockPackNames = new ArrayList<String>();
		lockPackNames = db.findAll();
		
		//the intent used to start PasswordEnterUI activity
		passIntent = new Intent(this,PassEnterActivity.class);
		//If set, this activity will become the start of a new task on this history stack
		// because service activity(LockService doesn't have task stack)!
		passIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(){
			public void run() {
				// dead loop , run forever.
				while(flag){
					//retrieve a ActivityManager for interacting with the global system state.
					ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
					//TODO
					RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
					String packName = taskInfo.topActivity.getPackageName();
//					Log.i(TAG, "User starts"+packName);
					if (tempStopLockPackNames.contains(packName)) {
						try {
							//since it it really power consuming , we only check every 0.7 sec
							Thread.sleep(500);
						} catch (Exception e) {
							e.printStackTrace();
						}
						continue;
					}
					
					//	check if this app is lock or not , if locked, start password input activity				
					if (lockPackNames.contains(packName)) {
						passIntent.putExtra("packname", packName);
						startActivity(passIntent);
					}
					try {
						Thread.sleep(700);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
		}.start();
	}
	
	@Override
	public void onDestroy() {
		flag = false;
		getContentResolver().unregisterContentObserver(observer);
		observer = null;
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}
	
	
	
	
}
