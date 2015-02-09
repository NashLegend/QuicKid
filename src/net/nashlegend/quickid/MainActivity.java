
package net.nashlegend.quickid;

import net.nashlegend.quickid.R;

import net.nashlegend.quickid.fragment.DialpadFragment;
import net.nashlegend.quickid.fragment.RecentContactsFragment;
import net.nashlegend.quickid.fragment.SearchFragment;
import net.nashlegend.quickid.fragment.DialpadFragment.OnDialpadQueryChangedListener;
import net.nashlegend.quickid.interfacc.OnListFragmentScrolledListener;
import net.nashlegend.quickid.util.Consts;
import net.nashlegend.quickid.util.ContactHelper;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.AbsListView.OnScrollListener;

public class MainActivity extends Activity implements OnClickListener,
        OnListFragmentScrolledListener, OnDialpadQueryChangedListener {

    private DialpadFragment mDialpadFragment;
    private RecentContactsFragment mFrequentDialFragment;
    private SearchFragment mSearchFragment;
    private ImageButton mDialpadButton;
    private ImageButton mDialButton;
    private ImageButton mSmsButton;
    private ImageButton mAllContactsButton;
    private static final String TAG_DIALPAD_FRAGMENT = "dialpad";
    private static final String TAG_RECENT_DIAL_FRAGMENT = "recent";
    private static final String TAG_SEARCH_FRAGMENT = "search";
    private int currentStatus = 0;
    private ContactBroadcastReceiver contactBroadcastReceiver;
    private static int Status_Show_Frequent = 0;
    private static int Status_Show_Search = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_dial, new RecentContactsFragment(),
                            TAG_RECENT_DIAL_FRAGMENT)
                    .add(R.id.layout_dialer_panel, new DialpadFragment(),
                            TAG_DIALPAD_FRAGMENT).commit();
        }
        mAllContactsButton = (ImageButton) findViewById(R.id.button_all_contacts);
        mDialpadButton = (ImageButton) findViewById(R.id.button_dialpad);
        mDialButton = (ImageButton) findViewById(R.id.button_dial);
        mSmsButton = (ImageButton) findViewById(R.id.button_send_message);
        mDialButton.setOnClickListener(this);
        mSmsButton.setOnClickListener(this);
        mDialpadButton.setOnClickListener(this);
        mAllContactsButton.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Consts.Action_Delete_One_Contact_From_Search);
        filter.addAction(Consts.Action_Delete_One_Call_Log);
        filter.addAction(Consts.Action_Clear_Call_Log);
        contactBroadcastReceiver = new ContactBroadcastReceiver();
        registerReceiver(contactBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(contactBroadcastReceiver);
        super.onDestroy();
    }

    class ContactBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Consts.Action_Delete_One_Contact_From_Search.equals(action)) {
                long id = intent.getLongExtra(Consts.Extra_Contact_ID, -1L);
                mSearchFragment.onContactDeleted(id);
                return;
            } else if (Consts.Action_Delete_One_Call_Log.equals(action)) {
                String num = intent.getStringExtra(Consts.Extra_Calllog_Number);
                mFrequentDialFragment.onCallLogDeleted(num);
                return;
            } else if (Consts.Action_Clear_Call_Log.equals(action)) {
                mFrequentDialFragment.onCallLogCleared();
                return;
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof DialpadFragment) {
            mDialpadFragment = (DialpadFragment) fragment;
            final FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.hide(mDialpadFragment);
            transaction.commit();
        } else if (fragment instanceof RecentContactsFragment) {
            mFrequentDialFragment = (RecentContactsFragment) fragment;
        }
        super.onAttachFragment(fragment);
    }

    private boolean isDialpadShowing() {
        return mDialpadFragment != null && mDialpadFragment.isVisible();
    }

    private void enterSearchUi() {
        final FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        if (currentStatus == Status_Show_Frequent) {
            transaction.remove(mFrequentDialFragment);
        }

        final String tag = TAG_SEARCH_FRAGMENT;

        mSearchFragment = (SearchFragment) getFragmentManager()
                .findFragmentByTag(tag);
        if (mSearchFragment == null) {
            mSearchFragment = new SearchFragment();
        }
        transaction.replace(R.id.layout_dial, mSearchFragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
        currentStatus = Status_Show_Search;
    }

    public void exitSearchUi() {
        if (currentStatus == Status_Show_Search) {
            getFragmentManager().popBackStack(0,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            currentStatus = Status_Show_Frequent;
        }
    }

    public void showDialpad(boolean animate) {
        // TODO 有一个bug，就是在hideDialpad动画没有完成之前就执行showDialpad的话，会导致永久hide
        // 这个bug在系统的拨号App中同样存在
        mDialpadFragment.setAdjustTranslationForAnimation(animate);
        mDialpadButton.setVisibility(View.GONE);
        mDialButton.setVisibility(View.VISIBLE);
        mSmsButton.setVisibility(View.VISIBLE);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (animate) {
            ft.setCustomAnimations(R.anim.slide_in, 0);
        } else {
            mDialpadFragment.setYFraction(0);
        }
        ft.show(mDialpadFragment);
        ft.commit();
    }

    public void hideDialpad(boolean animate, boolean clearDialpad) {
        if (mDialpadFragment == null)
            return;
        mDialButton.setVisibility(View.GONE);
        mSmsButton.setVisibility(View.GONE);
        mDialpadButton.setVisibility(View.VISIBLE);
        if (clearDialpad) {
            mDialpadFragment.clearDialpad();
        }
        if (!mDialpadFragment.isVisible())
            return;
        mDialpadFragment.setAdjustTranslationForAnimation(animate);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (animate) {
            ft.setCustomAnimations(0, R.anim.slide_out);
        }
        ft.hide(mDialpadFragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (isDialpadShowing()) {
            hideDialpad(true, false);
            return;
        }
        if (currentStatus == Status_Show_Search) {
            mDialpadFragment.clearDialpad();
            currentStatus = Status_Show_Frequent;
            return;
        }

        moveTaskToBack(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_dialpad:
                showDialpad(true);
                break;
            case R.id.button_dial:
                if (isDialpadShowing()) {
                    String number = mDialpadFragment.getDiapadNumber();
                    if (!TextUtils.isEmpty(number) && number.length() >= 3) {
                        ContactHelper.makePhoneCall(number);
                    }
                }
                break;
            case R.id.button_send_message:
                if (isDialpadShowing()) {
                    String number = mDialpadFragment.getDiapadNumber();
                    if (!TextUtils.isEmpty(number) && number.length() >= 3) {
                        ContactHelper.sendSMS(number);
                    }
                }
                break;
            case R.id.button_all_contacts:
                Intent intent = new Intent(this, AllContactActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialpadQueryChanged(String query) {
        if (TextUtils.isEmpty(query)) {
            exitSearchUi();
        } else {
            if (currentStatus == Status_Show_Frequent) {
                enterSearchUi();
            }
            mSearchFragment.onQueryChanged(query);
        }
    }

    @Override
    public void onListFragmentScrollStateChange(int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            hideDialpad(true, false);
        }
    }

}
