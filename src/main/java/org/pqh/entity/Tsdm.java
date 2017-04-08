package org.pqh.entity;


import org.pqh.util.TimeUtil;

import java.util.Date;

/**
 * Created by reborn on 2017/3/30.
 */
public class Tsdm {
    private String tsdmUrl;
    private String animeName;
    private Date playTime;
    private String updateTime;
    private String copyright;
    private String lastUpdateTime;

    public Tsdm() {
    }

    public Tsdm(String tsdmUrl, String animeName, Date playTime, String updateTime, String copyright, String lastUpdateTime) {
        this.tsdmUrl = tsdmUrl;
        this.animeName = animeName;
        this.playTime = playTime;
        this.updateTime = updateTime;
        this.copyright = copyright;
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "Tsdm{" +
                "tsdmUrl='" + tsdmUrl + '\'' +
                ", 番剧='" + animeName + '\'' +
                ", 开播时间=" + TimeUtil.formatDate(playTime,TimeUtil.DATE) +
                ", 更新时间='" + updateTime + '\'' +
                ", 版权='" + copyright + '\'' +
                ", 天使动漫论坛资源楼层最后更新时间集合='" + lastUpdateTime + '\'' +
                '}';
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getTsdmUrl() {
        return tsdmUrl;
    }

    public void setTsdmUrl(String tsdmUrl) {
        this.tsdmUrl = tsdmUrl;
    }

    public String getAnimeName() {
        return animeName;
    }

    public void setAnimeName(String animeName) {
        this.animeName = animeName;
    }

    public Date getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Date playTime) {
        this.playTime = playTime;
    }
}
