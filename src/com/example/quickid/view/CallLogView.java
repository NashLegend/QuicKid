
package com.example.quickid.view;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import com.example.quickid.AppApplication;
import com.example.quickid.R;
import com.example.quickid.model.Contact;
import com.example.quickid.model.Contact.PointPair;
import com.example.quickid.util.ContactHelper;
import com.example.quickid.util.IconContainer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.AsyncTask.Status;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.Contacts;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class CallLogView extends FrameLayout {

    private Contact contact;
    private QuickContactBadge badge;
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView dateTextView;
    private IconLoadTask task;
    private ImageView typeImageView;
    private ImageButton smsButton;
    private View number_layoutView;

    public CallLogView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_calllog_item, this);
        badge = (QuickContactBadge) findViewById(R.id.badge_contact_item);
        nameTextView = (TextView) findViewById(R.id.text_contact_name);
        phoneTextView = (TextView) findViewById(R.id.text_contact_phone);
        typeImageView = (ImageView) findViewById(R.id.imageview_call_type);
        dateTextView = (TextView) findViewById(R.id.textview_call_date);
        smsButton = (ImageButton) findViewById(R.id.button_send_sms);
        number_layoutView = findViewById(R.id.layout_phone_numbers);
        smsButton.setOnClickListener(onClickListener);
        number_layoutView.setClickable(true);
        number_layoutView.setOnTouchListener(new OnShortLongClickListener());
    }

    public CallLogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CallLogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void build() {
        switch (contact.Last_Contact_Call_Type) {
            case Calls.OUTGOING_TYPE:
                typeImageView
                        .setImageResource(R.drawable.ic_call_outgoing_holo_dark);
                break;
            case Calls.INCOMING_TYPE:
                typeImageView
                        .setImageResource(R.drawable.ic_call_incoming_holo_dark);
                break;
            case Calls.MISSED_TYPE:
                typeImageView.setImageResource(R.drawable.ic_call_missed_holo_dark);
                break;

            default:
                break;
        }
        dateTextView.setText(getDateDescription(contact.Last_Time_Contacted));
        String nameString = contact.getName();
        nameTextView.setText(nameString);
        if (TextUtils.isEmpty(contact.getName())) {
            nameTextView.setText(contact.Last_Contact_Number);
            phoneTextView.setText("");
        } else {
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.Last_Contact_Number);
        }
        if (!TextUtils.isEmpty(contact.getLookupKey())) {
            badge.assignContactUri(Contacts.getLookupUri(
                    contact.getContactId(), contact.getLookupKey()));
        }
        loadAvatar();
    }

    private String getDateDescription(long mills) {
        String dateString = "";
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(mills);

        Calendar date2 = Calendar.getInstance();
        date2.setTimeInMillis(System.currentTimeMillis());

        DateFormat format;

        if (mills / 86400000 != System.currentTimeMillis() / 86400000) {
            if (date.get(Calendar.YEAR) == date2.get(Calendar.YEAR)) {
                format = new SimpleDateFormat("M月d日 hh:mm");
                dateString = format.format(date.getTime());
            } else {
                format = new SimpleDateFormat("yyyy年M月d日 hh:mm");
                dateString = format.format(date.getTime());
            }
        } else {
            long diff = System.currentTimeMillis() - mills;
            if (diff > 1800000) {
                format = new SimpleDateFormat("hh:mm");
                dateString = format.format(date.getTime());
            } else if (diff >= 60000) {
                dateString = (diff / 60000) + "分钟前";
            } else {
                dateString = (diff / 1000) + "秒前";
            }
        }
        return dateString;
    }

    private void loadAvatar() {
        badge.setImageResource(R.drawable.ic_contact_picture_holo_light);
        if (!TextUtils.isEmpty(contact.getPhotoUri())) {
            if (task != null && task.getStatus() == Status.RUNNING) {
                task.cancel(true);
            }
            Bitmap bmp = IconContainer.get(contact);
            if (bmp == null) {
                task = new IconLoadTask();
                task.execute(contact);
            } else {
                badge.setImageBitmap(bmp);
            }
        } else {
            setDefaultAvatar();
        }
    }

    private static TypedArray sColors;
    private static int sDefaultColor;
    private static final int NUM_OF_TILE_COLORS = 8;

    @SuppressLint("Recycle")
    private void setDefaultAvatar() {
        if (sColors == null) {
            sColors = getResources().obtainTypedArray(
                    R.array.letter_tile_colors);
            sDefaultColor = getResources().getColor(
                    R.color.letter_tile_default_color);
        }
        badge.setBackgroundColor(pickColor(contact.getName()));
        badge.setImageResource(R.drawable.ic_list_item_avatar);
    }

    private int pickColor(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return sDefaultColor;
        }
        final int color = Math.abs(identifier.hashCode()) % NUM_OF_TILE_COLORS;
        return sColors.getColor(color, sDefaultColor);
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }

    class IconLoadTask extends AsyncTask<Contact, Integer, Bitmap> {

        Contact originalContact;

        @Override
        protected Bitmap doInBackground(Contact... params) {
            originalContact = params[0];
            Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
                    contact.getContactId());
            InputStream input = Contacts.openContactPhotoInputStream(
                    AppApplication.getApplicationContentResolver(), uri);
            Bitmap bmp = BitmapFactory.decodeStream(input);
            if (bmp != null) {
                IconContainer.put(originalContact, bmp);
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (contact.equals(this.originalContact)) {
                if (result != null) {
                    badge.setImageBitmap(result);
                } else {
                    badge.setImageResource(R.drawable.ic_list_item_avatar);
                }
            }
            super.onPostExecute(result);
        }
    }

    CharSequence[] items = {
            "Make a phone call", "Send SMS", "Delete log", "See contact", "Delete all"
    };
    static final int MAKE_PHONE_CALL = 0;
    static final int SEND_SMS = 1;
    static final int DELETE_LOG = 2;
    static final int SEE_CONTACT = 3;
    static final int DELETE_ALL = 4;

    private void onLongClick() {
        // TODO
        new AlertDialog.Builder(getContext()).setTitle("")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case MAKE_PHONE_CALL:
                                ContactHelper.makePhoneCall(contact.Last_Contact_Number);
                                break;
                            case SEND_SMS:
                                ContactHelper.sendSMS(contact.Last_Contact_Number);
                                break;
                            case DELETE_LOG:
                                ContactHelper.deleteCallLogByNumber(contact.Last_Contact_Number);
                                break;
                            case SEE_CONTACT:
                                if (contact.getContactId() > 1L) {
                                    ContactHelper.openContactDetail(contact.getContactId());
                                }
                                break;
                            case DELETE_ALL:
                                ContactHelper.clearCallLogs();
                                break;
                            default:
                                break;
                        }
                    }
                }).create().show();
    }

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_send_sms:
                    ContactHelper.sendSMS(contact.Last_Contact_Number);
                    break;
                default:
                    break;
            }
        }
    };

    class OnShortLongClickListener implements OnTouchListener {
        long longDura = 800L;
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
                        ContactHelper.makePhoneCall(contact.Last_Contact_Number);
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
