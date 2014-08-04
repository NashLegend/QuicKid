package com.example.quickid;

import com.example.legendutils.Tools.ToastUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AllContactActivity extends Activity {
	private ContactUpdateReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_contact);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Consts.Action_All_Contacts_Changed);
		receiver = new ContactUpdateReceiver();
		registerReceiver(receiver, filter);
	}

	class ContactUpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ToastUtil.showToast(context, "Changed");
			//Notify
		}

	}
}
