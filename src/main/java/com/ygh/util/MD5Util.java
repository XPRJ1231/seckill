package com.ygh.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    /**
     * 第一次MD5加密，用于网络传输
     *
     * @param inputPass 原始密码
     */
    public static String inputPassToFormPass(String inputPass) {
        //避免在网络传输被截取然后反推出密码，所以在md5加密前先打乱密码
        return formPassToDBPass(inputPass, salt);
    }

    /**
     * 第二次MD5加密，用于存储到数据库
     *
     * @param formPass 第一次加密后的密码
     * @param salt     盐值
     * @return 第二次加密后密码
     */
    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 加密方法
     *
     * @param input  原始密码
     * @param saltDB 盐值
     * @return 两次加密后的密码
     */
    public static String inputPassToDbPass(String input, String saltDB) {
        String formPass = inputPassToFormPass(input);
        return formPassToDBPass(formPass, saltDB);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));
    }

}
