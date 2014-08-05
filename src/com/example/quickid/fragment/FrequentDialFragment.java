package com.example.quickid.fragment;

import com.example.quickid.AppApplication;
import com.example.quickid.R;
import com.example.quickid.R.layout;
import com.example.quickid.adapter.ContactAdapter;
import com.example.quickid.util.Util;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FrequentDialFragment extends Fragment {
	private ContactAdapter adapter;
	private ListView listView;

	public FrequentDialFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layoutView = inflater.inflate(R.layout.fragment_recent, container,
				false);
		listView = (ListView) layoutView
				.findViewById(R.id.listview_frequent_contact);
		adapter = new ContactAdapter(getActivity());
		adapter.setContacts(AppApplication.FrequentContacts);
		listView.setAdapter(adapter);
		loadFrequent();
		return layoutView;
	}

	private void loadFrequent() {
		Util.loadFrequent();
		adapter.setContacts(AppApplication.FrequentContacts);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

}
