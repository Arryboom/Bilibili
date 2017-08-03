package org.pqh.entity.history;

/**
 * Created by reborn on 2017/4/13.
 */
public class Data {
    private int aid;
    private int tid;
    private String tname;
    private int copyright;
    private String pic;
    private String title;
    private int pubdate;
    private int ctime;
    private String desc;
    private int state;
    private int attribute;
    private String reject;
    private int duration;
    private String tags;
    private String rights;
    private String owner;
    private String stat;
    private int view_at;
    private boolean favorite;
    private int access;

    public Data() {
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public int getCopyright() {
        return copyright;
    }

    public void setCopyright(int copyright) {
        this.copyright = copyright;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getPubdate() {
        return pubdate;
    }

    public void setPubdate(int pubdate) {
        this.pubdate = pubdate;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(int ctime) {
        this.ctime = ctime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public String getReject() {
        return reject;
    }

    public void setReject(String reject) {
        this.reject = reject;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public long getView_at() {
        return view_at;
    }

    public void setView_at(int view_at) {
        this.view_at = view_at;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}