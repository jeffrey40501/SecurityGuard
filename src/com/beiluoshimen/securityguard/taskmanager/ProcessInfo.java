package com.beiluoshimen.securityguard.taskmanager;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	private String packName;
	private boolean isUserProcess;
	private Drawable icon;
	private long memory;
	private boolean checked;
	private int pid;
	private String appName;
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getMemory() {
		return memory;
	}
	public void setMemory(long memory) {
		this.memory = memory;
	}
	public boolean isUserProcess() {
		return isUserProcess;
	}
	public void setUserProcess(boolean isUserProcess) {
		this.isUserProcess = isUserProcess;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}
