package org.pqh.util;

import org.apache.log4j.Logger;
/**
 * Created by 10295 on 2016/8/4.
 */
public class ThreadUtil {
    private static Logger log= Logger.getLogger(ThreadUtil.class);


    /**
     * 等待指定时长
     * @param time 传入整型数字单位为秒，长整形数字单位为毫秒
     */
    public static <T extends Number> void sleep(T time){
        try {
            if(time.getClass().equals(Integer.class)){
                Thread.sleep((Integer)time*1000);
            }else{
                Thread.sleep((Long) time);
            }

        } catch (InterruptedException e) {
            LogUtil.outPutLog(LogUtil.getLineInfo(),e);
        }
    }


    /**
     * 等待指定时长
     * @param message 提示信息
     * @param time 毫秒
     */
    public static <T extends Number> void sleep(String message,T time){
        try {
            String msg=message + "休息" + time;
            if(time.getClass().equals(Integer.class)){
                if((Integer)time>10) {
                    log.info(msg+"秒");
                }
                log.debug(msg+"秒");
                Thread.sleep((Integer)time*1000);
            }else{
                if((Long)time>10) {
                    log.info(msg+"豪秒");
                }
                log.debug(msg+"豪秒");
                Thread.sleep((Long) time);
            }

        } catch (InterruptedException e) {
            LogUtil.outPutLog(LogUtil.getLineInfo(),e);
        }
    }

}
