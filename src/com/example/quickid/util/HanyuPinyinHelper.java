package com.example.quickid.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.example.legendutils.R;

/**
 * 汉语转拼音Helper类
 */
@SuppressLint("DefaultLocale")
public class HanyuPinyinHelper {

	private StringBuffer buffer = new StringBuffer();
	private List<String> list = new ArrayList<String>();
	private Properties allPinyin = new Properties();
	private boolean isSimple = false;

	public HanyuPinyinHelper(Context context) {
		init(context);
	}

	public void init(Context context) {
		try {
			TypedValue typedValue = new TypedValue();
			allPinyin.load(context.getResources().openRawResource(
					R.raw.hanyu_pinyin, typedValue));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] getHanyuPinyins(char c) {
		int codePointOfChar = c;
		String codepointHexStr = Integer.toHexString(codePointOfChar)
				.toUpperCase();
		String str = (String) allPinyin.get(codepointHexStr);
		String[] strs = str.split(",");
		for (int i = 0; i < strs.length; i++) {
			strs[i] = UpperFirstLetter(strs[i]);
		}
		return strs;
	}

	public String UpperFirstLetter(String str) {
		if (str.length() > 1) {
			return String.valueOf(str.charAt(0)).toUpperCase()
					+ str.substring(1);
		} else {
			return str.toUpperCase();
		}
	}

	/**
	 * @param str
	 *            要转换的字符
	 * @param isSimple
	 *            只输出首字母(true)或者全部输出(false)
	 * @return 拼音列表
	 */
	public List<String> hanyuPinYinConvert(String str, boolean isSimple) {
		if (str == null || "".equals(str))
			return null;
		this.isSimple = isSimple;
		list = new ArrayList<String>();
		buffer.delete(0, buffer.length());
		convert(0, str);
		return list;
	}

	/**
	 * @param str
	 *            要转换的字符
	 * @param isSimple
	 *            只输出首字母(true)或者全部输出(false)
	 * @return 拼音列表
	 */
	public List<String> convertString2PinyinWithSpace(String str,
			boolean isSimple) {
		if (str == null || "".equals(str))
			return null;
		this.isSimple = isSimple;
		list = new ArrayList<String>();
		buffer.delete(0, buffer.length());
		convert(0, str);
		return list;
	}

	/**
	 * @param str
	 *            要转换的字符
	 * @return 拼音列表
	 */
	public List<String> hanyuPinYinConvert(String str) {
		if (str == null || "".equals(str))
			return null;
		list = new ArrayList<String>();
		buffer.delete(0, buffer.length());
		this.isSimple = true;
		convert(0, str);
		buffer.delete(0, buffer.length());
		this.isSimple = false;
		convert(0, str);
		return list;
	}

	private void convert(int n, String str) {
		if (n == str.length()) {
			String temp = buffer.toString();
			if (!list.contains(temp)) {
				list.add(buffer.toString());
			}
			return;
		} else {
			char c = str.charAt(n);
			if (0x3007 == c || (0x4E00 <= c && c <= 0x9FA5)) {
				String[] arrayStrings = getHanyuPinyins(c);
				if (arrayStrings == null) {
					buffer.append(c);
					convert(n + 1, str);
				} else if (arrayStrings.length == 0) {
					buffer.append(c);
					convert(n + 1, str);
				} else if (arrayStrings.length == 1) {
					if (isSimple) {
						if (!"".equals(arrayStrings[0])) {
							buffer.append(arrayStrings[0].charAt(0) + " ");
						}
					} else {
						buffer.append(arrayStrings[0] + " ");
					}
					convert(n + 1, str);
				} else {
					int len;
					for (int i = 0; i < arrayStrings.length; i++) {
						len = buffer.length();
						if (isSimple) {
							if (!"".equals(arrayStrings[i])) {
								buffer.append(arrayStrings[i].charAt(0) + " ");
							}
						} else {
							buffer.append(arrayStrings[i] + " ");
						}
						convert(n + 1, str);
						buffer.delete(len, buffer.length());
					}
				}
			} else {
				buffer.append(c);
				convert(n + 1, str);
			}
		}
	}

}
