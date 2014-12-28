package com.beiluoshimen.securityguard.lock;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppLockDB {
	private AppLockDBOpenHelper helper;
	public AppLockDB(Context context){
		helper = new AppLockDBOpenHelper(context);
	}
	
	/**
	 * find a locked app package name
	 * @return true if the package name is found ,else return false
	 */
	public boolean find(String packageName) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			//use sql-method to query
			Cursor cursor = db.rawQuery("select * from applock where packname =?", new String[]{packageName});
			if (cursor.moveToFirst()) {
				result = true;
			}// else means the result is empty, 
			cursor.close();
			db.close();
		}
		return result;
	}
	
	/**
	 * Add a locked app package name to db
	 */
	public boolean add(String packageName) {
		// to prevent re-add the same lock-app package name
		if (find(packageName)) {
			return false;
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("insert into applock (packname) values (?)",new Object[]{packageName});
			db.close();
		}
		return find(packageName);  // check if we add successfully or not.
	}
	/**
	 * delete package name from db
	 */
	public void delete(String packageName){
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from applock where packname=?",new Object[]{packageName});
			db.close();
		}
	}
	/**
	 * Find all locked apps' package names from db
	 */
	public List<String> findAll() {
		List<String> names = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select packname from applock", null);
			while (cursor.moveToNext()) {
				names.add(cursor.getString(0));
			}
			cursor.close();
			db.close();
			
		}
		return names;
	}
}
