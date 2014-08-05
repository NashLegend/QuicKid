package com.example.quickid.fragment;

import com.example.quickid.R;
import com.example.quickid.R.layout;
import com.example.quickid.interfacc.OnQueryContactListener;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchFragment extends Fragment implements OnQueryContactListener {

	private String preQueryString = "";
	
	public SearchFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View layoutView = inflater.inflate(R.layout.fragment_search,
				container, false);
		return layoutView;
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
