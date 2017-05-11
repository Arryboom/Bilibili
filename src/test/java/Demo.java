import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.pqh.entity.Cid;
import org.pqh.util.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by reborn on 2017/4/23.
 */
public class Demo {
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
            RunCommand.decompression(file,"E:\\BaiduNetdiskDownload");
            FileUtils.deleteQuietly(file);
        }
    }
@Test
    public void testDoming() {

        List<String> domains = null;
        try {
            domains = FileUtils.readLines(new File("E:\\ideaIU-2017.1.win\\Projects\\selenium\\domain.txt"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < domains.size();i++ ) {
            String checkDomain=domains.get(i);
            JsonNode jsonNode=CrawlerUtil.jsoupGet("http://cgi.urlsec.qq.com/index.php?a=check&m=check&url="+checkDomain, CrawlerUtil.DataType.json, Connection.Method.GET);
            int j=jsonNode.get("data").get("results").get("whitetype").asInt();
            if(j==3){
                try {
                    FileUtils.writeStringToFile(new File("safedomain.txt"),checkDomain,"UTF-8",true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ThreadUtil.sleep(null,1);
            System.out.println("读取到第" + (i+1) + "行");
        }

    }

}
