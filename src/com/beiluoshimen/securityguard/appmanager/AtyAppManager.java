package com.beiluoshimen.securityguard.appmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.lock.AppInfo;
import com.beiluoshimen.securityguard.lock.AppInfoProvider;
import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;
import com.beiluoshimen.securityguard.tools.DensityUtil;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月23日下午9:51:48
 */
@SuppressLint("NewApi")
//TODO
//show the app size , need to properly use reflection to access hidden method
//TODO
//TODO
//TODO
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AtyAppManager extends BaseActivity implements OnClickListener{
	public AtyAppManager(int titleRes) {
		super(R.string.title_app_manager);
	}
	
	public AtyAppManager(){
		super(R.string.title_app_manager);
	}

	//use to debug.
	protected static final String TAG = "AppManagerActivity";
	private final int USER_LABEL = 1;
	private final int SYSTEM_LABEL = 0;
	
	private final int LOAD_APP_DONE = 2;
	private TextView tv_avail_ram;//available ram
	private TextView tv_avail_sdcard;
	private ListView lv_apps;
	private LinearLayout ll_load;//use to show progress bar
	private PackageManager pm;
	private List<AppInfo> infoAllApp;
	private List<AppInfo> infoUserApp;
	private List<AppInfo> infoSystemApp;
	
	//Popup window
	private PopupWindow popupWindow;
	private LinearLayout ll_uninstall,ll_start,ll_share;
	private String clickedPackageName;

	/**
	 * the method handleMessage is called, once the loading of ArrayList is done
	 * the handelr will set the listView based on managerAdapter (which use Arraylist to sey listview)
	 */
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOAD_APP_DONE:
				ll_load.setVisibility(View.INVISIBLE);
				lv_apps.setAdapter(new managerAdapter());
				break;
			}
		};
	};
	
	private class managerAdapter extends BaseAdapter{

		
		@Override
		public int getCount() {
			//we add +2 since one is for two additional item, which show 
//			the label - "User app" and "System app"
			return infoSystemApp.size()+infoUserApp.size()+2;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return USER_LABEL;
			}else if (position <= infoUserApp.size()) {
				return infoUserApp.get(position-1);
			}else if (position == infoUserApp.size()+1) {
				return SYSTEM_LABEL;
			}else {
				return infoSystemApp.get(position-infoUserApp.size()-2);
			}
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextSize(18);
				tv.setTextColor(Color.parseColor("#AAAAAA"));
				tv.setText("User app ("+infoUserApp.size()+")");
				return tv;
			}else if (position <= infoUserApp.size()) {
				int newposition = position-1;
				View view;
				ViewHolder holder;
				if ( (convertView == null) || convertView instanceof TextView) {
					view = View.inflate(getApplicationContext(), R.layout.app_manager_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) view.findViewById(R.id.iv_appmanager_icon);
					holder.tv_name = (TextView) view.findViewById(R.id.tv_appmanager_name);
					holder.tv_version = (TextView) view.findViewById(R.id.tv_appmanager_version);
					view.setTag(holder);
				}else {
					view = convertView;
					//Returns this view's tag.(the Object stored in this view as a tag, or null if not set)
					holder = (ViewHolder) view.getTag();
				}
				AppInfo appInfo = infoUserApp.get(newposition);
				holder.iv_icon.setImageDrawable(appInfo.getAppIcon());
				holder.tv_name.setText(appInfo.getName());
				holder.tv_version.setText("Version:"+appInfo.getVersion());
				return view;
			}else if (position == (infoUserApp.size()+1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextSize(20);
				tv.setText("System app ("+infoSystemApp.size()+")");
				return tv;
			}else {
				int newposition = position-infoUserApp.size()-2;
				View view;
				ViewHolder holder;
				if ( (convertView == null) || convertView instanceof TextView) {
					view = View.inflate(getApplicationContext(), R.layout.app_manager_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) view.findViewById(R.id.iv_appmanager_icon);
					holder.tv_name = (TextView) view.findViewById(R.id.tv_appmanager_name);
					holder.tv_version = (TextView) view.findViewById(R.id.tv_appmanager_version);
					view.setTag(holder);
				}else {
					view = convertView;
					//Returns this view's tag.(the Object stored in this view as a tag, or null if not set)
					holder = (ViewHolder) view.getTag();
				}
				AppInfo appInfo = infoSystemApp.get(newposition);
				holder.iv_icon.setImageDrawable(appInfo.getAppIcon());
				holder.tv_name.setText(appInfo.getName());
				holder.tv_version.setText("Version:"+appInfo.getVersion());
				return view;
			}
		}
		//disable the two label.....
		@Override
		public boolean isEnabled(int position) {
			if (position == 0 || position == (infoUserApp.size()+1)) {
				return false;
			}
			return super.isEnabled(position);
		}
		
	}
	
	
	private static class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_version;
	}
	
	@Override
	protected void onPause() {
		dismissPopupWindow();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_app_manager);
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.ALPHA);

//		set this property after UI update()
//		setSlidingActionBarEnabled(false);

		
		tv_avail_ram = (TextView) findViewById(R.id.tv_avail_mem);
		tv_avail_sdcard = (TextView) findViewById(R.id.tv_avail_sdcard);
		lv_apps = (ListView) findViewById(R.id.lv_appmanager);
		ll_load = (LinearLayout) findViewById(R.id.ll_manager_load);
		tv_avail_sdcard.setText("SD card available:"+getAvailaleSDSize());
		tv_avail_ram.setText("Internal storage available:"+getAvailableInternalStorage());
		pm = getPackageManager();
	
		//load app
		ll_load.setVisibility(View.VISIBLE);
		uiUpdate();
		//set Action bar to be not poped up
		setSlidingActionBarEnabled(false);
		
		lv_apps.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//have to dismiss the popup window already exist when we click another item.
				dismissPopupWindow();
				View contentView = View.inflate(getApplicationContext(),R.layout.popup_manager_item, null);
				ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_popup_uninstall);
				ll_start = (LinearLayout) contentView.findViewById(R.id.ll_popup_start);
				ll_share = (LinearLayout) contentView.findViewById(R.id.ll_popup_share);
				ll_share.setOnClickListener(AtyAppManager.this);
				ll_start.setOnClickListener(AtyAppManager.this);
				ll_uninstall.setOnClickListener(AtyAppManager.this);
				LinearLayout ll_popup_container = (LinearLayout) contentView.findViewById(R.id.ll_popup_manager_container);
				int top = view.getTop();
				int bottom = view.getBottom();     
				//View contentView, int width, int height)
				popupWindow = new PopupWindow(contentView, DensityUtil.dip2px(getApplicationContext(), 200),
						bottom-top+DensityUtil.dip2px(getApplicationContext(), 40));
				// we must set the background Drawable
				//TODO
//				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				
				// the array contains the x and y location in that order.
				int[] location = new int[2];
				view.getLocationInWindow(location);
				// (View parent, int gravity, int x, int y)
				popupWindow.showAtLocation(view, Gravity.TOP|Gravity.END,location[0], location[1]);
				//show the animation
				new SwitchAnimationUtil().startAnimation(ll_popup_container, AnimationType.SCALE);
				
				Object obj = lv_apps.getItemAtPosition(position);
				if (obj instanceof AppInfo) { // is not label
					AppInfo appinfo = (AppInfo) obj;
					clickedPackageName = appinfo.getPackageName();
					if (appinfo.isUserApp()) {
						ll_uninstall.setTag(true);
					} else {
						//we use this Tag to indicate that system app can't be uninstalled
						ll_uninstall.setTag(false);
					}
				} else { // do nothing if user click LABEL (only two)
					return;
				}
			}
		});
		
	}

	private String getAvailableInternalStorage() {
		
		int version = android.os.Build.VERSION.SDK_INT;
		if (version < 19) {
			return getString(R.string.manager_no_sdcard);
		}
		//Return the user data directory.
		File path = Environment.getRootDirectory();
		StatFs stat = new StatFs(path.getPath());
		long availBlocks = stat.getAvailableBlocksLong();
		long blockSize = stat.getBlockSizeLong();
		return Formatter.formatFileSize(this, blockSize*availBlocks);
		
	}


	public String getAvailaleSDSize(){
		
		
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Log.i(TAG, "we have sd card");
				File path = Environment.getExternalStorageDirectory();
//				//onstruct a new StatFs for looking at the stats of the filesystem at path
				int version = android.os.Build.VERSION.SDK_INT;
				System.out.println(version);
				System.out.println(version);
				System.out.println(version);
				System.out.println(version);
				System.out.println(version);
				System.out.println(version);
				if (version < 19) {
					return getString(R.string.manager_no_sdcard);
				}
				StatFs stat = new StatFs(path.getPath()); 
				long blockSize = stat.getBlockSizeLong(); 
//				//The total number of blocks on the file system. 
				long availableBlocks = stat.getAvailableBlocksLong();
//				//calculate total size
				return Formatter.formatFileSize(this, blockSize*availableBlocks);
			}else {
				Log.i(TAG, "no sd card");

				return getString(R.string.manager_no_sdcard);
			}
		} catch (Exception e) {
			Log.i(TAG, "no sd card");
			return getString(R.string.manager_no_sdcard);

		}
		

		}
	
	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_popup_share:
			Log.i(TAG, "share");
			shareApplication();
			break;
		case R.id.ll_popup_start:
			Log.i(TAG, "start");
			startAppliction();
			break;
		case R.id.ll_popup_uninstall:
			Log.i(TAG, "uninstall");
			uninstallApplication();
			break;
		}
	}
	
	/**
	 * share one app by using intent - action.send - category.default
	 */
	private void shareApplication() {
		dismissPopupWindow();
       	Intent intent = new Intent();
       	//the shareing action
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		// the title 
		intent.putExtra("subject", R.string.ma_share);
		// main body
		intent.putExtra("sms_body", R.string.ma_recommend_you_one_app_+clickedPackageName);
		//TODO
		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.ma_recommend_you_one_app_)+clickedPackageName);
		startActivity(intent);
	}
	/**
	 * uninstall on app by using intent.
	 */
	private void uninstallApplication() {
		dismissPopupWindow();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+clickedPackageName));
		startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode = 1 means it's the result of uninstalling
		if (requestCode == 1) {
			// we have to update ui since we delete one app.
			uiUpdate();
			
			
			tv_avail_sdcard.setText("SD card available:"+getAvailaleSDSize());
			tv_avail_ram.setText("Internal storage available:"+getAvailableInternalStorage());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * start the app clicked
	 */
	private void startAppliction() {
		dismissPopupWindow();
		Intent intent = new Intent();
		PackageInfo packinfo;
		try {
			// the app might have not only one activity
			//GET_ACTIVITIES = return information about activities in the package in activities.
			packinfo = pm.getPackageInfo(clickedPackageName,PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityinfos = packinfo.activities;
			//check 
			if (activityinfos != null && activityinfos.length > 0) {
				//start the first activity 
				String className = activityinfos[0].name;
				intent.setClassName(clickedPackageName, className);
				//give this activity it's own task stack.!!!
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.ma_the_app_can_t_be_start_, Toast.LENGTH_SHORT).show();
			}
		} catch (NameNotFoundException e) {
			//foe C code , don't have corresponding package in DDMS
			e.printStackTrace();
			Toast.makeText(this, R.string.ma_the_app_can_t_be_start_, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * this function will update the infoSystemApp and infoUserApp
	 *  and call the handler's methood sendMessage once the update is done.
	 */
	private void uiUpdate(){
		new Thread(){
			public void run() {
				AppInfoProvider provider = new AppInfoProvider(AtyAppManager.this);
				infoAllApp = provider.getInfos();
				infoSystemApp = new ArrayList<AppInfo>();
				infoUserApp = new ArrayList<AppInfo>();
				for (AppInfo appInfo : infoAllApp) {
//					System.out.println(appInfo.getSize()+"!!!!!!!");
					if (appInfo.isUserApp()) {
						infoUserApp.add(appInfo);
					}else {
						infoSystemApp.add(appInfo);
					}
				}
				// told main thread loading is done
				Message msg = Message.obtain();
				msg.what = LOAD_APP_DONE;
				handler.sendMessage(msg);
			};
		}.start();
	}
}





