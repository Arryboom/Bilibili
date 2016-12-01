package org.pqh.util;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Date;

/**反射工具类
 * Created by 10295 on 2016/8/4.
 */
public class ReflexUtil {
    private static Logger log= TestSlf4j.getLogger(ReflexUtil.class);
    /**
     * 根据类名获取对象
     * @param classname
     * @return
     */
    public static Object getObject(String classname){
        try {
            return Class.forName(classname).newInstance();
        } catch (InstantiationException e) {
            TestSlf4j.outputLog(e,log,false);
        } catch (IllegalAccessException e) {
            TestSlf4j.outputLog(e,log,false);
        } catch (ClassNotFoundException e) {
            TestSlf4j.outputLog(e,log,false);
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
            TestSlf4j.outputLog(e,log,false);
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
    public static Object setObject(Object object,String key,String value) {
        Field field=null;
        try {
            field = object.getClass().getDeclaredField(key);
        }catch (NoSuchFieldException e) {
            try {
                field = object.getClass().getSuperclass().getDeclaredField(key);
            } catch (NoSuchFieldException e1) {
                TestSlf4j.outputLog(e,log,false);
            }
        }
        field.setAccessible(true);
        Class type = field.getType();
        try {
            if (type==Integer.class) {
                field.set(object, Integer.parseInt(value));
            } else if (type==Long.class) {
                field.set(object, Long.parseLong(value));
            } else if (type== Float.class) {
                field.set(object, Float.parseFloat(value));
            } else if (type==Boolean.class) {
                field.set(object, Boolean.parseBoolean(value));
            } else if (type== Date.class) {
                if (value.contains(":")) {
                    field.set(object,TimeUtil.formatStringToDate(value,Constant.DATETIME));
                } else {
                    field.set(object, TimeUtil.formatStringToDate(value,null));
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
                    TestSlf4j.outputLog(e,log,false);
                }
            }
        }
        catch (IllegalAccessException e) {
            TestSlf4j.outputLog(e,log,false);
        }
        return object;
    }
}
