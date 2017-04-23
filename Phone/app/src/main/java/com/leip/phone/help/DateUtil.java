package com.leip.phone.help;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 被咯苏州 on 2016/8/5.
 */
public class DateUtil {


    public static String MS_TO_TIME1 = "dd天HH时mm分ss秒";
    public static String MS_TO_TIME2 = "HH时mm分ss秒";
    public static String MS_TO_TIME3 = "mm分ss秒";
    public static String DATE_FORMAT_STR1 = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_FORMAT_STR2 = "MM.dd";
    public static String DATE_FORMAT_STR3 = "yyyyMMdd";
    public static String DATE_FORMAT_STR4 = "yyyy/MM/dd HH:mm:ss";
    public static String DATE_FORMAT_STR5 = "yyyy.MM.dd";
    public static String DATE_FORMAT_STR6 = "yyyy-MM-dd";
    public static String DATE_FORMAT_STR7 = "MM.dd HH:mm:ss";
    public static String DATE_FORMAT_STR8 = "MM/dd HH:mm:ss";
    public static String DATE_FORMAT_STR9 = "MM/dd";
    public static String DATE_FORMAT_STR10 = "yyyy/MM/dd";
    public static String DATE_FORMAT_STR11 = "yyyyMMddHHmmss";

    /**
     * 时间戳毫秒转format字符串
     * @param currentTime
     * @param format
     * @return
     */
    public static String currentTime4String(long currentTime,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(currentTime);
        String formatDate = sdf.format(date);
        return formatDate;
    }

    /**
     * 日期转为时间戳毫秒
     *
     * @return
     */

    public static long string4CurrentTime(String date,String format) {
        long timePoor = 0;
        SimpleDateFormat df = new SimpleDateFormat(format);
        SimpleDateFormat start = new SimpleDateFormat(DATE_FORMAT_STR1);
        try {
            Date parseEnd = df.parse(date);
            Date parseStart = start.parse("1970-01-01 00:00:00");
            timePoor = parseEnd.getTime() - parseStart.getTime();
        } catch (ParseException e) {
            timePoor = 0;
        }
        return timePoor;
    }
}
