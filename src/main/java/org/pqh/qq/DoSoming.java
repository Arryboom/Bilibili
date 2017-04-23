package org.pqh.qq;

import com.scienjus.smartqq.Receiver;
import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.Message;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.pqh.entity.Param;
import org.pqh.entity.vstorage.Data;
import org.pqh.service.AvCountService;
import org.pqh.util.LogUtil;
import org.pqh.util.SpringContextHolder;
import org.pqh.util.StringUtil;
import org.pqh.util.ThreadUtil;

import java.util.*;

import static org.pqh.util.SpringContextHolder.biliDao;
/**
 * qq机器人响应一些指令运行一些操作并返回操作结果
 * Created by reborn on 2017/3/24.
 */
public class DoSoming {

    private static Logger log= Logger.getLogger(DoSoming.class);

    public static Map<String,Long> groupFromID=new HashMap<>();

    public static Map<String,Boolean> flag;

    static {
        flag=new HashMap<>();
        flag.put("acgdoge",false);
        flag.put("ithome",false);
        flag.put("rank", false);
        flag.put("flag",false);
    }

    public void doSoming(Message msg){


    }

    public void doSoming(GroupMessage msg){
        String message=msg.getContent();
        String params[]=message.split(" ");
        Param param=biliDao.selectParam("MonitoringGroup").get(0);
        if((param.getValue()).contains(Receiver.getGroupName(msg))){
            CommandLineParser parser = new BasicParser( );
            Options options = new Options();
            options.addOption("h", false, "查看命令使用方法");
            options.addOption("ctrl",true,"控制消息推送、爬虫开关");
            options.addOption("bg",false,"查看今天更新的番剧");
            try {
                CommandLine commandLine = parser.parse( options, params);
                if(commandLine.getOptions().length==0){
                    return;
                }
                if(commandLine.hasOption('h')){
                    HelpFormatter formatter = new HelpFormatter();
                    String help=StringUtil.getSystemOut(()->{
                        formatter.printHelp("命令使用方法",options);
                    });
                    messagePush("\n\n"+help);
                }else if(commandLine.hasOption("ctrl")){
                    String value=commandLine.getOptionValue("ctrl");
                    if(flag.get(value)!=null){
                        message="";
                        flag.put(value,!flag.get(value));
                        if("flag".equals(value)){
                            message+="\n\n";
                        }
                        message+=value+"状态："+(flag.get(value)?"开":"关");
                    }else{
                        message="不存在这个"+value+"状态";
                    }
                    log.info(message);
                    DoSoming.messagePush(message);
                }else if(commandLine.hasOption("bg")){
                    new MessagePush().doUpdate();
                }

            } catch (ParseException e) {
                log.error(e);
            }
        }

    }

    public void doSoming(DiscussMessage msg){

    }

    public static <T> void messagePush(T obj) {
        boolean isStr=obj instanceof String;
        if(!flag.get("flag")&&!(isStr&&obj.toString().startsWith("\n\n"))){
            return;
        }
        Param param = biliDao.selectParam("MonitoringGroup").get(0);
        for (String groupname : param.getValue().split(",")) {
            long groupId = groupFromID.get(groupname);
            List<String> msgs=new ArrayList<>();
            if(isStr){
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
