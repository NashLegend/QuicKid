package com.example.quickid.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.example.quickid.AppApplication;
import com.example.quickid.adapter.ContactAdapter.ContactComparator;
import com.example.quickid.model.Contact;
import com.example.quickid.model.Contact.phoneStruct;
import com.example.quickid.model.RecentContact;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;

public class ContactHelper {

	/**
	 * 加载所有联系人
	 */
	synchronized public static void loadContacts() {
		ArrayList<Contact> AllContacts = new ArrayList<Contact>();
		ContentResolver resolver = AppApplication.globalApplication
				.getContentResolver();
		// 要使用RawContacts.CONTACT_ID而不是Contacts.CONTACT_ID
		String[] PROJECTION = { RawContacts.CONTACT_ID, Contacts.DISPLAY_NAME,
				Contacts.LOOKUP_KEY, Contacts.PHOTO_THUMBNAIL_URI,
				Phone.NUMBER, Phone.TYPE, Contacts.STARRED };
		Cursor cursor = resolver.query(Phone.CONTENT_URI, PROJECTION, null,
				null, Contacts.SORT_KEY_PRIMARY);
		String preLookupKey = "";
		Contact preContact = null;
		if (cursor.moveToFirst()) {
			do {
				long contractID = cursor.getInt(0);
				String displayName = cursor.getString(1);
				String lookupKey = cursor.getString(2);
				String photoUri = cursor.getString(3);
				boolean starred = cursor.getInt(6) == 1;
				if (lookupKey.equals(preLookupKey) && preContact != null) {
					preContact.addPhone(cursor.getString(4), cursor.getInt(5));
				} else {
					Contact contact = new Contact();
					contact.setContactId(contractID);
					contact.setName(displayName);
					contact.setLookupKey(lookupKey);
					contact.setPhotoUri(photoUri);
					contact.addPhone(cursor.getString(4), cursor.getInt(5));
					contact.setStarred(starred);
					AllContacts.add(contact);
					preLookupKey = lookupKey;
					preContact = contact;
				}
			} while (cursor.moveToNext());
		} else {
			// No Phone Number Found
		}
		cursor.close();
		AppApplication.AllContacts = AllContacts;
	}

	/**
	 * 加载通话记录
	 */
	synchronized public static void loadCallLogsCombined() {
		if (AppApplication.AllContacts.size() == 0) {
			loadContacts();
		}
		ArrayList<Contact> recentContacts = new ArrayList<Contact>();
		String[] projection = { Calls._ID, Calls.TYPE, Calls.CACHED_NAME,
				Calls.CACHED_NUMBER_TYPE, Calls.DATE, Calls.DURATION,
				Calls.NUMBER };
		ContentResolver resolver = AppApplication.globalApplication
				.getContentResolver();
		Cursor cursor = resolver.query(Calls.CONTENT_URI, projection, null,
				null, Calls.DEFAULT_SORT_ORDER);
		while (cursor.moveToNext()) {
			long callID = cursor.getInt(0);
			int callType = cursor.getInt(1);
			String name = cursor.getString(2);
			int numberType = cursor.getInt(3);
			long date = cursor.getLong(4);
			int duration = cursor.getInt(5);
			String number = cursor.getString(6);
			if (TextUtils.isEmpty(name)) {
				Contact tmpContact = new Contact();
				tmpContact.Times_Contacted = 1;
				tmpContact.Last_Contact_Call_ID = callID;
				tmpContact.Last_Contact_Call_Type = callType;
				tmpContact.Last_Contact_Number = number;
				tmpContact.Last_Contact_Phone_Type = numberType;
				tmpContact.Last_Time_Contacted = date;
				tmpContact.Last_Contact_Duration = duration;
				recentContacts.add(tmpContact);
			} else {
				boolean matched = false;
				match: for (Iterator<Contact> iterator = recentContacts
						.iterator(); iterator.hasNext();) {
					Contact con = iterator.next();
					ArrayList<phoneStruct> phones = con.getPhones();
					for (Iterator<phoneStruct> iterator2 = phones.iterator(); iterator2
							.hasNext();) {
						phoneStruct phoneStruct = iterator2.next();
						if (phoneStruct.phoneNumber.equals(number)) {
							matched = true;
							con.Times_Contacted++;
							break match;
						}
					}
				}

				if (!matched) {
					match2: for (Iterator<Contact> iterator = AppApplication.AllContacts
							.iterator(); iterator.hasNext();) {
						Contact con = iterator.next();
						ArrayList<phoneStruct> phones = con.getPhones();
						for (Iterator<phoneStruct> iterator2 = phones
								.iterator(); iterator2.hasNext();) {
							phoneStruct phoneStruct = iterator2.next();
							if (phoneStruct.phoneNumber.equals(number)) {
								matched = true;
								Contact tmpContact = con.clone();
								tmpContact.Times_Contacted = 1;
								tmpContact.Last_Contact_Call_ID = callID;
								tmpContact.Last_Contact_Call_Type = callType;
								tmpContact.Last_Contact_Number = number;
								tmpContact.Last_Contact_Phone_Type = numberType;
								tmpContact.Last_Time_Contacted = date;
								tmpContact.Last_Contact_Duration = duration;
								recentContacts.add(tmpContact);
								break match2;
							}
						}
					}
				}
			}
		}
		cursor.close();
		AppApplication.RecentContacts = recentContacts;
	}

	public static void removeCallLog(long call_ID) {
		ContentResolver resolver = AppApplication.globalApplication
				.getContentResolver();
		if (resolver.delete(Contacts.CONTENT_STREQUENT_URI, Calls._ID,
				new String[] { String.valueOf(call_ID) }) > 0) {
			// delete ok
		}
	}

	public static void clearCallLogs() {
		ContentResolver resolver = AppApplication.globalApplication
				.getContentResolver();
		if (resolver.delete(Calls.CONTENT_URI, null, null) > 0) {
			// delete ok
		}
	}

	/**
	 * 加载最近联系人（和收藏联系人）
	 */
	public static void loadStrequent() {
		ArrayList<Contact> StrequentContacts = new ArrayList<Contact>();
		String[] projection = { Contacts._ID, Contacts.DISPLAY_NAME,
				Contacts.LOOKUP_KEY, Contacts.PHOTO_THUMBNAIL_URI,
				Contacts.TIMES_CONTACTED, Contacts.LAST_TIME_CONTACTED,
				Contacts.STARRED, Contacts.PHOTO_ID };
		ContentResolver resolver = AppApplication.globalApplication
				.getContentResolver();
		// 显示最近联系人和收藏的联系人
		Cursor cursor = resolver.query(Contacts.CONTENT_STREQUENT_URI,
				projection, null, null, null);
		while (cursor.moveToNext()) {
			Contact contact = new Contact();
			long contractID = cursor.getInt(0);
			String displayName = cursor.getString(1);
			String lookupKey = cursor.getString(2);
			String photoUri = cursor.getString(3);
			int TIMES_CONTACTED = cursor.getInt(4);
			long LAST_TIME_CONTACTED = cursor.getLong(5);
			boolean starred = cursor.getInt(6) == 1;
			contact.setContactId(contractID);
			contact.setName(displayName);
			contact.setLookupKey(lookupKey);
			contact.setPhotoUri(photoUri);
			contact.setStarred(starred);
			contact.Times_Contacted = TIMES_CONTACTED;
			contact.Last_Time_Contacted = LAST_TIME_CONTACTED;
			StrequentContacts.add(contact);
		}
		cursor.close();
		// notify
	}

	public static void removeStrequent(long contact_ID) {
		ContentResolver resolver = AppApplication.globalApplication
				.getContentResolver();
		if (resolver.delete(Contacts.CONTENT_STREQUENT_URI, Contacts._ID,
				new String[] { String.valueOf(contact_ID) }) > 0) {
			// delete ok
		}
	}

	public static void clearStrequent() {
		ContentResolver resolver = AppApplication.globalApplication
				.getContentResolver();
		if (resolver.delete(Contacts.CONTENT_STREQUENT_URI, null, null) > 0) {
			// delete ok
		}
	}

	/**
	 * 根据电话号码寻出联系人
	 * 
	 * @param contactNumber
	 * @return
	 */
	public static Contact getContactByPhoneNumber(String contactNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(contactNumber));
		return lookupContactFromUri(uri);
	}

	public static Contact lookupContactFromUri(Uri uri) {
		final Contact info;
		Cursor phonesCursor = AppApplication.globalApplication
				.getContentResolver().query(uri, PhoneQuery._PROJECTION, null,
						null, null);

		if (phonesCursor != null) {
			try {
				if (phonesCursor.moveToFirst()) {
					info = new Contact();
					long contactId = phonesCursor.getLong(PhoneQuery.PERSON_ID);
					String lookupKey = phonesCursor
							.getString(PhoneQuery.LOOKUP_KEY);
					info.setLookupKey(lookupKey);
					info.setLookupUri(Contacts.getLookupUri(contactId,
							lookupKey));
					info.setName(phonesCursor.getString(PhoneQuery.NAME));

					info.type = phonesCursor.getInt(PhoneQuery.PHONE_TYPE);
					info.label = phonesCursor.getString(PhoneQuery.LABEL);
					info.number = phonesCursor
							.getString(PhoneQuery.MATCHED_NUMBER);
					info.normalizedNumber = phonesCursor
							.getString(PhoneQuery.NORMALIZED_NUMBER);
					info.photoId = phonesCursor.getLong(PhoneQuery.PHOTO_ID);
					info.setPhotoUri(phonesCursor
							.getString(PhoneQuery.PHOTO_URI));
					info.formattedNumber = null;
				} else {
					info = new Contact();
				}
			} finally {
				phonesCursor.close();
			}
		} else {
			info = null;
		}
		return info;
	}

	final static class PhoneQuery {
		public static final String[] _PROJECTION = new String[] {
				PhoneLookup._ID, PhoneLookup.DISPLAY_NAME, PhoneLookup.TYPE,
				PhoneLookup.LABEL, PhoneLookup.NUMBER,
				PhoneLookup.NORMALIZED_NUMBER, PhoneLookup.PHOTO_ID,
				PhoneLookup.LOOKUP_KEY, PhoneLookup.PHOTO_URI };

		public static final int PERSON_ID = 0;
		public static final int NAME = 1;
		public static final int PHONE_TYPE = 2;
		public static final int LABEL = 3;
		public static final int MATCHED_NUMBER = 4;
		public static final int NORMALIZED_NUMBER = 5;
		public static final int PHOTO_ID = 6;
		public static final int LOOKUP_KEY = 7;
		public static final int PHOTO_URI = 8;
	}

	private static Vibrator vibrator;

	public static void vibrate(long duaration) {
		if (vibrator == null) {
			vibrator = (Vibrator) AppApplication.globalApplication
					.getSystemService(Context.VIBRATOR_SERVICE);
		}
		vibrator.vibrate(duaration);
	}

	public static void makePhoneCall(String number) {
		if (TextUtils.isEmpty(number) || number.length() < 3) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		AppApplication.globalApplication.startActivity(intent);
	}
}
