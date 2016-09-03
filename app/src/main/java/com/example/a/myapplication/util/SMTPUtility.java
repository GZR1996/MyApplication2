package com.example.a.myapplication.util;

import android.content.Context;

import com.example.a.myapplication.database.MailDB;
import com.example.a.myapplication.model.User;

import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by Administrator on 2016/8/13.
 */
public class SMTPUtility {

    private static final String PORT = "465";
    private String host;
    private String from;
    private String to;
    private String password;
    private Context context;
    private MailDB mailDB;

    public SMTPUtility(Context context,String host,String from,String to) {
        this.context = context;
        this.host = host;
        this.from = from;
        this.to = to;
        mailDB = MailDB.getInstance(context);
        password = mailDB.loadUser(from).getPassword();
    }

    public void sendMessage(String subject,String content) throws MessagingException{
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");

        MyAuthenticator authenticator = new MyAuthenticator(from,password);
        Session session = Session.getDefaultInstance(properties, authenticator);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        message.setSubject(subject);
        message.setText(content);
        message.saveChanges();
        Transport.send(message);
    }

    public void sendMessageWithAttach(String subject,String content,List<String> filePath) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", PORT);

        MyAuthenticator auth = new MyAuthenticator(from, password);
        Session session = Session.getDefaultInstance(props, auth);
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content,"text/html");
        multipart.addBodyPart(mimeBodyPart);

        if (filePath != null && filePath.size() > 0) {
            for (String path : filePath) {
                MimeBodyPart attachPart = new MimeBodyPart();
                DataSource source = new FileDataSource(path);
                attachPart.setDataHandler(new DataHandler(source));
                attachPart.setFileName(path);
                multipart.addBodyPart(attachPart);
            }
        }

        message.setContent(multipart);

        Transport transport = session.getTransport("smtp");
        transport.connect(host,Integer.parseInt(PORT),from,password);

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        Transport.send(message);
    }

    class MyAuthenticator extends Authenticator {

        private String user;
        private String password;

        public MyAuthenticator(String user,String password) {
            this.user = user;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password);
        }
    }
}
