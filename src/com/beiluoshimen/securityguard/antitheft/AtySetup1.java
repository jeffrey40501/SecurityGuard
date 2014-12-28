package com.beiluoshimen.securityguard.antitheft;

import com.beiluoshimen.securityguard.R;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AtySetup1 extends Activity implements OnClickListener{
	Button btnNext1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_at_setup1);
		//animation!!
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.HORIZION_RIGHT);
		btnNext1 = (Button) findViewById(R.id.btnNext1);
		btnNext1.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this,AtySetup3.class);
		startActivity(intent);
		finish();
		// set the transition animation !!
		overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
		
	}
}
