package org.pqh.task;

import org.pqh.util.LogUtil;
import org.pqh.util.ThreadUtil;

import java.util.Observable;
import java.util.Observer;
/**
 * Created by reborn on 2016/11/19.
 */

public class Listener implements Observer {



    public void update(Observable o, Object arg) {
        TaskBili taskBili= (TaskBili) arg;
        taskBili.addObserver(this);
        Thread thread=new Thread(taskBili,"insertHistory");
        ThreadUtil.excute(thread);
        LogUtil.getLogger().error("TaskBili线程重启");
    }
}
