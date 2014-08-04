package com.example.quickid;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.legendutils.Tools.HanyuPinyinHelper;

import android.app.Application;
import android.content.Intent;

public class AppApplication extends Application {
	public static ArrayList<Contact> AllContacts = new ArrayList<Contact>();
	public static ArrayList<RecentContact> AllRecentContacts = new ArrayList<RecentContact>();
	public static HanyuPinyinHelper hanyuPinyinHelper;
	public static Application globalApplication;
	public static HashMap<Character, String[]> keyMaps;
	public static HashMap<Character, Integer> keyBoardMaps;
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

		keyBoardMaps = new HashMap<Character, Integer>();
		keyBoardMaps.put('a', 1);
		keyBoardMaps.put('b', 1);
		keyBoardMaps.put('c', 1);
		keyBoardMaps.put('d', 2);
		keyBoardMaps.put('e', 2);
		keyBoardMaps.put('f', 2);
		keyBoardMaps.put('g', 3);
		keyBoardMaps.put('h', 3);
		keyBoardMaps.put('i', 3);
		keyBoardMaps.put('j', 4);
		keyBoardMaps.put('k', 4);
		keyBoardMaps.put('l', 4);
		keyBoardMaps.put('m', 5);
		keyBoardMaps.put('n', 5);
		keyBoardMaps.put('o', 5);
		keyBoardMaps.put('p', 6);
		keyBoardMaps.put('q', 6);
		keyBoardMaps.put('r', 6);
		keyBoardMaps.put('s', 6);
		keyBoardMaps.put('t', 7);
		keyBoardMaps.put('u', 7);
		keyBoardMaps.put('v', 7);
		keyBoardMaps.put('w', 8);
		keyBoardMaps.put('x', 8);
		keyBoardMaps.put('y', 8);
		keyBoardMaps.put('z', 8);

		Intent intent = new Intent(this, ContactService.class);
		startService(intent);
	}

}
