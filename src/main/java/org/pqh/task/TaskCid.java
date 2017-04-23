package org.pqh.task;

import static org.pqh.util.SpringContextHolder.insertService;

/**
 * Created by 10295 on 2016/5/9.
 */
public class TaskCid implements Runnable {
    private int cid;



    public TaskCid(int cid) {
        this.cid = cid;
    }

    public void run() {
            insertService.insertCid(cid);
    }

}
