package org.pqh.task;

import org.pqh.util.DownLoadUtil;

/**
 * Created by 10295 on 2016/8/8.
 */
public class TaskShowImg implements Runnable{
    private String message;
    private String imgPath="img/";

    public TaskShowImg(String message, String imgPath) {
        this.message = message;
        if(imgPath!=null){
            this.imgPath = imgPath;
        }
    }

    public void run() {
       DownLoadUtil.dLWordArt(message,this.imgPath);
    }
}
