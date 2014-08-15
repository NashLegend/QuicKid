
package com.example.quickid.adapter;

import java.util.ArrayList;

import android.R.string;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.quickid.model.Contact;
import com.example.quickid.util.SectionedBaseAdapter;
import com.example.quickid.view.ContactView;
import com.example.quickid.view.SplitterView;

public class AllContactsAdapter extends SectionedBaseAdapter {
    public SparseArray<ArrayList<Contact>> contactMaps = new SparseArray<ArrayList<Contact>>();
    public SparseArray<String> charMaps = new SparseArray<String>();
    private Context mContext;

    public AllContactsAdapter(Context context) {
        mContext = context;
    }

    public void setData(SparseArray<ArrayList<Contact>> contactMaps,
            SparseArray<String> charMaps) {
        this.contactMaps = contactMaps;
        this.charMaps = charMaps;
    }

    @Override
    public Object getItem(int section, int position) {
        return contactMaps.get(section).get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return 0;
    }

    @Override
    public int getSectionCount() {
        return charMaps.size();
    }

    @Override
    public int getCountForSection(int section) {
        return contactMaps.get(section).size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            ContactView contactView = new ContactView(mContext, ContactView.Display_Mode_Display);
            holder.contactView = contactView;
            contactView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.contactView.setContact((Contact) getItem(section, position));
        holder.contactView.build();
        return holder.contactView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            SplitterView splitterView = new SplitterView(mContext);
            holder.splitterView = splitterView;
            splitterView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.splitterView.build(charMaps.get(section));
        return holder.splitterView;
    }

    class ViewHolder {
        ContactView contactView;
        SplitterView splitterView;
    }

}
