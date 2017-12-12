package com.meitu.plugin.utils

public class TextUtil {

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true
        } else {
            return false
        }
    }

}