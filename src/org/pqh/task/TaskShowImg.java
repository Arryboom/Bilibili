package org.pqh.task;

import org.pqh.util.DownLoadUtil;

/**
 * Created by 10295 on 2016/8/8.
 */
public class TaskShowImg implements Runnable{
    private String message;
    private String imgPath;

    public TaskShowImg(String message, String imgPath) {
        this.message = message;
        this.imgPath = imgPath;
    }

    public TaskShowImg(String message) {
        this.message = message;
        this.imgPath="img/";
    }


    public void run() {
       DownLoadUtil.dLWordArt(message,this.imgPath);
    }
}
