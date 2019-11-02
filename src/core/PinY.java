package core;

import com.github.promeg.pinyinhelper.Pinyin;
import com.github.promeg.tinypinyin.lexicons.java.cncity.CnCityDict;

public class PinY {
    static {
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance()));
    }

    public static String[] get(String str) {
        if (str == null || str.isEmpty()) return new String[]{"-", ""};
        String pinyin = Pinyin.toPinyin(str, " ");
        StringBuilder shortName = new StringBuilder();
        for (String py : pinyin.split(" ")) {
            if (py != null && !py.isEmpty()) {
                shortName.append(py.charAt(0));
            }
        }
        return new String[]{pinyin, shortName.toString()};
    }
}
