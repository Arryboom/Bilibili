package org.pqh.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 10295 on 2016/7/9.
 */
public class TsdmUtil {
    private static Logger log=Logger.getLogger(TsdmUtil.class);
    public static final String SC="zh-hans";
    public static final String TC="zh-hant";
    static {
        CrawlerUtil.cookie=PropertiesUtil.getProperties("TSDMCookie",String.class);
    }
    public static List getNewBangumi(){
        List<String> list=new ArrayList<String>();
        Document document= null;
        document = CrawlerUtil.jsoupGet(ApiUrl.tsdmMusicIndex.getUrl(), Document.class, Connection.Method.GET);
        Elements elements=document.select("#postmessage_3261490>a");

        for(Element element:elements){
            if(element.select("font[color=red]").size()!=0){
                list.add(element.attr("href"));
            }
        }
        list.remove(0);
        return list;
    }


    public static List getMusicHref(List<String> list){
        List<String> musicHref=new ArrayList<String>();
        for(String href:list) {
            Document document = null;
            document = CrawlerUtil.jsoupGet(href,Document.class,Connection.Method.GET);
            Elements elements=document.select("a:contains(OP)");
            if(elements.size()!=0){
                musicHref.add(elements.get(0).attr("href"));
            }
            elements=document.select("a:contains(ED)");
            if(elements.size()!=0){
                musicHref.add(elements.get(0).attr("href"));
            }
        }
            return musicHref;
    }

    public static Map<String,String> getYunHref(List<String> list){
        Map<String,String> yunHref=new HashMap<String, String>();
        for(String href:list) {
            Document document = null;
            document = CrawlerUtil.jsoupGet(href,Document.class,Connection.Method.GET);
            for(Element element:document.select("a[href^="+ApiUrl.yunPan.getUrl()+"]")){
                href=element.attr("href");
                String pwd=StringUtil.matchStr(document.html(),"密码:\\s*\\w+",String.class).replaceAll("\\W+","");
                yunHref.put(href,pwd);
            }
        }
        return yunHref;
    }

    public static String switchZN (String chinese,String font){
        JsonNode jsonNode=CrawlerUtil.jsoupGet(ApiUrl.zhConvert.getUrl(),JsonNode.class,Connection.Method.POST,"code",chinese,"operate",font);
        return jsonNode.get("text").asText();
    }
}
