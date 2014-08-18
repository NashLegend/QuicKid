
package com.example.quickid;

import java.util.ArrayList;

import com.example.legendutils.Tools.ToastUtil;
import com.example.quickid.adapter.AllContactsAdapter;
import com.example.quickid.model.Contact;
import com.example.quickid.util.Consts;
import com.example.quickid.util.ContactHelper;
import com.example.quickid.util.PinnedHeaderListView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class AllContactActivity extends Activity {
    private ContactUpdateReceiver receiver;
    private final SparseArray<ArrayList<Contact>> contactMaps = new SparseArray<ArrayList<Contact>>();
    private final SparseArray<String> charMaps = new SparseArray<String>();
    PinnedHeaderListView listView;
    AllContactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contact);

        ContactHelper.splitAllContacts(contactMaps, charMaps);
        listView = (PinnedHeaderListView) findViewById(R.id.listview_all_contacts);
        adapter = new AllContactsAdapter(this);
        adapter.setData(contactMaps, charMaps);
        listView.setAdapter(adapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.Action_All_Contacts_Changed);
        receiver = new ContactUpdateReceiver();
        registerReceiver(receiver, filter);
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
