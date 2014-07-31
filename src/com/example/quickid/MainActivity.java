package com.example.quickid;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ContactAdapter adapter;
	private ListView listView;
	private DialpadFragment mDialpadFragment;
	private ImageButton trigger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listview_all_contacts);
		adapter = new ContactAdapter(this);
		loadContacts();
		adapter.setContacts(AppApplication.AllContacts);
		listView.setAdapter(adapter);

		trigger = (ImageButton) findViewById(R.id.dialpad_button);
		trigger.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialpadFragment=new DialpadFragment();
				final FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.add(R.id.diaPanel, mDialpadFragment);
				ft.commit();
			}
		});
	}

	private void loadContacts() {
		AppApplication.AllContacts.clear();
		String[] projection = { Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY,
				Contacts.LOOKUP_KEY, Contacts.PHOTO_THUMBNAIL_URI };
		String order = Contacts.DISPLAY_NAME + " ASC";
		ContentResolver resolver = getContentResolver();
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

	@Override
	public void onBackPressed() {
		moveTaskToBack(false);
	}
}
