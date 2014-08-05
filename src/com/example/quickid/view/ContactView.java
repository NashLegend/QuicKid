package com.example.quickid.view;

import java.io.InputStream;

import com.example.quickid.AppApplication;
import com.example.quickid.R;
import com.example.quickid.model.Contact;
import com.example.quickid.util.IconContainer;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class ContactView extends FrameLayout {

	private Contact contact;
	private QuickContactBadge badge;
	private TextView nameTextView;
	private TextView phoneTextView;
	private IconLoadTask task;

	public ContactView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_contact_item, this);
		badge = (QuickContactBadge) findViewById(R.id.badge_contact_item);
		nameTextView = (TextView) findViewById(R.id.text_contact_name);
		phoneTextView = (TextView) findViewById(R.id.text_contact_phone);
	}

	public ContactView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContactView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void build() {
		badge.assignContactUri(Contacts.getLookupUri(contact.getContactId(),
				contact.getLookupKey()));
		nameTextView.setText(contact.getName());
		if (contact.hasNumber() > 0) {
			phoneTextView.setText(contact.getPhones().get(0).phoneNumber);
		}
		loadAvatar();
	}

	private void loadAvatar() {
		badge.setImageResource(R.drawable.ic_contact_picture_holo_light);
		if (!TextUtils.isEmpty(contact.getPhotoUri())) {
			if (task != null && task.getStatus() == Status.RUNNING) {
				task.cancel(true);
			}
			Bitmap bmp = IconContainer.get(contact);
			if (bmp == null) {
				task = new IconLoadTask();
				task.execute(contact);
			} else {
				badge.setImageBitmap(bmp);
			}
		}
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

	class IconLoadTask extends AsyncTask<Contact, Integer, Bitmap> {

		Contact originalContact;

		@Override
		protected Bitmap doInBackground(Contact... params) {
			originalContact = params[0];
			Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
					contact.getContactId());
			InputStream input = Contacts.openContactPhotoInputStream(
					AppApplication.getApplicationContentResolver(), uri);
			Bitmap bmp = BitmapFactory.decodeStream(input);
			if (bmp != null) {
				IconContainer.put(originalContact, bmp);
			}
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (contact.equals(this.originalContact)) {
				if (result != null) {
					badge.setImageBitmap(result);
				} else {
					// badge.setImageResource(fileItem.getIcon());
				}
			}
			super.onPostExecute(result);
		}

	}

}
