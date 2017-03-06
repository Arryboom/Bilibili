package main.java.org.pqh.util;

import com.fasterxml.jackson.databind.JsonNode;
import main.java.org.pqh.entity.Type;
import org.apache.log4j.Logger;
import main.java.org.pqh.entity.Data;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 10295 on 2016/8/4.
 * 数据库工具类
 */
public class SqlUtil {
    private static Logger log= TestSlf4j.getLogger(SqlUtil.class);
    /**
     * 创建全字段添加语句以及更新语句
     * @param tableName 表名
     * @param primarykey 主键
     * @param jsonNode 字段
     */
    public static void createSql(String tableName,String primarykey,JsonNode jsonNode){
        StringBuffer stringBuffer=new StringBuffer("INSERT INTO "+tableName+" VALUES(");
        StringBuffer stringBuffer1=new StringBuffer("UPDATE "+tableName+" SET ");
        StringBuffer stringBuffer2=new StringBuffer("(");
        Iterator<String> iterator=jsonNode.fieldNames();
        while(iterator.hasNext()){
            String key=iterator.next();
            stringBuffer.append("#{"+key+"},");
            stringBuffer2.append(key+",");
            //除了主键之外其他字段更新
            if(!key.equals(primarykey)) {
                stringBuffer1.append(key + "=#{" + key + "},");
            }
        }

        stringBuffer2.replace(stringBuffer2.length()-1,stringBuffer2.length(),")");
        stringBuffer.replace(stringBuffer.length()-1,stringBuffer.length(),")");
        stringBuffer1.replace(stringBuffer1.length()-1,stringBuffer1.length()," WHERE "+primarykey+"=#{"+primarykey+"}");
        stringBuffer.insert(stringBuffer.indexOf("VALUES"),stringBuffer2);
        System.out.println("添加语句：\n"+stringBuffer+"\n更新语句：\n"+stringBuffer1);
    }
    /**
     * 生成创建表sql
     * @param tablename 表名
     * @param primarykey 表主键
     * @param map 存放表字段，字段类型map对象
     */
    public static void createTable(String tablename,String primarykey,Map<String,String> map){
        System.out.println("创建表语句\nCREATE TABLE `"+tablename+"` (");
        for(String key:map.keySet()){
            if(key.equals(primarykey)){
                System.out.println("`"+primarykey+"`  int NOT NULL ,");
            }else{
                System.out.println("`"+key+"`  "+map.get(key)+" NULL ,");
            }
        }
        System.out.println("PRIMARY KEY (`"+primarykey+"`)\n)\n;");
    }
    /**
     * 根据文件名，包名，以及字段名字段类型组成的map对象创建一个类文件
     * @param name 文件名
     * @param $package 包名
     * @param map 字段以及字段类型组成的键值对
     */
    public static void createClass(String name,String $package,Map<String,String> map){
        String path="src\\"+$package.replace(".","\\\\")+"\\";
        path+=name+".java";
        File file=new File(path);

        BufferedWriter bufferedWriter=null;
        try {
            file.createNewFile();
            bufferedWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            bufferedWriter.write("package "+$package+";"+"\r\n\r\n");
            bufferedWriter.write("import java.io.Serializable;\r\n\r\n");
            bufferedWriter.write("import java.util.Date;\r\n\r\n");
            bufferedWriter.write("public class "+name+" implements Serializable{\r\n\r\n");
            for(Object key:map.keySet()){
                bufferedWriter.write("\tprivate "+map.get(key)+" "+key+";\r\n\r\n");
            }
            bufferedWriter.write("}");
        } catch (FileNotFoundException e) {
            TestSlf4j.outputLog(e,log);
        } catch (IOException e) {
            TestSlf4j.outputLog(e,log);
        }finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    TestSlf4j.outputLog(e,log);
                }
            }
        }
    }


    /**
     * 根据json对象返回字段key以及字段类型value
     * @param jsonNode json对象
     * @return 返回HashMap键值对
     */
    public static  Map<String,Map<String,String>> getFieldType(JsonNode jsonNode){
        Map<String,Map<String,String>> map=new HashMap<String, Map<String, String>>();
        //存放字段类型
        Map<String,String> mapType=new HashMap<String, String>();
        //存放数据库数据类型
        Map<String,String> mapSql=new HashMap<String, String>();
        Iterator<String> iterator=jsonNode.fieldNames();
        while(iterator.hasNext()){
            String typename=null;
            String key=iterator.next();
            JsonNode subNode=jsonNode.get(key);
            if(subNode.isBoolean()){
                typename=Boolean.class.getName();
            }else if(subNode.isNumber()&&subNode.asText().contains(".")){
                typename =Float.class.getName();
            }else if(subNode.isNumber()){
                typename =Integer.class.getName();
            }else if(checkDate(subNode.asText())){
                typename =Data.class.getName();
            }else if(subNode.isArray()){
                typename= List.class.getName();
            }else if(subNode.isObject()){
                typename= Object.class.getName();
            }else{
                typename= String.class.getName();
            }
            mapType.put(key.toString(),typename.substring(typename.lastIndexOf(".")+1));
            mapSql.put(key.toString(), Type.getValue(typename).value);
        }

        map.put("Type",mapType);
        map.put("Sql",mapSql);
        return map;
    }

    /**
     * 检查日期格式有效性
     * @param date 日期
     * @return
     */
    private static boolean checkDate(String date){
        try {
            new SimpleDateFormat(Constant.DATETIME).parse(date);
        } catch (ParseException e) {
            try {
                new SimpleDateFormat(Constant.DATE).parse(date);
            } catch (ParseException e1) {
                return false;
            }
        }
        return true;
    }
}
