package com.example.quickid;

import android.content.Context;
import android.provider.ContactsContract.Contacts;
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
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

}
