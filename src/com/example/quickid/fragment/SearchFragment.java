package com.example.quickid.fragment;

import com.example.quickid.AppApplication;
import com.example.quickid.R;
import com.example.quickid.R.layout;
import com.example.quickid.adapter.ContactAdapter;
import com.example.quickid.interfacc.OnListFragmentScrolledListener;
import com.example.quickid.interfacc.OnQueryContactListener;
import com.example.quickid.util.ContactHelper;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
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
	private String preQueryString = "";
	private OnListFragmentScrolledListener mActivityScrollListener;

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
		final View layoutView = inflater.inflate(R.layout.fragment_search,
				container, false);
		listView = (ListView) layoutView
				.findViewById(R.id.listview_search_contact);
		adapter = new ContactAdapter(getActivity());
		adapter.setContacts(AppApplication.AllContacts);
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
		loadAllContact();
		return layoutView;
	}

	private void loadAllContact() {
		ContactHelper.loadContacts();
		adapter.setContacts(AppApplication.AllContacts);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onQueryChanged(String queryString) {
		if (queryString == null) {
			queryString = "";
		}
		if (this.preQueryString.equals(queryString)) {
			return;
		}
		if (TextUtils.isEmpty(queryString)) {
			onQueryEmpty();
		} else {
			int preLength = this.preQueryString.length();
			int queryLength = queryString.length();
			if (preLength > 0 && (preLength == queryLength + 1)
					&& this.preQueryString.startsWith(queryString)) {
				onQueryDeleteOneCharactorFromEnd(queryString);
			} else if (preLength > 0 && (preLength == queryLength - 1)
					&& queryString.startsWith(this.preQueryString)) {
				onQueryAddOneCharactorFromEnd(queryString);
			} else {
				onSimpleQuery(queryString);
			}
		}
		preQueryString = queryString;
	}

	@Override
	public void onQueryAddOneCharactorFromEnd(String charString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onQueryDeleteOneCharactorFromEnd(String charString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onQueryEmpty() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSimpleQuery(String queryString) {
		// TODO Auto-generated method stub

	}

}
