package com.example.quickid.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.net.Uri;
import android.text.TextUtils;

import com.example.legendutils.Tools.TextUtil;
import com.example.quickid.AppApplication;

/**
 * 毫无疑问，现在的匹配算法是愚蠢和原始的
 * 
 * @author Pan
 *
 */
public class Contact {

	private List<String> fullNamesString = new ArrayList<String>();// String是带空格的
	private List<String> abbreviationStrings = new ArrayList<String>();
	private List<ArrayList<String>> fullNameNumber = new ArrayList<ArrayList<String>>();
	private List<String> fullNameNumberWithoutSpace = new ArrayList<String>();
	private List<String> abbreviationNumber = new ArrayList<String>();
	// 以上三个列表在绝大多数情况下长度为一
	private String name = "";
	private List<phoneStruct> phones = new ArrayList<Contact.phoneStruct>();
	private long contactId = 0L;
	private String lookupKey = "";
	private String photoUri;
	private Uri lookupUri;
	private boolean starred = false;

	public int TIMES_CONTACTED = 0;
	public long LAST_TIME_CONTACTED = 0l;
	public int type;
	public String label;
	public String number;
	public String formattedNumber;
	public String normalizedNumber;
	public long photoId;

	public int sourceType = 0;

	public static final int Match_Level_None = 0;
	public static final int Match_Level_Headless = 1000;
	public static final int Match_Level_Back_Acronym_Overflow = 2000;
	public static final int Match_Level_Fore_Acronym_Overflow = 3000;
	public static final int Match_Level_Complete = 4000;
	public static final int Match_Score_Reward = 1;
	public static final float Match_Miss_Punish = 0.001f;
	public static final int Max_Reward_Times = 999;
	public static final int Max_Punish_Times = 999;

	public static class phoneStruct {
		public String phoneNumber;
		public int phoneType;
		public String displayType;

		public phoneStruct(String number, int type) {
			number.replaceFirst("^\\+86", "");
			phoneNumber = number;
			phoneType = type;

		}
	}

	public float matchValue = 0f;

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
				String abbra = "";
				String fullNameNumberWithoutSpaceString = "";
				for (int i = 0; i < pinyins.length; i++) {
					String string = pinyins[i];
					String res = convertString2Number(string);
					abbra += res.charAt(0);
					fullNameNumberWithoutSpaceString += res;
					lss.add(res);
				}
				abbreviationNumber.add(abbra);
				fullNameNumberWithoutSpace
						.add(fullNameNumberWithoutSpaceString);
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
			} else {
				sb.append(str.charAt(i));
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
		// 无法通过第一个字母来判断是不是后置匹配
		// 但是可以通过第一个字母判断是不是前置匹配
		// match的原则是匹配尽可能多的字符
		// 事实上前五种匹配方式都可以使用crossMatch来实现
		float degree = 0f;
		if (!TextUtils.isEmpty(reg)) {
			if (canPrematch(reg)) {
				if ((degree = completeMatch(reg)) == 0f) {
					degree = foreAcronymOverFlowMatch(reg);
				}
			} else {
				if ((degree = backAcronymOverFlowMatch(reg)) == 0f) {
					degree = backHeadlessParagraphMatch(reg);
				}
			}
		}
		matchValue = degree;
		return degree;
	}

	/**
	 * 判断是否有可能前置匹配。返回true不意味着一定能够匹配，因为这里只检测第一个字母。
	 * 因为在大部分情况下，大多数联系人是不可能前置匹配的，在这样的情况下如果仍然先挨个检查四个前置匹配显然是不明智的
	 * 
	 * @return
	 */
	private boolean canPrematch(String reg) {
		char ch = reg.charAt(0);
		for (Iterator<String> iterator = abbreviationNumber.iterator(); iterator
				.hasNext();) {
			String string = (String) iterator.next();
			if (ch == string.charAt(0)) {
				return true;
			}
		}
		for (Iterator<phoneStruct> iterator = phones.iterator(); iterator
				.hasNext();) {
			phoneStruct phone = iterator.next();
			if (ch == phone.phoneNumber.charAt(0)) {
				return true;
			}
		}
		return false;
	}

	private float completeMatch(String reg) {
		for (Iterator<String> iterator = fullNameNumberWithoutSpace.iterator(); iterator
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
		return 0f;
	}

	private float foreAcronymOverFlowMatch(String reg) {
		// TODO
		float score = 0f;
		for (Iterator<ArrayList<String>> iterator = fullNameNumber.iterator(); iterator
				.hasNext();) {
			ArrayList<String> names = iterator.next();
			float tmp = foreAcronymOverFlowMatch(names, reg);
			if (tmp > score) {
				score = tmp;
			}
		}
		// 下面这一段可以不用添加，backHeadlessParagraphMatch将会做这件事
		// 因为很少有人会直接输入号码来查找人的，所以输入号码匹配的优先级应该低
		// 另外由于号码通常同01开头，而人名很少能够以01开头
		// 所以一旦输入了号码，通常情况下是直接进入backHeadlessParagraphMatch查找
		// 徒增笑尔
		for (Iterator<phoneStruct> iterator = phones.iterator(); iterator
				.hasNext();) {
			phoneStruct phone = iterator.next();
			if (phone.phoneNumber.startsWith(reg)) {
				float tmp = Match_Level_Fore_Acronym_Overflow + reg.length()
						* Match_Score_Reward
						- (phone.phoneNumber.length() - reg.length())
						* Match_Miss_Punish;
				if (tmp > score) {
					score = tmp;
				}
			}
		}
		return score;
	}

	// 在第一个字母确定的情况下，第二个字母有可能有三种情况
	// 一、在第一个字母所在单词的邻居位置charAt(x+1);
	// 二、在第二个单词的首字母处
	// 三、以上两种情况皆不符合，不匹配，出局

	private float foreAcronymOverFlowMatch(ArrayList<String> names, String reg) {
		// TODO
		if (names.get(0).charAt(0) == reg.charAt(0)) {
			int cross = crossWords(names, reg, 0, 0, 0).crossed;
			if (cross > 0) {
				return Match_Level_Fore_Acronym_Overflow + cross
						* Match_Score_Reward - (names.size() - cross)
						* Match_Miss_Punish;
			}
		}
		return 0;
	}

	/**
	 * 返回一串字符能跨越另一串字符的长度，若要保证能跨越最长的长度，只要保证下一个字符能跨越最长的长度即可，这就构成了一个递归
	 * 
	 * @param names
	 * @param regString
	 *            匹配字符串
	 * @param listIndex
	 *            匹配到的list的第M个单词
	 * @param strIndex
	 *            匹配到第M个单词中的第N个index
	 * @param regIndex
	 *            regchar的匹配位置
	 * @return
	 */
	private OverflowMatchValue crossWords(ArrayList<String> names,
			String regString, int listIndex, int strIndex, int regIndex) {
		OverflowMatchValue reser = new OverflowMatchValue(0, false);
		OverflowMatchValue impul = new OverflowMatchValue(0, false);
		if (regIndex < regString.length() - 1) {
			char nextChar = regString.charAt(regIndex + 1);
			if (listIndex < names.size() - 1
					&& nextChar == names.get(listIndex + 1).charAt(0)) {
				impul = crossWords(names, regString, listIndex + 1, 0,
						regIndex + 1);
			}
			if (strIndex < names.get(listIndex).length() - 1
					&& nextChar == names.get(listIndex).charAt(strIndex + 1)) {
				reser = crossWords(names, regString, listIndex, strIndex + 1,
						regIndex + 1);
			}
		} else {
			return new OverflowMatchValue((strIndex == 0) ? 1 : 0, true);
		}

		OverflowMatchValue result = new OverflowMatchValue(0, false);
		if (reser.matched || impul.matched) {
			result.matched = true;
			result.crossed = ((strIndex == 0) ? 1 : 0)
					+ Math.max(reser.crossed, impul.crossed);
		}
		return result;
	}

	static class OverflowMatchValue {
		public int crossed = 0;
		public boolean matched = false;

		public OverflowMatchValue(int c, boolean m) {
			this.crossed = c;
			this.matched = m;
		}
	}

	private float backAcronymOverFlowMatch(String reg) {
		float score = 0f;
		for (Iterator<ArrayList<String>> iterator = fullNameNumber.iterator(); iterator
				.hasNext();) {
			ArrayList<String> names = iterator.next();
			float tmp = backAcronymOverFlowMatch(names, reg);
			if (tmp > score) {
				score = tmp;
			}
		}
		return score;
	}

	private float backAcronymOverFlowMatch(ArrayList<String> names, String reg) {
		int score = 0;
		int punish = 0;
		for (int i = 0; i < names.size(); i++) {
			String string = (String) names.get(i);
			if (string.charAt(0) == reg.charAt(0)) {
				int cross = crossWords(names, reg, i, 0, 0).crossed;
				int lost = names.size() - cross;
				if (cross > score || cross == score && punish > lost) {
					score = cross;
					punish = lost;
				}
			}
		}
		if (score == 0) {
			return 0;
		}
		return Match_Level_Back_Acronym_Overflow + score * Match_Score_Reward
				- punish * Match_Miss_Punish;

	}

	private float backHeadlessParagraphMatch(String reg) {
		int score = 0;
		int punish = 0;
		String matched = "";
		for (Iterator<String> iterator = fullNameNumberWithoutSpace.iterator(); iterator
				.hasNext();) {
			String str = iterator.next();
			// 不可能等于0
			int sco = str.indexOf(reg);
			if (sco >= 0) {
				int lost = str.length() - reg.length();
				if (score < sco || sco == score && punish > lost) {
					score = sco;
					matched = str;
					punish = lost;
				}
			}
		}
		for (Iterator<phoneStruct> iterator = phones.iterator(); iterator
				.hasNext();) {
			phoneStruct phone = iterator.next();
			// 不可能等于0
			int sco = phone.phoneNumber.indexOf(reg);
			if (sco >= 0) {
				int lost = phone.phoneNumber.length() - reg.length();
				if (score < sco || sco == score && punish > lost) {
					score = sco;
					matched = phone.phoneNumber;
					punish = lost;
				}
			}
		}
		if (score > 0) {
			return Match_Level_Headless - score * Match_Score_Reward - punish
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

	public boolean isStarred() {
		return starred;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
	}

}
