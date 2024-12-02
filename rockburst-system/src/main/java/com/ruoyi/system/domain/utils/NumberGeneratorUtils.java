package com.ruoyi.system.domain.utils;

/**
 * @author: shikai
 * @date: 2024/11/28
 * @description:
 */
public class NumberGeneratorUtils {

    /**
     * 获取并增加四位数的值
     * @return 当前四位数的值
     */
    public static String getNextValue(String str) {
        StringBuilder builder = new StringBuilder();
        String s = str.substring(0, str.length() - 4);
        // 截取最后四位
        String substring = str.substring(str.length() - 4);
        // 将截取的字符串转换为整数
        int strFmt = Integer.parseInt(substring);
        strFmt ++ ;
        // 格式化输出四位数
        String formattedValue = String.format("%04d", strFmt);
        return builder.append(s).append(formattedValue).toString();
    }
}