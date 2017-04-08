package org.pqh.qq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.pqh.entity.Bdu;
import org.pqh.entity.Param;
import org.pqh.entity.Tsdm;
import org.pqh.util.ApiUrl;
import org.pqh.util.CrawlerUtil;
import org.pqh.util.StringUtil;
import org.pqh.util.TimeUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static org.pqh.util.SpringContextHolder.bduDao;
import static org.pqh.util.SpringContextHolder.biliDao;
/**
 * 信息推送
 * Created by reborn on 2017/3/29.
 */
@Component
public class MessagePush {
    private static Logger log= Logger.getLogger(MessagePush.class);

    public static boolean acgdoge=true;

    public static boolean ithome=false;

    public static List<Tsdm> todayTsdms;

    private static Map<String,Map<String,String>> pushUrl = new HashMap<>();

    public void put(String animeName,String videoSite,String url){
        Map<String,String> map=pushUrl.get(animeName);
        String value=map.get(videoSite);
        if(value==null){
            DoSoming.messagePush(url);
            map.put(videoSite,url);
            pushUrl.put(animeName,map);
        }
    }

    //    @Resource
//    private BiliDao biliDao;
    @Scheduled(cron = "0 0 0/1 * * ?")
    public  void acgdoge(){
        if(!acgdoge){
            return;
        }

        Document document=null;
        int count=0;
        while(document==null&&count<3){
            document= CrawlerUtil.jsoupGet(ApiUrl.acgdoge.getUrl(),Document.class, Connection.Method.GET);
            count++;
        }

        Param param=biliDao.selectParam("acgdoge");
        int a=0;
        int b=Integer.parseInt(param.getValue());

        Elements articles=document.select("#content>article");
        List<String> msgs=new ArrayList<>();
        for(int i=articles.size()-1;i>=0;i--){
            Element article=articles.get(i);
            Element title=article.select("h2.post_h>a").first();

            String href=title.attr("href");
            a=Integer.parseInt(href.substring(href.lastIndexOf("/")+1));
            if(a>b){
                String type=article.select("span.post_ct").text();
                List<String> tags=new ArrayList<>();
                for(Element e:article.select("div.post_tag>a")){
                    tags.add(e.text());
                }
                String msg=type+"\t\""+title.text()+"\"\t传送门："+href+"\n标签："+tags;
                log.info(msg);
                msgs.add(msg);
            }
        }
        DoSoming.messagePush(msgs);
        param.setValue(a+"");
        biliDao.updateParam(param);
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void ithome(){
        if(!ithome){
            return;
        }

        Document document= null;
        int count=0;
        while(document==null&&count<3){
            document= CrawlerUtil.jsoupGet(ApiUrl.ithome.getUrl(),Document.class, Connection.Method.GET);
            count++;
        }

        Param param=biliDao.selectParam("ithome");
        int a=0;
        int b=Integer.parseInt(param.getValue());

        Elements newList=document.select("div.block.new-list-1>ul:eq(0)>li.new>span.title>a");
        List<String> msgs=new ArrayList<>();
        for(int i=newList.size()-1;i>=0;i--){
            Element _new =newList.get(i);
            String href=_new.attr("href");
            String text=_new.text();
            a=Integer.parseInt(href.substring(href.lastIndexOf("/")+1).replaceAll("\\D+",""));
            if(a>b){
                String msg=text+"\t传送门："+href;
                log.info(msg);
                msgs.add(msg);
            }
        }
        DoSoming.messagePush(msgs);
        param.setValue(a+"");
        biliDao.updateParam(param);
    }

    public void parseYouku(String animeName){
        Document document=null;
        try {
            document=CrawlerUtil.jsoupGet(ApiUrl.youkuSerach.getUrl(URLEncoder.encode(animeName,"UTF-8")),Document.class, Connection.Method.GET);
            Elements a=document.select("h2.base_name>a[target=_blank]");
            int log_sid=0;
            for(Element href:a){
                int _log_sid=Integer.parseInt(href.attr("_log_sid"));
                if(_log_sid>log_sid){
                    log_sid=_log_sid;
                }
            }
            String link=document.select("h2.base_name>a[_log_sid="+log_sid+"]").attr("href");
            document=CrawlerUtil.jsoupGet("http:"+link,Document.class, Connection.Method.GET);
            if(document.select(".p-btn.p-btn-gray").size()>0){
                log.info(animeName+"\t未开播");
            }else {
                link = document.select(".p-play>a").attr("href");
                document = CrawlerUtil.jsoupGet("http:" + link, Document.class, Connection.Method.GET);
                log.info("优酷番剧播放列表地址：" + "http:" + link);
                String tvintro = document.select("#Drama p.tvintro").text();
                String msg=animeName + "\t" + tvintro;
                log.info(msg);
                put(animeName,"youku",msg);
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void parseBilibili(String animeName){
        Document document=null;
        try {
            document=CrawlerUtil.jsoupGet(ApiUrl.bangumiSearch.getUrl(URLEncoder.encode(animeName,"UTF-8")),Document.class, Connection.Method.GET);
            Elements elements=document.select("a[href^=//bangumi.bilibili.com/anime/]");
            if(elements.size()>0) {
                String sessionId = elements.first().attr("href").split("\\?")[0].replaceAll("\\D+", "");
                document=CrawlerUtil.jsoupGet(ApiUrl.bangumiAnime.getUrl(sessionId),Document.class, Connection.Method.GET);
                String jsonStr=document.body().html();
                jsonStr=jsonStr.substring(jsonStr.indexOf("{"),jsonStr.lastIndexOf("}"))+"}";
                JsonNode jsonNode=new ObjectMapper().readTree(jsonStr).get("result").get("episodes");
                if(jsonNode.size()>0){
                    jsonNode=jsonNode.get(0);
                    String av=jsonNode.get("av_id").asText();
                    String index_title=jsonNode.get("index_title").asText();
                    String index=jsonNode.get("index").asText();
                    String msg=animeName+"\t已更新第"+index+"集\t"+index_title+"\n传送门:"+ApiUrl.AV.getUrl(av,1);
                    log.info(msg);
                    put(animeName,"Bilibili",ApiUrl.AV.getUrl(av,1));
                }else{
                    log.info(animeName+"\t没开播");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parseiqiyi(String animeName){
        Document document;
        String href=null;
        try {
            document=CrawlerUtil.jsoupGet(ApiUrl.iqiyiSerach.getUrl(URLEncoder.encode(animeName,"UTF-8")),Document.class, Connection.Method.GET);
            href=document.select("a[title="+ animeName+"]").attr("href");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        document=CrawlerUtil.jsoupGet(href,Document.class, Connection.Method.GET);
        String id=document.select(".play_source").attr("data-doc-id");
        if(id.equals("")){
            log.info(animeName+"\t没有开播");
        }else{
            JsonNode jsonNode=CrawlerUtil.jsoupGet(ApiUrl.iqiyi.getUrl(id),JsonNode.class, Connection.Method.GET).get("video_info");
            jsonNode=jsonNode.get(jsonNode.size()-1);
            String title=jsonNode.get("title").asText();
            String play_url=jsonNode.get("play_url").asText();
            String msg=title+"\t已更新，播放地址："+play_url;
            log.info(msg);
            put(animeName,"iqiyi",play_url);
        }
    }


    public void parseUpdateTsdm(String animeName){
        List<Tsdm> tsdms;
        if(StringUtil.isEmpty(animeName)){
            tsdms= bduDao.selectAllTsdm();
        }else{
            tsdms=bduDao.selectTsdm(animeName);
        }


        for(Tsdm tsdm:tsdms) {
            log.info(tsdm);
            String url = tsdm.getTsdmUrl();
            Document postmessage = CrawlerUtil.jsoupGet(url, Document.class, Connection.Method.GET);
            //
            Elements pstatus = postmessage.select(".pstatus");

            List<String> list=new ArrayList<>();
            String lastUpdateTime[]=null;
            if(!StringUtil.isEmpty(tsdm.getLastUpdateTime())){
                lastUpdateTime=tsdm.getLastUpdateTime().split(",");
            }
            for (int i=0;i<pstatus.size();i++) {
                Element a=pstatus.get(i);
                Element td=a.parent();

                Elements panHref=td.select("a[href*=pan.baidu.com]");
                if(panHref.size()==0){
                    continue;
                }


                String lastUpdate[] = a.text().split(" ");
                log.info(Arrays.asList(lastUpdate));
                Date date = TimeUtil.parseDate(lastUpdate[3] + " " + lastUpdate[4], "yyyy-MM-dd HH:mm");
                if(lastUpdateTime!=null&&i<=lastUpdateTime.length-1){
                    Date date1=TimeUtil.parseDate(lastUpdateTime[i], "yyyy-MM-dd HH:mm");
                    if(date.getTime()>date1.getTime()){
                        String num = a.parent().attr("id").replaceAll("\\D", "");
                        //论坛资源具体楼层链接
                        String msg="天使论坛已经更新该番剧"+tsdm.getAnimeName()+"资源，传送门："+tsdm.getTsdmUrl()+"#"+num;
                        log.info(msg);
                        DoSoming.messagePush(msg);
                        //具体资源链接。

                        //获取字幕组信息
                        Elements subs=td.select("strong:has(font:matches([\\[\\【]))");
                        int index=0;
                        Elements children=td.children();
                        //遍历楼层里面的所有百度云链接
                        for(Element pan :panHref){
                            String desc="";
                            if(subs.size()>0&&subs.size()==1){
                                desc+=subs.first().text();
                            }else if(subs.size()>1){
                                //同一个楼层有多个字幕组资源情况下，需要判断当前资源所属字幕组
                                boolean isLast=subs.get(index).equals(subs.last());
                                if(isLast){
                                    desc+=subs.last().text();
                                }else if(children.indexOf(pan.parent())<children.indexOf(subs.get(index+1).parent())) {
                                    desc+=subs.get(index).text();
                                }else{
                                    desc+=subs.get(++index).text();
                                }
                            }

                            Element element= pan.previousElementSibling();
                            Node node= pan.previousSibling();
                            if(element!=null&&!element.text().isEmpty()) {
                                String d=element.text().replaceAll("[:：]","");
                                if(!d.isEmpty()) {
                                    desc += ";"+d;
                                }
                            }
                            if(node!=null&&!((TextNode) node).isBlank()&&!node.equals(element)){
                                String d=node.outerHtml().replaceAll("[:：链接：\\.\\s]","");
                                if(!d.isEmpty()){
                                    desc+=";"+d;
                                }
                            }

                            String href= pan.attr("href");
                            Node pass= pan.nextSibling();
                            String password=null;
                            if(pass!=null&&pass.outerHtml().contains("密码")){
                                password=pass.outerHtml().replaceAll("\\W+","").replaceAll("nbsp","");
                            }

                            msg="desc="+desc+" href="+href+" password="+password;
                            log.info(msg);
                            DoSoming.messagePush(msg);

                            Bdu bdu=new Bdu(href,password,desc);
                            try {
                                bduDao.insertBdu(bdu);
                            }catch (DuplicateKeyException e){
                                log.error("云盘链接无法入库："+e.getMessage());
                            }
                        }
                    }
                }
                String time=TimeUtil.formatDate(date, "yyyy-MM-dd HH:mm");
                log.info(time);
                list.add(time);

            }
            tsdm.setLastUpdateTime(StringUtils.join(list,","));
            bduDao.updateTsdm(tsdm);
        }
    }

    public void tsdm(){

        //获取萌娘百科番剧条目
        Document anime = CrawlerUtil.jsoupGet("https://zh.moegirl.org/zh-hans/日本2017年春季动画", Document.class, Connection.Method.GET);
        //萌娘百科番局条目父元素
        Element mw = anime.select("#mw-content-text").first();

        for (Element a : anime.select("li.toclevel-1:gt(0)>a")) {
            Element span=mw.select("[id="+a.attr("href").replace("#","")+"]").first();
            String title = span.text();
            if(title.equals("参见")){
                break;
            }
            log.info("title="+title);
            Element dl = span.parent().nextElementSibling();
            int i = 1;
            while (!dl.tagName().equals("dl")) {
                dl = dl.nextElementSibling();
                i++;
            }

            String time[] = dl.select("dd:eq(1)").text().split(" ");
            log.info("时间：" + Arrays.asList(time));
            //格式化开播时间
            String playtime = time[0].replace("起", "");
            Date formatPlayTime = DateUtils.parseDate(playtime, new String[]{"yyyy年MM月dd日","yyyy年MM月"});

            String updatetime = time[1];
            String copyright = "";
            //如果中国大陆有放送权提取放松地址信息
            if (dl.text().contains("中国大陆")) {
                copyright = dl.select("dd:contains(中国大陆)").first().text().split("：")[1];
                log.info("中国大陆放送权：" + copyright);
            }
            Tsdm tsdm = new Tsdm("", title, formatPlayTime, updatetime, copyright,"");
            try {
                bduDao.insertTsdm(tsdm);
            } catch (DuplicateKeyException e) {
                log.error("异常信息" + e.getMessage());
            }

        }
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void check(){
        for(Tsdm tsdm:todayTsdms){
            String copyrights[]=tsdm.getCopyright().split("／");
            log.info(tsdm.getAnimeName()+"版权:"+Arrays.asList(copyrights));
            for(String copyright:copyrights){
                if("Bilibili".equals(copyright)){
                    parseBilibili(tsdm.getAnimeName());
                }else if("优酷网".equals(copyright)||"土豆网".equals(copyright)){
                    parseYouku(tsdm.getAnimeName());
                }else if("爱奇艺".equals(copyright)){
                    parseiqiyi(tsdm.getAnimeName());
                }else if(StringUtils.isNotEmpty(tsdm.getTsdmUrl())){
                    parseUpdateTsdm(tsdm.getAnimeName());
                }
            }
        }

    }

    @Scheduled(cron = "0 0 0/24 * * ?")
    public void doUpdate() {
        if (todayTsdms == null || TimeUtil.formatDate(new Date(), "HH:mm").equals("00:00")){
            String e = TimeUtil.formatDate(new Date(), "E").replace("星期", "每周");
            todayTsdms = bduDao.selectUpdate(e + "%");
    }
        pushUrl = new HashMap<>();
        List<String> strings=new ArrayList<>();
        for(Tsdm tsdm:todayTsdms){
            pushUrl.put(tsdm.getAnimeName(),new HashMap<>());
            strings.add(tsdm.toString());
        }
        log.info(strings);
        DoSoming.messagePush(strings);
    }

    public static void main(String[] args) {

//            new MessagePush().parseiqiyi( "爱丽丝与藏六");
        //new MessagePush().parseBilibili("小林家的龙女仆");
//        new MessagePush().parseYouku("重启咲良田");
        // new MessagePush().parseUpdateTsdm("进击的巨人 season 2");




    }

}
