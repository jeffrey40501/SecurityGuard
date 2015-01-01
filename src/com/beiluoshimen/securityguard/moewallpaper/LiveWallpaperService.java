/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package com.beiluoshimen.securityguard.moewallpaper;


import com.beiluoshimen.securityguard.moe.CleanInterface;
import com.beiluoshimen.securityguard.moe.CleanService;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.MotionEvent;
import android.widget.Toast;
import net.rbgrn.android.glwallpaperservice.*;

public class LiveWallpaperService extends GLWallpaperService {
	
		//service
		private static Intent serviceIntent;
		public CleanInterface iService;
		private static MyConnection connection;
		private class MyConnection implements ServiceConnection{
			// called when we successfully connect to service
					@Override
					public void onServiceConnected(ComponentName name, IBinder service) {
						// upward type transfer
						iService = (CleanInterface) service;

					}
					
//				Called when a connection to the Service has been lost. 
//				This typically happens when the process hosting the service has crashed or been killed. 
					@Override
					public void onServiceDisconnected(ComponentName name) {
						System.out.println("Connected fail!!!!!!");
					}
					
				}	
	
	public LiveWallpaperService() {
		super();
	}

	public Engine onCreateEngine() {
		
		serviceIntent = new Intent(this,CleanService.class);
		connection = new MyConnection();
		bindService(serviceIntent, connection, BIND_AUTO_CREATE);
		
		MyEngine engine = new MyEngine();
		return engine;
	}

	class MyEngine extends GLEngine {
		Live2DRenderer renderer;

		public MyEngine() {
			super();
			// handle prefs, other initialization
			renderer = new Live2DRenderer(getApplicationContext());
			setRenderer(renderer);
			setRenderMode(RENDERMODE_CONTINUOUSLY);
		}

		@Override
		public void onTouchEvent (MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
//				iService.onCallClean();
				//TODO  BUG??????
				System.out.println("call clean");
				break;
			case MotionEvent.ACTION_UP:
				renderer.resetDrag();
				break;
			case MotionEvent.ACTION_MOVE:
				renderer.drag(event.getX(), event.getY());
				break;
			case MotionEvent.ACTION_CANCEL:
				break;
			}
		}

		public void onDestroy() {
			super.onDestroy();
			if (renderer != null) {
				renderer.release();
			}
			renderer = null;
		}
	}
}
