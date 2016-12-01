package org.pqh.task;

import org.pqh.dao.BiliDao;
import org.pqh.service.InsertService;
import org.pqh.test.Test;

import java.util.Observable;

/**
 * Created by reborn on 2016/11/19.
 */

public class TaskBili extends Observable implements Runnable{

    private  InsertService insertService;

    private  BiliDao biliDao;

    private static Test test;

    public TaskBili(InsertService insertService, BiliDao biliDao) {
        this.insertService = insertService;
        this.biliDao = biliDao;
        test=new Test();
    }

    public TaskBili(){}

    // 此方法一经调用，立马可以通知观察者，在本例中是监听线程
    public void doBusiness(Object obj){
        if(true){
            super.setChanged();
        }
        if(obj==null){
            notifyObservers(this);
        }else{
            notifyObservers(obj);
        }

    }

    public void run() {
        final Thread word=new Thread(()->{
            insertService.insertBili();},"aid数据采集");
        word.start();

        Thread stop=new Thread(()->{
            while(true){
                if(test.checkRun()){
                    word.stop();
                    doBusiness(null);
                    break;
                }
            }
        },"线程终结者");
        stop.start();

    }


}
