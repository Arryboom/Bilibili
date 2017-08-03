package org.pqh.util;

import org.apache.commons.io.FileUtils;
import org.pqh.entity.Param;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

import static org.pqh.util.SpringContextHolder.biliDao;
/**
 * Created by 10295 on 2016/8/4.
 */
@Component
public class PropertiesUtil {

    private static Map<String,String> fileMap=new HashMap<>();

    private static String path=getFilePath("config.properties");

    public static String getFilePath(String fileName){
        if(fileMap.get(fileName)==null){
            URL url=PropertiesUtil.class.getClassLoader().getResource(fileName);
            if(url==null){
                return null;
            }else{
                fileMap.put(fileName,url.getPath());
            }
        }

        return fileMap.get(fileName);
    }
    /**
     * 获取配置文件所有配置项
     * @return
     */
    public static Properties getProperties(){
        InputStream inputStream=null;

        try {
            inputStream=new FileInputStream(path);
            Properties p = new Properties();
            p.load(inputStream);
            return p;
        } catch (IOException e) {
            LogUtil.getLogger().error(String.valueOf(e));
            return  null;
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                LogUtil.getLogger().error(String.valueOf(e));
            }
        }
    }

    /**
     * 自动添加或更新配置项
     * @param key
     * @param value
     * @param desc
     */
    public  void createParam(String key,String value,String desc){
        desc=StringUtil.gbEncoding(desc);
        Param param=biliDao.selectParam(key).get(0);
        if(param!=null){
            param.setValue(value);
            param.setDesc("#"+desc);
            biliDao.updateParam(param);
        }else{
            param=new Param(key,value,"#"+desc);
            biliDao.insertParam(param);
        }
        PropertiesUtil.createConfig(new File(path));
    }

    /**
     * 获取配置文件指定配置项
     */
    public static <T>T getProperties(String key,Class<T> type){
        if(type==String.class){
            return (T) getProperties().getProperty(key);
        }
         int index=type.getName().lastIndexOf(".")+1;
         String typename=type.getName().substring(index);
        if(typename.equals("Integer")){
            typename="Int";
        }
        try {
            return (T) type.getDeclaredMethod("parse"+typename,String.class).invoke(null,getProperties().getProperty(key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("只支持基本八大类型转换");
    }

    /**
     * 更新单个配置项
     * @param key
     * @param value
     * @param desc
     */

    public static void updateProperties(String key,String value,String desc){
        try {
            File file=new File(path);
            List<String> strings= FileUtils.readLines(file,"GBK");
            List<String> _strings=new ArrayList<String>();
            for(String s:strings){
                if(s.contains(key)){
                    String _value=s.substring(s.indexOf("=")+1);
                    s=!_value.isEmpty()?s.replace(_value,value):s+value;
                    int descindex = _strings.size() - 1;
                    String _desc=strings.get(descindex);
                    if(desc!=null) {
                        _desc=desc="#"+ StringUtil.gbEncoding(desc);
                        _strings.remove(descindex);
                        _strings.add(desc);
                    }
                }
                _strings.add(s);
            }
            FileUtils.writeLines(file,"GBK",_strings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public  void updateProperties(){
        File file=new File(path);
        try {
            List<String> strings= FileUtils.readLines(file,"GBK");
            for(int i=0;i<strings.size();i+=2){
                String desc=StringUtil.convert(strings.get(i));
                String str=strings.get(i+1);
                int index=str.indexOf("=");
                String key=str.substring(0,index);
                String value=str.substring(index+1,str.length());
                Param param=new Param(key,value,desc);
                LogUtil.getLogger().debug(param.toString());
                if(biliDao.selectParam(key)==null){
                    biliDao.insertParam(param);
                }else{
                    biliDao.updateParam(param);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            LogUtil.getLogger().error(e.getMessage());
        }
    }

    /**
     * 从数据库创建配置文件
     * @param file 生成的配置文件对象
     */
    public static void createConfig(File file){
        List<Param> list=biliDao.selectParam(null);
        List<String> stringList=new ArrayList<String>();
        for(Param param:list){
            stringList.add(StringUtil.gbEncoding(param.getDesc()));
            stringList.add(param.getKey()+"="+(param.getValue()!=null?param.getValue():""));
        }
        try {
            FileUtils.writeLines(file,"GBK",stringList);
        } catch (IOException e) {
            LogUtil.getLogger().error(String.valueOf(e));
        }
    }
}
