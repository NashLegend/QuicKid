package com.example.quickid.view;

import com.example.legendutils.Tools.TextUtil;
import com.example.quickid.R;
import com.example.quickid.model.Contact.PhoneStruct;
import com.example.quickid.util.ContactHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhoneView extends FrameLayout {

	TextView numberText;
	LinearLayout layoutRoot;
	ImageButton smsButton;
	PhoneStruct phone;
	boolean matchNumber = false;
	String matchedNumber = "";

	public PhoneView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_phone_view, this);
		smsButton = (ImageButton) findViewById(R.id.button_send_sms);
		layoutRoot = (LinearLayout) findViewById(R.id.layout_phone);
		numberText = (TextView) findViewById(R.id.textview_phone_numbers);
		smsButton.setOnClickListener(onClickListener);
		layoutRoot.setClickable(true);
		layoutRoot.setOnTouchListener(new OnShortLongClickListener());
	}

	public void setPhone(PhoneStruct p) {
		this.phone = p;
		numberText.setText(phone.phoneNumber);
	}

	public void setPhone(PhoneStruct p, String mStr) {
		this.phone = p;
		numberText.setText(phone.phoneNumber);
		String str = phone.phoneNumber;
		System.out.println(str+","+mStr);
		if (!TextUtils.isEmpty(mStr)) {
			int idx = str.indexOf(mStr);
			if (idx >= 0) {
				SpannableStringBuilder builder = new SpannableStringBuilder(str);
				ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
				builder.setSpan(redSpan, idx, idx + mStr.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				numberText.setText(builder);
			}
		}

	}

	private void onLongClick() {
		// do nothing
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_send_sms:
				ContactHelper.sendSMS(numberText.getText().toString());
				break;
			default:
				break;
			}
		}
	};

	class OnShortLongClickListener implements OnTouchListener {
		long longDura = 1000L;
		long shortDura = 300L;
		long startTime = 0L;
		Handler handler = new Handler();
		Runnable longPressRunnable = new Runnable() {
			public void run() {
				onLongClick();
			}
		};

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startTime = System.currentTimeMillis();
				handler.removeCallbacks(longPressRunnable);
				handler.postDelayed(longPressRunnable, longDura);
				break;
			case MotionEvent.ACTION_UP:
				handler.removeCallbacks(longPressRunnable);
				if (System.currentTimeMillis() - startTime < shortDura) {
					ContactHelper
							.makePhoneCall(numberText.getText().toString());
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				handler.removeCallbacks(longPressRunnable);
				break;
			default:
				break;
			}
			return false;
		}
	}

}
