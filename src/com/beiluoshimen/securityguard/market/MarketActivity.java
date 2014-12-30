package com.beiluoshimen.securityguard.market;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.slideingmenu.BaseActivity;
import com.beiluoshimen.securityguard.tools.DensityUtil;
import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
	private final static String TEST_URL = "https://192.168.200.100:8443";
	
//	private final String TEST_URL = "https://127.0.0.1:8443";
//	private final String TEST_URL = "https://10.0.2.2:8443";
	

	
    protected static  AccountSvcApi accountService = new RestAdapter.Builder()
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
	private static final int LOAD_CHARACTERS_SUCCESS = 1;
	private static final int LOAD_CHARACTERS_FAILURE = 2;
	private static final int LOAD_USER_DATA_DONE = 3;
	private static final int LOAD_ONE_PIC = 4;
	private static final int BUY_CHARACTER_SUCCESS = 5;
	private static final int BUY_CHARACTER_FAILURE = 6;
	private static final int BUY_CHARACTER_FAILURE_ALREADY_HAVE = 7;
	
	
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
	//result code for login activity.
	public final int LOGIN_SUCCEED_CODE = 1;
	public final int LOGIN_FAIL_CODE = 2;
	
	//user info
	private String username,password;
	private Account account;
	
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
		loadCharacters();
		
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
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_popup_buy:
			Log.i(TAG, "buy");
			if(!isLogin){
				Intent login = new Intent(MarketActivity.this,AtyLogin.class);
				startActivityForResult(login,0);
			}else {
				dismissPopupWindow();
				showBuyCharacterDialog();	
			}
			break;
		case R.id.ll_popup_dl:
			Log.i(TAG, "dl");
			if(!isLogin){
				Intent login = new Intent(MarketActivity.this,AtyLogin.class);
				startActivityForResult(login,0);
			}else {
				dismissPopupWindow();
				//TODO show downloading...
				dlCharacter();
			}
			break;
		case R.id.tv_market_coin:
			if(!isLogin){
			Intent login = new Intent(MarketActivity.this,AtyLogin.class);
			startActivityForResult(login,0);
			}
			break;
		case R.id.btn_market_buydialog_confirm:
			buyCharacter();
			dialog.dismiss();
			break;
		case R.id.btn_market_buydialog_cancel:
			dialog.cancel();
			break;
		}
	}
	
	//network new thread.
	private void buyCharacter() {
		new Thread(){
			@Override
			public void run() {
				super.run();
				Message msg = Message.obtain();
				try {
					//checked if we already have this char?
					if(account.getCharacters().contains(clickedNo)){
						msg.what = BUY_CHARACTER_FAILURE_ALREADY_HAVE;						
					}
					//network buy
					else if(accountService.buyCharacter(username,password, clickedNo)){
						msg.what = BUY_CHARACTER_SUCCESS;
						//update local account info. if succeed
						account.setCoin(account.getCoin()-clickedCoin);
						account.getCharacters().add(clickedNo);
					}else {
						msg.what = BUY_CHARACTER_FAILURE;
					}
				} catch (Exception e) {
					msg.what = BUY_CHARACTER_FAILURE;
				}
				handler.sendMessage(msg);
			}
		}.start();		
	}
	
	private void dlCharacter() {
		
		//TODO
		
	}
	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
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
	

	private void loadCharacters(){
		new Thread(){
			public void run() {
				Message msg = Message.obtain();
				try {
					chars = (ArrayList<Character>) accountService.getCharacters();
					msg.what = LOAD_CHARACTERS_SUCCESS;
				} catch (Exception e) {
					msg.what = LOAD_CHARACTERS_FAILURE;
				}
				handler.sendMessage(msg);
				
				
//				DEBUG ONLY
//				" NO NEED TO LOGIN "
//				accountService.addAccount(new Account("dindin", "123", 123, new ArrayList<Integer>()));
//				accountService.findByUsernameAndPassword("Andy1", "123");
				
				// the following services do not "NEED LOGIN "
//				accountService.login("coursera", "changeit");
//				accountService.addCoin("test2", "123", 21133);
//				accountService.buyCharacter("test2", "123", 103);
				

			};
		}.start();
	}
	
	
	private void loadUserData(){
		new Thread(){
			public void run() {
				account = accountService.findByUsernameAndPassword(username, password).iterator().next();
				Log.i(TAG, "load account info succedd");
				Message msg = Message.obtain();
				msg.what = LOAD_USER_DATA_DONE;
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
			case LOAD_CHARACTERS_SUCCESS:
				ll_load.setVisibility(View.INVISIBLE);
				lv_chars.setAdapter(new MarketAdapter());
				break;
			case LOAD_CHARACTERS_FAILURE:
				Toast.makeText(MarketActivity.this, "Fail to connect to server!", Toast.LENGTH_SHORT).show();
				break;					
			case LOAD_ONE_PIC:
				
				break;
			case LOAD_USER_DATA_DONE:
				tv_coin.setText("You have "+account.getCoin()+" coins.\n");
				break;
			case BUY_CHARACTER_SUCCESS:
				tv_coin.setText("You have "+account.getCoin()+" coins.\n");
				Toast.makeText(MarketActivity.this, "Buy "+clickedName+" Succeed!", Toast.LENGTH_SHORT).show();
				break;
			case BUY_CHARACTER_FAILURE:	
				Toast.makeText(MarketActivity.this, "Fail to buy new character!", Toast.LENGTH_SHORT).show();
				break;
			case BUY_CHARACTER_FAILURE_ALREADY_HAVE:
				Toast.makeText(MarketActivity.this, "You already have this character!", Toast.LENGTH_SHORT).show();
				break;
				
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
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == AtyLogin.LOGIN_SUCCEED) {
			isLogin = true;
			username = data.getStringExtra("username");
			password = data.getStringExtra("password");
			loadUserData();
			Log.d(TAG, "market onactivity result login succeed.");
			
		}else if (resultCode == AtyLogin.LOGIN_FAIL) {
			isLogin = false;
			
		}
		
	}
	
}





