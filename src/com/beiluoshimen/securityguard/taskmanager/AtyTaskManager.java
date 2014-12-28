package com.beiluoshimen.securityguard.taskmanager;

import java.util.ArrayList;
import java.util.List;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * This activity is singleton
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月27日下午4:03:20
 */

public class AtyTaskManager extends BaseActivity implements OnClickListener{
	public AtyTaskManager(int titleRes) {
		super(R.string.titile_task_manager);
	}
	
	public AtyTaskManager(){
		super(R.string.titile_task_manager);
	}
	private ListView lv_user;
	private ListView lv_system;
	//use to check current status (lv_user or lv_system)
	private boolean lvIsUser;
	private Button btn_user, btn_system, btn_selct_all,btn_kill;
	//used to set list view
	private UserAdapter useradapter;
	private SystemAdapter systemadapter;
	private ProcessInfoProvider provider;
	// unsafe to kill system app;
	private List<ProcessInfo> userProcessInfos;
	private List<ProcessInfo> systemProcessInfos;
	//use tp show while the data is loading...
	private LinearLayout ll_loading;
	private RelativeLayout rl_ui;
	
	private final int LOAD_DONE = 2;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOAD_DONE:
				ll_loading.setVisibility(View.GONE);
				rl_ui.setVisibility(View.VISIBLE);
				

				
				useradapter = new UserAdapter();
				systemadapter = new SystemAdapter();
				lv_user.setAdapter(useradapter);
				lv_system.setAdapter(systemadapter);
//				useradapter = new UserAdapter();
//				lv_user.setAdapter(useradapter);
//				systemadapter = new SystemAdapter();
//				lv_system.setAdapter(systemadapter);
				break;
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_task_manager);
		setSlidingActionBarEnabled(false);
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.ALPHA);

		lvIsUser = true;
		ll_loading = (LinearLayout) findViewById(R.id.ll_tm_loading);
		rl_ui = (RelativeLayout) findViewById(R.id.rl_tm_ui);
		new Thread(){
			public void run() {
				userProcessInfos = new ArrayList<ProcessInfo>();
				systemProcessInfos = new ArrayList<ProcessInfo>();
				// use provider to get all processes...
				provider = new ProcessInfoProvider(AtyTaskManager.this);
				List<ProcessInfo> mRunningProcessInfos = provider.getProcessInfos();
				//sort by type
				for (ProcessInfo info : mRunningProcessInfos) {
					if (info.isUserProcess()) {
						userProcessInfos.add(info);
					} else {
						systemProcessInfos.add(info);
					}
				}
				Message message = Message.obtain();
				message.what = LOAD_DONE;
				handler.sendMessage(message);
			};
		}.start();
		
		lv_system = (ListView) findViewById(R.id.lv_systemtask);
		lv_system.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb_taskmanager);
				ProcessInfo info = (ProcessInfo) lv_system.getItemAtPosition(position);
				if (info.isChecked()) {
					info.setChecked(false);
					cb.setChecked(false);
				} else {
					info.setChecked(true);
					cb.setChecked(true);
				}
			}
		});
		lv_user = (ListView) findViewById(R.id.lv_usertask);
		lv_user.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb_taskmanager);
				ProcessInfo info = (ProcessInfo) lv_user.getItemAtPosition(position);
				//we can't kill ourself!
				if (info.getPackName().equals(getPackageName())) {
					return; //waste this clock
				}
				if (info.isChecked()) {
					info.setChecked(false);
					cb.setChecked(false);
				} else {
					info.setChecked(true);
					cb.setChecked(true);
				}
			}
		});
		
		btn_kill = (Button) findViewById(R.id.btn_tm_kill);
		btn_selct_all = (Button) findViewById(R.id.btn_tm_select_all);
		
		
		
		btn_user = (Button) findViewById(R.id.btn_user);
//		btn_user.setBackground(getResources().getDrawable(R.drawable.user_process));
		btn_user.setOnClickListener(this);
		
		btn_system = (Button) findViewById(R.id.btn_system);
//		btn_system.setBackground(getResources().getDrawable(R.drawable.system_process));
		btn_system.setOnClickListener(this);
//		ll_loading.setVisibility(View.VISIBLE);
		
		lv_system.setVisibility(View.GONE);
		//default = show user info.
		btn_kill.setOnClickListener(this);
		btn_selct_all.setOnClickListener(this);

		
		useradapter = new UserAdapter();
		systemadapter = new SystemAdapter();
		lv_user.setAdapter(useradapter);
		lv_system.setAdapter(systemadapter);
		
		
//		new Thread(){
//			public void run() {

		
		
//				Message message = Message.obtain();
//				message.what = LOAD_DONE;
//				handler.sendMessage(message);
				
//			};
//		}.start();
		



	}
	
	//Adapter!!
	private class UserAdapter extends BaseAdapter {
		public int getCount() {
			return userProcessInfos.size();
		}
		public Object getItem(int position) {
			//return the processInfo
			return userProcessInfos.get(position);
		}
		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			//the only view ...use to make app better.
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				//inflate one new view....
				view = View.inflate(getApplicationContext(),R.layout.task_manager_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_tm_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_tm_appname);
				holder.tv_mem = (TextView) view.findViewById(R.id.tv_taskmanager_mem);
				holder.cb = (CheckBox) view.findViewById(R.id.cb_taskmanager);
				view.setTag(holder);
			} else {
				//just used the convertView!
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			// RMK : holder is always the pointer 
			// In case we kill ourself , we need to hide our own checkbox
			ProcessInfo info = userProcessInfos.get(position);
			if (info.getPackName().equals(getPackageName())) {
				holder.cb.setVisibility(View.INVISIBLE);
			} else {
				holder.cb.setVisibility(View.VISIBLE);
			}
			holder.iv_icon.setImageDrawable(info.getIcon());
			holder.tv_name.setText(info.getAppName());
			holder.tv_mem.setText(Formatter.formatFileSize(getApplicationContext(), info.getMemory()));
			holder.cb.setChecked(info.isChecked()); // default is false...
			return view;
		}
	}
	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_mem;
		CheckBox cb;
	}
	
	private class SystemAdapter extends BaseAdapter {
		public int getCount() {
			return systemProcessInfos.size();
		}
		public Object getItem(int position) {
			return systemProcessInfos.get(position);
		}
		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),R.layout.task_manager_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_tm_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_tm_appname);
				holder.tv_mem = (TextView) view.findViewById(R.id.tv_taskmanager_mem);
				holder.cb = (CheckBox) view.findViewById(R.id.cb_taskmanager);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			ProcessInfo info = systemProcessInfos.get(position);
			holder.iv_icon.setImageDrawable(info.getIcon());
			holder.tv_name.setText(info.getAppName());
			holder.tv_mem.setText(Formatter.formatFileSize(getApplicationContext(), info.getMemory()));
			holder.cb.setChecked(info.isChecked());
			return view;
		}
	}
	//change the listview between user and system
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_user:
			lvIsUser = true;
			// show one bottome is pressed , one is not
//			btn_system.setBackground(getResources().getDrawable(R.drawable.fun9));
//			btn_user.setBackground(getResources().getDrawable(R.drawable.fun1));
			lv_system.setVisibility(View.INVISIBLE);
			lv_user.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_system:
			lvIsUser = false;
//			btn_user.setBackground(getResources().getDrawable(R.drawable.fun9));
//			btn_system.setBackground(getResources().getDrawable(R.drawable.fun1));
			lv_user.setVisibility(View.INVISIBLE);
			lv_system.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_tm_kill:
			killAll();
			break;
		case R.id.btn_tm_select_all:
			selectAll();
			break;
		}
	}

	public void selectAll() {
		if (lvIsUser) {
			for (ProcessInfo info : userProcessInfos) {
				info.setChecked(true);
				useradapter.notifyDataSetChanged();
			}

		} else {
			for (ProcessInfo info : systemProcessInfos) {
				info.setChecked(true);
				systemadapter.notifyDataSetChanged();
			}
		}
	}
	public void killAll() {
		//Interact with the overall activities running in the system.
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		long memsize = 0; // total
		//use to store what processInfo We have killed ,after killing all apps checked,
		// use thise list to update List<> systemInfos or userInfos
		List<ProcessInfo> killedProcessInfo = new ArrayList<ProcessInfo>();
		if (lvIsUser) {
			for (ProcessInfo info : userProcessInfos) {
				if (info.isChecked()) {
					count++;
					memsize += info.getMemory();
					am.killBackgroundProcesses(info.getPackName());
					killedProcessInfo.add(info);
				}
			}
		} else {
			for (ProcessInfo info : systemProcessInfos) {
				if (info.isChecked()) {
					count++;
					memsize += info.getMemory();
					am.killBackgroundProcesses(info.getPackName());
					killedProcessInfo.add(info);
				}
			}
		}
		//Update both List
		for (ProcessInfo info : killedProcessInfo) {
			if (info.isUserProcess()) {
				if (userProcessInfos.contains(info)) {
					userProcessInfos.remove(info);
				}
			} else {
				if (systemProcessInfos.contains(info)) {
					systemProcessInfos.remove(info);
				}
			}
		}
		// update UI
		if (lvIsUser) {
			useradapter.notifyDataSetChanged();
		} else {
			systemadapter.notifyDataSetChanged();
		}
		 Toast.makeText( this, "Kill" + count + "process(s),release" +
		  Formatter.formatFileSize(this, memsize) + "memory", Toast.LENGTH_SHORT).show();
		}
}

