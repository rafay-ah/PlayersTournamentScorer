package com.players.nest.Notifications;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.players.nest.ModelClasses.User;

public class NotificationReq {

    @SerializedName("to")
    @Expose
    private String to;

    @SerializedName("notification")
    @Expose
    private Notification notification;

    @SerializedName("data")
    @Expose
    private Data data;

    public NotificationReq(String to, Notification notification, Data data) {
        this.to = to;
        this.data = data;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }


    public static class Notification {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("body")
        @Expose
        private String body;
        @SerializedName("click_action")
        @Expose
        private String click_action;
        @SerializedName("sound")
        @Expose
        private String sound;


        public Notification(String title, String body, String click_action, String sound) {
            this.title = title;
            this.body = body;
            this.sound = sound;
            this.click_action = click_action;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getClick_action() {
            return click_action;
        }

        public void setClick_action(String click_action) {
            this.click_action = click_action;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }
    }


    public static class Data {

        @SerializedName("PARCEL_KEY")
        @Expose
        private String PARCEL_KEY;
        @SerializedName("USER_OBJECT")
        @Expose
        private User USER_OBJECT;
        @SerializedName("ALERT_FRAGMENT")
        @Expose
        private String ALERT_FRAGMENT;

        public Data(String ALERT_FRAGMENT) {
            this.ALERT_FRAGMENT = ALERT_FRAGMENT;
        }

        public Data(String PARCEL_KEY, User USER_OBJECT) {
            this.PARCEL_KEY = PARCEL_KEY;
            this.USER_OBJECT = USER_OBJECT;
        }

        public String getPARCEL_KEY() {
            return PARCEL_KEY;
        }

        public void setPARCEL_KEY(String PARCEL_KEY) {
            this.PARCEL_KEY = PARCEL_KEY;
        }

        public User getUSER_OBJECT() {
            return USER_OBJECT;
        }

        public void setUSER_OBJECT(User USER_OBJECT) {
            this.USER_OBJECT = USER_OBJECT;
        }

        public String getALERT_FRAGMENT() {
            return ALERT_FRAGMENT;
        }

        public void setALERT_FRAGMENT(String ALERT_FRAGMENT) {
            this.ALERT_FRAGMENT = ALERT_FRAGMENT;
        }
    }
}
