package org.pqh.util;

import org.pqh.dao.BduDao;
import org.pqh.dao.BiliDao;
import org.pqh.dao.BiliHistoryDao;
import org.pqh.dao.VstorageDao;
import org.pqh.service.AvCountService;
import org.pqh.service.InsertService;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 *
 */
public class SpringContextHolder{
//    private static ApplicationContext applicationContext;

    public static BiliDao biliDao;
    public static InsertService insertService;
    public static ThreadPoolTaskExecutor threadPoolTaskExecutor;
    public static AvCountService avCountService;
    private static AbstractApplicationContext applicationContext;
    public static BduDao bduDao;
    public static VstorageDao vstorageDao;
    public static BiliHistoryDao biliHistoryDao;
    static {
        applicationContext=new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        biliDao=SpringContextHolder.getBean("biliDao");
        insertService=SpringContextHolder.getBean("insertService");
        threadPoolTaskExecutor=SpringContextHolder.getBean("taskExecutor");
        vstorageDao=SpringContextHolder.getBean("vstorageDao");
        avCountService=SpringContextHolder.getBean("avCountService");
        bduDao=SpringContextHolder.getBean("bduDao");
        biliHistoryDao=SpringContextHolder.getBean("biliHistoryDao");
    }

    public static void close(){
        applicationContext.registerShutdownHook();
    }


    public  void destroy(String beanName) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
                .getBeanFactory();

        LogUtil.getLogger().info("destroy bean " + beanName);
        if(beanFactory.containsBean(beanName)){
            beanFactory.destroySingleton(beanName);
            beanFactory.destroyBean(beanName);
            beanFactory.removeBeanDefinition(beanName);
        }else {
            LogUtil.getLogger().info("No {} defined in bean container.", beanName);
        }
    }

    public static void addToBeanFactory(Class<?> beanClass,String beanName,Object ...params){

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
                .getBeanFactory();
        if(!beanFactory.containsBean(beanName)){
            BeanDefinitionBuilder beanDefinitionBuilder= BeanDefinitionBuilder.rootBeanDefinition(beanClass);

            if(params!=null&&params.length%2==0){
                for(int i=0;i<params.length/2;i++){
                    beanDefinitionBuilder.addPropertyValue(params[2*i].toString(),params[2*i+1]);
                }
            }

            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
            LogUtil.getLogger().info("Add {} to bean container.", beanName);
        }
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