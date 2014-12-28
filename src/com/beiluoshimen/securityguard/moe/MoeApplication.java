package com.beiluoshimen.securityguard.moe;


import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;

import jp.live2d.utils.android.FileManager;
import jp.live2d.utils.android.SoundManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MoeApplication extends BaseActivity
{
	private LAppLive2DManager live2DMgr ;
	static private Activity instance;
	
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
				
//			Called when a connection to the Service has been lost. 
//			This typically happens when the process hosting the service has crashed or been killed. 
				@Override
				public void onServiceDisconnected(ComponentName name) {
					System.out.println("Connected fail!!!!!!");
					finish();
				}
				
			}	
	
	
	public MoeApplication(){
		super(R.string.title_secure_niang);
		instance=this;
		if(LAppDefine.DEBUG_LOG)
		{
			Log.d( "", "==============================================\n" ) ;
			Log.d( "", "   Live2D Debug beiluoshimen \n" ) ;
			Log.d( "", "==============================================\n" ) ;
		}

		SoundManager.init(this);
		live2DMgr = new LAppLive2DManager(this);
	}
	
	public MoeApplication(String title)
	{
		super(R.string.title_secure_niang);
		instance=this;
		if(LAppDefine.DEBUG_LOG)
		{
			Log.d( "", "==============================================\n" ) ;
			Log.d( "", "   Live2D Debug beiluoshimen \n" ) ;
			Log.d( "", "==============================================\n" ) ;
		}

		SoundManager.init(this);
		live2DMgr = new LAppLive2DManager(this);
	}


	 static public void exit()
    {
		SoundManager.release();
    	instance.finish();
    }


	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        
      	setupGUI();
      	FileManager.init(this.getApplicationContext());
      	
      	serviceIntent = new Intent(this,CleanService.class);
		connection = new MyConnection();
		bindService(serviceIntent, connection, BIND_AUTO_CREATE);
		//Prove that it take time to bind the service!!!!
//      	new Thread(){
//      		public void run() {
//      			boolean tt= true;
//      			while (tt) {
//					if (iService!=null) {
//						memInfo = iService.onCallClean();
//						System.out.println("廉潔成功 成功清理");
//						tt = false;
//					}else {
//						System.out.println("廉潔中");
//					}
//				}
//      		};
//      	}.start();
		
		
    }
	
	public void showCleanText(int count,long memsize){
		Toast.makeText(MoeApplication.this,"Kill" + count + "process(s),release" +
				  Formatter.formatFileSize(this, memsize) + "memory", Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	protected void onDestroy() {
		unbindService(connection);
		super.onDestroy();
	}
	
	void setupGUI()
	{
    	setContentView(R.layout.activity_main);

        
        LAppView view = live2DMgr.createView(this) ;
        FrameLayout layout=(FrameLayout) findViewById(R.id.live2DLayout);
		layout.addView(view, 0, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

	}





	
	@Override
	protected void onResume()
	{
		//live2DMgr.onResume() ;
		super.onResume();
	}


	
	@Override
	protected void onPause()
	{
		live2DMgr.onPause() ;
    	super.onPause();
	}
}
