package com.example.quickid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class ContactAdapter extends BaseAdapter implements Filterable {

	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private Context mContext;

	public ContactAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			ContactView contactView = new ContactView(mContext);
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

	private void sortContacts() {

	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	private Filter filter = new Filter() {

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			sortContacts();
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public ArrayList<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contact> contacts) {
		this.contacts = contacts;
		long tick = System.currentTimeMillis();
		List<String> poss = Util.getPossibleKeys("726");
		for (Iterator<Contact> iterator = contacts.iterator(); iterator
				.hasNext();) {
			Contact contact = iterator.next();
			contact.setPossibleStrings(poss);
			contact.match();
		}
		System.out.println(System.currentTimeMillis() - tick);
	}
}
