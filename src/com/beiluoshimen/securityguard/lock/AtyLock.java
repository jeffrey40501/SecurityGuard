package com.beiluoshimen.securityguard.lock;

import java.net.URI;
import java.util.List;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.antitheft.AtyAntitheft;
import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月23日下午1:57:34
 */
public class AtyLock extends BaseActivity implements OnCheckedChangeListener{
	
	public AtyLock(){
		super(R.string.title_app_lock);

	}
	
	public AtyLock(int titleRes) {
		super(R.string.title_app_lock);
	}


	// use to show all the apps
	private ListView lv_lock;
	//use to show while loading data
	private LinearLayout ll_loading;
	private AppLockDB db;
	private AppInfoProvider provider;
	private List<AppInfo> appInfos;
	private List<String> lockedPackNames;
	
	private CheckBox cb_lock_on;
	private CheckBox cb_auto_lock_on;
	
	private Intent dog;
	private SharedPreferences sp;
	private boolean lockOn,autoLockOn;
	
	
	//TODO issue?
//	Provides more information about this issue.
//
//	Since this Handler is declared as an inner class, it 
//	 may prevent the outer class from being garbage 
//	 collected. If the Handler is using a Looper or 
//	 MessageQueue for a thread other than the main 
//	 thread, then there is no issue. If the Handler is using 
//	 the Looper or MessageQueue of the main thread, you 
//	 need to fix your Handler declaration, as follows: 
//	 Declare the Handler as a static class; In the outer 
//	 class, instantiate a WeakReference to the outer class 
//	 and pass this object to your Handler when you 
//	 instantiate the Handler; Make all references to 
//	 members of the outer class using the WeakReference 
//	 object.
	
	//handle the data child thread has get by this handler.
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//once db has fetched the data, hide loading...
			ll_loading.setVisibility(View.INVISIBLE);
			// show main UI
			lv_lock.setVisibility(View.VISIBLE);
			lv_lock.setAdapter(new LockAdapter());
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_lock);
		//slideing action bar
		setSlidingActionBarEnabled(false);
		
		lv_lock = (ListView) findViewById(R.id.lv_lock);
		ll_loading = (LinearLayout) findViewById(R.id.ll_lock_load);
		cb_auto_lock_on = (CheckBox) findViewById(R.id.cb_turn_auto);
		cb_lock_on = (CheckBox) findViewById(R.id.cb_turn_on);
		cb_auto_lock_on.setOnCheckedChangeListener(this);
		cb_lock_on.setOnCheckedChangeListener(this);
		//animation
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.ALPHA);
		
		// show loading...
		ll_loading.setVisibility(View.VISIBLE);
		//check if autoOn is on?
		sp = getSharedPreferences(getString(R.string.sp_config), MODE_PRIVATE);
		if (!passwordIsSet()) {
			Toast.makeText(this,R.string.lock_you, Toast.LENGTH_SHORT).show();;
			startActivity(new Intent(this, AtyAntitheft.class));
			overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
			finish();
		}
		
		
		lockOn = sp.getBoolean(getString(R.string.sp_lock_on), true); //default is false
		autoLockOn = sp.getBoolean(getString(R.string.sp_auto_lock_on), false); 
		if (autoLockOn == true && lockOn == true) {
			cb_auto_lock_on.setChecked(true);
			cb_lock_on.setChecked(true);
		}else if (autoLockOn == false && lockOn == true) {
			cb_auto_lock_on.setChecked(false);
			cb_lock_on.setChecked(true);
		}else if (autoLockOn == true && lockOn == false) {
			cb_auto_lock_on.setChecked(true);
			cb_lock_on.setChecked(false);
		}else { //false // false
			cb_auto_lock_on.setChecked(false);
			cb_lock_on.setChecked(false);
		}
		
		
		//find all the locked package name from db
		db = new AppLockDB(this);
		lockedPackNames = db.findAll();
		//find all appinfo on the devices
		provider = new AppInfoProvider(this);
		
		new Thread(){
			public void run() {
				appInfos = provider.getInfos();
				handler.sendEmptyMessage(0);
			};
		}.start();
		
		//set listener to listview
		lv_lock.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//get the pointer of this position
				AppInfo appInfo = (AppInfo) lv_lock.getItemAtPosition(position);
				
				//get the name and the status image.
				String packName = appInfo.getPackageName();
				ImageView iv = (ImageView) view.findViewById(R.id.iv_lock_status);
				//set an animation to show the state changing
				TranslateAnimation anim = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.2f,
						Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
				anim.setDuration(300);
				if (lockedPackNames.contains(packName)) {
//					db.delete(packName);
					//use content resolver to change the db by using content provider.
					Uri uri = Uri.parse("content://com.beiluoshimen.securityguard/DELETE");
					getContentResolver().delete(uri, null, new String[]{packName});
					
					iv.setImageResource(R.drawable.unlock);
					lockedPackNames.remove(packName);
				}else {
//					db.add(packName);
					//use content resolver to change the db by using content provider.
					Uri uri = Uri.parse("content://com.beiluoshimen.securityguard/ADD");
					ContentValues values = new ContentValues();
					values.put("packname", packName);
					getContentResolver().insert(uri, values);
					
					iv.setImageResource(R.drawable.lock);
					lockedPackNames.add(packName);
				}
				//show the animation
				view.startAnimation(anim);
			}
		});
		
		
		
	};
	private boolean passwordIsSet() {
		String pass = sp.getString(getString(R.string.sp_password), "");
		if (TextUtils.isEmpty(pass)) {
			return false;
		}else {
			return true;
		}
	}
	
	private class LockAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return appInfos.size();
		}

		@Override
		public AppInfo getItem(int arg0) {
			return appInfos.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
//TODO
		//!!!!!!!!!Important!!
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			//check if we can reuse 
			if(convertView !=null){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else { // can't reuse 
				view = View.inflate(getApplicationContext(),R.layout.lock_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_lock_icon);
				holder.iv_status = (ImageView) view.findViewById(R.id.iv_lock_status);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_lock_name);
				view.setTag(holder);
			}
			
			//get the info about this app, and set it's corresponding status,name,image...
			AppInfo appInfo = appInfos.get(position);
			holder.iv_icon.setImageDrawable(appInfo.getAppIcon());
			holder.tv_name.setText(appInfo.getName());
			//check if the app is locked or not
			if (lockedPackNames.contains(appInfo.getPackageName())) {
				holder.iv_status.setImageResource(R.drawable.lock);
			}else {
				holder.iv_status.setImageResource(R.drawable.unlock);
			}
			return view;
		}
	}
	/**
	 * All the item share this view....
	 */
	public static class ViewHolder{
		ImageView iv_icon;
		ImageView iv_status;
		TextView tv_name;
	}
		
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		System.err.println("@@@@@@@1");
		Editor editor = sp.edit();
		if (buttonView.getId() == R.id.cb_turn_auto) {
			if (isChecked) {
				System.err.println("@@@@@@@2");
				cb_auto_lock_on.setChecked(true);
				editor.putBoolean(getString(R.string.sp_auto_lock_on), true);
			}else {
				System.err.println("@@@@@@@3");
				cb_auto_lock_on.setChecked(false);
				editor.putBoolean(getString(R.string.sp_auto_lock_on), false);
			}
		}else if (buttonView.getId() == R.id.cb_turn_on) {
			if (isChecked) {
				System.err.println("@@@@@@@4");
				cb_lock_on.setChecked(true);
				editor.putBoolean(getString(R.string.sp_lock_on), true);
				dog = new Intent(AtyLock.this,LockService.class);
				startService(dog);
			}else {
				System.err.println("@@@@@@@5");
				editor.putBoolean(getString(R.string.sp_lock_on), false);
				cb_lock_on.setChecked(false);
				dog = new Intent(AtyLock.this,LockService.class);
				stopService(dog);
				
			}
		}
		editor.commit();
	}
	
}
