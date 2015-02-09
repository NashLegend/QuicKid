
package net.nashlegend.quickid.view;

import net.nashlegend.quickid.model.Contact.PhoneStruct;
import net.nashlegend.quickid.util.ContactHelper;

import net.nashlegend.quickid.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
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
                        ContactHelper.makePhoneCall(numberText.getText().toString());
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
