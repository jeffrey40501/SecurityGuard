package com.beiluoshimen.securityguard;




import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.os.Bundle;
/**
 * This is the main table activity.
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月12日下午4:36:18
 */
public class MainUI extends BaseActivity{
	
	public MainUI() {
		super(R.string.title);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set the Above View
		setContentView(R.layout.main_ui);
		getSupportFragmentManager().beginTransaction().commit();
//		getSupportFragmentManager().beginTransaction().replace(R.id.main_ui, new MyListFragment()).commit();
		
		//ActionBar will be slided when we slide...
		setSlidingActionBarEnabled(true);
		//animation
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.SCALE);

	}
}
