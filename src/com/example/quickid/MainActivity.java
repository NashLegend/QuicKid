package com.example.quickid;

import com.example.quickid.DialpadFragment.OnDialpadQueryChangedListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener,
		OnDialpadQueryChangedListener {

	private DialpadFragment mDialpadFragment;
	private ImageButton mDialpadButton;
	private ImageButton mDialButton;
	private static final String TAG_DIALPAD_FRAGMENT = "dialpad";
	private static final String TAG_RECENT_DIAL_FRAGMENT = "recent";
	private static final String TAG_SEARCH_FRAGMENT = "search";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.add(R.id.layout_dial, new RecentDialFragment(),
							TAG_RECENT_DIAL_FRAGMENT)
					.add(R.id.layout_dialer_panel, new DialpadFragment(),
							TAG_DIALPAD_FRAGMENT).commit();
		}

		mDialpadButton = (ImageButton) findViewById(R.id.button_dialpad);
		mDialButton = (ImageButton) findViewById(R.id.button_dial);
		mDialButton.setOnClickListener(this);
		mDialpadButton.setOnClickListener(this);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		if (fragment instanceof DialpadFragment) {
			mDialpadFragment = (DialpadFragment) fragment;
			final FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			transaction.hide(mDialpadFragment);
			transaction.commit();
		}
		super.onAttachFragment(fragment);
	}

	private boolean isDialpadShowing() {
		return mDialpadFragment != null && mDialpadFragment.isVisible();
	}

	private void enterSearchUi() {
		// TODO
	}

	public void exitSearchUi() {
		// TODO
	}

	public void showDialpad(boolean animate) {
		// TODO 有一个bug，就是在hideDialpad动画没有完成之前就执行showDialpad的话，会导致永久hide
		// 这个bug在系统的拨号App中同样存在
		mDialpadFragment.setAdjustTranslationForAnimation(animate);
		mDialpadButton.setVisibility(View.GONE);
		mDialButton.setVisibility(View.VISIBLE);
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
		moveTaskToBack(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_dialpad:
			showDialpad(true);
			break;
		case R.id.button_dial:
			hideDialpad(true, false);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDialpadQueryChanged(String query) {

	}
}
