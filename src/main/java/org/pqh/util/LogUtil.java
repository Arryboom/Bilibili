package org.pqh.util;


import org.apache.log4j.Logger;

/**
 * Created by 10295 on 2016/5/29.
 */
public class LogUtil {

    public static void outputLog(Exception e,Logger logger) {
       logger.error("错误类型：" + e.getClass()+ "\t错误信息" + e.toString());
    }

    /**
     * 输出错误日志并抛出异常信息
     * @param logger
     * @param msg 错误信息
     * @param flag 是否抛出异常
     */
    public static void errorLog(Logger logger,String msg,boolean flag){
        logger.error(msg);
        if(flag){
            throw new RuntimeException(msg);
        }
    }

    public static String getLineInfo()
    {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return ste.getFileName() + ": Line " + ste.getLineNumber();
    }
}
