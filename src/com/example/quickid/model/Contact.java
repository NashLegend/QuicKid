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
	private List<String> abbreviationStrings = new ArrayList<String>();
	private List<ArrayList<String>> fullNameNumber = new ArrayList<ArrayList<String>>();
	private String name = "";
	private List<phoneStruct> phones = new ArrayList<Contact.phoneStruct>();
	private long contactId = 0L;
	private String lookupKey = "";
	private String photoUri;
	private Uri lookupUri;

	public int TIMES_CONTACTED = 0;
	public long LAST_TIME_CONTACTED = 0l;
	public int type;
	public String label;
	public String number;
	public String formattedNumber;
	public String normalizedNumber;
	public long photoId;

	public int sourceType = 0;

	private int matchLevel = 0;
	public static final int Match_Level_None = 0;
	public static final int Match_Level_Headless = 100;
	public static final int Match_Level_Back = 200;
	public static final int Match_Level_Front = 300;
	public static final int Match_Level_Complete = 400;
	public static final int Match_Score_Reward = 1;
	public static final float Match_Miss_Punish = 0.01f;
	public static final int Max_Reward_Times = 99;
	public static final int Max_Punish_Times = 99;

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
			for (Iterator<String> iterator = fullNamesString.iterator(); iterator
					.hasNext();) {
				String str = iterator.next();
				ArrayList<String> lss = new ArrayList<String>();
				String[] pinyins = TextUtil.splitIgnoringEmpty(str, " ");
				for (int i = 0; i < pinyins.length; i++) {
					String string = pinyins[i];
					String res = convertString2Number(string);
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
	public float match(String reg) {
		float degree = 0f;
		if ((degree = completeMatch(reg)) > 0) {

		} else if ((degree = foreAcronymCompleteMatch(reg)) > 0) {

		} else if ((degree = foreAcronymOverFlowMatch(reg)) > 0) {

		} else if ((degree = foreParagraphCompleteMatch(reg)) > 0) {

		} else if ((degree = backAcronymCompleteMatch(reg)) > 0) {

		} else if ((degree = backAcronymOverFlowMatch(reg)) > 0) {

		} else if ((degree = backParagraphCompleteMatch(reg)) > 0) {

		} else if ((degree = backHeadlessParagraphMatch(reg)) > 0) {

		} else {
			degree = 0;
		}
		return degree;
	}

	private float completeMatch(String reg) {
		for (Iterator<String> iterator = fullNamesString.iterator(); iterator
				.hasNext();) {
			String str = iterator.next();
			if (reg.equals(str)) {
				return Match_Level_Complete;
			}
		}
		for (Iterator<phoneStruct> iterator = phones.iterator(); iterator
				.hasNext();) {
			phoneStruct phone = iterator.next();
			if (reg.equals(phone.phoneNumber)) {
				return Match_Level_Complete;
			}
		}
		return 0;
	}

	private float foreAcronymCompleteMatch(String reg) {
		int punish = 10000;
		String matched = "";
		boolean hasMatch = false;
		for (Iterator<String> iterator = abbreviationStrings.iterator(); iterator
				.hasNext();) {
			String str = iterator.next();
			if (str.startsWith(reg)) {
				hasMatch = true;
				int diff = str.length() - reg.length();
				if (diff < punish) {
					punish = diff;
					matched = str;
				}
			}
		}
		if (hasMatch) {
			return Match_Level_Front + 2 - punish + Match_Miss_Punish;
		}
		return 0;
	}

	private float foreAcronymOverFlowMatch(String reg) {
		// TODO
		return 0;
	}

	/**
	 * @param reg
	 * @return
	 * 
	 * @deprecated
	 */
	private float foreParagraphCompleteMatch(String reg) {
		return 0;
	}

	private float backAcronymCompleteMatch(String reg) {
		int punish = 10000;
		String matched = "";
		boolean hasMatch = false;
		for (Iterator<String> iterator = abbreviationStrings.iterator(); iterator
				.hasNext();) {
			String str = iterator.next();
			// 在backAcronymCompleteMatch之前肯定先调用了foreAcronymCompleteMatch()
			// 而走到这一步说明str.startsWith(reg)是不可能的,所以直接contains()就可以了
			if (str.contains(reg)) {
				hasMatch = true;
				int diff = str.length() - reg.length();
				if (diff < punish) {
					punish = diff;
					matched = str;
				}
			}
		}
		if (hasMatch) {
			return Match_Level_Back + 2 - punish + Match_Miss_Punish;
		}
		return 0;
	}

	private float backAcronymOverFlowMatch(String reg) {
		return 0;
	}

	/**
	 * @param reg
	 * @return
	 * 
	 * @deprecated
	 */
	private float backParagraphCompleteMatch(String reg) {
		return 0;
	}

	private float backHeadlessParagraphMatch(String reg) {
		int score = 0;
		int punish = 0;
		String matched = "";
		for (Iterator<String> iterator = fullNamesString.iterator(); iterator
				.hasNext();) {
			String str = iterator.next();
			// 不可能等于0
			int sco = -1;
			if ((score = str.indexOf(reg)) > 0) {
				if (score < sco) {
					score = sco;
					matched = str;
				}
				punish = str.length() - reg.length();
			}
		}
		for (Iterator<phoneStruct> iterator = phones.iterator(); iterator
				.hasNext();) {
			phoneStruct phone = iterator.next();
			// 不可能等于0
			int sco = -1;
			if ((score = phone.phoneNumber.indexOf(reg)) > 0) {
				if (score < sco) {
					score = sco;
					matched = phone.phoneNumber;
				}
				punish = phone.phoneNumber.length() - reg.length();
			}
		}
		if (score > 0) {
			return Match_Level_Headless + score * Match_Score_Reward - punish
					* Match_Miss_Punish;
		}
		return 0;
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
