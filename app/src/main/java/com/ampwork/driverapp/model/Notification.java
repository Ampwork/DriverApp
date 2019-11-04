package com.ampwork.driverapp.model;

public class Notification {

    private String date;
    private String message;
    private String usergroup;

    public Notification() {
    }

    public Notification(String date, String message, String usergroup) {
        this.date = date;
        this.message = message;
        this.usergroup = usergroup;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsergroup() {
        return usergroup;
    }

    public void setUsergroup(String usergroup) {
        this.usergroup = usergroup;
    }
}
