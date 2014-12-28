package com.beiluoshimen.securityguard.taskmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

//Learning "Context"
//Class Overview
//Interface to global information about an application environment. This is an abstract class 
//whose implementation is provided by the Android system. It allows access to application-specific 
//resources and classes, as well as up-calls for application-level operations such as launching activities,
//broadcasting and receiving intents, etc.

public class ProcessInfoProvider {
	private Context context;
	public ProcessInfoProvider(Context context) {
		this.context = context;
	}
	/**
	 * return all the process' information currently running
	 * @return
	 */
	public List<ProcessInfo> getProcessInfos(){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//Class for retrieving various kinds of information related to the application packages that
//		are currently installed on the device. You can find this class through getPackageManager().
		PackageManager pm = context.getPackageManager();
		//return all the process running and use List{ProcessInfo> to store values
		List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
		List<RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
		
		ProcessInfo processInfo;
		for (RunningAppProcessInfo info : runningAppProcessInfos) {
			processInfo = new ProcessInfo();
			processInfo.setPid(info.pid);
			//The name of the process that this object is associated with = packname
			processInfo.setPackName(info.processName);
			//getTotalPrivateDirty: Return total private dirty memory usage in kB.
			processInfo.setMemory(am.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty()*1024);
			//default , all process not checked...
			processInfo.setChecked(false);
			try {
				//getApplicationInfo : Retrieve all of the information we know about a particular package/application.
				ApplicationInfo applicationInfo = pm.getApplicationInfo(info.processName, 0);
				if (filter(applicationInfo)) {
					processInfo.setUserProcess(true);
				}else {
					processInfo.setUserProcess(false);
				}
				processInfo.setAppName(applicationInfo.loadLabel(pm).toString());
				processInfo.setIcon(applicationInfo.loadIcon(pm));
			} catch (NameNotFoundException e) {
				//since some app is not implemented by JAVA , (but implemented by C),
				//if the app is written in C , package name will not be found.
				e.printStackTrace();
			}
			processInfos.add(processInfo);
		}
		processInfo = null;
		return processInfos;
	}
	public boolean filter(ApplicationInfo info){
		//this is set if this application has been install as an update to a built-in system application.
		// this is usually set whose app is provided by maker (like ASUS)
		if ( (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0) {
			return true;//we deem ASUS app as user app.
			//if set, this application is installed in the device's system image.
		}else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;//not system
		}else {
			return false;
		}
	}
	
}
