package com.example.quickid.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.quickid.AppApplication;
import com.example.quickid.R;
import com.example.quickid.model.Contact;
import com.example.quickid.model.Contact.PointPair;
import com.example.quickid.util.IconContainer;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.provider.ContactsContract.Contacts;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class ContactView extends FrameLayout {

	private Contact contact;
	private QuickContactBadge badge;
	private TextView nameTextView;
	private TextView pinyinTextView;
	private TextView phoneTextView;
	private IconLoadTask task;
	public int Display_Mode = 0;
	public static final int Display_Mode_Recent = 1;
	public static final int Display_Mode_Search = 2;
	public static final int Display_Mode_Display = 3;

	public ContactView(Context context, int display) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_contact_item, this);
		badge = (QuickContactBadge) findViewById(R.id.badge_contact_item);
		nameTextView = (TextView) findViewById(R.id.text_contact_name);
		pinyinTextView = (TextView) findViewById(R.id.text_contact_pinyin);
		phoneTextView = (TextView) findViewById(R.id.text_contact_phone);
		this.Display_Mode = display;
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
		String nameString = contact.getName();
		String phoneString = "";
		if (contact.getPhones().size() > 0) {
			phoneString = contact.getPhones().get(0).toString();
		}
		nameTextView.setText(nameString);
		switch (Display_Mode) {
		case Display_Mode_Display:
		case Display_Mode_Recent:
			if (contact.hasNumber() > 0) {
				phoneTextView.setText(phoneString);
			}
			break;
		case Display_Mode_Search:
			if (contact.matchValue.matchLevel == Contact.Level_Complete) {
				if (contact.matchValue.matchType == Contact.Match_Type_Name) {
					String str = contact.fullNameNumberWithoutSpace
							.get(contact.matchValue.nameIndex);
					SpannableStringBuilder builder = new SpannableStringBuilder(
							str);
					ForegroundColorSpan redSpan = new ForegroundColorSpan(
							Color.RED);
					builder.setSpan(redSpan, 0, str.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					pinyinTextView.setText(builder);
				} else {
					String str = contact.getPhones().get(
							contact.matchValue.nameIndex).phoneNumber;
					SpannableStringBuilder builder = new SpannableStringBuilder(
							str);
					ForegroundColorSpan redSpan = new ForegroundColorSpan(
							Color.RED);
					builder.setSpan(redSpan, 0, str.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					phoneTextView.setText(builder);
				}
			} else if (contact.matchValue.matchLevel == Contact.Level_Headless) {
				String str = contact.getPhones().get(
						contact.matchValue.nameIndex).phoneNumber;
				SpannableStringBuilder builder = new SpannableStringBuilder(str);
				ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
				builder.setSpan(redSpan,
						contact.matchValue.pairs.get(0).strIndex,
						contact.matchValue.pairs.get(0).strIndex
								+ contact.matchValue.reg.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				phoneTextView.setText(builder);
			} else {
				String str = contact.fullNamesString.get(
						contact.matchValue.nameIndex).replaceAll(" ", "");
				ArrayList<PointPair> pa = getColoredString(
						contact.fullNameNumber
								.get(contact.matchValue.nameIndex),
						contact.matchValue.pairs, "#FF0000");
				SpannableStringBuilder builder = new SpannableStringBuilder(str);
				for (Iterator<PointPair> iterator = pa.iterator(); iterator
						.hasNext();) {
					PointPair pointPair = iterator.next();
					builder.setSpan(new ForegroundColorSpan(Color.RED),
							pointPair.listIndex, pointPair.strIndex,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				pinyinTextView.setText(builder);
			}
			break;

		default:
			break;
		}
		loadAvatar();
	}

	private ArrayList<PointPair> getColoredString(ArrayList<String> strings,
			ArrayList<PointPair> pairs, String color) {
		int k = 0;
		int idx = -1;
		int crtHead = -1;
		int crtTail = -1;
		ArrayList<PointPair> ps = new ArrayList<PointPair>();
		for (int i = 0; i < strings.size(); i++) {
			String str = strings.get(i);
			for (int j = 0; j < str.length() && k < pairs.size(); j++) {
				idx++;
				if (pairs.get(k).listIndex == i && pairs.get(k).strIndex == j) {
					if (crtHead == -1) {
						crtHead = idx;
						crtTail = idx + 1;
					} else {
						if (crtTail == idx) {
							crtTail = idx + 1;
						}
					}
					k++;
				} else {
					if (crtHead != -1) {
						ps.add(new PointPair(crtHead, crtTail));
						crtHead = -1;
						crtTail = -1;
					}
				}
			}
		}
		if (crtHead != -1) {
			ps.add(new PointPair(crtHead, crtTail));
			crtHead = -1;
			crtTail = -1;
		}
		return ps;
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
		} else {
			setDefaultAvatar();
		}
	}

	private static TypedArray sColors;
	private static int sDefaultColor;
	private static final int NUM_OF_TILE_COLORS = 8;

	@SuppressLint("Recycle")
	private void setDefaultAvatar() {
		if (sColors == null) {
			sColors = getResources().obtainTypedArray(
					R.array.letter_tile_colors);
			sDefaultColor = getResources().getColor(
					R.color.letter_tile_default_color);
		}
		badge.setBackgroundColor(pickColor(contact.getName()));
		badge.setImageResource(R.drawable.ic_list_item_avatar);
	}

	private int pickColor(final String identifier) {
		if (TextUtils.isEmpty(identifier)) {
			return sDefaultColor;
		}
		final int color = Math.abs(identifier.hashCode()) % NUM_OF_TILE_COLORS;
		return sColors.getColor(color, sDefaultColor);
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
					badge.setImageResource(R.drawable.ic_list_item_avatar);
				}
			}
			super.onPostExecute(result);
		}

	}

}
