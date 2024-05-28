package cc.baka9.catseedlogin.util;


import cc.baka9.catseedlogin.bukkit.Config;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class Mail {

    private Mail(){
    }


    public static void sendMail(String receiveMailAccount, String subject, String content, String smtpHost, String smtpPort, boolean sslAuthVerify, String emailAccount, String emailPassword, String fromPersonal) throws Exception {

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", smtpHost);
        props.setProperty("mail.smtp.auth", "true");

        props.setProperty("mail.smtp.port", smtpPort);

        if (sslAuthVerify) {
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.socketFactory.port", smtpPort);
        }

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailAccount, emailPassword);
            }
        });

        // 设置为debug模式, 查看详细的发送 log
        session.setDebug(true);

        // 创建邮件
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailAccount, fromPersonal, "UTF-8"));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMailAccount, "", "UTF-8"));
        message.setSubject(subject, "UTF-8");
        message.setContent(content, getContentType());
        message.setSentDate(new Date());
        message.saveChanges();

        // 发送
        Transport.send(message);
    }

    private static String getContentType() {
        return Util.isOSLinux() ? "text/html; charset=UTF-8" : "text/html; charset=GBK";
    }

}
