package com.example.a.myapplication.model;

/**
 * Created by a on 2016/7/30.
 */
public class MailInformation {

    //邮件信息
    //所属邮箱
    private String mailUser;
    //标题
    private String subject;
    //发件人
    private String from;
    //收件人
    private String receiveAddress;
    //发送时间
    private String sentDate;
    //优先级
    private String priority;
    //已读标志
    private boolean seen;
    //回执标志(是否需要回执)
    private boolean reply;
    //是否包含附件
    private boolean isContainAttachment;
    //邮件大小
    private int size;
    //文本邮件内容
    private String content;
    //HTML邮件内容
    private String HTML;

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean getReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public boolean getIsContainAttachment() {
        return isContainAttachment;
    }

    public void setIsContainAttachment(boolean isContainAttachment) {
        this.isContainAttachment = isContainAttachment;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHTML() {
        return HTML;
    }

    public void setHTML(String HTML) {
        this.HTML = HTML;
    }

}
