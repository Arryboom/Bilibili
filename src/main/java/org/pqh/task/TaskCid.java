package org.pqh.task;

import static org.pqh.util.SpringContextHolder.insertService;

/**
 * Created by 10295 on 2016/5/9.
 */
public class TaskCid implements Runnable {
    private int cid;

    private int type;

    public TaskCid(int cid,int type) {
        this.cid = cid;
        this.type = type;
    }


    public void run() {
            if(type==0){
                insertService.insertVstorage(cid);
            }else if(type==1){
                insertService.insertCid(cid);
            }
    }

}
