package com.ruoyi.system.domain.utils;

/**
 * @author: shikai
 * @date: 2025/8/16
 * @description:
 */
public class ColumStringUtils {
    /**
     * 截取字符串前三位
     *
     * @param input 输入字符串
     * @return 截取后的字符串（前3位）
     */
    public static String extractFirstThreeChars(String input) {
        if (input == null || input.length() < 3) {
            return input;
        }
        return input.substring(0, 3);
    }

    /**
     * 根据字符串最后一位数字获取对应的中文含义
     *
     * @param input 输入字符串
     * @return 对应的中文含义
     */
    public static String getColumnMeaning(String input) {
        if (input == null || input.isEmpty()) {
            return "未知";
        }

        char lastChar = input.charAt(input.length() - 1);

        switch (lastChar) {
            case '1':
                return "前柱";
            case '2':
                return "后柱";
            case '3':
                return "左柱";
            case '4':
                return "右柱";
            case '5':
                return "前探梁";
            case '6':
                return "平衡千斤顶";
            case '7':
                return "推溜千斤顶";
            case '8':
                return "其他";
            default:
                return "未知";
        }
    }
}