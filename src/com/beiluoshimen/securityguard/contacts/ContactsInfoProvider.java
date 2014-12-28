package com.beiluoshimen.securityguard.contacts;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
/**
 * This class will fetch the contacts in this phone.
 * 
 * @author Hsieh Yu-Hua
 * @date 2014年11月20日下午6:10:45
 */
public class ContactsInfoProvider {
	private Context	 context;
	public ContactsInfoProvider(Context context) {
		this.context = context;
	}
	// return all the info of contacts
	public List<ContactInfo> getContactInfos() {
		
//		@Deprecated
//		ContentResolver contentResolver = context.getContentResolver();
//		String[] projection = new String[]{Contacts.People.NAME,Contacts.People.NUMBER};
//		Cursor cursor = contentResolver.query(Contacts.People.CONTENT_URI, projection, null, null, null);
//		List<ContactInfo> infos= new ArrayList<ContactInfo>();
//		for (int i = 0; i < cursor.getCount(); i++) {
//			cursor.moveToPosition(i);
//			ContactInfo contactInfo = new ContactInfo();
//			contactInfo.setName(cursor.getString(0));
//			contactInfo.setPhone(cursor.getString(1));
//			infos.add(contactInfo);
//		}
//		return infos;
		List<ContactInfo> contacts = new ArrayList<ContactInfo>();
		String id,name,phoneNumber;
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		while(cursor.moveToNext()) {
			id=cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
			name=cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME));
			
			//Fetch Phone Number
			Cursor phoneCursor = contentResolver.query(
					android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null, android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id, null, null);
			while(phoneCursor.moveToNext()) {
				phoneNumber = phoneCursor.getString(
						phoneCursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER));
				contacts.add(new ContactInfo(name,phoneNumber));
			}
			phoneCursor.close();
			
			
		}
		cursor.close();
		return contacts;
		
	}
}
