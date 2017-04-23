package org.pqh.entity;

/**
 * Created by reborn on 2017/4/3.
 */
public class Bdu {
    private int id;
    private String url;
    private String password;
    private String subtitle;
    private String episode;
    private String remark;
    private String desc;
    private String animeName;

    public Bdu(String url, String password, String subtitle, String episode, String remark, String animeName) {
        this.url = url;
        this.password = password;
        this.subtitle = subtitle;
        this.episode = episode;
        this.remark = remark;
        this.animeName = animeName;
    }

    public Bdu() {
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

    public String getAnimeName() {
        return animeName;
    }

    public void setAnimeName(String animeName) {
        this.animeName = animeName;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Bdu{" +
                "url='" + url + '\'' +
                ", password='" + password + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", episode='" + episode + '\'' +
                ", remark='" + remark + '\'' +
                ", animeName='" + animeName + '\'' +
                '}';
    }
}
