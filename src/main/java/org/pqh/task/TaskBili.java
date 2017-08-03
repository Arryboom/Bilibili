package org.pqh.task;

import org.pqh.service.InsertService;
import org.pqh.util.LogUtil;

import java.util.Observable;

import static org.pqh.util.SpringContextHolder.insertService;

/**
 * Created by reborn on 2016/11/19.
 */

public class TaskBili extends Observable implements Runnable{

    private int id;

    private int aid;

    private int page;

    private int $aid;

    public TaskBili() {
        this.id=1;
    }

    public TaskBili(int id,int aid, int page, int $aid) {
        this.id=id;
        this.aid = aid;
        this.page = page;
        this.$aid = $aid;
    }

    public TaskBili(int id) {
        this.id = id;
    }

    // 此方法一经调用，立马可以通知观察者，在本例中是监听线程
    public void doBusiness(Object obj){
        super.setChanged();
        if(obj==null){
            notifyObservers(this);
        }else{
            notifyObservers(obj);
        }

    }

    public void run() {

        try {
            if(id==4) {
                InsertService.stop=false;
                insertService.insertHistory();
            }else {
                insertService.insertBili(id, aid, page, $aid);
            }
        } catch (Exception e){
            LogUtil.getLogger().error("爬虫任务意外中止，异常信息："+e.getMessage());
        } finally{
            doBusiness(null);
        }
    }

}
