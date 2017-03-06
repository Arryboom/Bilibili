package main.java.org.pqh.task;

import main.java.org.pqh.service.InsertService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 10295 on 2016/5/9.
 */

public class TaskCid implements Runnable {
    private int cid;
    private InsertService insertService;
    private  Method method;

    public TaskCid(InsertService insertService,int cid, String methodName) {
        this.cid = cid;
        this.insertService=insertService;
        try {
            method = insertService.getClass().getDeclaredMethod(methodName, Integer.class);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }


    public void run() {
        try {
            method.invoke(insertService,cid);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
