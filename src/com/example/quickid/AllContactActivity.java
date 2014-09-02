
package com.example.quickid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.example.quickid.adapter.AllContactsAdapter;
import com.example.quickid.model.Contact;
import com.example.quickid.util.Consts;
import com.example.quickid.util.ContactHelper;
import com.example.quickid.util.PinnedHeaderListView;
import com.example.quickid.view.SideBar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.SparseArray;

public class AllContactActivity extends Activity {
    private ContactUpdateReceiver receiver;
    private final SparseArray<ArrayList<Contact>> contactMaps = new SparseArray<ArrayList<Contact>>();
    private final SparseArray<String> charMaps = new SparseArray<String>();
    private final HashMap<String, Integer> keyMaps = new HashMap<String, Integer>();
    PinnedHeaderListView listView;
    AllContactsAdapter adapter;
    SideBar sideBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contact);

        ContactHelper.splitAllContacts(contactMaps, charMaps);
        listView = (PinnedHeaderListView) findViewById(R.id.listview_all_contacts);
        adapter = new AllContactsAdapter(this);
        adapter.setData(contactMaps, charMaps);
        listView.setAdapter(adapter);

        int pos = 0;
        for (int i = 0; i < charMaps.size(); i++) {
            int idx = charMaps.keyAt(i);
            keyMaps.put(charMaps.get(idx), pos);
            pos += contactMaps.get(idx).size() + 1;
        }

        sideBar = (SideBar) findViewById(R.id.sidebar_all_contacts);

        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.Action_All_Contacts_Changed);
        receiver = new ContactUpdateReceiver();
        registerReceiver(receiver, filter);
    }

    class OnTouchingLetterChangedListener implements SideBar.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (keyMaps.get(s) != null) {
                int idx = keyMaps.get(s);
                listView.setSelection(idx);
            }
        }

    }

    private void updateData() {
        ContactHelper.splitAllContacts(contactMaps, charMaps);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    class ContactUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateData();
        }

    }
}
