package com.beiluoshimen.securityguard.lock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

public class AppInfoProvider {
	//use packagemanager to fetch all the infos regarding to apps installed on device
	private PackageManager pm;
	private AppInfo appInfo;
	private Context context;
	public AppInfoProvider(Context context){
		this.context =  context;
		pm = context.getPackageManager();
	}
	
	/**
	 * return all the infos of apps installed on the device
	 * @return
	 */
	public List<AppInfo> getInfos(){
		File path;
		StatFs stat;
		long availBlocks;
		long blockSize;
		List<PackageInfo> infos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for(PackageInfo info:infos){
			appInfo = new AppInfo();
//			appInfo.setSize(Formatter.formatFileSize(context, ps.cacheSize+ps.codeSize+ps.dataSize));
			appInfo.setPackageName(info.packageName);
			appInfo.setVersion(info.versionName);
			appInfo.setAppIcon(info.applicationInfo.loadIcon(pm));
			appInfo.setName(info.applicationInfo.loadLabel(pm).toString());
			appInfo.setUserApp(filter(info.applicationInfo));
			appInfos.add(appInfo);
		}
		appInfo = null;
		return appInfos;
		
	}
	
	/**
	 * this is a filter function, which can check if this app is belonging to 3rd party or not
	 * @return true if it is 3rd party app
	 */
	
	public boolean filter(ApplicationInfo info){
//		 is set if this application has been install as an update to a built-in system application.
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
			//if set, this application is installed in the device's system image.
		}else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
}
