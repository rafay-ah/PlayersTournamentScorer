package com.players.nest.ModelClasses;

public class Chats_MessageAdapt {

    Chats chats;
    User user;

    public Chats_MessageAdapt(Chats chats, User user) {
        this.chats = chats;
        this.user = user;
    }

    public Chats getChats() {
        return chats;
    }

    public void setChats(Chats chats) {
        this.chats = chats;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
