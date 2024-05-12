package com.zst.configcenter.client.utils;

public class StringUtils extends org.springframework.util.StringUtils {
    public static boolean equals(String str1, String str2) {
        return str1 != null && str1.equals(str2);
    }

}
