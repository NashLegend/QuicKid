package com.example.quickid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.net.Uri;

import com.example.legendutils.Tools.TextUtil;
import com.example.quickid.AppApplication;
import com.example.quickid.model.Contact.phoneStruct;

public class Contact {

	private List<String> fullNamesString = new ArrayList<String>();
	private List<ArrayList<String>> fullNameNumber = new ArrayList<ArrayList<String>>();
	private String name = "";
	private List<phoneStruct> phones = new ArrayList<Contact.phoneStruct>();
	private long contactId = 0L;
	private String lookupKey = "";
	private String photoUri;
	private Uri lookupUri;
	private List<String> possibleStrings = new ArrayList<String>();

	public int TIMES_CONTACTED = 0;
	public long LAST_TIME_CONTACTED = 0l;
	public int type;
	public String label;
	public String number;
	public String formattedNumber;
	public String normalizedNumber;
	public long photoId;

	public int sourceType = 0;

	public static class phoneStruct {
		public String phoneNumber;
		public int phoneType;
		public String displayType;

		public phoneStruct(String number, int type) {
			phoneNumber = number;
			phoneType = type;

		}
	}

	public Contact() {

	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Contact) {
			if (getLookupKey().equals(((Contact) o).getLookupKey())) {
				return true;
			}
		}
		return false;
	}

	public void initPinyin() {
		synchronized (AppApplication.globalApplication) {
			String trimmed = name.replaceAll(" ", "");
			fullNamesString = AppApplication.hanyuPinyinHelper
					.hanyuPinYinConvert(trimmed, false);
			System.out.println("*********************");
			for (Iterator<String> iterator = fullNamesString.iterator(); iterator
					.hasNext();) {
				String str = iterator.next();
				ArrayList<String> lss = new ArrayList<String>();
				String[] pinyins = TextUtil.splitIgnoringEmpty(str, " ");
				for (int i = 0; i < pinyins.length; i++) {
					String string = pinyins[i];
					String res = convertString2Number(string);
					System.out.println(res + " " + string);
					lss.add(res);
				}
				fullNameNumber.add(lss);
			}
		}
	}

	public String convertString2Number(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			Character ch = AppApplication.keyBoardMaps.get(str.charAt(i));
			if (ch != null) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	public int hasNumber() {
		return phones.size();
	}

	/**
	 * @param key
	 *            a String of 0-9
	 * @return
	 */
	public int match() {
		int degree = -1000;
		String matchedString = "";
		for (Iterator<String> iterator = possibleStrings.iterator(); iterator
				.hasNext();) {
			String possible = (String) iterator.next();
			for (Iterator<phoneStruct> iterator2 = phones.iterator(); iterator2
					.hasNext();) {
				String string2 = iterator2.next().phoneNumber;
				if (string2.contains(possible)) {
					int ind = -string2.indexOf(possible);
					if (degree < ind) {
						matchedString = string2;
						degree = ind;
					}
				}
			}
			if (!possible.contains("0") && !possible.contains("1")) {
				for (Iterator<String> iterator3 = fullNamesString.iterator(); iterator3
						.hasNext();) {
					String string3 = (String) iterator3.next();
					if (string3.contains(possible)) {
						int ind = -string3.indexOf(possible);
						if (degree < ind) {
							matchedString = string3;
							degree = ind;
						}
					}
				}
			}
		}
		if (degree != -1000) {
			System.out.println(matchedString + "_" + degree);
		}
		return degree;
	}

	public void setPossibleStrings(List<String> lss) {
		possibleStrings = lss;
	}

	public void addPhone(String number, int type) {
		phoneStruct pStruct = new phoneStruct(number, type);
		phones.add(pStruct);
	}

	public String getName() {
		return name;
	}

	public void setName(String displayName) {
		if (displayName == null) {
			displayName = "";
		}
		this.name = displayName;
		initPinyin();
	}

	public long getContactId() {
		return contactId;
	}

	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	public String getLookupKey() {
		if (lookupKey == null) {
			lookupKey = "";
		}
		return lookupKey;
	}

	public void setLookupKey(String lookupKey) {
		this.lookupKey = lookupKey;
	}

	public List<String> getFullNames() {
		return fullNamesString;
	}

	public List<phoneStruct> getPhones() {
		return phones;
	}

	public void setPhones(List<phoneStruct> phones) {
		this.phones = phones;
	}

	public String getPhotoUri() {
		return photoUri;
	}

	public void setPhotoUri(String photoUri) {
		this.photoUri = photoUri;
	}

	public Uri getLookupUri() {
		return lookupUri;
	}

	public void setLookupUri(Uri lookupUri) {
		this.lookupUri = lookupUri;
	}

}
