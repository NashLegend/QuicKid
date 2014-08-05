package com.example.quickid.interfacc;

public interface OnQueryContactListener {

	void onQueryChanged(String queryString);

	void onSimpleQuery(String queryString);

	void onQueryAddOneCharactorFromEnd(String charString);

	void onQueryDeleteOneCharactorFromEnd(String charString);

	void onQueryEmpty();
}
