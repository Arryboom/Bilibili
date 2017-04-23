package org.pqh.util;

import org.apache.log4j.Logger;
import org.pqh.entity.*;
import org.pqh.entity.vstorage.*;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**反射工具类
 * Created by 10295 on 2016/8/4.
 */
public class ReflexUtil {
    private static Logger log= Logger.getLogger(ReflexUtil.class);

    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(Vstorage.class.getName(),new Vstorage());
        map.put(Data.class.getName(),new Data());
        map.put(Files.class.getName(),new ArrayList<Files>());
        map.put(Dispatch_servers.class.getName(),new ArrayList<Dispatch_servers>());
        map.put(Upload.class.getName(),new ArrayList<Upload>());
        map.put(Node_server.class.getName(),new ArrayList<Node_server>());
        map.put(Upload_meta.class.getName(),new Upload_meta());
        return map;
    }


    /**
     * 根据类名获取对象
     * @param classname
     * @return
     */
    public static Object getObject(String classname){
        try {
            return Class.forName(classname).newInstance();
        } catch (InstantiationException e) {
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        } catch (ClassNotFoundException e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 检查对象字段是否全为空
     * @param object
     * @return
     */
    public static boolean checkFieldsNaN(Object object){
        Class _class= null;
        Field fields[]=null;
        try {
            _class = object.getClass();
            fields=_class.getDeclaredFields();
            for(Field field:fields){
                field.setAccessible(true);
                if(field.get(object)!=null){
                    return false;
                }
            }
            return true;
        } catch (IllegalAccessException e) {
            log.error(e);
        }
        return false;
    }
    /**
     * 为对象的指定属性赋值
     * @param object
     * @param key
     * @param value
     * @return
     */
    public static <T>T setObject(Object object,String key,Object value) {
        Field field=null;
        try {
            field = object.getClass().getDeclaredField(key);
        }catch (NoSuchFieldException e) {
            try {
                field = object.getClass().getSuperclass().getDeclaredField(key);
            } catch (NoSuchFieldException e1) {
                if(object instanceof Bili){
                    int flag=JOptionPane.showConfirmDialog(null,"Bili实体类不存在"+key+"字段，无法为该字段写入'"+value+"'值,是否屏蔽这个节点数据？");
                    if(flag==0){
                        String excludenode=PropertiesUtil.getProperties("excludenode",String.class);
                        PropertiesUtil.updateProperties("excludenode",excludenode.equals("")?key:excludenode+","+key,null);
                    }
                }
                log.error(e+"\n"+object.getClass()+" "+key+" "+value);
                return (T)object;
            }
        }
        field.setAccessible(true);
        Class type = field.getType();
        try {
            String v=value.toString();
            if (type==Integer.class) {
                field.set(object, Integer.parseInt(v));
            } else if (type==Long.class) {
                field.set(object, Long.parseLong(v));
            } else if (type== Float.class) {
                field.set(object, Float.parseFloat(v));
            } else if (type==Boolean.class) {
                field.set(object, Boolean.parseBoolean(v));
            } else if (type== Date.class) {
                if (v.contains(":")) {
                    field.set(object,TimeUtil.parseDate(v,TimeUtil.DATETIME));
                } else {
                    field.set(object, TimeUtil.parseDate(v,null));
                }
            } else {
                field.set(object, value);
            }
        }
        catch (NumberFormatException e){
            if(e.getMessage().equals("For input string: \"\"")){
                try {
                    field.set(object,null);
                } catch (IllegalAccessException e1) {
                    log.error(e);
                }
            }
        }
        catch (IllegalAccessException e) {
            log.error(e);
        }
        return (T)object;
    }
}
