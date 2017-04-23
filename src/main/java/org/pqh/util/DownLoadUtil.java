package org.pqh.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.pqh.entity.vstorage.Data;

import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.Queue;

import static org.pqh.util.SpringContextHolder.vstorageDao;

/**
 * Created by 10295 on 2016/8/7.
 */

public class DownLoadUtil {
    private static Logger log= Logger.getLogger(DownLoadUtil.class);
    /**
     * 下载资源
     * @param href 下载链接
     * @param outputPath 输出文件
     */
    public static void downLoad(String href,String outputPath){
        log.info("下载链接:"+href);
        OutputStream outputStream = null;
        HttpEntity httpEntity=null;
        CloseableHttpResponse closeableHttpResponse=null;
        File file=null;
        try {
            closeableHttpResponse=CrawlerUtil.doGet(href);
            httpEntity=closeableHttpResponse.getEntity();
//            Class c=httpEntity.getClass().getSuperclass();
//            Field field=c.getDeclaredField("wrappedEntity");
//            field.setAccessible(true);
//            String values=field.get(httpEntity).toString();
//            values=values.substring(values.indexOf("[")+1,values.indexOf("]"));
//            Map<String,String> map=new HashMap<String, String>();
//            for(String value:values.split(",")){
//                map.put(value.split(":")[0],value.split(":")[1].trim());
//            }
            file=new File(outputPath);
            if(file.exists()){
                file.delete();
            }else if(file.createNewFile()){
                outputStream=new FileOutputStream(file);
                httpEntity.writeTo(outputStream);
            }else{
                throw new RuntimeException("写入文件异常"+file.getPath());
            }

        } catch (IOException e) {
            if(e.toString().contains("Connection timed out")){
                downLoad(href,outputPath);
            }
        }  finally {
            try {
                if(outputStream!=null){
                    outputStream.close();
                }
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                log.error(e);
            }
        }

    }

    /**
     *  获取弹幕
     * @param href
     * @return
     */
    public static <T>T downLoadDanmu(String href){
        SAXReader saxReader=new SAXReader();
        InputStream in= null;
        HttpEntity httpEntity=null;
        try {
            httpEntity = CrawlerUtil.doGet(href).getEntity();
            in = httpEntity.getContent();
            if(href.contains("rolldate")) {
                ObjectMapper objectMapper=new ObjectMapper();
                JsonNode jsonNode=objectMapper.readTree(in);
                return (T)jsonNode;
            }
            else {
                Element root=saxReader.read(in).getRootElement();
                if(root.element("source")!=null&&root.element("source").getText().equals("e-r")){
                    log.error(href+"该链接没有弹幕");
                    return null;
                }else{
                    return (T)root;
                }
            }
        } catch (IOException e) {
            log.error(e);
        } catch (DocumentException e) {
            log.error(e);
        }finally {
//            try {
//                EntityUtils.consume(httpEntity);
//            } catch (IOException e) {
//                log.error(e);
//            }

        }
        return null;
    }

    /**
     *
     * @param sohu_m 滚动样式
     * @param sohu_s 字体大小
     * @param sohu_p 位置
     * @return
     */
    public static List sohuDanmu(String sohu_m, String sohu_s, String sohu_p) {
		/* m 滚动样式   f  固定  l 移动
   	 s 字体大小   l  大   m 中    s 小
   	 p  位置      t  顶部 m 中部  b  底部*/
        String bilistyle="";
        String biliFontsize="";
        List ls=new ArrayList();
        if(sohu_m.contains("f")){
            if(sohu_p.equals("t") )
                bilistyle="5";
            else if (sohu_p.equals("m"))
                bilistyle="5";
            else
                bilistyle="4";
        }


        if(sohu_m.equals("l")){
            if(sohu_p.equals("t") )
                bilistyle="1";
            else if (sohu_p.equals("m"))
                bilistyle="1";
            else
                bilistyle="2";
        }


        if(sohu_s.equals("l")){
            biliFontsize="30";
        }else if(sohu_s.equals("m")){
            biliFontsize="25";
        }else {
            biliFontsize="18";
        }

        ls.add(bilistyle);
        ls.add(biliFontsize);
        return ls;
    }

    public static void main(String[] args) {
        pptvDanmu("http://v.pptv.com/show/lJ56ibGDGNnTXVb0.html","test/测试pptv弹幕");

    }

    public static void pptvDanmu(String url,String filePath){
        Document document=CrawlerUtil.jsoupGet(url,Document.class, Connection.Method.GET);
        String webcfg=document.html().substring(document.html().indexOf("webcfg")+9);
        JsonNode cfg=null;
        List<String> xml=new ArrayList<>();
        try {
            //视频相关信息
            cfg=new ObjectMapper().readTree(webcfg.substring(0,webcfg.indexOf("]}")+2));
            int pos=0;

            do {
                //弹幕接口
                JsonNode jsonNode = CrawlerUtil.jsoupGet(ApiUrl.pptvDanMu.getUrl(cfg.get("id").asText(),pos), JsonNode.class, Connection.Method.GET).get("data").get("infos");
                pos+=1000;
                if(jsonNode.size()==1&&jsonNode.get(0).get("id").asInt()==0){
                    break;
                }
                for (JsonNode danmu : jsonNode) {
                    if(danmu.get("id").asInt()==0){
                        continue;
                    }
                    String content = danmu.get("content").asText();
                    String play_point = danmu.get("play_point").asInt() / 10 + "";
                    String font_color = danmu.get("font_color").asText().replace("null","ffffff").replace("#","");


                    String d="<d p=\""+play_point+",1,25,"+Integer.parseInt(font_color,16) +",12450,,,\">"+content +"</d>";
                    log.info(d);
                    xml.add(d);
                }
            }while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createDanmuFile(xml,filePath);
    }



    public static void sohuDanmu(String url, String filePath){
        Document document=CrawlerUtil.jsoupGet(url,Document.class, Connection.Method.GET);

        String vid=document.select("meta[property=og:video]").attr("content").replaceAll("\\D","");

        Set<String> xml =new HashSet<>();

        SAXReader reader = new SAXReader();

        org.dom4j.Document doc = null;

        Element d;
        try {
            doc = reader.read("http://cdn.danmu.56.com/xml/2/v_" + vid + ".xml");
            d = (Element) doc.getRootElement().elements("d").get(0);
        } catch (DocumentException e) {
            log.error("获取弹幕文件异常"+e.getMessage());
            return;

        }

        log.info("成功获取"+vid + ".xml");
        List nodes = d.elements("c");
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Element elm = (Element) it.next();


            String time=elm.attributeValue("v");
            String colour=elm.attributeValue("c");
            String values=elm.getText();

            List bilisx = sohuDanmu(elm.attributeValue("m"), elm.attributeValue("s"), elm.attributeValue("p"));
            String bilistyle=bilisx.get(0).toString();
            String biliFontsize=bilisx.get(1).toString();

            String b="<d p=\""+time+","+bilistyle+","+biliFontsize+","+colour +",12450,,,\">"+values +"</d>";
            xml.add(b);

        }


        createDanmuFile(xml,filePath);

    }

    /**
     * 生成弹幕文件
     * @param c 弹幕集合
     * @param filePath 生成路径
     */
    public static void createDanmuFile(Collection<String> c,String filePath){
        List<String> strings=new ArrayList<>();
        strings.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        strings.add("<i>");
        strings.addAll(c);
        strings.add("</i>");

        try {
            File file=new File(filePath +".xml");
            FileUtils.writeLines(file,"UTF-8",strings);
            log.info("弹幕成功下载到"+file.getPath()+"目录下");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载优酷弹幕
     * @param href 视频播放页
     */
    public static void youKuDanmu(String href){
        int index=0;
        Elements elements=null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        do {

            Document document = CrawlerUtil.jsoupGet(href, Document.class, Connection.Method.GET);
            elements=document.select("div.items>li.item");
            String title=elements.get(index).attr("title");
            href=ApiUrl.youkuPlay.getUrl(elements.get(index++).attr("id").replace("item_",""));
            String keyword = "{";
            String html="";
            for(org.jsoup.nodes.Element element:document.select("script[type=text/javascript]")){
                if(element.html().contains("PageConfig")){
                    html=element.html();
                }
            }
            html = html.substring(html.indexOf(keyword), html.length() - 1).replace("'", "\"");

            try {
                JsonNode jsonNode = mapper.readTree(html);
                title=StringUtil.convertFileName(title);
                youKuDanmu(jsonNode.get("videoId").asText(),"test/"+title+".xml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }while (index<elements.size());
    }

    /**
     *优酷弹幕转换为B站弹幕
     * @param iid
     * @param filePath 弹幕生成路径
     */
    public static void youKuDanmu(String iid, String filePath){
        log.info("iid="+iid+"弹幕生成路径="+ filePath);
        Queue<String> strings = new LinkedList<>();
        int begin=0;
        List<JsonNode> jsonNodes=new ArrayList<>();
        while (true){
            strings.offer(iid);
            strings.offer(begin+"");
            begin++;
            String params[]= BiliUtil.parseXml(strings,ApiUrl.youkuDanMu.getUrl()).get("params_").split(",");
            JsonNode jsonNode = CrawlerUtil.jsoupGet(ApiUrl.youkuDanMu.getUrl(), JsonNode.class, Connection.Method.POST,params);
            if(jsonNode==null){
                break;
            }
            boolean next=jsonNode.get("next")!=null&&jsonNode.get("next").asInt()>0;
            boolean count=jsonNode.get("count")!=null&&jsonNode.get("count").asInt()>0;
            if(next&&count){
                for(JsonNode node:jsonNode.get("data")){
                    jsonNodes.add(node);
                }
            }else{
                break;
            }
        }
        log.info("爬取了"+jsonNodes.size()+"条弹幕");
        Set<String> set=new HashSet<>();
        for(JsonNode s:jsonNodes){
            String content = s.get("content").asText();
            String playat = s.get("playat").asInt()/ 1000.0 + "";
            JsonNode propertis = s.get("propertis");
            String size, color, pos, effect;
            if (!propertis.asText().equals("")) {
                try {
                    propertis=new ObjectMapper().readTree(propertis.asText().replace("\n",""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                size = propertis.get("size").asText();
                size = size.equals("1") ? "18" : "25";
                color = propertis.get("color").asText();
                pos = propertis.get("pos").asText();
                pos = pos.equals("3") ? "1" : pos.equals("4") ? "5" : "4";
                //effect=s.getString("effect");
            } else {
                size = "18";
                color = "16777215";
                pos = "1";
                //effect="0";
            }
            String b = "<d p=\"" + playat + "," + pos + "," + size + "," + color + ",12450,,,\">" + content + "</d>";
            set.add(b);
        }

        createDanmuFile(set,filePath);
    }

    /**
     * 下载历史弹幕
     * @param cid 弹幕编号
     * @param path 保存路径
     * @param flag 弹幕密度没达到标准是否下载历史弹幕
     */
    public static void downLoadDanmu(int cid,String path,boolean flag){
        int max_count=0;
        int min_count=0;
        Set<DefaultElement> set=null;
        String href=ApiUrl.danMu.getUrl(cid);
        org.dom4j.Element element=downLoadDanmu(href);
        if(element==null){
            return;
        }
        set=new HashSet<>(element.elements("d"));
        max_count=Integer.parseInt(element.element("maxlimit").getStringValue());
        min_count=PropertiesUtil.getProperties("danmu%",Integer.class);

        if(flag&&set.size() < max_count * min_count / 100) {
            JsonNode jsonNode = downLoadDanmu(ApiUrl.danmuHistorys.getUrl(cid));
            if (jsonNode != null) {
                for (int i = 0; i < jsonNode.size(); i++) {
                    if (flag && set.size() < max_count * min_count / 100) {
                        Long timestamp = jsonNode.get(i).get("timestamp").asLong();
                        href = ApiUrl.danmuDmroll.getUrl(timestamp,cid);
                        log.debug("正在获取" + TimeUtil.formatDate(new Date(timestamp * 1000), null) + "历史弹幕");
                        element = downLoadDanmu(href);
                        set.addAll(element.elements("d"));
                    } else {
                        break;
                    }
                }
            }
        }


        List<String> list=new ArrayList<>();


        for (DefaultElement e:set){
            list.add(e.asXML());
        }

        createDanmuFile(list,path);

    }


    /**
     * 检查ID有效性
     * @param map ID存放的map
     */
    public static void checkId(Map<String,String> map){
        int count=0;
        for(String key:map.keySet()) {
            if(key.contains("id")) {
                String error=map.get(key)+"：不合法ID参数,ID参数正确格式应该是纯数字，如果是多个ID则数字之间要用逗号隔开";
                if(map.get(key).indexOf(",")==-1&&map.get(key).replaceAll("\\D","").isEmpty()){
                    throw new RuntimeException(error);
                }
                for (String s : map.get(key).split(",")) {
                    if (s.replaceAll("\\D", "").isEmpty()) {
                        throw new RuntimeException(error);
                    } else {
                        count++;
                    }
                }
                log.info(key+"参数共检测出" + count + "个ID准备拼接到sql语句里面进行查询");
            }

        }

    }

    /**
     * 查询条件
     * @param map
     */
    public static void downLoadDanmu(Map<String,String> map, String dirPath, int type){
        long a=System.currentTimeMillis();
        checkId(map);
        log.info("查询参数"+map);
        List<Data> dataList=null;
        switch (type){
            case 1:dataList=vstorageDao.selectData(map);break;
            case 2:dataList=vstorageDao.selectDataCid(map);break;
            default:throw new RuntimeException("不存在第"+type+"条查询语句");
        }
        long b=System.currentTimeMillis();
        log.info("查询耗费时间"+ TimeUtil.longTimeFormatString(b-a)+"，查询到"+dataList.size()+"条记录");
        Map<String,List<Data>> listMap=new HashMap<>();
        for(Data data:dataList){
            String dirname= StringUtil.convertFileName(data.getTitle());
            if(listMap.get(dirname)==null){
                listMap.put(dirname,new ArrayList<>());
            }
            listMap.get(dirname).add(data);
        }
        for(String dirName:listMap.keySet()){
            dataList=listMap.get(dirName);
            for(Data data:dataList){
                String path=dirName;
                if(dataList.size()>1){
                    if(data.getSubtitle()!=null){
                        path+="/"+StringUtil.convertFileName(data.getSubtitle());
                    }else{
                        path+="/"+data.getCid()+"";
                    }
                }
                downLoadDanmu(data.getCid(),dirPath+"/"+path+".xml",true);
            }
            File file=new File(dirPath+"/"+dirName);
            if(file.isDirectory()) {
                int fileCount = file.listFiles().length;
                if (fileCount == 0) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        log.error(e);
                    }
                }
            }
        }
    }

    /**
     * 生成闪光文字图片
     * @param text 文字内容
     * @param path 生成路径
     */
    public static void dLWordArt(String text,String path){
        CrawlerUtil.cookie=PropertiesUtil.getProperties("assqqlcookie",String.class);
        Document document= null;
        try {
            document = CrawlerUtil.jsoupGet(ApiUrl.wordArt.getUrl(URLEncoder.encode(text, "GBK")), Document.class, Connection.Method.GET);
            if(!document.body().html().contains(".jpg")) {
                log.info("assqqlcookie:" + CrawlerUtil.cookie + "已过期");
                CrawlerUtil.cookie = CrawlerUtil.doGet(ApiUrl.assqql.getUrl()).getHeaders("Set-Cookie")[0].getValue();
                PropertiesUtil.updateProperties("assqqlcookie", CrawlerUtil.cookie, null);
                log.info("assqqlcookie:" + CrawlerUtil.cookie + "更新完毕尝试重新发送请求");
                document = CrawlerUtil.jsoupGet(ApiUrl.wordArt.getUrl(URLEncoder.encode(text, "GBK")) , Document.class, Connection.Method.GET);
            }
        } catch (UnsupportedEncodingException e) {
            dLWordArt(text,path);
        }
        String html=document.body().html();
        String href=ApiUrl.wordArtPath.getUrl(html.substring(0,html.indexOf(".jpg")+4));
        text=StringUtil.convert(text);
        String jpgPath=path+text+".jpg";
        downLoad(href,jpgPath);
        try {
            Desktop.getDesktop().open(new File(jpgPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 下载二维码
     * @param text 文字内容
     * @param filepath 生成路径
     * @*param width二维码宽度
     */
    public static void dLQrcode(String text,String filepath,String width){
        downLoad(ApiUrl.qrcode.getUrl(text,width),filepath);
        try {
            Desktop.getDesktop().open(new File(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
