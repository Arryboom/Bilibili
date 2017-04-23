package org.pqh.task;

import org.apache.log4j.Logger;
import org.pqh.service.InsertService;
import org.pqh.util.ThreadUtil;

import java.util.Observable;
import java.util.Observer;
/**
 * Created by reborn on 2016/11/19.
 */

public class Listener implements Observer {

    private static Logger log= Logger.getLogger(Listener.class);

    public void update(Observable o, Object arg) {
        log.error("TaskBili线程出现异常");
        TaskBili taskBili= (TaskBili) arg;
        taskBili.addObserver(this);
        Thread thread=new Thread(taskBili,"insertHistory");
        InsertService.stop=false;
        ThreadUtil.excute(thread);
        log.error("TaskBili线程重启");
    }
}
