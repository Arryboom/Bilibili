package org.pqh.util;


import org.apache.log4j.Logger;

/**
 * Created by 10295 on 2016/5/29.
 */
public class TestSlf4j {
    public static Logger getLogger(Class c){
        return Logger.getLogger(c);
    }

    public static void outputLog(Exception e,Logger logger,boolean flag) {
        if(flag){
            e.printStackTrace();
        }else{
            logger.error("错误类型：" + e.getClass()+ "\t错误信息" + e.toString());
        }
    }


    public static String getLineInfo()
    {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return ste.getFileName() + ": Line " + ste.getLineNumber();
    }
}
