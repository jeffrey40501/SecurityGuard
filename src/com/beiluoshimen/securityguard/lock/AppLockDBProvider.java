package com.beiluoshimen.securityguard.lock;

import android.R.integer;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
/**
 * 
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月23日下午2:01:10
 */

//	we only care about add and delete.
public class AppLockDBProvider extends ContentProvider{

	private AppLockDB db;
	//To use this class, build up a tree of UriMatcher objects. For example:
	private static final int ADD = 1;
	private static final int DELETE = 2;
	//content://com.beiluoshimen.securityguard/ADD
	//content://com.beiluoshimen.securityguard/DELETE
	public static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	static{
		matcher.addURI("com.beiluoshimen.securityguard", "ADD", ADD);
		matcher.addURI("com.beiluoshimen.securityguard", "DELETE", DELETE);
	}
	
	@Override
	public boolean onCreate() {
		db = new AppLockDB(getContext());
		return false;
	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int result = matcher.match(uri);
		if (result == DELETE) {
			//db operation
			db.delete(selectionArgs[0]);
			//publish that contents have been changed!!!
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return 0;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int result = matcher.match(uri);
		if (result == ADD) {
			//db operation
			db.add(values.getAsString("packname"));
			//publish that contents have been changed!!!
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return null;
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
}
