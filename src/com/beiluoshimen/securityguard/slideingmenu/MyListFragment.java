package com.beiluoshimen.securityguard.slideingmenu;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.antitheft.AtyAntitheft;
import com.beiluoshimen.securityguard.appmanager.AtyAppManager;
import com.beiluoshimen.securityguard.lock.AtyLock;
import com.beiluoshimen.securityguard.moe.MoeApplication;
import com.beiluoshimen.securityguard.taskmanager.AtyTaskManager;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月27日下午3:50:27
 */
public class MyListFragment extends ListFragment {
	private Context context;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// need to return a View (which is list)
		return inflater.inflate(R.layout.list, null);
	}

	//the listener method 
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent;
		switch (position) {
		
		case 0:
			intent = new Intent(getActivity().getApplicationContext(),MoeApplication.class);
			startActivity(intent);
			break;
		case 1:
			intent = new Intent(getActivity().getApplicationContext(),AtyAntitheft.class);
			startActivity(intent);

			break;
		case 2:
			
			intent = new Intent(getActivity().getApplicationContext(),AtyLock.class);
			startActivity(intent);
			break;
		case 3:
			intent = new Intent(getActivity().getApplicationContext(),AtyAppManager.class);
			startActivity(intent);
			break;
		case 4:
			
			intent = new Intent(getActivity().getApplicationContext(),AtyTaskManager.class);
			startActivity(intent);
			
			break;
			
			
		case 5:
			//author
			Toast.makeText(context, "BeiluoShimen\n beiluo.shimen@icloud.com", Toast.LENGTH_LONG).show();
			
			break;

		}
		
			//TODO
//			MyListFragment myFragment = (MyListFragment)getFragmentManager().findFragmentByTag("MY_FRAGMENT");
//			if (myFragment.isVisible()) {
//				
//				FragmentTransaction manager = getFragmentManager().beginTransaction();
//				manager.show(arg0)
//				manager.replace(myFragment, R.id.menu_frame);
//				manager.commit();
//				
//			}
			


	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        context = getActivity();           
		
		//Our list adapter
		MyAdapter adapter = new MyAdapter(getActivity());
		adapter.add(new MyItem("アクセル娘", R.drawable.user_process));
		adapter.add(new MyItem("Anti-theft", R.drawable.antitheft));
		adapter.add(new MyItem("App Lock", R.drawable.unlock));
		adapter.add(new MyItem("App Manager", R.drawable.system_process));
		adapter.add(new MyItem("Memory Clean",R.drawable.boost));
		adapter.add(new MyItem("Author", R.drawable.open));
		
		//!!!In ListFragment, we have to use setListAdapter to set the list adapter
		setListAdapter(adapter);
		
	}

	private class MyItem {
		public String tag;
		public int iconRes;
		public MyItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	private class MyAdapter extends ArrayAdapter<MyItem> {

		public MyAdapter(Context context) {
			super(context, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				//inflate the list item (row)
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}//or we can reuse convertView
			
			//set the resources
			//TODO can be improved.... only single instance of ImageView TextView 
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			
			icon.setImageResource(getItem(position).iconRes);
			title.setText(getItem(position).tag);
			

			return convertView;
		}
		
		@Override
		public int getPosition(MyItem item) {
			// TODO Auto-generated method stub
			return super.getPosition(item);
		}
		
		@Override
		public MyItem getItem(int position) {
			// TODO Auto-generated method stub
			return super.getItem(position);
		}

	}
}
