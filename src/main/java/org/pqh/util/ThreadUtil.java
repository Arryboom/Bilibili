package org.pqh.util;

import org.apache.log4j.Logger;
import org.pqh.task.TaskCid;

import static org.pqh.util.SpringContextHolder.biliDao;
import static org.pqh.util.SpringContextHolder.threadPoolTaskExecutor;

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
            log.error(e);
        }
    }

    public static void addTask(int id,int type) {
        for (int cid = BiliUtil.getSave(id);; cid++) {
            if(biliDao.selectSave(id).get(0).isLatest()){
                ThreadUtil.sleep(15);
                cid=BiliUtil.getSave(id);
            }
            TaskCid taskCid=new TaskCid(cid);
            excute(new Thread(taskCid,("insertCid:"+cid)));
        }
    }


    /**
     * 多线程执行任务简单封装
     * @param runnable
     */
    public static void excute(Runnable runnable){
        threadPoolTaskExecutor.execute(runnable);
        ThreadUtil.sleep(100l);
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
            log.error(e);
        }
    }

}
