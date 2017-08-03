package org.pqh.util;


import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.pqh.util.PropertiesUtil.getFilePath;

/**
 * Created by 10295 on 2016/5/29.
 */

public class LogUtil {

    public static Logger getLogger(){
        return LoggerFactory.getLogger(getStrack(2).getClassName());
    }

    public static void reloadLog4jConfig()
    {
        PropertyConfigurator.configure(getFilePath("log4j.properties"));
    }

    /**
     * 获取方法调用者
     * @param i
     * @return
     */
    public static StackTraceElement getStrack(int i){
        return  new Throwable().getStackTrace()[i];
    }


}
