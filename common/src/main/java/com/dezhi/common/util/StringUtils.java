package com.dezhi.common.util;

/**
 * 字符串工具类
 *
 * @author liaodezhi
 * @date 2023/1/29
 */
public class StringUtils {

    /**
     * 判断字符串是否为空串
     * @param s 字符串
     * @return 为空返回true, 否则返回false
     */
    public static boolean isBlank(String s) {
        if(s == null || s.length() == 0) {
            return true;
        }
        // 判断字符串全部字符是否为空
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
