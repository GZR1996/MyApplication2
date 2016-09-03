    package com.example.a.myapplication.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.a.myapplication.activity.InboxActivity;
import com.example.a.myapplication.database.MailDB;
import com.example.a.myapplication.model.MailInformation;
import com.example.a.myapplication.model.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * Created by a on 2016/7/30.
 */
public class POP3Utility {

    public static final String TAG = "POP3Utility";

    private Context context;
    private String user;
    private String password;

    private MailDB mailDB;

    public POP3Utility(Context context,String user, String password) {
        this.context = context;
        this.user = user;
        this.password = password;
        this.mailDB = MailDB.getInstance(context);
    }

    public void login() throws MessagingException{
        Properties pros = new Properties();
        pros.setProperty("mail.store.protocol", "pop3");
        pros.setProperty("mail.pop3.port", "110");
        pros.setProperty("mail.pop3.host", "pop3.163.com");

        Folder folder = null;
        Store store = null;

        Log.d(TAG, "user：" + user + "\npassword：" + password);
        Session session = Session.getInstance(pros);
        store = session.getStore();
        store.connect(user, password);

        folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        User u = new User();
        u.setUser(user);
        u.setPassword(password);
        u.setNumMessage(0);
        u.setNumNewMessage(0);
        u.setNumUnreadMessage(0);
        mailDB.updateUser(u);
    }

    public void getMailInformation() throws MessagingException{
        Properties pros = new Properties();
        pros.setProperty("mail.store.protocol", "pop3");
        pros.setProperty("mail.pop3.port", "110");
        pros.setProperty("mail.pop3.host", "pop3.163.com");

        Folder folder = null;
        Store store = null;

        Log.d(TAG,"user：" + user +"\npassword：" + password);
        Session session = Session.getInstance(pros);
        store = session.getStore();
        store.connect(user,password);

        folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        System.out.println("未读邮件（"+folder.getUnreadMessageCount()+"封）");
        System.out.println("新邮件数： "+folder.getNewMessageCount());     //163永远是0
        System.out.println("邮件总数："+folder.getMessageCount());

        Message[] messages = folder.getMessages();
        User u = mailDB.loadUser(user);
        Log.d(TAG,u.getUser() + " "+u.getNumMessage() + "  "+folder.getMessageCount());
        if (u.getNumMessage() < folder.getMessageCount()) {
            parseMessage(messages);

            User userNew = new User();
            userNew.setUser(user);
            userNew.setPassword(password);
            userNew.setNumMessage(folder.getMessageCount());
            userNew.setNumNewMessage(folder.getNewMessageCount());
            userNew.setNumUnreadMessage(folder.getUnreadMessageCount());
            mailDB.updateUser(userNew);
        }

        Log.d(TAG,"POP3 ok");
    }

    private void parseMessage(Message[] messages) {
        try {
            if(messages == null || messages.length < 1) {
                throw new MessagingException("未找到邮件");
            }

            MailDB mailDB = MailDB.getInstance(context);

            for(int i = 0,count = messages.length;i < count;i++) {
                MailInformation mail = new MailInformation();
                StringBuilder content = new StringBuilder();
                MimeMessage msg = (MimeMessage)messages[i];
                getContent(msg,content);
                Log.d(TAG,content.toString());
                mail.setMailUser(user);
                mail.setSubject(getSubject(msg));
                mail.setFrom(getFrom(msg));
                mail.setReceiveAddress(getReceiveAddress(msg, null));
                mail.setSentDate(getSentDate(msg, null));
                mail.setSeen(isSeen(msg));
                mail.setPriority(getPriority(msg));
                mail.setReply(isReplySign(msg));
                mail.setSize(msg.getSize() * 1024);
                mail.setIsContainAttachment(isContainAttachment(msg));
                mail.setContent(content.toString());
                mailDB.saveMailInformation(mail);
            }
        } catch (Exception e) {

        }
    }

    private static String getSubject(MimeMessage msg) throws MessagingException,UnsupportedEncodingException{
        return MimeUtility.decodeText(msg.getSubject());
    }

    private static String getFrom(MimeMessage msg)  {
        String person = "";
        try {
            Address[] froms = msg.getFrom();
            if (froms.length < 1) {
                throw new MessagingException("没有发件人!");
            }

            InternetAddress address = (InternetAddress) froms[0];
            person = address.getPersonal();
            if (person != null) {
                person = MimeUtility.decodeText(person) + " ";
            } else {
                person = "<" + address.getAddress() + ">";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return person;
        }
    }

    private static String getReceiveAddress(MimeMessage msg,Message.RecipientType type) throws MessagingException {
        StringBuilder receiveAddress = new StringBuilder();
        Address[] addresses = null;
        if (type == null) {
            addresses = msg.getAllRecipients();
        } else {
            addresses = msg.getRecipients(type);
        }

        if (addresses == null || addresses.length < 1) {
            throw new MessagingException("没有收件人!");
        }
        for (Address address : addresses) {
            InternetAddress internetAddress = (InternetAddress)address;
            receiveAddress.append(internetAddress.toUnicodeString());
            receiveAddress.append(",");
        }

        receiveAddress.deleteCharAt(receiveAddress.length()-1);

        return receiveAddress.toString();
    }

    private static String getSentDate(MimeMessage msg,String pattern)  {
        Date receivedDate = null;
        try {
            receivedDate = msg.getSentDate();
            if (receivedDate == null) {
                return "";
            }

            if(pattern == null || pattern.equals("")) {
                pattern = "yyyy年MM月dd日  E HH:mm";
            }
        } catch (MessagingException e) {

        } finally {
            return new SimpleDateFormat(pattern).format(receivedDate);
        }
    }

    private static boolean isSeen(MimeMessage msg) throws MessagingException{
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    private static boolean isReplySign(MimeMessage msg) {
        boolean replySign = false;
        try {
            String[] header = msg.getHeader("Disposition-Notification-To");
            if (header != null) {
                replySign = true;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            return replySign;
        }
    }

    private static String getPriority(MimeMessage msg) {
        String priority = "普通";
        try {
            String[] header = msg.getHeader("X-Priority");
            if (header != null) {
                String headerPriority = header[0];
                if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1) {
                    priority = "紧急";
                } else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1) {
                    priority = "低";
                } else {
                    priority = "普通";
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            return priority;
        }
    }

    private static boolean isContainAttachment(Part part) {
        boolean isContainAttachment = false;
        try {
            if (part.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) part.getContent();
                int partCount = mimeMultipart.getCount();
                for (int i = 0;i < partCount;i++) {
                    BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                    String disp = bodyPart.getDisposition();
                    if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                        isContainAttachment = true;
                    } else if (bodyPart.isMimeType("multipart/*")) {
                        isContainAttachment = isContainAttachment(bodyPart);
                    } else {
                        String contentType = bodyPart.getContentType();
                        if (contentType.indexOf("application") != -1) {
                            isContainAttachment = true;
                        }

                        if (contentType.indexOf("name") != -1) {
                            isContainAttachment = true;
                        }
                    }

                    if (isContainAttachment == true) {
                        break;
                    }
                }
            } else if (part.isMimeType("message/rfc822")) {
                isContainAttachment = isContainAttachment((Part)part.getContent());
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return isContainAttachment;
        }
    }

    public static void getContent(Part part,StringBuilder content) {
        boolean isContainTextAttach = false;
        try {
            if (part.getContentType().indexOf("name") > 0) {
                isContainTextAttach = false;
            }

            if (part.isMimeType("text/*") && !isContainTextAttach) {
                content.append(part.getContent().toString());
            } else if (part.isMimeType("message/rfc822")) {
                getContent((Part)part.getContent(),content);
            } else if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart)part.getContent();
                int partCount = multipart.getCount();
                for (int i = 0;i < partCount;i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    getContent(bodyPart,content);
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
