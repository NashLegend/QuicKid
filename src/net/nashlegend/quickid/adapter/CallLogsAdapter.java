package net.nashlegend.quickid.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import net.nashlegend.quickid.AppApplication;
import net.nashlegend.quickid.model.Contact;
import net.nashlegend.quickid.model.Contact.PointPair;
import net.nashlegend.quickid.model.Contact.ScoreAndHits;
import net.nashlegend.quickid.view.CallLogView;
import net.nashlegend.quickid.view.ContactView;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class CallLogsAdapter extends BaseAdapter implements Filterable {

	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private Context mContext;
	
	public CallLogsAdapter(Context context) {
		mContext = context;
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
			CallLogView callLogView = new CallLogView(mContext);
			holder.callLogView = callLogView;
			callLogView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.callLogView.setContact(contacts.get(position));
		holder.callLogView.build();
		return holder.callLogView;
	}

	class ViewHolder {
		CallLogView callLogView;
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
