package com.trustai.common_base.utils;

public final class StringUtils {
    private StringUtils() {}

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }
}