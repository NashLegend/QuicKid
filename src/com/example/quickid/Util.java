package com.example.quickid;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;

public class Util {

	public static ArrayList<String> getPossibleKeys(String key) {
		ArrayList<String> list = new ArrayList<String>();
		if (key.length() > 0) {
			if (key.contains("1") || key.contains("0")) {
				list.add(key);
			} else {
				int keyLen = key.length();
				String[] words;
				if (keyLen == 1) {
					words = AppApplication.keyMaps.get(key.charAt(0));
					for (int i = 0; i < words.length; i++) {
						list.add(words[i]);
					}
				} else {
					ArrayList<String> sonList = getPossibleKeys(key.substring(
							0, key.length() - 1));
					words = AppApplication.keyMaps
							.get(key.charAt(key.length() - 1));
					for (int i = 0; i < words.length; i++) {
						for (Iterator<String> iterator = sonList.iterator(); iterator
								.hasNext();) {
							String sonStr = iterator.next();
							list.add(sonStr + words[i]);
						}
					}
				}
			}
		}
		return list;
	}

	synchronized public static void loadContacts() {
		ArrayList<Contact> AllContacts = new ArrayList<Contact>();
		String[] projection = { Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY,
				Contacts.LOOKUP_KEY, Contacts.PHOTO_THUMBNAIL_URI };
		String order = Contacts.DISPLAY_NAME + " ASC";
		ContentResolver resolver = AppApplication.globalApplication
				.getContentResolver();
		Cursor cursor = resolver.query(Contacts.CONTENT_URI, projection, null,
				null, order);
		while (cursor.moveToNext()) {
			Contact contact = new Contact();

			long contractID = cursor.getInt(0);
			String displayName = cursor.getString(1);
			String lookupKey = cursor.getString(2);
			String photoUri = cursor.getString(3);

			contact.setContactId(contractID);
			contact.setName(displayName);
			contact.setLookupKey(lookupKey);
			contact.setPhotoUri(photoUri);

			String[] PROJECTION = { Phone.NUMBER, Phone.TYPE };
			String SELECTION = Data.LOOKUP_KEY + " = ?" + " AND "
					+ Data.MIMETYPE + " = " + "'" + Phone.CONTENT_ITEM_TYPE
					+ "'";
			String[] mSelectionArgs = { lookupKey };
			Cursor cursor1 = resolver.query(Phone.CONTENT_URI, PROJECTION,
					SELECTION, mSelectionArgs, null);
			if (cursor1.moveToFirst()) {
				do {
					contact.addPhone(cursor1.getString(0), cursor1.getInt(1));
				} while (cursor1.moveToNext());
			} else {
				// No Phone Number Found
			}
			cursor1.close();
			AllContacts.add(contact);
		}
		cursor.close();
		AppApplication.AllContacts = AllContacts;
		// TODO notify
		Intent intent = new Intent();
		intent.setAction(Consts.Action_All_Contacts_Changed);
		AppApplication.globalApplication.sendBroadcast(intent);
	}

	synchronized public static void loadCallLogs() {
		ArrayList<RecentContact> AllRecentContacts = new ArrayList<RecentContact>();
		String[] projection = { Calls._ID, Calls.TYPE, Calls.CACHED_NAME,
				Calls.CACHED_NUMBER_TYPE, Calls.DATE, Calls.DURATION,
				Calls.NUMBER };
		ContentResolver resolver = AppApplication.globalApplication
				.getContentResolver();
		Cursor cursor = resolver.query(Calls.CONTENT_URI, projection, null,
				null, null);
		while (cursor.moveToNext()) {
			RecentContact contact = new RecentContact();

			long contractID = cursor.getInt(0);
			int callType = cursor.getInt(1);
			String name = cursor.getString(2);
			int numberType = cursor.getInt(3);
			long date = cursor.getLong(4);
			int duration = cursor.getInt(5);
			String number = cursor.getString(6);

			contact.setContractID(contractID);
			contact.setCallType(callType);
			contact.setDate(date);
			contact.setDuration(duration);
			contact.setName(name);
			contact.setNumber(number);
			contact.setNumberType(numberType);

			System.out.println(number + " " + name + " " + numberType);

			AllRecentContacts.add(contact);
		}
		cursor.close();
		AppApplication.AllRecentContacts = AllRecentContacts;
		// TODO notify
		Intent intent = new Intent();
		intent.setAction(Consts.Action_All_Contacts_Changed);
		AppApplication.globalApplication.sendBroadcast(intent);
	}

	public static Contact getContactByPhoneNumber(String contactNumber) {
		final Contact info;
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

}
