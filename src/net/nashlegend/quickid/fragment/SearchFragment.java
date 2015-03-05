
package net.nashlegend.quickid.fragment;

import net.nashlegend.quickid.adapter.ContactAdapter;
import net.nashlegend.quickid.interfacc.OnListFragmentScrolledListener;
import net.nashlegend.quickid.interfacc.OnQueryContactListener;
import net.nashlegend.quickid.model.Contact;
import net.nashlegend.quickid.util.Consts;
import net.nashlegend.quickid.util.ContactHelper;
import net.nashlegend.quickid.view.ContactView;
import net.nashlegend.quickid.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class SearchFragment extends Fragment implements OnQueryContactListener {

    private ContactAdapter adapter;
    private ListView listView;
    private OnListFragmentScrolledListener mActivityScrollListener;
    private View layoutView;
    private View footer;// 使用FooterView会导致快速点击的时候bm
    private String currentNumber;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivityScrollListener = (OnListFragmentScrolledListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnListFragmentScrolledListener");
        }
    }

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_search, container,
                false);
        listView = (ListView) layoutView
                .findViewById(R.id.listview_search_contact);
        adapter = new ContactAdapter(getActivity(), ContactView.Display_Mode_Search);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mActivityScrollListener
                        .onListFragmentScrollStateChange(scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

            }
        });
        footer = inflater.inflate(R.layout.layout_add_contact, null);
        listView.addFooterView(footer);
        footer.setVisibility(View.GONE);
        footer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ContactHelper.addContact(currentNumber);
            }
        });
        registeReceiver();
        return layoutView;
    }

    public void onContactDeleted(long id) {
        for (int i = 0; i < adapter.getContacts().size(); i++) {
            if (adapter.getContacts().get(i).getContactId() == id) {
                adapter.getContacts().remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onQueryChanged(final String queryString) {
        if (isAdded()) {
            currentNumber = queryString;
            if (TextUtils.isEmpty(queryString) || queryString.length() < 3) {
                footer.setVisibility(View.GONE);
            } else {
                footer.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(queryString)) {
                adapter.getFilter().filter(queryString);
            }
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    onQueryChanged(queryString);
                }
            });
        }
    }
    
    private void registeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.Action_All_Contacts_Changed);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRetrive) {
            needRetrive = false;
            updateData();
        }
    }

    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    boolean needRetrive = false;

    private void updateData() {
        if (!TextUtils.isEmpty(currentNumber)) {
            adapter.clear();
            adapter.getFilter().filter(currentNumber);
        }
    }

    ContactUpdateReceiver receiver = new ContactUpdateReceiver();

    class ContactUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Consts.Action_All_Contacts_Changed.equals(action)) {
                if (isVisible()) {
                    updateData();
                } else {
                    needRetrive = true;
                }
            }
        }
    }

}
