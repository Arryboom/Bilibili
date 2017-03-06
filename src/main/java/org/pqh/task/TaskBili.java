package main.java.org.pqh.task;

import main.java.org.pqh.test.Test;
import main.java.org.pqh.dao.BiliDao;
import main.java.org.pqh.service.InsertService;

import java.util.Observable;

/**
 * Created by reborn on 2016/11/19.
 */

public class TaskBili extends Observable implements Runnable{

    private InsertService insertService;

    private  BiliDao biliDao;

    private int aid;

    private int page;

    private int $aid;

    public TaskBili(InsertService insertService, BiliDao biliDao) {
        this.insertService = insertService;
        this.biliDao = biliDao;
        this.aid=0;
        this.page=0;
        this.$aid=0;
    }

    public TaskBili(InsertService insertService, BiliDao biliDao, int aid, int page, int $aid) {
        this.insertService = insertService;
        this.biliDao = biliDao;
        this.aid = aid;
        this.page = page;
        this.$aid = $aid;
    }

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
            insertService.insertBili(aid,page,$aid);},"aid数据采集");
        word.start();

        Thread stop=new Thread(()->{
            while(true){
                if(Test.checkRun()){
                    word.stop();
                    doBusiness(null);
                    break;
                }
            }
        },"线程终结者");
        stop.start();

    }


}
