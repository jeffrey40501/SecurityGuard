package com.beiluoshimen.securityguard.slideingmenu;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.settings.AtySettings;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

	//this title of slide
	private int mTitleRes;
	
	//put this ListFragment in the SlideFragment
	protected ListFragment listFragment;

	public BaseActivity(int titleRes) {
		//set title
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(mTitleRes);

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
			//set our self-implementation listfragment
			listFragment = new MyListFragment();
			transaction.replace(R.id.menu_frame, listFragment,"MY_FRAGMENT");
			transaction.commit();
		} else {
			listFragment = (ListFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}
		
		
		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case R.id.menu_main_settings:
			System.out.println("@@@@@@@@@@@@");
//			toggle();
			startActivity(new Intent(this,AtySettings.class));
			break;
		case R.id.menu_main_author:
			
			
			//TODO show author.
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.main_menu, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.menu_main_settings:
//			startActivity(new Intent(MainUI.this,AtySettings.class));
//			break;
////			more functions can be added below
////			---------------------------------
//		default:
//			break;
//		}
//		
//		return super.onOptionsItemSelected(item);
//	}
	
}
