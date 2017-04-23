package org.pqh.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 10295 on 2016/8/4.
 */
public class TimeUtil{
    //日期格式
    public static final String DATE="yyyy-MM-dd";
    public static final String DATETIME="yyyy-MM-dd HH:mm:ss";
    /**
     * 毫秒换算几分钟几秒几毫秒
     * @param time 毫秒
     * @return
     */
    public static String longTimeFormatString(long time){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String timestr="";
        int ms=calendar.get(Calendar.MINUTE)>0?calendar.get(Calendar.MINUTE)*60*1000:0;
        if(ms>0){
            timestr+=calendar.get(Calendar.MINUTE)+"m";
        }
        ms+=calendar.get(Calendar.SECOND)>0?calendar.get(Calendar.SECOND)*1000:0;
        if(ms>0){
            timestr+=calendar.get(Calendar.SECOND)+"s";
        }
        return timestr+((time-ms)>0?(time-ms)+"ms":"");
    }

    public static String formatDate(Date date,String pattern){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(pattern!=null?pattern:DATE);
        return simpleDateFormat.format(date!=null?date:new Date());
    }


    /**
     * 字符串转日期
     * @param date 日期字符串
     * @param format 格式
     * @return
     */
    public static Date parseDate(String date, String format){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format!=null?format:DATE);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("无法按照\t\""+format+"\"\t格式解析日期");
        }
    }

    /**
     * 检查日期格式有效性
     * @param date 日期
     * @return
     */
    public static boolean checkDate(int index,String date){
        String formats[]=new String[]{DATE,DATETIME};
        try {
            new SimpleDateFormat(TimeUtil.DATETIME).parse(date);
            return true;
        } catch (ParseException e) {
            if(index==formats.length-1){
                return false;
            }else{
                return checkDate(index++,date);
            }

        }

    }
}
