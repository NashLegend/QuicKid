package com.example.quickid.service;

import java.util.Timer;

import com.example.legendutils.Tools.TimerUtil;
import com.example.quickid.AppApplication;
import com.example.quickid.util.Consts;
import com.example.quickid.util.ContactHelper;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.Contacts;

public class ContactService extends Service {
	private final Handler mHandler = new Handler();

	public ContactService() {

	}

	@Override
	public void onCreate() {
		AppApplication.globalApplication.getContentResolver()
				.registerContentObserver(Contacts.CONTENT_URI, true,
						new ContactObserver());
		AppApplication.globalApplication.getContentResolver()
				.registerContentObserver(Calls.CONTENT_URI, true,
						new CallLogsContactObserver());
		AppApplication.globalApplication.getContentResolver()
				.registerContentObserver(Contacts.CONTENT_STREQUENT_URI, true,
						new StrequentContactObserver());
		super.onCreate();
	}

	private final class ContactObserver extends ContentObserver {

		private boolean inwaitstate = false;
		private Timer updateTimer;

		public ContactObserver() {
			super(mHandler);
		}

		@Override
		public void onChange(boolean selfChange) {
			if (inwaitstate) {
				TimerUtil.clearTimeOut(updateTimer);
			}
			inwaitstate = true;
			updateTimer = TimerUtil.setTimeOut(runnable, 3000);
			super.onChange(selfChange);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			super.onChange(selfChange, uri);
		}

		private Runnable runnable = new Runnable() {

			@Override
			public void run() {
				ContactHelper.loadCallLogs();
				Intent intent = new Intent();
				intent.setAction(Consts.Action_All_Contacts_Changed);
				getApplicationContext().sendBroadcast(intent);
			}
		};
	}

	private final class StrequentContactObserver extends ContentObserver {

		private boolean inwaitstate = false;
		private Timer updateTimer;

		public StrequentContactObserver() {
			super(mHandler);
		}

		@Override
		public void onChange(boolean selfChange) {
			if (inwaitstate) {
				TimerUtil.clearTimeOut(updateTimer);
			}
			inwaitstate = true;
			updateTimer = TimerUtil.setTimeOut(runnable, 3000);
			super.onChange(selfChange);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			super.onChange(selfChange, uri);
		}

		private Runnable runnable = new Runnable() {

			@Override
			public void run() {
				ContactHelper.loadStrequent();
				Intent intent = new Intent();
				intent.setAction(Consts.Action_Strequent_Contacts_Changed);
				getApplicationContext().sendBroadcast(intent);
			}
		};
	}

	private final class CallLogsContactObserver extends ContentObserver {

		private boolean inwaitstate = false;
		private Timer updateTimer;

		public CallLogsContactObserver() {
			super(mHandler);
		}

		@Override
		public void onChange(boolean selfChange) {
			if (inwaitstate) {
				TimerUtil.clearTimeOut(updateTimer);
			}
			inwaitstate = true;
			updateTimer = TimerUtil.setTimeOut(runnable, 3000);
			super.onChange(selfChange);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			super.onChange(selfChange, uri);
		}

		private Runnable runnable = new Runnable() {

			@Override
			public void run() {
				ContactHelper.loadCallLogs();
				Intent intent = new Intent();
				intent.setAction(Consts.Action_CallLogs_Changed);
				getApplicationContext().sendBroadcast(intent);
			}
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("onstartcomand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("onbind");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		System.out.println("onunbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		System.out.println("ondestroy");
		super.onDestroy();
	}

}
