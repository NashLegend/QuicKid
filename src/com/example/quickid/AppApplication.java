package com.example.quickid;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.quickid.model.Contact;
import com.example.quickid.model.RecentContact;
import com.example.quickid.service.ContactService;
import com.example.quickid.util.ContactHelper;
import com.example.quickid.util.HanyuPinyinHelper;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;

public class AppApplication extends Application {
	public static ArrayList<Contact> AllContacts = new ArrayList<Contact>();
	public static ArrayList<RecentContact> AllRecentContacts = new ArrayList<RecentContact>();
	public static ArrayList<Contact> StrequentContacts = new ArrayList<Contact>();
	public static HanyuPinyinHelper hanyuPinyinHelper;
	public static Application globalApplication;
	public static ContentResolver contentResolver;
	public static HashMap<Character, String[]> keyMaps;
	public static HashMap<Character, Character> keyBoardMaps;

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

		keyBoardMaps = new HashMap<Character, Character>();
		keyBoardMaps.put('a', '2');
		keyBoardMaps.put('b', '2');
		keyBoardMaps.put('c', '2');
		keyBoardMaps.put('d', '3');
		keyBoardMaps.put('e', '3');
		keyBoardMaps.put('f', '3');
		keyBoardMaps.put('g', '4');
		keyBoardMaps.put('h', '4');
		keyBoardMaps.put('i', '4');
		keyBoardMaps.put('j', '5');
		keyBoardMaps.put('k', '5');
		keyBoardMaps.put('l', '5');
		keyBoardMaps.put('m', '6');
		keyBoardMaps.put('n', '6');
		keyBoardMaps.put('o', '6');
		keyBoardMaps.put('p', '7');
		keyBoardMaps.put('q', '7');
		keyBoardMaps.put('r', '7');
		keyBoardMaps.put('s', '7');
		keyBoardMaps.put('t', '8');
		keyBoardMaps.put('u', '8');
		keyBoardMaps.put('v', '8');
		keyBoardMaps.put('w', '9');
		keyBoardMaps.put('x', '9');
		keyBoardMaps.put('y', '9');
		keyBoardMaps.put('z', '9');

		keyBoardMaps.put('A', '2');
		keyBoardMaps.put('B', '2');
		keyBoardMaps.put('C', '2');
		keyBoardMaps.put('D', '3');
		keyBoardMaps.put('E', '3');
		keyBoardMaps.put('F', '3');
		keyBoardMaps.put('G', '4');
		keyBoardMaps.put('H', '4');
		keyBoardMaps.put('I', '4');
		keyBoardMaps.put('J', '5');
		keyBoardMaps.put('K', '5');
		keyBoardMaps.put('L', '5');
		keyBoardMaps.put('M', '6');
		keyBoardMaps.put('N', '6');
		keyBoardMaps.put('O', '6');
		keyBoardMaps.put('P', '7');
		keyBoardMaps.put('Q', '7');
		keyBoardMaps.put('R', '7');
		keyBoardMaps.put('S', '7');
		keyBoardMaps.put('T', '8');
		keyBoardMaps.put('U', '8');
		keyBoardMaps.put('V', '8');
		keyBoardMaps.put('W', '9');
		keyBoardMaps.put('X', '9');
		keyBoardMaps.put('Y', '9');
		keyBoardMaps.put('Z', '9');

		Intent intent = new Intent(this, ContactService.class);
		startService(intent);

		ContactHelper.loadContacts();
	}

	public static ContentResolver getApplicationContentResolver() {
		if (contentResolver == null) {
			contentResolver = AppApplication.globalApplication
					.getContentResolver();
		}
		return contentResolver;
	}

}
