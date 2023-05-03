package com.todostudy.tools.utils;

import com.todostudy.tools.fm.PC;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil extends DateUtils {



    /**
     * 在日期date上增加amount天 。
     *
     * @param date   处理的日期，非null
     * @param amount 要加的天数，可能为负数
     */
    public static Date addDays(Date date, int amount) {
        Calendar now =Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE,now.get(Calendar.DATE)+amount);
        return now.getTime();
    }

    /**
     * 时间戳返回周期数
     *
     * @param currentTimeMillis 时间戳
     * @return 字符串
     */
    public static Integer covertCycle(Long currentTimeMillis) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(currentTimeMillis);
        Integer cycle = gregorianCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        return cycle;
    }

    public static Long addMin(Long timestamp, int min) {
        if(!((timestamp+"").length()>=12)) {
            throw new RuntimeException("时间格式有误");
        }
        return ((timestamp / 1000 / 60) + min) * 60 * 1000;
    }

    public static Long addMinApi(Long timestamp,Integer min) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(timestamp);
        gregorianCalendar.add(GregorianCalendar.MINUTE,min);
        return gregorianCalendar.getTimeInMillis();
    }

    public static Long addHour(Long timestamp,Integer h) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(timestamp);
        gregorianCalendar.add(GregorianCalendar.HOUR,h);
        return gregorianCalendar.getTimeInMillis();
    }

    public static int getHour(Long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int getMin(Long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        int min = calendar.get(Calendar.MINUTE);
        return min;
    }

    public static String getYestoryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        SimpleDateFormat sdf = new SimpleDateFormat(PC.DATA_FORM_1);
        return sdf.format(calendar.getTime());
    }
}
