package com.beiluoshimen.securityguard.antitheft;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;
import com.beiluoshimen.securityguard.tools.MD5Tools;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
/**
 *  
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月12日下午10:02:42
 */
public class AtyAntitheft extends BaseActivity implements OnClickListener{
	
	public AtyAntitheft(){
		super(R.string.titile_anti_theft);
	}
	public AtyAntitheft(int titleRes) {
		super(R.string.titile_anti_theft);
	}

	private SharedPreferences sp;
	
	//first time the antitheft module is entered
	private EditText et_dialog_first_pass;
	private EditText et_dialog_first_padd_confirm;
	private ImageButton btn_dialog_first_confirm;
	
	private ImageButton btn_dialog_first_cancel;
	//second time the antitheft module is entered
	private EditText et_dialog_pass;
	private ImageButton btn_dialog_confirm;
	private ImageButton btn_dialog_cancel;
	//alertdialog
	private AlertDialog dialog;
	
	//after dialog , the main antitheft window.
	private Button btnResetup;
	private TextView tv_safe_num;
	private TextView tv_remote_wipe_out;
	private TextView tv_sim_card;
	
	@Override
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.anti_theft);
		setSlidingActionBarEnabled(false);
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.ALPHA);
		
		//initialize UI and tools
		sp = getSharedPreferences(getString(R.string.sp_config),MODE_PRIVATE);
		btnResetup = (Button) findViewById(R.id.btn_resetup);
		tv_remote_wipe_out = (TextView) findViewById(R.id.tv_remote_wipe_out);
		tv_safe_num = (TextView) findViewById(R.id.tv_safe_num);
		tv_sim_card = (TextView) findViewById(R.id.tv_sim_card);
		
		//show if the remote control and safe number are set or not. 
		boolean remote = sp.getBoolean(getString(R.string.sp_remote_wipe_out), false);
		if (remote == true) {
			tv_remote_wipe_out.setText(getString(R.string.at_on));
		}else {
			tv_remote_wipe_out.setText(getString(R.string.at_off));
		}
		
		String safeNum = sp.getString(getString(R.string.sp_safenumber), "");
		if (safeNum.equals("")) {
			tv_safe_num.setText(R.string.at_not_yet_set);
		}else {
			tv_safe_num.setText(safeNum);
		}
		
		Boolean sim = sp.getBoolean(getString(R.string.sp_sim_protect_on), false);
		if (sim) {
			tv_sim_card.setText(getString(R.string.at_on));
		}else {
			tv_sim_card.setText(getString(R.string.at_off));
		}
		
		btnResetup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//check the user set up.
				if (passwordIsSet()) {
					showNormalDialog();//password is set
				}else { //password not yet set
					showFirstDialog();
				}
			}
		});	
	}
	
	
	private void showFirstDialog() {
		AlertDialog.Builder builder = new Builder(this);
//		use inflator to inflate an alert dialog
		View view = View.inflate(this, R.layout.at_first_entry_dialog, null);
		et_dialog_first_padd_confirm = (EditText) view.findViewById(R.id.et_dialog_first_pwd_confirm);
		et_dialog_first_pass = (EditText) view.findViewById(R.id.et_dialog_first_pwd);
		btn_dialog_first_cancel = (ImageButton) view.findViewById(R.id.btn_dialog_first_cancel);
		btn_dialog_first_confirm = (ImageButton) view.findViewById(R.id.btn_dialog_first_confirm);
		btn_dialog_first_cancel.setOnClickListener(this);
		btn_dialog_first_confirm.setOnClickListener(this);
		// add the view above to the builder
		builder.setView(view);
		//create the dialog by builder
		dialog = builder.create();
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
//				AtyAntitheft.this.finish();
			}
		});
		dialog.show();
		
		
	}

	private void showNormalDialog() {
		AlertDialog.Builder builder = new Builder(this);
//		use inflator to inflate an alert dialog
		View view = View.inflate(this, R.layout.at_normal_dialog, null);
		et_dialog_pass = (EditText) view.findViewById(R.id.et_dialog_normal_pwd);
		btn_dialog_cancel = (ImageButton) view.findViewById(R.id.btn_dialog_normal_cancel);
		btn_dialog_confirm = (ImageButton) view.findViewById(R.id.btn_dialog_normal_confirm);
		btn_dialog_cancel.setOnClickListener(this);
		btn_dialog_confirm.setOnClickListener(this);
		// add the view above to the builder
		builder.setView(view);
		//create the dialog by builder
		dialog = builder.create();
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
//				AtyAntitheft.this.finish();
			}
		});
		
		dialog.show();
		
	}
	
	/*
	 * This function can check weather the password is set or not
	 */
	private boolean passwordIsSet() {
		String pass = sp.getString(getString(R.string.sp_password), "");
		if (TextUtils.isEmpty(pass)) {
			return false;
		}else {
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//first time the anti-theft is entered.
		case R.id.btn_dialog_first_confirm:
			
			String pwd = et_dialog_first_padd_confirm.getText().toString();
			String pwd_confirm = et_dialog_first_pass.getText().toString();
			System.out.println(pwd);
			if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
				Toast.makeText(this, R.string.at_the_password_can_t_be_empty, Toast.LENGTH_SHORT).show();
				return;
			}else if (pwd.equals(pwd_confirm)) {
				System.out.println( MD5Tools.MD5(pwd));
				//save the password into sharedpreference in the form of MD5
				// this is the user "first time" use the function
				Editor editor = sp.edit();
				editor.putString(getString(R.string.sp_password), MD5Tools.MD5(pwd));
				editor.commit();
				System.out.println(MD5Tools.MD5(pwd));
				dialog.dismiss();
				Toast.makeText(this, R.string.at_password_is_set_successfully, Toast.LENGTH_SHORT).show();
				
				
				
				//start the setup1
				Intent i = new Intent(this,AtySetup1.class);
				startActivity(i);
				finish();
				overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				
				
				finish();
			}else {
				Toast.makeText(this, R.string.at_both_password_aren_t_the_same_, Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.btn_dialog_first_cancel:
			dialog.cancel();
//			AtyAntitheft.this.finish();//go back to mainUI
			break;
			
			// >= 2 time the anti-theft is entered.
			
		case R.id.btn_dialog_normal_confirm:
			String userinputpwd = et_dialog_pass.getText().toString();
//			System.out.println(userinputpwd);
//			System.out.println(MD5Tools.MD5(userinputpwd));
//			System.out.println(MD5Tools.MD5(sp.getString(getString(R.string.sp_password),"")));
			if (TextUtils.isEmpty(userinputpwd)) {
				Toast.makeText(this, R.string.at_the_password_can_t_be_empty, Toast.LENGTH_SHORT).show();
				return;
			}else if (MD5Tools.MD5(userinputpwd).equals(sp.getString(getString(R.string.sp_password),""))) {
				Toast.makeText(this, R.string.at_correct_password_, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				
				//start the activity of setup1
				Intent resetup = new Intent(AtyAntitheft.this,AtySetup1.class);
				startActivity(resetup);
				finish();
				overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);		
				
				return;
			}else {
				Toast.makeText(this, R.string.at_the_password_is_wrong_, Toast.LENGTH_SHORT).show();
				return;
			}
			
		case R.id.btn_dialog_normal_cancel:
			dialog.cancel();
//			AtyAntitheft.this.finish();//go back to mainUI
//			finish();//go back to mainUI
			break;
			

		default:
			break;
		}
	}
	

}
