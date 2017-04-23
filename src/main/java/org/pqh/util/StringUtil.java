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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by reborn on 2017/1/5.
 */
public class StringUtil {
    private static Logger log= Logger.getLogger(StringUtil.class);
    private final String json;
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
     * 正则表达式匹配
     * @param str 匹配字符串
     * @param regex 正则表达式
     * @param c 返回类型
     * @param <T>
     * @return
     */
    public static <T>T matchStr(String str,String regex,Class<T> c){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        String s=null;
        List<String> list=null;
        while (matcher.find()) {
            s=matcher.group();
            log.debug("匹配结果"+s);
            if(c==String.class) {
                return (T)s;
            }else if(c==List.class){
                if(list==null){
                    list=new ArrayList<>();
                }
                list.add(s);
            }
        }
        if(s==null){
            return null;
        }else if(c==String.class){
            return (T) s;
        }else if(c==List.class){
            return (T) list;
        }else{
            return null;
        }

    }

    /**
     * uncoide解码
     * @param str
     * @return
     */
    public static String convert(String str){
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while((i=str.indexOf("\\u", pos)) != -1){
            sb.append(str.substring(pos, i));
            if(i+5 < str.length()){
                pos = i+6;
                sb.append((char)Integer.parseInt(str.substring(i+2, i+6), 16));
            }
        }
        sb.append(str.substring(pos));
        return sb.toString();
    }

    /**
     * uncoide解码
     *
     * @return
     */
    public   String convert(){
        String sb=convert(json);

        String strs[]=new String[]{"{","}","[","]","(",")"};
        boolean flag[]=new boolean[12];
        for(int j=0;j<strs.length;j++){
            flag[j*2]=sb.contains(strs[j]);
            if(!flag[j*2]){
                flag[j*2+1]=flag[j*2];
                continue;
            }
            flag[j*2+1]=sb.toString().startsWith(strs[j]);
        }
        if(flag[1]&&flag[3]){
            return sb.substring(sb.indexOf(strs[0]),sb.lastIndexOf(strs[1]));
        }else if(flag[5]&&flag[7]){
            return sb.substring(sb.indexOf(strs[2]),sb.lastIndexOf(strs[3]));
        }else if(flag[8]&&flag[10]&&sb.endsWith(";")){
            return sb.substring(sb.indexOf(strs[4])+1,sb.lastIndexOf(strs[5]));
        }else{
            return fuckJson().toString();
        }

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
        String returnJson="";
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
                returnJson = json.replace( title,  title.replace("\"", "\\\""));
            }

        }

        return returnJson;
    }

}
