package org.pqh.task;

import org.apache.log4j.Logger;
import org.pqh.entity.Bili;
import org.pqh.qq.DoSoming;
import org.pqh.qq.QueryRule;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by reborn on 2017/3/15.
 */
@Component
public class TaskQuery implements Runnable {
    private Bili bili;

    public void setBili(Bili bili) {
        this.bili = bili;
    }
    @Resource
    private QueryRule queryA;

    private static Logger log= Logger.getLogger(TaskQuery.class);
    @Override
    public void run() {
        if(DoSoming.groupFromID.size()>0&&queryA.check(bili)){
            DoSoming.messagePush(bili);
        }
    }
}
