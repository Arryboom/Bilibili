package org.pqh.entity;

import java.sql.Timestamp;

/**
 * Created by reborn on 2016/11/20.
 */
public class Save {
    private Integer id;
    private String bilibili;
    private Timestamp lastUpdateTime;

    public Save(Integer id, String bilibili, Timestamp lastUpdateTime) {
        this.id = id;
        this.bilibili = bilibili;
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBilibili() {
        return bilibili;
    }

    public void setBilibili(String bilibili) {
        this.bilibili = bilibili;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
