
package net.nashlegend.quickid.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import net.nashlegend.quickid.AppApplication;
import net.nashlegend.quickid.model.Contact;
import net.nashlegend.quickid.model.Contact.PhoneStruct;
import net.nashlegend.quickid.model.Contact.PointPair;
import net.nashlegend.quickid.util.Consts;
import net.nashlegend.quickid.util.ContactHelper;
import net.nashlegend.quickid.util.IconContainer;

import net.nashlegend.quickid.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.provider.ContactsContract.Contacts;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class ContactView extends FrameLayout {

    private Contact contact;
    private QuickContactBadge badge;
    private TextView nameTextView;
    private TextView pinyinTextView;
    private TextView phoneTextView;
    private LinearLayout phoneLayout;
    private IconLoadTask task;
    public int Display_Mode = 0;
    private ImageButton smsButton;
    private LinearLayout phoneViews;
    public static final int Display_Mode_Recent = 1;
    public static final int Display_Mode_Search = 2;
    public static final int Display_Mode_Display = 3;

    public ContactView(Context context, int display) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_contact_item, this);
        badge = (QuickContactBadge) findViewById(R.id.badge_contact_item);
        smsButton = (ImageButton) findViewById(R.id.button_send_sms);
        nameTextView = (TextView) findViewById(R.id.text_contact_name);
        pinyinTextView = (TextView) findViewById(R.id.text_contact_pinyin);
        phoneTextView = (TextView) findViewById(R.id.text_contact_phone);
        phoneViews = (LinearLayout) findViewById(R.id.layout_more_phones);
        phoneLayout = (LinearLayout) findViewById(R.id.layout_phone_numbers);
        this.Display_Mode = display;
    }

    public ContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void build() {
        phoneViews.removeAllViews();
        boolean shouldDisplayMorePhones = true;
        phoneTextView.setText("");
        pinyinTextView.setText("");
        badge.assignContactUri(Contacts.getLookupUri(contact.getContactId(),
                contact.getLookupKey()));
        String nameString = contact.getName();
        String phoneString = "";
        if (contact.getPhones().size() > 0) {
            phoneString = contact.getPhones().get(0).phoneNumber;
        }
        nameTextView.setText(nameString);
        switch (Display_Mode) {
            case Display_Mode_Display:
                smsButton.setVisibility(View.GONE);
                phoneTextView.setVisibility(View.GONE);
                shouldDisplayMorePhones = false;
                break;
            case Display_Mode_Search:
                smsButton.setVisibility(View.VISIBLE);
                phoneTextView.setVisibility(View.VISIBLE);
                phoneTextView.setText(phoneString);
                if (contact.matchValue.nameIndex < 0
                        || contact.matchValue.nameIndex > contact.fullNamesString.size() - 1) {
                    break;
                }
                if (contact.matchValue.matchLevel == Contact.Level_Complete) {
                    if (contact.matchValue.matchType == Contact.Match_Type_Name) {
                        String str = contact.fullNamesString.get(
                                contact.matchValue.nameIndex).replaceAll(" ", "");
                        SpannableStringBuilder builder = new SpannableStringBuilder(
                                str);
                        ForegroundColorSpan redSpan = new ForegroundColorSpan(
                                Color.RED);
                        builder.setSpan(redSpan, 0, str.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        pinyinTextView.setText(builder);
                    } else {
                        shouldDisplayMorePhones = false;
                        String str = contact.getPhones().get(
                                contact.matchValue.nameIndex).phoneNumber;
                        SpannableStringBuilder builder = new SpannableStringBuilder(
                                str);
                        ForegroundColorSpan redSpan = new ForegroundColorSpan(
                                Color.RED);
                        builder.setSpan(redSpan, 0, str.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        phoneTextView.setText(builder);
                    }
                } else if (contact.matchValue.matchLevel == Contact.Level_Headless) {
                    shouldDisplayMorePhones = false;
                    String str = contact.getPhones().get(
                            contact.matchValue.nameIndex).phoneNumber;
                    SpannableStringBuilder builder = new SpannableStringBuilder(str);
                    ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
                    builder.setSpan(redSpan,
                            contact.matchValue.pairs.get(0).strIndex,
                            contact.matchValue.pairs.get(0).strIndex
                                    + contact.matchValue.reg.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    phoneTextView.setText(builder);
                } else {
                    String str = contact.fullNamesString.get(
                            contact.matchValue.nameIndex).replaceAll(" ", "");
                    ArrayList<PointPair> pa = getColoredString(
                            contact.fullNameNumber
                                    .get(contact.matchValue.nameIndex),
                            contact.matchValue.pairs, "#FF0000");
                    SpannableStringBuilder builder = new SpannableStringBuilder(str);
                    for (Iterator<PointPair> iterator = pa.iterator(); iterator
                            .hasNext();) {
                        PointPair pointPair = iterator.next();
                        builder.setSpan(new ForegroundColorSpan(Color.RED),
                                pointPair.listIndex, pointPair.strIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    pinyinTextView.setText(builder);
                }
                break;

            default:
                break;
        }
        if (shouldDisplayMorePhones) {
            for (int i = 1; i < contact.getPhones().size(); i++) {
                PhoneStruct phoneStruct = contact.getPhones().get(i);
                PhoneView phoneView = new PhoneView(getContext());
                phoneView.setPhone(phoneStruct);
                phoneViews.addView(phoneView);
            }
        }
        loadAvatar();

        smsButton.setOnClickListener(onClickListener);
        phoneLayout.setClickable(true);
        phoneLayout.setOnTouchListener(new OnShortLongClickListener());
    }

    private ArrayList<PointPair> getColoredString(ArrayList<String> strings,
            ArrayList<PointPair> pairs, String color) {
        int k = 0;
        int idx = -1;
        int crtHead = -1;
        int crtTail = -1;
        ArrayList<PointPair> ps = new ArrayList<PointPair>();
        for (int i = 0; i < strings.size(); i++) {
            String str = strings.get(i);
            for (int j = 0; j < str.length() && k < pairs.size(); j++) {
                idx++;
                if (pairs.get(k).listIndex == i && pairs.get(k).strIndex == j) {
                    if (crtHead == -1) {
                        crtHead = idx;
                        crtTail = idx + 1;
                    } else {
                        if (crtTail == idx) {
                            crtTail = idx + 1;
                        }
                    }
                    k++;
                } else {
                    if (crtHead != -1) {
                        ps.add(new PointPair(crtHead, crtTail));
                        crtHead = -1;
                        crtTail = -1;
                    }
                }
            }
        }
        if (crtHead != -1) {
            ps.add(new PointPair(crtHead, crtTail));
            crtHead = -1;
            crtTail = -1;
        }
        return ps;
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
            "拨打电话", "发送短信", "删除联系人", "查看联系人"
    };
    static final int MAKE_PHONE_CALL = 0;
    static final int SEND_SMS = 1;
    static final int DELETE_CONTACT = 2;
    static final int SEE_CONTACT = 3;

    private void onLongClick() {
        new AlertDialog.Builder(getContext()).setTitle("")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case MAKE_PHONE_CALL:
                                if (contact.getPhones().size() > 0) {
                                    ContactHelper
                                            .makePhoneCall(contact.getPhones().get(0).phoneNumber);
                                }
                                break;
                            case SEND_SMS:
                                if (contact.getPhones().size() > 0) {
                                    ContactHelper
                                            .sendSMS(contact.getPhones().get(0).phoneNumber);
                                }
                                break;
                            case DELETE_CONTACT:
                                if (ContactHelper.deleteContactsByID(contact.getContactId()) > 0) {
                                    Intent intent = new Intent();
                                    if (Display_Mode == Display_Mode_Display) {
                                        intent.setAction(Consts.Action_Delete_One_Contact_From_All);
                                    } else if (Display_Mode == Display_Mode_Search) {
                                        intent.setAction(Consts.Action_Delete_One_Contact_From_Search);
                                    }
                                    intent.putExtra(Consts.Extra_Contact_ID,
                                            contact.getContactId());
                                    getContext().sendBroadcast(intent);
                                }
                                break;
                            case SEE_CONTACT:
                                ContactHelper.openContactDetail(contact.getContactId());
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
                    if (contact.getPhones().size() > 0) {
                        ContactHelper
                                .sendSMS(contact.getPhones().get(0).phoneNumber);
                    }
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
                        if (Display_Mode == Display_Mode_Display) {
                            ContactHelper.openContactDetail(contact.getContactId());
                        } else if (Display_Mode == Display_Mode_Search) {
                            ContactHelper.makePhoneCall(contact.getPhones().get(0).phoneNumber);
                        }
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
