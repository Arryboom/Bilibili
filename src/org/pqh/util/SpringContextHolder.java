package org.pqh.util;

import org.pqh.dao.BiliDao;
import org.pqh.dao.VstorageDao;
import org.pqh.service.AvCountService;
import org.pqh.service.InsertService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext. 
 *
 */
public class SpringContextHolder{
    private static ApplicationContext applicationContext;

    public static BiliDao biliDao;
    public static InsertService insertService;
    public static ThreadPoolTaskExecutor threadPoolTaskExecutor;
    public static VstorageDao vstorageDao;
    public static AvCountService avCountService;
    public static AbstractApplicationContext abstractApplicationContext;

    static {
        abstractApplicationContext=new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        applicationContext=abstractApplicationContext;
        biliDao=SpringContextHolder.getBean("biliDao");
        insertService=SpringContextHolder.getBean("insertServiceImpl");
        threadPoolTaskExecutor=SpringContextHolder.getBean("taskExecutor");
        vstorageDao=SpringContextHolder.getBean("vstorageDao");
        avCountService=SpringContextHolder.getBean("avCountService");
    }

    public static void close(){
        abstractApplicationContext.close();
    }


    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return (T) applicationContext.getBeansOfType(clazz);
    }

    /**
     * 清除applicationContext静态变量.
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
        }
    }
}  