package com.example.quickid.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.example.quickid.AppApplication;
import com.example.quickid.model.Contact;
import com.example.quickid.model.Contact.PointPair;
import com.example.quickid.model.Contact.ScoreAndHits;
import com.example.quickid.view.ContactView;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class ContactAdapter extends BaseAdapter implements Filterable {

	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private Context mContext;
	private int display_Mode = ContactView.Display_Mode_Display;

	public ContactAdapter(Context context, int display) {
		mContext = context;
		display_Mode = display;
	}

	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			ContactView contactView = new ContactView(mContext, display_Mode);
			holder.contactView = contactView;
			contactView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.contactView.setContact(contacts.get(position));
		holder.contactView.build();
		return holder.contactView;
	}

	class ViewHolder {
		ContactView contactView;
	}

	public void sortContact(ArrayList<Contact> lis) {
		ContactComparator comparator = new ContactComparator();
		Collections.sort(lis, comparator);
	}

	public class ContactComparator implements Comparator<Contact> {

		@Override
		public int compare(Contact lhs, Contact rhs) {

			if (lhs.matchValue.score > rhs.matchValue.score) {
				return -1;
			} else if (lhs.matchValue.score == rhs.matchValue.score) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	public ArrayList<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contact> contacts) {
		this.contacts = contacts;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	private String preQueryString = "";

	private Filter filter = new Filter() {
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			if (results != null) {
				setContacts((ArrayList<Contact>) results.values);
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			if (TextUtils.isEmpty(constraint)
					|| preQueryString.equals(constraint)) {
				return null;
			}
			String queryString = constraint.toString();
			FilterResults results = new FilterResults();
			int preLength = preQueryString.length();
			int queryLength = queryString.length();
			ArrayList<Contact> baseList = new ArrayList<Contact>();
			ArrayList<Contact> resultList = new ArrayList<Contact>();
			if (preLength > 0 && (preLength == queryLength - 1)
					&& queryString.startsWith(preQueryString)) {
				baseList = contacts;
			} else {
				baseList = AppApplication.AllContacts;
			}

			for (Iterator<Contact> iterator = baseList.iterator(); iterator
					.hasNext();) {
				Contact contact = (Contact) iterator.next();
				if (contact.match(queryString) > 0) {
					resultList.add(contact);
				}
			}
			sortContact(resultList);
			preQueryString = queryString;
			results.values = resultList;
			results.count = resultList.size();
			return results;
		}
	};
}
