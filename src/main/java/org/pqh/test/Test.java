package org.pqh.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.scienjus.smartqq.Receiver;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pqh.entity.vstorage.Vstorage;
import org.pqh.qq.DoSoming;
import org.pqh.task.Listener;
import org.pqh.task.TaskBili;
import org.pqh.task.TaskCid;
import org.pqh.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.pqh.util.SpringContextHolder.*;

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
    @Value("${mysqlPath}")
    private String mysqlPath;
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
    @Value("${errortime}")
    private int errortime;

    public static void main(String[] args) throws Exception {
        log.info("开始爬虫程序");
        new Test().testTask();
        SpringContextHolder.close();
        System.exit(0);
        log.info("结束爬虫程序");

    }



    /**
     * 测试各种方法
     */
    @org.junit.Test
    public void testMethod() {

    }

    public  void testTask(){

        Thread insertCid=new Thread(()->{
            try {
                addTask(2,1);
            } catch (InterruptedException e) {
                log.error("insertCid close");
            }
        },"insertCid");
        Thread insertVstorage=new Thread(()->{
            try {
                addTask(3,0);
            } catch (InterruptedException e) {
                log.error("insertVstorage close");
            }
        },"insertVstorage");

        excute(insertVstorage);
        excute(insertCid);
        excute(()-> {
            Receiver.main(new String[]{});
            for(long id:Receiver.groupFromID.keySet()){
                DoSoming.groupFromID.put(Receiver.groupFromID.get(id).getName(),id);
            }
        });

        Listener listener = new Listener();

        TaskBili taskBili=new TaskBili();
        taskBili.addObserver(listener);

        Thread bili=new Thread(taskBili,"insertBili");

        excute(bili);

//监控UDP端口收到指令进行相应的操作废弃，用smartqq机器人可取代
//        Client client=new Client(Client.UDP);
//        client.connect(6767,0);
//        client.getMsg((inetAddress,str)->{
//            if(str.startsWith("exit")){
//                threadPoolTaskExecutor.shutdown();
//            }else if(str.startsWith("restart bili")){
//                bili.interrupt();
//            }else if(str.startsWith("stop")){
//                return false;
//            }else{
//                log.info("未知命令："+str);
//            }
//            if(threadPoolTaskExecutor.getActiveCount()==0){
//                return false;
//
//            }
//           log.info("活动线程"+threadPoolTaskExecutor.getThreadPoolExecutor().getActiveCount());
//           return true;
//        });

    }

    /**
     * 获取动画开播日期
     * @param document
     * @return
     */
    public static String getInfo(Document document){
        if(document.select("ul.polysemantList-wrapper .selected").text().contains("动画")){
            Elements elements=document.select("div.basic-info>dl>dt");
            for(Element element:elements){
                if(element.text().equals("播放期间")){
                    int index=elements.indexOf(element);
                    element=document.select("div.basic-info>dl>dd").get(index);
                    return element.text();
                }
            }
        }
        Elements elements=document.select("ul.polysemantList-wrapper>.item>a");
        for(Element element:elements){
            if(!element.attr("title").isEmpty()&&element.attr("title").contains("动画")){
                log.info(element.attr("title")+"跳转到动画条目"+ApiUrl.baikeIndex.getUrl(element.attr("href")));
                return getInfo(CrawlerUtil.jsoupGet(ApiUrl.baikeIndex.getUrl(element.attr("href")),Document.class, Connection.Method.GET));
            }
        }
        return "";
    }

    /**
     * 压缩备份文件
     * @param _7zFile 压缩包文件
     * @param sqlFile 数据库文件
     */
    public  void compress(File _7zFile,File sqlFile){
        String command="7z a -t7z "+_7zFile.getAbsolutePath()+" "+sqlFile.getAbsolutePath()+" -mx=9 -m0=LZMA2:a=2:d=26 -ms=4096m -mmt -p"+_7zpwd;
        runCommand(command,false);
    }

    /**
     * 删除旧的备份文件
     * @param date 比较的时间
     * @param parentDir 备份文件父目录
     */
    public  static void delOldFile(Date date,String parentDir){
        for(File subdir:new File(parentDir).listFiles()) {
            if (FileUtils.isFileOlder(subdir, date)) {
                try {
                    FileUtils.deleteDirectory(subdir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.info("删除旧备份目录" + subdir.getAbsoluteFile());
            }
        }
    }

    /**
     * 删除junit产生的临时文件
     */
    @Scheduled(cron = "${dbbackup}")
    public void deleteTestTemp(){
        //临时文件，选中扩展名为out格式的文件
        Collection<File> fileList=FileUtils.listFiles(FileUtils.getTempDirectory(),new String[]{"out"},true);
        for(File file:fileList){
            //确认是idea产生的临时文件则删除
            if(file.getName().contains("idea")){
                file.delete();
            }
        }
    }

    public static void uploadBdu(String path){
        runCommand("aria2",false);
        ThreadUtil.sleep(1);
        String command="bypy.bat upload \""+path+"\" --downloader aria2";
        runCommand(command,false);
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
        String command="\""+mysqlPath+"bin/mysqldump\" --default-character-set=utf8 -u"+dbusername+" -p"+dbpassword+" bilibili "+backuptables+">"+sqlFile.getAbsolutePath();
        try {
            FileUtils.writeStringToFile(new File("test.bat"),command,"GBK");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new File(todayDir).mkdir();
        runCommand("test.bat",true);
//        TaskShowImg taskShowImg=new TaskShowImg("数据库于"+TimeUtil.formatDate(null,null)+"备份到"+sqlFile.getAbsolutePath(),null);
//        threadPoolTaskExecutor.execute(taskShowImg);

        //每天定时打包一次数据库放到服务器
        File _7zFile=new File(serverPath+date_2+"/"+date_1+".7z");
        //打包sql文件
        compress(_7zFile,sqlFile);
//        taskShowImg=new TaskShowImg("数据库于"+TimeUtil.formatDate(null,null)+"打包到"+_7zFile.getAbsolutePath(),null);
//        threadPoolTaskExecutor.execute(taskShowImg);
        //上传sql到百度云
//        uploadBdu(serverPath);
//        taskShowImg=new TaskShowImg("数据库于"+TimeUtil.getNowDate()+"上传到百度云",null);
//        threadPoolTaskExecutor.execute(taskShowImg);

        delOldFile(date,localPath);
        delOldFile(date,serverPath);
    }

    /**
     * 调用命令行运行命令
     * @param command 运行命令
     * @param flag 是否保留命令行文件
     */
    public static void  runCommand(String command,boolean flag){
        InputStreamReader ir=null;
        BufferedReader br=null;
        InputStream in=null;
        try {
            long a=System.currentTimeMillis();
            Process ps=Runtime.getRuntime().exec(command);
            in=ps.getInputStream();
            ir = new InputStreamReader(in,"GBK");
            br = new BufferedReader(ir);
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.replaceAll(" ","").isEmpty()) {
                    log.info(line);
                }
            }
            long b=System.currentTimeMillis();
            log.info("运行命令花费时间"+TimeUtil.longTimeFormatString(b-a));

        } catch (IOException e) {
            LogUtil.outPutLog(LogUtil.getLineInfo(),e);
        }finally {
            try {
                if(in!=null)
                    in.close();
                if(ir!=null)
                    ir.close();
                if(br!=null)
                    br.close();
                System.gc();
                File file=new File(command);
                if(file.exists()&&flag){
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把json对象存进map里面
     * @param jsonNode json对象
     * @param map json对象转换的实体类字典
     * @param classname 根节点名称
     * @param flag 反射操作对象是否为集合的标记
     * @param index 反射当前集合某个索引的对象
     * @param cid
     * @return
     */
    public Map getMap(JsonNode jsonNode,Map map,String classname,boolean flag,int index,int cid){
        Iterator<String> iterator=jsonNode.fieldNames();
        while(iterator.hasNext()) {
            String key=iterator.next();
            JsonNode subNode=jsonNode.get(key);
            if (subNode.isArray()) {
                classname = NodeUtil.getChildNode(classname,key.toString());

                for (int i=0;i<subNode.size();i++) {
                    index = i;
                    ((List)map.get(classname)).add(ReflexUtil.getObject(classname));
                    getMap(subNode.get(i), map, classname,true,index,cid);
                    ReflexUtil.setObject(((List)map.get(classname)).get(index),"cid",String.valueOf(cid));
                    ReflexUtil.setObject(((List)map.get(classname)).get(index),"id",String.valueOf(i+1));
                }
                classname=NodeUtil.getParentsNode(classname);
            } else if (subNode.isObject()&&subNode!=null) {
                JsonNode copyNode = jsonNode;
                classname = NodeUtil.getChildNode(classname,key.toString());
                getMap(subNode, map, classname,false,index,cid);
                ReflexUtil.setObject(map.get(classname),"cid",String.valueOf(cid));
                jsonNode = copyNode;
                classname = NodeUtil.getParentsNode(classname);
            } else {
                String value = subNode.asText();
                if(flag){
                    Object o=((List)map.get(classname)).get(index);
                    ReflexUtil.setObject(o, key.toString(), value);
                }else {
                    map.put(classname, ReflexUtil.setObject(map.get(classname), key.toString(), value));
                }
            }
        }
        if(classname.equals(Vstorage.class.getName())) {
            map.put(classname, ReflexUtil.setObject(map.get(classname), "id", String.valueOf(cid)));
        }
        return map;
    }

    /**
     * 通过反射把相应的爬虫数据写入数据库不同表
     * @param map
     */
    public void setData(Map<String,Object> map){
        Class c=vstorageDao.getClass();
        String name=null;
        String classnames[] = exclude.split(",");
        for(String classname:classnames){
            map.remove(classname);
        }

        for(String key:map.keySet()) {
            Method insertMethod = null;
            Method updateMethod = null;
            try {
                name = key.substring(key.lastIndexOf(".") + 1);
                insertMethod = c.getDeclaredMethod("insert" + name, Class.forName(key));
                updateMethod = c.getDeclaredMethod("update" + name, Class.forName(key));
            } catch (NoSuchMethodException e) {
                LogUtil.outPutLog(LogUtil.getLineInfo(),e);
            } catch (ClassNotFoundException e) {
                LogUtil.outPutLog(LogUtil.getLineInfo(),e);
            }
            if (map.get(key).getClass().getName().contains("List")) {
                List list = (List) map.get(key);
                if (list.size() == 0) {
                    continue;
                }
                for (Object object : (List) map.get(key)) {
                    if (ReflexUtil.checkFieldsNaN(object)) {
                        continue;
                    }
                    try {
                        insertMethod.invoke(vstorageDao, object);
                    } catch (InvocationTargetException e) {
                        Field field=null;
                        String detailMessage=null;
                        try {
                            field=Throwable.class.getDeclaredField("detailMessage");
                            field.setAccessible(true);
                            detailMessage=field.get(e.getTargetException().getCause()).toString();
                            detailMessage=BiliUtil.matchStr(detailMessage,"\\d+\\-\\d+",String.class);
                            if(!detailMessage.isEmpty()){
//                                log.info("更新"+name+"复合主键："+detailMessage+"信息");
                                updateMethod.invoke(vstorageDao, object);
                            }
                        } catch (NoSuchFieldException e1) {
                            log.error(object+"无法获取详细报错信息！！！");
                        } catch (IllegalAccessException e1) {
                            LogUtil.outPutLog(LogUtil.getLineInfo(),e);
                        } catch (InvocationTargetException e1) {
                            LogUtil.outPutLog(LogUtil.getLineInfo(),e);
                        }
                    } catch (IllegalAccessException e) {
                        LogUtil.outPutLog(LogUtil.getLineInfo(),e);
                    }
                }

            } else {
                if (ReflexUtil.checkFieldsNaN(map.get(key))) {
                    continue;
                }
                try {
                    insertMethod.invoke(vstorageDao, map.get(key));
                } catch (InvocationTargetException e) {
                    if(e.getTargetException().getClass().equals(DuplicateKeyException.class)){
//                        log.info("更新"+name+"主键："+key+"信息");
                    }else{
                        LogUtil.outPutLog(LogUtil.getLineInfo(),e);
                    }
                } catch (IllegalAccessException e) {
                    LogUtil.outPutLog(LogUtil.getLineInfo(),e);
                }
            }
        }
    }

    public static <T>T getSave(int type,int id){
        if(type==0) {
            String str[] = biliDao.getAid(1).getBilibili().split(":");
            int num[] = new int[2];
            num[0] = Integer.parseInt(str[0]);
            num[1] = Integer.parseInt(str[1]);
            return (T) num;
        }else{
            return (T) Integer.valueOf((biliDao.getAid(id).getBilibili()));
        }
    }

    public  boolean checkRun(){
        String s1=biliDao.getAid(1).getBilibili();
        ThreadUtil.sleep(errortime);
        String s2=biliDao.getAid(1).getBilibili();
        return s1.equals(s2);
    }

    public static void addTask(int id,int type) throws InterruptedException{
        for (int cid = getSave(1,id);; cid++) {
            if(biliDao.getAid(id).isLatest()){
                ThreadUtil.sleep(15);
                cid=getSave(1,id);
            }
            TaskCid taskCid=new TaskCid(cid,type);
            excute(new Thread(taskCid,(type==0?"insertVstorage":"insertCid")+":"+cid));
        }
    }



    /**
     * 多线程执行任务简单封装
     * @param runnable
     */
    public static void excute(Runnable runnable){
        threadPoolTaskExecutor.execute(runnable);
        ThreadUtil.sleep(100l);
    }


}
