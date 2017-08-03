package org.pqh.task;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.pqh.msg.MessagePush;
import org.pqh.service.AvCountService;
import org.pqh.test.Test;
import org.pqh.util.*;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.security.DigestException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.pqh.util.SpringContextHolder.biliDao;

/**
 * Created by reborn on 2017/5/8.
 * 动态定时器任务管理类
 */
@Component
public class DynamicTimer extends  ScheduledTaskRegistrar{

    @Resource
    private MessagePush messagePush;

    @Resource
    private Test test;

    @Resource
    private AvCountService avCountService;

    @Resource PropertiesUtil propertiesUtil;

    //存放cron表达式，key=任务名称，通过更新任务名称对应的cron表达式，动态修改定时器任务触发时间周期。
    public static Map<String,String> maps=new HashMap<>();

    //定时器任务名称
    private static String initTask[]={"public void org.pqh.service.AvCountService.setPlays()","public void org.pqh.msg.MessagePush.check()","public void org.pqh.msg.MessagePush.parseTsdm()",
    "public void org.pqh.msg.MessagePush.ithome()","public void org.pqh.msg.MessagePush.acgdoge()","public void org.pqh.msg.MessagePush.doUpdate()","public void org.pqh.test.Test.backupMysql()",
    "public void org.pqh.util.PropertiesUtil.updateProperties()","updateIP"};
    static {
        //初始化定时器任务名称和表达式
        maps.put(initTask[0],"0 0 0/1 * * ?");
        maps.put(initTask[1],"0 0/10 * * * ?");
        maps.put(initTask[2],"0 0/10 * * * ?");
        maps.put(initTask[3],"0 0/10 * * * ?");
        maps.put(initTask[4],"0 0 0/1 * * ?");
        maps.put(initTask[5],"0 0 0 * * ?");
        maps.put(initTask[6],"0 0 0 ? * 1");
        maps.put(initTask[7],"0 0/10 * * * ?");
        maps.put(initTask[8],"0 0/10 * * * ?");
    }
    public DynamicTimer(){
        addTriggerTask(()->{avCountService.setPlays();},initTask[0]);
        addTriggerTask(()->{messagePush.check();},initTask[1]);
        addTriggerTask(()->{messagePush.parseTsdm();},initTask[2]);
        addTriggerTask(()->{messagePush.ithome();},initTask[3]);
        addTriggerTask(()->{messagePush.acgdoge();},initTask[4]);
        addTriggerTask(()->{messagePush.doUpdate();},initTask[5]);
        addTriggerTask(()->{LogUtil.getLogger().info("备份数据库");
            test.backupMysql();
        },initTask[6]);
        addTriggerTask(()->{propertiesUtil.updateProperties();},initTask[7]);

        addTriggerTask(()->{
            LogUtil.getLogger().info("更新域名ip");
            try {
                String SecretKey=biliDao.selectParam("SecretKey").get(0).getValue();
                if(StringUtil.isEmpty(SecretKey)){
                    return;
                }
                String url=ApiUrl.qcloudip.getUrl().replace("https://","");
                Map<String,Object> map=new HashMap<>();
                map.put("Action","RecordModify");
                map.put("domain","mikuhime.xyz");
                map.put("recordId","297292314");
                map.put("subDomain","@");
                map.put("recordType","A");
                map.put("recordLine","默认");
                Document document= CrawlerUtil.jsoupGet(ApiUrl.ipAddress.getUrl(), CrawlerUtil.DataType.domcument, Connection.Method.GET);
                map.put("value", StringUtil.matchStr(document.body().html(),"(\\d+\\.){3}\\d+",String.class));
                map.put("Timestamp",System.currentTimeMillis()/1000+"");
                map.put("Nonce",(int)(Math.random()*89999)+10000+"");
                map.put("SecretId",biliDao.selectParam("SecretId").get(0).getValue());
                map.put("Region","gz");
                String otext="GET"+url+StringUtil.getOrderByLexicographic(map);
                LogUtil.getLogger().debug(otext);
                String Signature= new String(Base64.encodeBase64(AlgorithmUtil.HmacSHA1Encrypt(otext,SecretKey)));
                map.put("Signature",Signature);

                for(String key:map.keySet()){
                    map.put(key, URLEncoder.encode(map.get(key).toString(),"UTF-8"));
                }

                JsonNode jsonNode=CrawlerUtil.jsoupGet(ApiUrl.qcloudip.getUrl()+StringUtil.getOrderByLexicographic(map), CrawlerUtil.DataType.json, Connection.Method.GET);
                LogUtil.getLogger().info("请求地址:"+ApiUrl.qcloudip.getUrl()+StringUtil.getOrderByLexicographic(map)+"\n请求结果:"+jsonNode.toString());
            } catch (DigestException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        },initTask[8]);
    }

    public void addFixedRateTask(Runnable task, long interval, long initialDelay) {
        super.scheduleFixedRateTask(new IntervalTask(task,interval,initialDelay));
        super.destroy();
    }

    public void addFixedDelayTask(Runnable task, long interval, long initialDelay) {
        super.scheduleFixedDelayTask(new IntervalTask(task,interval,initialDelay));
    }

    public void addTriggerTask(Runnable task, String key) {
        if(maps.get(key)==null){
            LogUtil.getLogger().error("无法找到key="+key+"的cron表达式");
            return;
        }


        super.scheduleTriggerTask(new TriggerTask(task,(TriggerContext triggerContext)->{
            CronTrigger trigger = new CronTrigger(maps.get(key));
            LogUtil.getLogger().debug(triggerContext+"");
            Date nextExec = trigger.nextExecutionTime(triggerContext);
            return nextExec;
        }));
    }


    @Override
    public void addCronTask(Runnable task, String key) {
        if(maps.get(key)==null){
            LogUtil.getLogger().error("无法找到key="+key+"的cron表达式");
            return;
        }

        super.scheduleCronTask(new CronTask(task,new CronTrigger(maps.get(key))));
    }




}