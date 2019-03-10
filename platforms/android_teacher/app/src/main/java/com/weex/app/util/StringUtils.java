package com.weex.app.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

    public static String md5(String str) {
        if (TextUtils.isEmpty(str)) return "";
//        com.twmacinta.util.MD5 md5 = new com.twmacinta.util.MD5();
//        md5.Update(str);
//        return md5.asHex();
//        try {
//            MessageDigest digest = java.security.MessageDigest
//                .getInstance("MD5");
//            digest.update(str.getBytes());
//            byte messageDigest[] = digest.digest();
//
//            // Create Hex String
//            StringBuilder hexString = new StringBuilder(32);
//            for (byte aMessageDigest : messageDigest) {
//                String h = Integer.toHexString(0xFF & aMessageDigest);
//                if (h.length() < 2) hexString.append("0");
//                hexString.append(h);
//            }
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//        }
        return "";
    }

    public static boolean isNullOrEmpty(String str) {
        if (str == null) return true;
        if (str.equals("")) return true;
        return false;
    }

    public static String urlEncode(String str) {
        if (str == null) return "";
        try {
            return java.net.URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        return "";
    }
}
