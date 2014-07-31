package com.example.quickid;

import java.util.ArrayList;
import java.util.Iterator;

public class Util {

	public static ArrayList<String> getPossibleKeys(String key) {
		ArrayList<String> list = new ArrayList<String>();
		if (key.length() > 0) {
			if (key.contains("1") || key.contains("0")) {
				list.add(key);
			} else {
				int keyLen = key.length();
				String[] words;
				if (keyLen == 1) {
					words = AppApplication.keyMaps.get(key.charAt(0));
					for (int i = 0; i < words.length; i++) {
						list.add(words[i]);
					}
				} else {
					ArrayList<String> sonList = getPossibleKeys(key.substring(
							0, key.length() - 1));
					words = AppApplication.keyMaps
							.get(key.charAt(key.length() - 1));
					for (int i = 0; i < words.length; i++) {
						for (Iterator<String> iterator = sonList.iterator(); iterator
								.hasNext();) {
							String sonStr = iterator.next();
							list.add(sonStr + words[i]);
						}
					}
				}
			}
		}
		return list;
	}

}
