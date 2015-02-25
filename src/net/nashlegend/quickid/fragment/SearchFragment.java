
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
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class SearchFragment extends Fragment implements OnQueryContactListener {

    private ContactAdapter adapter;
    private ListView listView;
    private OnListFragmentScrolledListener mActivityScrollListener;
    private View layoutView;

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
                // TODO Auto-generated method stub

            }
        });
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
            if (TextUtils.isEmpty(queryString)) {
                // impossible,do nothing
            } else {
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
    
    class ContactUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Consts.Action_All_Contacts_Changed.equals(action)) {
            	
            }
        }
    }

}
