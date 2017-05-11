package org.pqh.task;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;

/**
 * Created by reborn on 2017/5/8.
 */
@EnableScheduling
public class DynamicTimer implements SchedulingConfigurer {

    private Runnable runnable;

    private String cronStr;

    public DynamicTimer(Runnable runnable, String cronStr) {
        this.runnable = runnable;
        this.cronStr = cronStr;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(runnable, (TriggerContext triggerContext)->{
            // 任务触发，可修改任务的执行周期
            CronTrigger trigger = new CronTrigger(cronStr);
            Date nextExec = trigger.nextExecutionTime(triggerContext);
            return nextExec;
        } );
    }
}
