package org.pqh.entity;


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
    
    private String lastUpdateTimes;
    
    private Integer index;

    private String bangumi;

    private String biliId;
    
    private String iqiyiId;
    
    private String youkuId;
    
    private String biliUrl;
    
    private String iqiyiUrl;
    
    private String youkuUrl;
    public Tsdm() {

    }

    public Tsdm(String animeName, Date playTime, String updateTime, String copyright) {
        this.animeName = animeName;
        this.playTime = playTime;
        this.updateTime = updateTime;
        this.copyright = copyright;
    }



    @Override
    public String toString() {
        return "Tsdm{" +
                "tsdmUrl='" + tsdmUrl + '\'' +
                ", 番剧='" + animeName + '\'' +
                ", 更新时间='" + updateTime + '\'' +
                ", 版权='" + copyright + '\'' +
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

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getLastUpdateTimes() {
        return lastUpdateTimes;
    }

    public void setLastUpdateTimes(String lastUpdateTimes) {
        this.lastUpdateTimes = lastUpdateTimes;
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

    public String getIqiyiUrl() {
        return iqiyiUrl;
    }

    public void setIqiyiUrl(String iqiyiUrl) {
        this.iqiyiUrl = iqiyiUrl;
    }

    public String getYoukuId() {
        return youkuId;
    }

    public void setYoukuId(String youkuId) {
        this.youkuId = youkuId;
    }

    public String getBiliId() {
        return biliId;
    }

    public void setBiliId(String biliId) {
        this.biliId = biliId;
    }

    public String getIqiyiId() {
        return iqiyiId;
    }

    public void setIqiyiId(String iqiyiId) {
        this.iqiyiId = iqiyiId;
    }

    public String getBiliUrl() {
        return biliUrl;
    }

    public void setBiliUrl(String biliUrl) {
        this.biliUrl = biliUrl;
    }


    public String getYoukuUrl() {
        return youkuUrl;
    }

    public void setYoukuUrl(String youkuUrl) {
        this.youkuUrl = youkuUrl;
    }

    public String getBangumi() {
        return bangumi;
    }

    public void setBangumi(String bangumi) {
        this.bangumi = bangumi;
    }

}
