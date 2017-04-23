package org.pqh.entity;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

/**
 * Created by 10295 on 2016/7/3.
 */
public class Bangumi {
    private Integer seasonId;
    private Integer bangumiId;
    private String title;

    public Bangumi() {
    }

    public Bangumi(Integer seasonId, Integer bangumiId, String title) {
        this.seasonId = seasonId;
        this.bangumiId = bangumiId;
        this.title = title;
    }

    public Bangumi(Integer seasonId) {
        this.seasonId = seasonId;
    }

    public Integer getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Integer seasonId) {
        this.seasonId = seasonId;
    }

    public Integer getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(Integer bangumiId) {
        this.bangumiId = bangumiId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Bangumi{" +
                "seasonId=" + seasonId +
                ", bangumiId=" + bangumiId +
                ", title='" + title + '\'' +
                '}';
    }
}
