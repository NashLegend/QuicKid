package com.example.quickid;

import android.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AllContactsFragment extends Fragment {
	private ContactAdapter adapter;
	private ListView listView;

	public AllContactsFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View layoutView = inflater.inflate(R.layout.fragment_all,
				container, false);
		listView = (ListView) layoutView
				.findViewById(R.id.listview_all_contacts);
		adapter = new ContactAdapter(getActivity());
		loadContacts();
		adapter.setContacts(AppApplication.AllContacts);
		listView.setAdapter(adapter);
		return layoutView;
	}

	private void loadContacts() {
		AppApplication.AllContacts.clear();
		String[] projection = { Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY,
				Contacts.LOOKUP_KEY, Contacts.PHOTO_THUMBNAIL_URI };
		String order = Contacts.DISPLAY_NAME + " ASC";
		ContentResolver resolver = getActivity().getContentResolver();
		Cursor cursor = resolver.query(Contacts.CONTENT_URI, projection, null,
				null, order);
		while (cursor.moveToNext()) {
			Contact contact = new Contact();

			long contractID = cursor.getInt(0);
			String displayName = cursor.getString(1);
			String lookupKey = cursor.getString(2);
			String photoUri = cursor.getString(3);

			contact.setContactId(contractID);
			contact.setDisplayName(displayName);
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
			AppApplication.AllContacts.add(contact);
		}
		cursor.close();
	}

}
