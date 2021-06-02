package com.players.nest.ModelClasses;

public class Chats {

    UsersPosts usersPost;
    String senderId, receiverId, message,
            messageId, type, imageUri;
    boolean isSeen;

    long timeCreated;

    public Chats() {

    }

    public Chats(String senderId, String receiverId, String message, String messageId, String type,
                 UsersPosts usersPost, String imageUri, boolean isSeen, long timeCreated) {
        this.type = type;
        this.isSeen = isSeen;
        this.message = message;
        this.imageUri = imageUri;
        this.senderId = senderId;
        this.usersPost = usersPost;
        this.receiverId = receiverId;
        this.messageId = messageId;
        this.timeCreated = timeCreated;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public UsersPosts getUsersPost() {
        return usersPost;
    }

    public void setUsersPost(UsersPosts usersPost) {
        this.usersPost = usersPost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
