package com.beiluoshimen.securityguard.tools;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * This class is used by MainUI - which is used to construct sexangleview.
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月21日下午2:23:32
 */
public class UtilTools {
	
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	public static float getScreenDensity(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}
	
	/**
	 * pxתdp
	 * @param id
	 * @param context
	 * @return
	 */
	public static int getDimenT(int id,Context context) {
		try {
			return (int) (id * getScreenDensity(context));
		} catch (Exception e) {
			return 0;
		}
	}

}
