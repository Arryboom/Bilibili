package org.pqh.entity;

/**
 * Created by reborn on 2017/4/3.
 */
public class Bdu {
    private int id;
    private String url;
    private String password;
    private String desc;

    public Bdu() {
    }

    public Bdu(String url, String password, String desc) {
        this.url = url;
        this.password = password;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
