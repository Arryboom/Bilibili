package org.pqh.entity.statistics;

import java.util.Date;

/**
 * Created by 10295 on 2016/5/18.
 */
public class AvCount {
    private Date date;
    private Integer count;

    public AvCount() {
    }

    public AvCount(Date date, Integer count) {
        this.date = date;
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
