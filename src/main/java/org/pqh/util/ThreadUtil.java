package main.java.org.pqh.util;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 10295 on 2016/8/4.
 */
public class ThreadUtil {
    private static Logger log= TestSlf4j.getLogger(ThreadUtil.class);



    /**
     *
     * @param c
     * @param methods
     */
    public static void threadRun(Class c, String methods[]){

        try {
            final Object obj=c.newInstance();
            for(String methodName:methods) {
                final Method method = c.getDeclaredMethod(methodName);
                Thread thread=new Thread(()->{
                    try {
                        method.invoke(obj);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                },methodName);

                thread.start();

            }
        } catch (NoSuchMethodException e) {
            TestSlf4j.outputLog(e,log);
        } catch (InstantiationException e) {
            TestSlf4j.outputLog(e,log);
        } catch (IllegalAccessException e) {
            TestSlf4j.outputLog(e,log);
        }
    }

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
            TestSlf4j.outputLog(e,log);
        }
    }


    /**
     * 等待指定时长
     * @param message 提示信息
     * @param time 毫秒
     */
    public static <T extends Number> void sleep(String message,T time){
        try {
            log.info(message);
            if(time.getClass().equals(Integer.class)){
                Thread.sleep((Integer)time*1000);
            }else{
                Thread.sleep((Long) time);
            }

        } catch (InterruptedException e) {
            TestSlf4j.outputLog(e,log);
        }
    }

}
