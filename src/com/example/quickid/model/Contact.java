
package com.example.quickid.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.text.TextUtils;
import com.example.legendutils.Tools.TextUtil;
import com.example.quickid.AppApplication;
import com.example.quickid.model.Contact.PhoneStruct;

/**
 * 毫无疑问，现在的匹配算法是愚蠢和原始的
 * 
 * @author Pan
 */
@SuppressLint("DefaultLocale")
public class Contact {

    public List<String> fullNamesString = new ArrayList<String>();// String是带空格的
    public List<ArrayList<String>> fullNameNumber = new ArrayList<ArrayList<String>>();
    public List<String> fullNameNumberWithoutSpace = new ArrayList<String>();
    public List<String> abbreviationNumber = new ArrayList<String>();
    // 以上三个列表在绝大多数情况下长度为一
    private String name = "";
    private ArrayList<PhoneStruct> phones = new ArrayList<Contact.PhoneStruct>();
    private long contactId = -1L;
    private String lookupKey = "";
    private String photoUri;
    private Uri lookupUri;
    private boolean starred = false;

    public int Last_Contact_Call_Type = 0;
    public String Last_Contact_Number = "";
    public int Last_Contact_Phone_Type = 0;
    public long Last_Contact_Call_ID = 0;
    public int Last_Contact_Duration = 0;
    public int Times_Contacted = 0;
    public long Last_Time_Contacted = 0l;
    public int type;
    public String label;
    public String number;
    public String formattedNumber;
    public String normalizedNumber;
    public long photoId;

    public String indexer = "";

    public int sourceType = 0;

    public static final int Match_Type_Name = 1;
    public static final int Match_Type_Phone = 2;

    public static final int Level_Complete = 4;
    public static final int Level_Fore_Acronym_Overflow = 3;
    public static final int Level_Back_Acronym_Overflow = 2;
    public static final int Level_Headless = 1;
    public static final int Level_None = 0;

    public static final float Match_Level_None = 0;
    public static final float Match_Level_Headless = 1000;
    public static final float Match_Level_Back_Acronym_Overflow = 2000;
    public static final float Match_Level_Fore_Acronym_Overflow = 3000;
    public static final float Match_Level_Complete = 4000;
    public static final float Match_Score_Reward = 1;
    public static final float Match_Miss_Punish = 0.001f;
    public static final int Max_Reward_Times = 999;
    public static final int Max_Punish_Times = 999;

    public static class PhoneStruct {
        public String phoneNumber;
        public int phoneType;
        public String displayType;

        public PhoneStruct(String number, int type) {
            phoneNumber = number.replaceAll("^\\+86", "").replaceAll("[^\\d]+",
                    "");
            phoneType = type;
        }
    }

    static class OverflowMatchValue {
        public int crossed = 0;
        public boolean matched = false;
        public ArrayList<PointPair> pairs = new ArrayList<PointPair>();

        public OverflowMatchValue(int c, boolean m) {
            this.crossed = c;
            this.matched = m;
        }
    }

    public static class PointPair {
        public int listIndex;
        public int strIndex;

        public PointPair(int listIndex, int strIndex) {
            this.listIndex = listIndex;
            this.strIndex = strIndex;
        }
    }

    public ScoreAndHits matchValue = new ScoreAndHits(-1, 0f,
            new ArrayList<PointPair>());

    public Contact() {

    }

    public Contact clone() {
        Contact contact = new Contact();
        contact.setContactId(contactId);
        contact.setName(name);
        contact.setLookupKey(lookupKey);
        contact.setPhotoUri(photoUri);
        contact.setLookupUri(lookupUri);
        contact.setStarred(starred);
        for (Iterator<PhoneStruct> iterator = phones.iterator(); iterator
                .hasNext();) {
            PhoneStruct phone = iterator.next();
            contact.addPhone(phone.phoneNumber, phone.phoneType);
        }
        return contact;
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

    private void setIndexer(char ch) {
        if (TextUtil.isEnglishCharactor(ch)) {
            indexer = String.valueOf(ch).toUpperCase();
        } else {
            indexer = "#";
        }
    }

    public void initPinyin() {
        synchronized (AppApplication.globalApplication) {
            String trimmed = name.replaceAll(" ", "");
            fullNamesString = AppApplication.hanyuPinyinHelper
                    .hanyuPinYinConvert(trimmed, false);
            setIndexer(fullNamesString.get(0).charAt(0));
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
     * 单个联系人能匹配多个号码时，只显示一个，因为不需要
     * 
     * @param key a String of 0-9
     * @return
     */
    public float match(String reg) {
        // 无法通过第一个字母来判断是不是后置匹配
        // 但是可以通过第一个字母判断是不是前置匹配
        // match的原则是匹配尽可能多的字符
        // 事实上前五种匹配方式都可以使用crossMatch来实现
        ScoreAndHits scoreAndHits = new ScoreAndHits(-1, 0f,
                new ArrayList<PointPair>());
        if (!TextUtils.isEmpty(reg)) {
            if (canPrematch(reg)) {
                if ((scoreAndHits = completeMatch(reg)).score == 0f) {
                    scoreAndHits = foreAcronymOverFlowMatch(reg);
                }
            } else {
                if ((scoreAndHits = backAcronymOverFlowMatch(reg)).score == 0f) {
                    scoreAndHits = backHeadlessParagraphMatch(reg);
                }
            }
        }
        scoreAndHits.reg = reg;
        matchValue = scoreAndHits;
        return scoreAndHits.score;
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
        return false;
    }

    private ScoreAndHits completeMatch(String reg) {
        ScoreAndHits scoreAndHits = new ScoreAndHits(-1, 0f,
                new ArrayList<PointPair>());

        for (int i = 0; i < fullNameNumberWithoutSpace.size(); i++) {
            String str = fullNameNumberWithoutSpace.get(i);
            if (reg.equals(str)) {
                scoreAndHits.nameIndex = i;
                scoreAndHits.score = Match_Level_Complete;
                scoreAndHits.pairs.add(new PointPair(i, -1));
                scoreAndHits.matchLevel = Level_Complete;
                return scoreAndHits;
            }
        }

        for (int i = 0; i < phones.size(); i++) {
            PhoneStruct phone = phones.get(i);
            if (reg.equals(phone.phoneNumber)) {
                scoreAndHits.nameIndex = i;
                scoreAndHits.score = Match_Level_Complete;
                scoreAndHits.pairs.add(new PointPair(i, -1));
                scoreAndHits.matchType = Match_Type_Phone;
                scoreAndHits.matchLevel = Level_Complete;
                return scoreAndHits;
            }
        }
        return new ScoreAndHits(-1, 0f, new ArrayList<PointPair>());
    }

    public static class ScoreAndHits {
        public float score = 0f;
        public int nameIndex;
        public ArrayList<PointPair> pairs = new ArrayList<PointPair>();
        public int matchType = Match_Type_Name;
        public int matchLevel = Level_None;
        public String reg = "";

        public ScoreAndHits(int nameIndex, float score,
                ArrayList<PointPair> pairs) {
            this.nameIndex = nameIndex;
            this.score = score;
            this.pairs = pairs;
        }
    }

    private ScoreAndHits foreAcronymOverFlowMatch(String reg) {
        ScoreAndHits scoreAndHits = new ScoreAndHits(-1, 0f,
                new ArrayList<PointPair>());
        for (int i = 0; i < fullNameNumber.size(); i++) {
            ArrayList<String> names = fullNameNumber.get(i);
            ScoreAndHits tmpscore = foreAcronymOverFlowMatch(names, reg);
            if (tmpscore.score > scoreAndHits.score) {
                scoreAndHits = tmpscore;
                scoreAndHits.nameIndex = i;
            }
        }
        scoreAndHits.matchLevel = Level_Fore_Acronym_Overflow;
        return scoreAndHits;
    }

    // 在第一个字母确定的情况下，第二个字母有可能有三种情况
    // 一、在第一个字母所在单词的邻居位置charAt(x+1);
    // 二、在第二个单词的首字母处
    // 三、以上两种情况皆不符合，不匹配，出局

    private ScoreAndHits foreAcronymOverFlowMatch(ArrayList<String> names,
            String reg) {
        ScoreAndHits scoreAndHits = new ScoreAndHits(-1, 0f,
                new ArrayList<PointPair>());
        if (names.get(0).charAt(0) == reg.charAt(0)) {
            OverflowMatchValue value = crossWords(names, reg, 0, 0, 0);
            int cross = crossWords(names, reg, 0, 0, 0).crossed;
            if (cross > 0) {
                scoreAndHits.score = Match_Level_Fore_Acronym_Overflow + cross
                        * Match_Score_Reward - (names.size() - cross)
                        * Match_Miss_Punish;
                scoreAndHits.pairs = value.pairs;
            }

        }
        return scoreAndHits;
    }

    /**
     * 返回一串字符能跨越另一串字符的长度，若要保证能跨越最长的长度，只要保证下一个字符能跨越最长的长度即可，这就构成了一个递归
     * 
     * @param names
     * @param regString 匹配字符串
     * @param listIndex 匹配到的list的第M个单词
     * @param strIndex 匹配到第M个单词中的第N个index
     * @param regIndex regchar的匹配位置
     * @return
     */
    private OverflowMatchValue crossWords(ArrayList<String> names,
            String regString, int listIndex, int strIndex, int regIndex) {
        OverflowMatchValue result = new OverflowMatchValue(0, false);
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
            result = new OverflowMatchValue((strIndex == 0) ? 1 : 0, true);
            result.pairs.add(0, new PointPair(listIndex, strIndex));
        }

        if (reser.matched || impul.matched) {
            if (impul.crossed > reser.crossed) {
                result = impul;
            } else {
                result = reser;
            }
            result.matched = true;
            result.crossed = ((strIndex == 0) ? 1 : 0)
                    + Math.max(result.crossed, result.crossed);
            result.pairs.add(0, new PointPair(listIndex, strIndex));
        }
        return result;
    }

    private ScoreAndHits backAcronymOverFlowMatch(String reg) {
        ScoreAndHits scoreAndHits = new ScoreAndHits(-1, 0f,
                new ArrayList<PointPair>());
        for (int i = 0; i < fullNameNumber.size(); i++) {
            ArrayList<String> names = fullNameNumber.get(i);
            ScoreAndHits tmp = backAcronymOverFlowMatch(names, reg);
            if (tmp.score > scoreAndHits.score) {
                scoreAndHits = tmp;
                scoreAndHits.nameIndex = i;
            }
        }
        scoreAndHits.matchLevel = Level_Back_Acronym_Overflow;
        return scoreAndHits;
    }

    private ScoreAndHits backAcronymOverFlowMatch(ArrayList<String> names,
            String reg) {
        int score = 0;
        int punish = 0;
        ScoreAndHits scoreAndHits = new ScoreAndHits(-1, 0f,
                new ArrayList<PointPair>());
        for (int i = 0; i < names.size(); i++) {
            String string = (String) names.get(i);
            if (string.charAt(0) == reg.charAt(0)) {
                OverflowMatchValue value = crossWords(names, reg, i, 0, 0);
                int cross = value.crossed;
                int lost = names.size() - cross;
                if (cross > score || cross == score && punish > lost) {
                    scoreAndHits.pairs = value.pairs;
                    score = cross;
                    punish = lost;
                }
            }
        }
        if (score > 0) {
            scoreAndHits.score = Match_Level_Back_Acronym_Overflow + score
                    * Match_Score_Reward - punish * Match_Miss_Punish;
            return scoreAndHits;
        } else {
            return new ScoreAndHits(-1, 0f, new ArrayList<PointPair>());
        }

    }

    private ScoreAndHits backHeadlessParagraphMatch(String reg) {
        int punish = 0;
        ScoreAndHits scoreAndHits = new ScoreAndHits(-1, -1f,
                new ArrayList<PointPair>());
        scoreAndHits.matchLevel = Level_Headless;
        scoreAndHits.matchType = Match_Type_Phone;
        // 不匹配姓名
        for (int i = 0; i < phones.size(); i++) {
            PhoneStruct phone = phones.get(i);
            int sco = phone.phoneNumber.indexOf(reg);
            if (sco >= 0) {
                int lost = phone.phoneNumber.length() - reg.length();
                if (scoreAndHits.score < sco || sco == scoreAndHits.score
                        && punish > lost) {
                    scoreAndHits.score = sco;
                    scoreAndHits.nameIndex = i;
                    scoreAndHits.pairs.add(new PointPair(i, sco));
                    punish = lost;
                }
            }
        }
        if (scoreAndHits.score >= 0) {
            scoreAndHits.score = Match_Level_Headless - scoreAndHits.score
                    * Match_Score_Reward - punish * Match_Miss_Punish;
        }
        return scoreAndHits;
    }

    public void addPhone(String number, int type) {
        PhoneStruct pStruct = new PhoneStruct(number, type);
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

    public ArrayList<PhoneStruct> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<PhoneStruct> phones) {
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
