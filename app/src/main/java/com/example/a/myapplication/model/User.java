package com.example.a.myapplication.model;

/**
 * Created by Administrator on 2016/8/6.
 */
public class User {

    private String user;
    private String password;
    private int numNewMessage;
    private int numUnreadMessage;
    private int numMessage;

    public String getUser(){
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getNumNewMessage() {
        return numNewMessage;
    }

    public int getNumUnreadMessage() {
        return numUnreadMessage;
    }

    public int getNumMessage() {
        return numMessage;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNumNewMessage(int numNewMessage) {
        this.numNewMessage = numNewMessage;
    }

    public void setNumUnreadMessage(int numUnreadMessage) {
        this.numUnreadMessage = numUnreadMessage;
    }

    public void setNumMessage(int numMessage) {
        this.numMessage = numMessage;
    }

}
