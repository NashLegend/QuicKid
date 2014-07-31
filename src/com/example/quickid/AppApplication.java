package com.example.quickid;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.legendutils.Tools.HanyuPinyinHelper;

import android.app.Application;

public class AppApplication extends Application {
	public static ArrayList<Contact> AllContacts = new ArrayList<Contact>();
	public static HanyuPinyinHelper hanyuPinyinHelper;
	public static Application globalApplication;
	public static HashMap<Character, String[]> keyMaps;
	public static ImageLoader imageLoader;

	public AppApplication() {

	}

	@Override
	public void onCreate() {
		super.onCreate();
		globalApplication = this;
		hanyuPinyinHelper = new HanyuPinyinHelper(this);
		keyMaps = new HashMap<Character, String[]>();
		keyMaps.put('0', new String[0]);
		keyMaps.put('1', new String[0]);
		keyMaps.put('2', new String[] { "a", "b", "c" });
		keyMaps.put('3', new String[] { "d", "e", "f" });
		keyMaps.put('4', new String[] { "g", "h", "i" });
		keyMaps.put('5', new String[] { "j", "k", "l" });
		keyMaps.put('6', new String[] { "m", "n", "o" });
		keyMaps.put('7', new String[] { "p", "q", "r", "s" });
		keyMaps.put('8', new String[] { "t", "u", "v" });
		keyMaps.put('9', new String[] { "w", "x", "y", "z" });
	}

}
