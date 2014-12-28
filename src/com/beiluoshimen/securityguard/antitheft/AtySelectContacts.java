package com.beiluoshimen.securityguard.antitheft;

import java.util.List;

import com.beiluoshimen.securityguard.R;
import com.beiluoshimen.securityguard.contacts.ContactInfo;
import com.beiluoshimen.securityguard.contacts.ContactsInfoProvider;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AtySelectContacts extends Activity {
	private ListView lv_contacts;
	private ContactsInfoProvider provider;
	private List<ContactInfo> infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_select_contact);
		lv_contacts = (ListView) findViewById(R.id.lv_select_contact);

		// get the contacts.
		provider = new ContactsInfoProvider(this);
		infos = provider.getContactInfos();
		lv_contacts.setAdapter(new ContactAdapter());
		lv_contacts.setOnItemClickListener(new OnItemClickListener() {	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				ContactInfo info = (ContactInfo) lv_contacts.getItemAtPosition(position);
				String number = info.getPhone();
				//set the result of this activity ,return phone number.
				setResult(0, new Intent().putExtra("phone", number));
				finish();
				
				
				overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				
				
				
			}
		});

	}
	//for listview adapter
	private class ContactAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public ContactInfo getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContactInfo info = infos.get(position);
			TextView tv = new TextView(getApplicationContext());
			tv.setTextSize(23);
			tv.setTextColor(Color.parseColor("#AAAAAA"));
			tv.setText(info.getName() + "\n" + info.getPhone());
			return tv;
		}

	}
}
