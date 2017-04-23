package org.pqh.entity;

import java.sql.Timestamp;

/**
 * Created by reborn on 2016/11/20.
 */
public class Save {
    private Integer id;
    private String bilibili;
    private Timestamp lastUpdateTime;
    private boolean latest;

    public Save(Integer id, String bilibili, Timestamp lastUpdateTime, Boolean latest) {
        this.id = id;
        this.bilibili = bilibili;
        this.lastUpdateTime = lastUpdateTime;
        this.latest = latest;
    }

    @Override
    public String toString() {
        return "Save{" +
                "id=" + id +
                ", 进度='" + bilibili + '\'' +
                ", 最后更新时间=" + lastUpdateTime +
                ", 是否更到最新=" + latest +
                '}';
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
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
