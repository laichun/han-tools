package com.todostudy;

public class StrUtil {


    public static boolean startWithIgnoreCase(CharSequence str, CharSequence prefix) {
        return startWith(str, prefix, true);
    }

    public static boolean startWith(CharSequence str, CharSequence prefix, boolean isIgnoreCase) {
        if (null != str && null != prefix) {
            return isIgnoreCase ? str.toString().toLowerCase().startsWith(prefix.toString().toLowerCase()) : str.toString().startsWith(prefix.toString());
        } else {
            return null == str && null == prefix;
        }
    }

    public static boolean equals(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, false);
    }
    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, true);
    }

    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (null == str1) {
            return str2 == null;
        } else if (null == str2) {
            return false;
        } else {
            return ignoreCase ? str1.toString().equalsIgnoreCase(str2.toString()) : str1.equals(str2);
        }
    }
}
