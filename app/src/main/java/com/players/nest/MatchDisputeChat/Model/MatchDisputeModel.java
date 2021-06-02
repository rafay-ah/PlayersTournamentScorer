package com.players.nest.MatchDisputeChat.Model;

public class MatchDisputeModel {
    private String message,
            senderId,
            receiverId,
            seen,
            type,
            imageUrl;
    private long timeCreated;

    public MatchDisputeModel() {
    }

    public MatchDisputeModel(String message, String senderId, String receiverId, String seen, String type, String imageUrl, long timeCreated) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.seen = seen;
        this.type = type;
        this.imageUrl = imageUrl;
        this.timeCreated = timeCreated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
