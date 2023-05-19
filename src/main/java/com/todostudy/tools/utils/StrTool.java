package com.todostudy.tools.utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrTool {

    public static String UUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * @Description 将驼峰转为下划线
     * @param str
     */
    public static String xX2x_x(String str) {
        Pattern compile = Pattern.compile("[A-Z]");
        Matcher matcher = compile.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb,  "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @Description 将下划线转为驼峰
     * @param str
     */
    public static String x_x2xX(String str) {
        str = str.toLowerCase();
        Pattern compile = Pattern.compile("_[a-z]");
        Matcher matcher = compile.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb,  matcher.group(0).toUpperCase().replace("_",""));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String replaceAll(String sourceStr,String reg,String replaceStr) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(sourceStr);
        return matcher.replaceAll(replaceStr);
    }


}
