
package net.nashlegend.quickid.fragment;

import net.nashlegend.quickid.AppApplication;
import net.nashlegend.quickid.adapter.CallLogsAdapter;
import net.nashlegend.quickid.model.Contact;
import net.nashlegend.quickid.util.Consts;

import net.nashlegend.quickid.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class RecentContactsFragment extends Fragment {
    private CallLogsAdapter adapter;
    private ListView listView;

    public RecentContactsFragment() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_recent, container,
                false);
        listView = (ListView) layoutView
                .findViewById(R.id.listview_frequent_contact);
        adapter = new CallLogsAdapter(getActivity());
        adapter.setContacts(AppApplication.RecentContacts);
        listView.setAdapter(adapter);
        registeReceiver();
        loadCallLogs();
        return layoutView;
    }

    public void onCallLogDeleted(String number) {
        if (TextUtils.isEmpty(number)) {
            return;
        }
        for (int i = 0; i < AppApplication.RecentContacts.size(); i++) {
            Contact type = AppApplication.RecentContacts.get(i);
            if (number.equals(type.Last_Contact_Number)) {
                AppApplication.RecentContacts.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void onCallLogCleared() {
        AppApplication.RecentContacts.clear();
        adapter.notifyDataSetInvalidated();
    }

    private void loadCallLogs() {
        adapter.setContacts(AppApplication.RecentContacts);
        adapter.notifyDataSetChanged();
    }

    private void registeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.Action_CallLogs_Changed);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
        	if (needRetrive) {
                needRetrive = false;
                loadCallLogs();
            }else {
				adapter.notifyDataSetChanged();
			}
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

    private boolean needRetrive = false;
    private DataChangeReceiver receiver = new DataChangeReceiver();

    class DataChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Consts.Action_CallLogs_Changed.equals(intent.getAction())) {
                if (isVisible()) {
                    loadCallLogs();
                } else {
                    needRetrive = true;
                }
            }
        }
    }

}
