package com.example.quickid.util;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import com.example.quickid.model.Contact;

import android.graphics.Bitmap;

public class IconContainer {
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> mCachedIcons = new ConcurrentHashMap<String, SoftReference<Bitmap>>();

	public static Bitmap get(String path) {
		Bitmap bm = null;
		if (mCachedIcons.containsKey(path)) {
			bm = mCachedIcons.get(path).get();
		}
		return bm;
	}

	public static Bitmap get(Contact contact) {
		return get(contact.getPhotoUri());
	}

	public static void put(String path, Bitmap bm) {
		SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bm);
		mCachedIcons.put(path, soft);
	}

	public static void put(Contact contact, Bitmap bm) {
		put(contact.getPhotoUri(), bm);
	}
}
