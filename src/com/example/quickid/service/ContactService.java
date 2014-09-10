
package com.example.quickid.service;

import java.util.Timer;

import com.example.legendutils.Tools.TimerUtil;
import com.example.quickid.AppApplication;
import com.example.quickid.util.Consts;
import com.example.quickid.util.ContactHelper;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.Contacts;

public class ContactService extends Service {
    private final Handler mHandler = new Handler();
    public static final int gap = 500;

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
            updateTimer = TimerUtil.setTimeOut(runnable, gap);
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        }

        private Runnable runnable = new Runnable() {

            @Override
            public void run() {
                ContactHelper.loadContacts();
                Intent intent = new Intent();
                intent.setAction(Consts.Action_All_Contacts_Changed);
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
            updateTimer = TimerUtil.setTimeOut(runnable, gap);
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        }

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ContactHelper.loadCallLogsCombined();
                Intent intent = new Intent();
                intent.setAction(Consts.Action_CallLogs_Changed);
                getApplicationContext().sendBroadcast(intent);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
