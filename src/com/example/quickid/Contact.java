package com.example.quickid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.example.quickid.Contact.phoneStruct;

public class Contact {

	private List<String> abbreviations = new ArrayList<String>();
	private List<String> fullNames = new ArrayList<String>();
	private String displayName = "";
	private List<phoneStruct> phones = new ArrayList<Contact.phoneStruct>();
	private long contactId = 0L;
	private String lookupKey = "";
	private String photoUri = "";
	private List<String> possibleStrings = new ArrayList<String>();

	public static class phoneStruct {
		String phoneNumber;
		int phoneType;
		String displayType;

		public phoneStruct(String number, int type) {
			phoneNumber = number;
			phoneType = type;

		}
	}

	public Contact() {

	}

	public void initPinyin() {
		synchronized (AppApplication.globalApplication) {
			String trimmed = displayName.replaceAll(" ", "");
			abbreviations = AppApplication.hanyuPinyinHelper
					.hanyuPinYinConvert(trimmed, true);
			fullNames = AppApplication.hanyuPinyinHelper.hanyuPinYinConvert(
					trimmed, false);
		}
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
				for (Iterator<String> iterator3 = fullNames.iterator(); iterator3
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		if (displayName == null) {
			displayName = "";
		}
		this.displayName = displayName;
		initPinyin();
	}

	public long getContactId() {
		return contactId;
	}

	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	public String getLookupKey() {
		return lookupKey;
	}

	public void setLookupKey(String lookupKey) {
		this.lookupKey = lookupKey;
	}

	public List<String> getAbbreviations() {
		return abbreviations;
	}

	public void setAbbreviations(List<String> abbreviations) {
		this.abbreviations = abbreviations;
	}

	public List<String> getFullNames() {
		return fullNames;
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

}
