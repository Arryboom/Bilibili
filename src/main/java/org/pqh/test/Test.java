package org.pqh.test;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.pqh.entity.Save;
import org.pqh.qq.DoSoming;
import org.pqh.qq.Receiver;
import org.pqh.service.InsertService;
import org.pqh.task.Listener;
import org.pqh.task.TaskBili;
import org.pqh.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import static org.pqh.util.SpringContextHolder.biliDao;

/**
 * Created by Reborn on 2016/2/5.
 */

@Component
@PropertySource("classpath:config.properties")
public class Test {
    private static Logger log= Logger.getLogger(Test.class);

    @Value("${7zpwd}")
    private String _7zpwd;
    @Value("${localPath}")
    private String localPath;
    @Value("${dbusername}")
    private String dbusername;
    @Value("${dbpassword}")
    private String dbpassword;
    @Value("${backuptables}")
    private String backuptables;
    @Value("${serverPath}")
    private String serverPath;
    @Value("${exclude}")
    private String exclude;
    @Value("${mysqlPath}")
    private String mysqlPath;

    public static void main(String[] args) throws Exception {
        log.info("开始爬虫程序");
        new Test().testTask();
        SpringContextHolder.close();
        System.exit(0);
        log.info("结束爬虫程序");

    }

    public  void testTask(){
        //爬取历史接口数据
        Listener listener=new Listener();
        TaskBili taskBili=new TaskBili(4);
        taskBili.addObserver(listener);
        Thread thread=new Thread(taskBili,"insertHistory");
        ThreadUtil.excute(thread);

        Thread insertCid=new Thread(()->{
            ThreadUtil.addTask(2,1);
        },"insertCid");
        ThreadUtil.excute(insertCid);

        //启动qq机器人
        ThreadUtil.excute(()-> {
            Receiver.main(null);
            for(long id: Receiver.groupFromID.keySet()){
                DoSoming.groupFromID.put(Receiver.groupFromID.get(id).getName(),id);
            }
        });



        //定时检测爬虫运行状态，不正常自动重启线程读取记录表记录继续爬取。
        String s=null;
        while(true) {
            ThreadUtil.sleep("检测爬虫程序状态运行正常", PropertiesUtil.getProperties("errortime",Integer.class));

            for (Save save : biliDao.selectSave(null)) {
                if(save.getId()==4){
                    if(s==null){
                        s=save.getBilibili();
                    }else if(!s.equals(save.getBilibili())){
                        s=save.getBilibili();
                    }else{
                        InsertService.stop=true;
                        thread.interrupt();
                    }

                }
                log.info(save);
            }
        }
    }

    /**
     * 备份数据库
     */
    @Scheduled(cron = "${dbbackup}")
    public  void saveDataBase(){
//        BiliUtil.openImage(new File("webapp/image/dbbackup.jpg"));
        Date date=new Date();
        String date_1=TimeUtil.formatDate(date,"HH_mm_ss");
        String date_2=TimeUtil.formatDate(date,null);

        //当前日期年月日作为备份数据库的目录
        String todayDir=localPath+date_2+"/";

        //当前日期时分秒作为备份数据库文件的文件名
        File sqlFile=new File(todayDir+date_1+".sql");

        //调用mysqldump备份命令备份数据库
        //运行备份命令
        try {
            FileUtils.writeStringToFile(sqlFile,null,"GBK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String command="mysqldump --default-character-set=utf8 -u"+dbusername+" -p"+dbpassword+" bilibili "+backuptables+">"+sqlFile.getAbsolutePath();

        RunCommand.runCommand(command);
//        File file=new File("run.bat");
//        try {
//            FileUtils.writeStringToFile(file,command,"GBK");
//            Desktop.getDesktop().open(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //每天定时打包一次数据库放到服务器
        File _7zFile=new File(serverPath+date_2+"/"+date_1+".7z");
        //打包sql文件
        RunCommand.compress(_7zFile,sqlFile,_7zpwd);

        //上传sql到百度云
//        uploadBdu(serverPath);
        String oldDirs[]=new String[]{localPath,serverPath};
        for(String dir:oldDirs) {
            for (File subdir : new File(dir).listFiles()) {
                if (FileUtils.isFileOlder(subdir, date)) {
                    try {
                        FileUtils.deleteDirectory(subdir);
                    } catch (IOException e) {
                        log.error("无法删除旧备份目录" + e.getMessage());
                    }
                    log.info("删除旧备份目录" + subdir.getAbsoluteFile());
                }
            }
        }

        //临时文件，选中扩展名为out格式的文件
        Collection<File> fileList=FileUtils.listFiles(FileUtils.getTempDirectory(),new String[]{"out"},true);
        for(File f:fileList){
            //确认是idea产生的临时文件则删除
            if(f.getName().contains("idea")){
                try {
                    FileUtils.forceDelete(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        BiliUtil.updateAccesskey();
    }



}
