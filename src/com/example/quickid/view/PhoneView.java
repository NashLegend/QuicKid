package com.example.quickid.view;

import com.example.quickid.R;
import com.example.quickid.model.Contact.PhoneStruct;
import com.example.quickid.util.ContactHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhoneView extends FrameLayout {

	TextView numberText;
	LinearLayout layoutRoot;
	ImageButton smsButton;
	PhoneStruct phone;

	public PhoneView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_phone_view, this);
		smsButton = (ImageButton) findViewById(R.id.button_send_sms);
		layoutRoot = (LinearLayout) findViewById(R.id.layout_phone);
		numberText = (TextView) findViewById(R.id.textview_phone_numbers);
		smsButton.setOnClickListener(onClickListener);
		layoutRoot.setOnClickListener(onClickListener);
	}

	public void setPhone(PhoneStruct p) {
		this.phone = p;
		numberText.setText(phone.phoneNumber);
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_send_sms:
				ContactHelper.sendSMS(numberText.getText().toString());
				break;
			case R.id.layout_phone:
				ContactHelper.makePhoneCall(numberText.getText().toString());
				break;
			default:
				break;
			}
		}
	};

}
