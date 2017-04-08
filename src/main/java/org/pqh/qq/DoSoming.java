package org.pqh.qq;

import com.scienjus.smartqq.Receiver;
import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.Message;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.pqh.entity.Param;
import org.pqh.entity.vstorage.Data;
import org.pqh.util.LogUtil;
import org.pqh.util.SpringContextHolder;
import org.pqh.util.StringUtil;

import java.util.*;

import static org.pqh.util.SpringContextHolder.biliDao;
/**
 * qq机器人响应一些指令运行一些操作并返回操作结果
 * Created by reborn on 2017/3/24.
 */
public class DoSoming {

    private static Logger log= Logger.getLogger(DoSoming.class);

    public static Map<String,Long> groupFromID=new HashMap<>();

    public void doSoming(Message msg){


    }

    public void doSoming(GroupMessage msg){
        String message=msg.getContent();
        String params[]=message.split(" ");
        Param param=biliDao.selectParam("MonitoringGroup");
        if((param.getValue()).contains(Receiver.getGroupName(msg))){
            CommandLineParser parser = new BasicParser( );
            Options options = new Options();
            options.addOption("h", "help", false, "Print this usage information");
            options.addOption("f", "find", true, "find data");
            options.addOption("a", "aid", true, "find data by aid");
            options.addOption("c", "cid", true, "find data by cid");
            options.addOption("t", "title", true, "find data by title");
            options.addOption("T", "typeid", true, "find data by typeid");

            options.addOption("ctrl","control",true,"控制消息推送开关");
            options.addOption("bg","bangumi",false,"查看今天更新的番剧");
            try {
                CommandLine commandLine = parser.parse( options, params);
                if(commandLine.hasOption('h')||commandLine.hasOption("help")){
                    HelpFormatter formatter = new HelpFormatter();
                    String help=StringUtil.getSystemOut(()->{
                        formatter.printHelp("ant",options);
                    });
                    messagePush(help);
                }else if(commandLine.hasOption('f')||commandLine.hasOption("find")){
                    log.info("接收到查找命令");
                    Map<String,String> map=new HashMap<>();
                    if(commandLine.hasOption('a')){
                        map.put("aid",commandLine.getOptionValue('a'));
                    }
                    if(commandLine.hasOption('c')){
                        map.put("cid",commandLine.getOptionValue("c"));
                    }
                    if(commandLine.hasOption('t')){
                        map.put("title",commandLine.getOptionValue('t'));
                    }
                    if(commandLine.hasOption("T")){
                        map.put("typeid",commandLine.getOptionValue("T"));
                    }
                    if(commandLine.hasOption('f')){
                        map.put("f",commandLine.getOptionValue('f'));
                    }
                    if(map.size()>0){
                        log.info("查询条件"+map);
                        long a=System.currentTimeMillis();
                        List<Data> dates;
                        if(map.get("f")!=null&&map.get("f").equals("0")&&map.get("title")!=null&&map.get("typeid")!=null){
                            log.info("根据title还有typeid进行查找");
                            dates=SpringContextHolder.vstorageDao.selectDataCid(map);
                        }else{
                            log.info("根据aid,cid,title进行查找");
                            dates= SpringContextHolder.vstorageDao.selectData(map);
                        }
                        long b=System.currentTimeMillis();
                        String time="--------------开始---------------\n查询耗费"+(b-a)+"ms,查询出"+dates.size()+"条记录";
                        log.info(time);
//                        Receiver.client.sendMessageToGroup(msg.getGroupId(),time);
                        for(int i=0;i<dates.size();i++){
//                            if(i==5){
//                                Receiver.client.sendMessageToGroup(msg.getGroupId(),"信息量太大只回复前五条记录\n-------------结束---------------");
//                                break;
//                            }
//                            Receiver.client.sendMessageToGroup(msg.getGroupId(),dates.get(i).toString());
                        }

                    }
                }else if(commandLine.hasOption("ctrl")){
                    String value=commandLine.getOptionValue("ctrl");
                    if("acgdoge".equals(value)){
                        MessagePush.acgdoge=!MessagePush.acgdoge;
                        log.info("acgdoge状态："+(MessagePush.acgdoge?"开":"关"));
                    }else if("ithome".equals(value)){
                        log.info("ithome状态："+(MessagePush.ithome?"开":"关"));
                    }
                }else if(commandLine.hasOption("bg")){
                    new MessagePush().doUpdate();
                }

            } catch (ParseException e) {
                LogUtil.outPutLog(LogUtil.getLineInfo(),e);
            }
        }

    }

    public void doSoming(DiscussMessage msg){

    }

    public static <T> void messagePush(T obj) {
        Param param = biliDao.selectParam("MonitoringGroup");
        for (String groupname : param.getValue().split(",")) {
            long groupId = groupFromID.get(groupname);
            List<String> msgs=new ArrayList<>();
            if(obj instanceof String){
                msgs.add((String) obj);
            }else if(obj instanceof List){
                msgs.addAll((List<String>)obj);
            }else{
                msgs.add(obj.toString());
            }
            for (String msg : msgs) {
                log.info("消息：" + msg + "\n推送到群" + groupname);
                Receiver.client.sendMessageToGroup(groupId, msg);
            }

        }
    }
}
