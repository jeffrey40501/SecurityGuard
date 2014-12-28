package com.beiluoshimen.securityguard.market;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipFile;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.lock.AppInfo;
import com.beiluoshimen.securityguard.lock.AppInfoProvider;
import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;
import com.beiluoshimen.securityguard.tools.DensityUtil;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月23日下午9:51:48
 */
public class MarketActivity extends BaseActivity implements OnClickListener{
	//Spring 
	private final String TEST_URL = "https://192.168.200.100:8443";
	
//	private final String TEST_URL = "https://127.0.0.1:8443";
//	private final String TEST_URL = "https://10.0.2.2:8443";
	
	
	private AccountSvcApi accountService = new RestAdapter.Builder()
	.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
	.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
	.create(AccountSvcApi.class);
	
	
	
	public MarketActivity(int titleRes) {
		super(R.string.title_market);
	}
	
	public MarketActivity(){
		super(R.string.title_market);
	}

	//use to debug.
	protected static final String TAG = "MarketActivity";
	
	//Handler 
	private final int LOAD_APP_DONE = 2;
	private final int LOAD_ONE_PIC = 4;
	
	private TextView tv_coin;
	private ListView lv_chars;
	private LinearLayout ll_load;//use to show progress bar
	private ArrayList<Character> chars;
	
	//Popup window
	private PopupWindow popupWindow;
	private LinearLayout ll_buy,ll_dl;
	
	//alert dialog 
	private AlertDialog dialog;
	private TextView tvBuyCoin;
	private ImageButton btnBuyConfirm;
	private ImageButton btnBuyCancel;
	
	//a mark to mark the clicked character ,this is used when we use "" buy character ""
	private int clickedNo;
	private int clickedCoin;
	private String clickedName;
	
	private boolean isLogin;
	

	private Drawable loadImageFromURL(String url){
        try{
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable draw = Drawable.createFromStream(is, "wwFDtp4.png");
            return draw;
        }catch (Exception e) {
            Log.i("loadingImg", e.toString());
            e.printStackTrace();
            return null;
        }
    }
	
	@Override
	protected void onPause() {
		dismissPopupWindow();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		we don't use sp to store session key,
//		every time the app need to use market.
//		we have to login once...
		isLogin = false; 
		
		setContentView(R.layout.aty_market);
		new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(),AnimationType.ALPHA);

//		set this property after UI update()
//		setSlidingActionBarEnabled(false);
		
		tv_coin = (TextView) findViewById(R.id.tv_market_coin);
		tv_coin.setOnClickListener(this);//user can press this to login
		lv_chars = (ListView) findViewById(R.id.lv_market);
		ll_load = (LinearLayout) findViewById(R.id.ll_market_load);
		
		//load app
		ll_load.setVisibility(View.VISIBLE);
		uiUpdate();
		
		//set Action bar to be not poped up
		setSlidingActionBarEnabled(false);
		lv_chars.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//have to dismiss the popup window already exist when we click another item.
				dismissPopupWindow();
				View contentView = View.inflate(getApplicationContext(), R.layout.popup_market_item, null);
				ll_buy = (LinearLayout) contentView.findViewById(R.id.ll_popup_buy);
				ll_dl = (LinearLayout) contentView.findViewById(R.id.ll_popup_dl);
				ll_buy.setOnClickListener(MarketActivity.this);
				ll_dl.setOnClickListener(MarketActivity.this);
				
				LinearLayout ll_popup_cotainer = (LinearLayout) contentView.findViewById(R.id.ll_popup_market_container);
				int top = view.getTop();
				int bottom = view.getBottom();
				
				popupWindow = new PopupWindow(contentView, DensityUtil.dip2px(getApplicationContext(), 120),bottom-top+DensityUtil.dip2px(getApplicationContext(), 30));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				popupWindow.showAtLocation(view, Gravity.TOP|Gravity.RIGHT, location[0], location[1]);
				new SwitchAnimationUtil().startAnimation(ll_popup_cotainer, AnimationType.SCALE);
				Character character = (Character) lv_chars.getItemAtPosition(position);
				clickedNo = character.getNo();
				clickedName = character.getName();
				clickedCoin = character.getPrice();
			}
		});

	}
	
	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_popup_buy:
			Log.i(TAG, "buy");
			buyCharacter();
			break;
		case R.id.ll_popup_dl:
			Log.i(TAG, "dl");
			dlCharacter();
			break;
		case R.id.tv_market_coin:
			//TODO login here 
			//remeber to update the coin tv to show.
			break;
		case R.id.btn_market_buydialog_confirm:
			//TODO
			if(accountService.buyCharacter("", "", clickedNo)){
				Toast.makeText(MarketActivity.this, "Buy "+clickedName+" Succeed!", Toast.LENGTH_LONG).show();
				//TODO nned to update our choice to character...
			}else {
				//buy failure i.e. user already has this character
				
			}
			
			break;
		case R.id.btn_market_buydialog_cancel:
			dialog.cancel();
			break;
		}
	}
	

	private void buyCharacter() {
		dismissPopupWindow();
		
		
		//TODO checked if ther user has already has this character
		if (isLogin) {
			//buy character 
			showBuyCharacterDialog();
			
		}else {
			//ASK the user to login first....
			////TODO show the list of character and coin user has...
			
		}
	}
	
	private void dlCharacter() {
		dismissPopupWindow();
		
		//TODO
		
	}
	
	void showBuyCharacterDialog(){
		AlertDialog.Builder builder = new Builder(this);
//		use inflator to inflate an alert dialog
		View view = View.inflate(this, R.layout.market_buy_dialog, null);
		tvBuyCoin = (TextView) view.findViewById(R.id.tv_market_buydialog_coin);
		btnBuyConfirm = (ImageButton) view.findViewById(R.id.btn_market_buydialog_confirm);
		btnBuyCancel = (ImageButton) view.findViewById(R.id.btn_market_buydialog_cancel);
		tvBuyCoin.setText("Buy Character:"+clickedName+"\nCoin:"+clickedCoin+"\nAre you sure you want to buy this item?");
		btnBuyCancel.setOnClickListener(this);
		btnBuyConfirm.setOnClickListener(this);
		
		// add the view above to the builder
		builder.setView(view);
		//create the dialog by builder
		dialog = builder.create();
		//if we don't set this listener ,we will....
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
			//do nothing ,	
			}
		});
		dialog.show();
	}
	

	private void uiUpdate(){
		new Thread(){
			public void run() {
				//" NO NEED TO LOGIN "
				chars = (ArrayList<Character>) accountService.getCharacters();
				
//				DEBUG ONLY
//				accountService.addAccount(v)" NO NEED TO LOGIN "
//				accountService.findByUsernameAndPassword(username, password);" NO NEED TO LOGIN "
				
				// the following services do not "NEED LOGIN "
//				accountService.login("coursera", "changeit");ok
//				accountService.addCoin("test2", "123", 21133);ok
//				accountService.buyCharacter("test2", "123", 103);
				
				// told main thread loading is done
				Message msg = Message.obtain();
				msg.what = LOAD_APP_DONE;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	/**
	 * the method handleMessage is called, once the loading of ArrayList is done
	 * the handelr will set the listView based on Adapter (which use Arraylist to sey listview)
	 */
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOAD_APP_DONE:
				ll_load.setVisibility(View.INVISIBLE);
				lv_chars.setAdapter(new MarketAdapter());
				break;
			case LOAD_ONE_PIC:
				
				
				
			}
		};
	};
	
	private class MarketAdapter extends BaseAdapter{

		
		@Override
		public int getCount() {
			return chars.size();
		}

		@Override
		public Object getItem(int position) {
			return chars.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
				View view;
//				ViewHolder holder;
				if ( convertView == null) {
					view = View.inflate(getApplicationContext(), R.layout.market__item, null);
					
//					holder = new ViewHolder();
//					holder.iv_icon = (ImageView) view.findViewById(R.id.iv_market_icon);
//					holder.tv_name = (TextView) view.findViewById(R.id.tv_market_name);
//					holder.tv_price = (TextView) view.findViewById(R.id.tv_market_price);
//					view.setTag(holder);
				}else {
					view = convertView;
					//Returns this view's tag.(the Object stored in this view as a tag, or null if not set)
//					holder = (ViewHolder) view.getTag();
				}
				final Character character = chars.get(position);
//				holder.iv_icon.setImageDrawable(loadImageFromURL(character.getPic()));
				TextView tv_name = (TextView) view.findViewById(R.id.tv_market_name);
				TextView tv_price = (TextView) view.findViewById(R.id.tv_market_price);
				final ImageView iv_icon =  (ImageView) view.findViewById(R.id.iv_market_icon);
				new Timer().schedule(new TimerTask() {
					private Drawable drawable;
					@Override
					public void run() {
						drawable = loadImageFromURL(character.getPic());
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								iv_icon.setImageDrawable(drawable);
							}
						});
					}
				}, 0);
				
				tv_name.setText("Theme:"+character.getName());
				tv_price.setText("Price:"+character.getPrice()+" coins");
//				holder.tv_name.setText(character.getName());
//				holder.tv_price.setText(character.getPrice()+"");
				
				return view;
			
			}
		}
			
	// we can't use viewholder to improve load speed here (for example, see appmanagerActivity)
	//why?
	//because we have to load image from uri (net connection),
	//that is to say, we have to keep every pointer to ImageView iv_icon,
	//since we can't load it immediately, we have to use a thread as net connection .
	//and use iv_icon (pointer) to set the Image we get from ney later.
	//so we have to keep every pointer...
	
//	private static class ViewHolder{
//		ImageView iv_icon;
//		TextView tv_name;
//		TextView tv_price;
//	}
	
}





