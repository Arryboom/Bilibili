package org.pqh.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by reborn on 2017/1/5.
 */
public class StringUtil {
    private static Logger log= Logger.getLogger(StringUtil.class);
    private String json;
    public StringUtil(String json){
        this.json=json;

    }

    public static String convertFileName(String fileName){
//        文件名在操作系统中不允许出现  / \ " : | * ? < > 故将其以空替代
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName);
// 将匹配到的非法字符以空替换
        return matcher.replaceAll("");
    }

    /**
     * uncoide编码
     * @param gbString
     * @return
     */
    public static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        log.info(gbString+"\tunicodeBytes is: " + unicodeBytes);
        return unicodeBytes;
    }

    /**
     * uncoide解码
     * @param utfString
     * @return
     */
    public static  String convert(String utfString){
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while((i=utfString.indexOf("\\u", pos)) != -1){
            sb.append(utfString.substring(pos, i));
            if(i+5 < utfString.length()){
                pos = i+6;
                sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
            }
        }
        sb.append(utfString.substring(pos));
        return sb.toString();
    }

    public String getVal(String k,String s){
        int start = json.indexOf(k) + k.length()+1;
        int end = json.indexOf(s, start) - 1;
        return json.substring(start, end);
    }

    public static boolean isEmpty(String s){
        return s==null||"".equals(s);
    }


    public interface Out{
        void systemOut();
    }

    /**
     * 获取控制台的输出
     * @param out
     * @return
     */
    public static String getSystemOut(Out out){
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
        PrintStream cacheStream = new PrintStream(baoStream);
        // old stream
        PrintStream oldStream = System.out;
        System.setOut(cacheStream);
        out.systemOut();
        // Restore old stream
        System.setOut(oldStream);
        return baoStream.toString();
    }

    /**
     * 手动修改无法解析的json数据
     * @param fileName
     * @return
     */
    public JsonNode fuckJson(String fileName){
        log.info("请手动修改json数据格式");
        try {
            File file=new File("ErrorJson/"+fileName);
            FileUtils.writeStringToFile(file,json,"UTF-8");
            long a=System.currentTimeMillis();
            while (true){
                if(FileUtils.isFileNewer(file,a)){
                    log.info("文件更新了,尝试重新解析json数据");
                    try {
                        JsonNode jsonNode=new ObjectMapper().readTree(file);
                        log.info("json数据解析成功");
                        FileUtils.cleanDirectory(file.getParentFile());
                        return jsonNode;
                    } catch (JsonParseException e) {
                        log.info("json解析失败"+e.getMessage());
                    }
                    a=System.currentTimeMillis();
                }
                ThreadUtil.sleep(3);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonNode fuckJson(){
        return fuckJson(System.currentTimeMillis()+"");
    }


    /**
     * 对json键值一些特殊字符进行转义处理
     * @param keywords
     * @return
     */
    public  String jsonValueEscape(String... keywords){

        for(String keyword:keywords) {
            String s=null;
            if(!keyword.equals("subtitle")||keyword.equals("subtitle")&&json.contains("\"duration\":")){
                s=",";
            }else{
                s="}";
            }
            String k = "\"" + keyword + "\":";
            if(!json.contains(k)){
                continue;
            }
            String title = getVal(k,s);
            if(title.contains("\"")&&!title.contains("\\\"")){
                json = json.replace( title,  title.replace("\"", "\\\""));
            }

        }

        return json;
    }

}
