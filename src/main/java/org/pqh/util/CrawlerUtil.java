package main.java.org.pqh.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 10295 on 2016/8/4.
 */
public class CrawlerUtil {
    private static Logger log= TestSlf4j.getLogger(CrawlerUtil.class);
    //请求cookie信息
    public static String cookie= "";
    //用户浏览器标识
    private static String userAgent=PropertiesUtil.getProperties("User-Agent",String.class);
    //连接超时时间
    private static int timeout=PropertiesUtil.getProperties("timeout",Integer.class);;
    //发送的表单数据
    public static Map<String,String> formMap=new HashMap<String, String>();

    /**
     * httpclient get请求封装
     * @param href
     * @return
     */
    public static CloseableHttpResponse doGet(String href){
        log.info("向地址："+href+"发送get请求");
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(href);
        httpGet.setHeader("User-Agent", userAgent);
        CloseableHttpResponse closeableHttpResponse= null;
        try {
            closeableHttpResponse = closeableHttpClient.execute(httpGet);
        } catch (IOException e) {
            log.info("get请求发生异常,"+timeout+"秒后重新尝试发送请求");
            ThreadUtil.sleep(TestSlf4j.getLineInfo(), timeout);
            return doGet(href);
        }
        return closeableHttpResponse;
    }
    /**
     *
     * @param url 爬虫的网址
     * @param tClass 返回的类对象
     * @param method 请求方式
     * @param <T>
     * @return  返回文档信息
     */
    public static <T>T jsoupGet(String url,Class<T> tClass,String method){
        Connection connection=null;
        log.info("连接URL:"+url);
        int i=0;
        try {
            if(tClass== org.dom4j.Document.class){
                SAXReader saxReader=new SAXReader();
                return (T) saxReader.read(url);
            }
            connection = Jsoup.connect(url).header("Cookie",cookie).userAgent(userAgent).data(formMap).timeout(timeout*1000).ignoreContentType(true);

            if(tClass==Document.class){
                if (method.equals(Constant.GET)) {
                    return (T) connection.get();
                } else if (method.equals(Constant.POST)) {
                    return (T) connection.post();
                } else {
                    throw new RuntimeException("不支持" + method + "请求");
                }
            }
            else if(tClass==String.class){
                return (T) connection.execute().body();
            }else if(tClass==JsonNode.class){
                ObjectMapper objectMapper=new ObjectMapper();
                String json=method.equals(Constant.GET)?connection.get().select("body").html():connection.post().body().html();
                return (T)objectMapper.readTree(json);
            }else {
                throw new RuntimeException("返回值不支持"+tClass.getName()+"这种类型");
            }
        }
        catch(JsonParseException e){
            TestSlf4j.outputLog(e,log);
            return null;
        }
        catch (HttpStatusException e){
            if(e.getStatusCode()==404&&url.contains(Constant.cidApi)){
                return null;
            }else{
                return jsoupGet(url,tClass,method);
            }
        }
        catch (IOException e) {
            while(isReachable("www.baidu.com")==null){
                log.error("无法连接到百度，应该是断网了，30秒后重新尝试连接");
                ThreadUtil.sleep(30);
            }

            return jsoupGet(url,tClass,method);
        } catch (DocumentException e) {
            if(e.getMessage().contains("在文档的元素内容中找到无效的 XML 字符")||e.getMessage().contains("前言中不允许有内容")||e.getMessage().contains("HTTP response code: 502")) {
                log.error(e.getMessage());
                return null;
            }
            while(isReachable("www.baidu.com")==null){
                log.error("无法连接到百度，应该是断网了，30秒后重新尝试连接");
                ThreadUtil.sleep(30);
            }
            return jsoupGet(url, tClass, method);
        }

    }

    /**
     * 传入需要连接的IP，返回是否连接成功
     * @param remoteInetAddr
     * @return
     */
    public static String isReachable(String remoteInetAddr) {
        String ip=null;
        try {
            ip=InetAddress.getByName(remoteInetAddr).getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
        return ip;
    }

}
