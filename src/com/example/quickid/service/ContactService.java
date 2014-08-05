package com.example.quickid.service;

import java.util.Timer;

import com.example.legendutils.Tools.TimerUtil;
import com.example.quickid.AppApplication;
import com.example.quickid.util.Util;

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
	private static boolean inwaitstate = false;
	private static Timer updateTimer;
	private final Handler mHandler = new Handler();

	public ContactService() {

	}

	@Override
	public void onCreate() {
		System.out.println("oncreate");
		AppApplication.globalApplication.getContentResolver()
				.registerContentObserver(Contacts.CONTENT_URI, true,
						new ContactObserver());
		AppApplication.globalApplication.getContentResolver()
				.registerContentObserver(Calls.CONTENT_URI, true,
						new ContactObserver());
		super.onCreate();
	}

	private final class ContactObserver extends ContentObserver {

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

	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			Util.loadCallLogs();
		}
	};

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
