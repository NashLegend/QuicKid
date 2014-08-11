package com.example.quickid.fragment;

import com.example.quickid.AppApplication;
import com.example.quickid.R;
import com.example.quickid.R.layout;
import com.example.quickid.adapter.ContactAdapter;
import com.example.quickid.util.Consts;
import com.example.quickid.util.ContactHelper;
import com.example.quickid.view.ContactView;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StrequentDialFragment extends Fragment {
	private ContactAdapter adapter;
	private ListView listView;

	public StrequentDialFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layoutView = inflater.inflate(R.layout.fragment_recent, container,
				false);
		listView = (ListView) layoutView
				.findViewById(R.id.listview_frequent_contact);
		adapter = new ContactAdapter(getActivity(),ContactView.Display_Mode_Recent);
		adapter.setContacts(AppApplication.StrequentContacts);
		listView.setAdapter(adapter);
		registeReceiver();
		loadStrequent();
		return layoutView;
	}

	private void loadStrequent() {
		ContactHelper.loadStrequent();
		adapter.setContacts(AppApplication.StrequentContacts);
		adapter.notifyDataSetChanged();
	}

	private void registeReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Consts.Action_Strequent_Contacts_Changed);
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
		if (needRetrive && adapter != null) {
			needRetrive = false;
			loadStrequent();
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
			if (Consts.Action_Strequent_Contacts_Changed.equals(intent
					.getAction())) {
				if (isVisible()) {
					loadStrequent();
				} else {
					needRetrive = true;
				}
			}
		}
	}

}
