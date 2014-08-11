package com.example.quickid.fragment;

import com.example.quickid.R;
import com.example.quickid.adapter.ContactAdapter;
import com.example.quickid.interfacc.OnListFragmentScrolledListener;
import com.example.quickid.interfacc.OnQueryContactListener;
import com.example.quickid.view.ContactView;

import android.app.Activity;
import android.app.Fragment;
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
		adapter = new ContactAdapter(getActivity(),ContactView.Display_Mode_Search);
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

}
