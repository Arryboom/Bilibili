package org.pqh.entity;

import java.io.File;
import java.util.Map;

import static org.pqh.util.SpringContextHolder.biliDao;

/**
 * Created by reborn on 2017/5/14.
 */
public class Email {
    private SmtpHost smtpHost;

    private boolean auth;

    private int timeout;

    private String username;

    private String password;

    private String[] to;

    private String title;

    private StringBuffer text;

    private Map<File,String> files;

    public Email(SmtpHost smtpHost, String username, String password, String title, StringBuffer text) {
        this.smtpHost = smtpHost;
        this.username = username;
        this.password = password;
        this.title = title;
        this.text = text;
    }

    public Email(String title, StringBuffer text) {
        this.smtpHost = SmtpHost._163;
        this.username = biliDao.selectParam("163_username").get(0).getValue();
        this.password = biliDao.selectParam("163_password").get(0).getValue();
        this.title = title;
        this.text = text;
    }

    public SmtpHost getSmtpHost() {
        return smtpHost;
    }

    public boolean isAuth() {
        return auth;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String[] getTo() {
        return to;
    }

    public String getTitle() {
        return title;
    }

    public StringBuffer getText() {
        return text;
    }

    public Map<File, String> getFiles() {
        return files;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(StringBuffer text) {
        this.text = text;
    }

    public void setFiles(Map<File, String> files) {
        this.files = files;
    }

    public enum  SmtpHost{
        _163("smtp.163.com"),

        _126("smtp.126.com"),

        qq("smtp.qq.com"),

        sina("smtp.sina.com"),

        yahoo("smtp.mail.yahoo.com"),

        sohu("smtp.sohu.com"),

        tom("smtp.tom.com"),

        gmail("smtp.gmail.com"),

        _263("smtp.263.net"),

        _21cn("smtp.21cn.com");


        private String host;

        SmtpHost(String host) {
            this.host = host;
        }

        public String getHost() {
            return host;
        }


    }

    @Override
    public String toString() {
        return "Email{" +
                "title='" + title + '\'' +
                ", text=" + text +
                '}';
    }
}
