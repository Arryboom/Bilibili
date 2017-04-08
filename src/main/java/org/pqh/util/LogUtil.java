package org.pqh.util;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2016/5/29.
 */
public class LogUtil {
    private static Logger log= Logger.getLogger(LogUtil.class);

    public static void outPutLog(String a,Exception e) {
        log.error("---------------异常信息分割线---------------");
        log.error(a+"\t"+e.getMessage());
        List<StackTraceElement> list=new ArrayList<>();
        for(StackTraceElement stackTraceElement:e.getStackTrace()){
            if(stackTraceElement.getClassName().contains("org.pqh")){
                list.add(stackTraceElement);
            }
        }
        e.setStackTrace(list.toArray(new StackTraceElement[]{}));
        e.printStackTrace();
    }


    public static String getLineInfo()
    {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return ste.getFileName() + ": Line " + ste.getLineNumber();
    }
}
