import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.pqh.entity.Cid;
import org.pqh.task.DynamicTimer;
import org.pqh.util.*;

import java.io.File;

/**
 * Created by reborn on 2017/4/23.
 */

public class Demo{
    public static void main(String[] args) {

    }
    /**
     *测试反射工具类
     */
    @Test
    public void testReflex(){
        Cid cid=new Cid();
        cid.setAid(123);
        System.out.println(cid.getAid());
        ReflexUtil.setObject(cid,"aid",456);
        System.out.println(cid.getAid());
    }

    /**
     * 测试下载工具类
     */
    @Test
    public void testDownLoad(){
        int count=0;
        for(int pn=1;pn<11;pn++){
            Document document= CrawlerUtil.jsoupGet("http://tieba.baidu.com/p/5084910382?pn="+pn,CrawlerUtil.DataType.domcument, Connection.Method.GET);
            System.out.println(document.title());
            Elements elements=document.select(".BDE_Image");
            for(Element image:elements){
                String href=image.attr("src");
                DownLoadUtil.downLoad(href,"image/"+(count++)+".jpg");
            }

        }

    }

    /**
     * 测试命令行工具类
     */
    @Test
    public void testdecompression(){
        File[] files=new File("E:\\BaiduNetdiskDownload").listFiles();
        for(File file:files) {
            RunCommandUtil.decompression(file,"E:\\BaiduNetdiskDownload");
            FileUtils.deleteQuietly(file);
        }
    }

    /**
     *测试动态定时器类
     */
    @Test
    public void testDynamicTimer(){
        DynamicTimer dynamicTimer=SpringContextHolder.getBean("dynamicTimer");

        DynamicTimer.maps.put("abc","0/1 * * * * ?");
       dynamicTimer.addTriggerTask(()->{
           LogUtil.getLogger().info("HI");
       },"abc");

        DynamicTimer.maps.put("abc","0/1 * * * * ?");
        dynamicTimer.addTriggerTask(()->{
            LogUtil.getLogger().info("Hello");
        },"abc");
       int i=10;
       while (i-->0){
           ThreadUtil.sleep(10);
       }

    }

  }



