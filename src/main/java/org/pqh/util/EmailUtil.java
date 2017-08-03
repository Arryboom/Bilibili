package org.pqh.util;

import org.pqh.entity.Email;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * Created by reborn on 2017/5/14.
 * 发送邮件的工具类
 */
public class EmailUtil {

    public static void sendEmail(Email email){
        LogUtil.getLogger().info("发送邮件:"+email);

        String encoding="UTF-8";

        JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
        //smtp服务器
        javaMailSender.setHost(email.getSmtpHost().getHost());
        //发送人邮箱
        javaMailSender.setUsername(email.getUsername());
        //发送人密码
        javaMailSender.setPassword(email.getPassword());
        //邮件内容编码，防止乱码
        javaMailSender.setDefaultEncoding(encoding);
        Properties properties=new Properties();
        properties.setProperty("mail.smtp.auth","true");
        properties.setProperty("mail.smtp.timeout",PropertiesUtil.getProperties("mail.smtp.timeout",String.class));
        properties.setProperty("mail.smtp.port","25");
        javaMailSender.setJavaMailProperties(properties);

        try {
            MimeMessage mimeMessage=javaMailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true,encoding);

            helper.setSubject(email.getTitle());

            if(email.getFiles()!=null) {
                Map<File, String> files = email.getFiles();
                for (File file : files.keySet()) {
                    helper.addAttachment(StringUtil.isEmpty(files.get(files)) ? file.getName() : files.get(files), file);
                }
            }
            helper.setFrom(email.getUsername());
            helper.setTo(email.getTo()==null? new String[]{email.getUsername()}:email.getTo());
            helper.setText(email.getText().toString(),true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
