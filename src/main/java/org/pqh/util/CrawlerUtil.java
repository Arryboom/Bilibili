package org.pqh.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 10295 on 2016/8/4.
 * 爬虫工具类
 */
public class CrawlerUtil {

    //请求cookie信息
    public static String cookie= "";
    //用户浏览器标识
    private static String userAgent=PropertiesUtil.getProperties("User-Agent",String.class);
    //连接超时时间
    private static int timeout=PropertiesUtil.getProperties("timeout",Integer.class);;

    /**
     * httpclient get请求封装
     * @param href
     * @return
     */
    public static CloseableHttpResponse doGet(String href){
        LogUtil.getLogger().debug("向地址："+href+"发送get请求");
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(href);
        httpGet.setHeader("User-Agent", userAgent);
        try {
            return closeableHttpClient.execute(httpGet);
        } catch (IOException e) {
            ThreadUtil.sleep("get请求发生异常,"+timeout+"秒后重新尝试发送请求", timeout);
            return doGet(href);
        }
    }

    public static HttpEntity makeMultipartEntity(List<NameValuePair> params, final Map<String, File> files) {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE); //如果有SocketTimeoutException等情况，可修改这个枚举

        //builder.setCharset(Charset.forName("UTF-8"));
        //不要用这个，会导致服务端接收不到参数

        if (params != null && params.size() > 0) {

            for (NameValuePair p : params) {

                builder.addTextBody(p.getName(), p.getValue(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));

            }

        }

        if (files != null && files.size() > 0) {

            Set<Map.Entry<String, File>> entries = files.entrySet();

            for (Map.Entry<String, File> entry : entries) {

                builder.addPart(entry.getKey(), new FileBody(entry.getValue()));

            }

        }

        return builder.build();

    }

    public enum DataType{
        xml,
        domcument,
        json,
        string
    }

    /**
     *
     * @param url 请求地址
     * @param dataType 预期返回的数据类型
     * @param method 请求方式
     * @param cookies 请求cookie
     * @param params 请求参数
     * @return  返回文档信息
     */
    public static <T>T jsoupGet(String url, DataType dataType, Connection.Method method,Map<String,String> cookies,Map<String,String> params){
        Connection connection;
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
        LogUtil.getLogger().debug("连接URL:"+url);
        int i=0;
        String json=null;
        try {
            if(DataType.xml.equals(dataType)){
                SAXReader saxReader=new SAXReader();
                return (T) saxReader.read(url);
            }
            connection = Jsoup.connect(url);
            if(BiliUtil.access_key.isEmpty()&&BiliUtil.bili_cookie.size()==3&&url.contains("bilibili")){
                connection.cookies(BiliUtil.bili_cookie);
            }
            if(cookies!=null){
                connection.cookies(cookies);
            }
            if(params!=null){
                connection.data(params);
            }
            connection.userAgent(userAgent).timeout(timeout*1000).ignoreContentType(true);
            if(DataType.domcument.equals(dataType)){
                if (method.equals(Connection.Method.GET)) {
                    return (T) connection.get();
                } else if (method.equals(Connection.Method.POST)) {
                    return (T) connection.post();
                } else {
                    throw new RuntimeException("不支持" + method + "请求");
                }
            }
            else if(DataType.string.equals(dataType)){
                return (T) connection.execute().body();
            }else if(DataType.json.equals(dataType)){
                json=method.equals(Connection.Method.GET)?connection.get().body().text():connection.post().body().text();
                return (T) objectMapper.readTree(json);
            }else{
                return null;
            }
        }
        catch(JsonParseException e){
            StringUtil stringUtil=new StringUtil(json);
            try {
                if(url.contains("vstorage")){
                    return (T)objectMapper.readTree(stringUtil.jsonValueEscape("title","author","subtitle"));
                }else{
                    return (T)objectMapper.readTree(stringUtil.convert());
                }
            } catch (JsonParseException e1) {
                if(!url.contains("vstorage")){
                    return (T)stringUtil.fuckJson();
                }else{
                    return null;
                }
            }catch (IOException e1) {
                LogUtil.getLogger().error("异常信息"+e1.getMessage());
                return null;
            }
        }
        catch (HttpStatusException e){
            if(e.getStatusCode()==404){
                LogUtil.getLogger().error("非法地址"+url);
                return null;
            }else{
                ThreadUtil.sleep(e.getMessage(),60);
                return jsoupGet(url,dataType,method,cookies,params);
            }
        }catch (UnknownHostException e){
            LogUtil.getLogger().debug("网络异常"+e.getMessage());
            ThreadUtil.sleep(60);
            return jsoupGet(url,dataType,method,cookies,params);
        }catch (IOException e) {
            LogUtil.getLogger().debug("网络异常"+e.getMessage());
            ThreadUtil.sleep(10);
            return jsoupGet(url,dataType,method,cookies,params);
        } catch (DocumentException e) {
            LogUtil.getLogger().error("解析xml文档出错，异常信息"+e.getMessage());
            if(e.getMessage().contains("在文档的元素内容中找到无效的 XML 字符")||e.getMessage().contains("前言中不允许有内容")||e.getMessage().contains("HTTP response code: 502")) {
                return null;
            }
            ThreadUtil.sleep(10);
            return jsoupGet(url,dataType,method,cookies,params);
        }

    }

    /**
     * 带参数请求
     */
    public static <T>T jsoupGet(String url, DataType dataType, Connection.Method method,String ...parmas ){

        Map<String,String> paramMap=new HashMap<>();
        for(int i=0;i<parmas.length/2;i++){
            paramMap.put(parmas[2*i],parmas[2*i+1]);
        }
        return jsoupGet(url,dataType,method,null,paramMap);
    }


    /**
     * 普通请求
     */
    public static <T>T jsoupGet(String url, DataType dataType, Connection.Method method){
        return jsoupGet(url,dataType,method,null,null);
    }


}
