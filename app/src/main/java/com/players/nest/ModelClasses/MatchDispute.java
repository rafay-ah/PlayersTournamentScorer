package com.players.nest.ModelClasses;

public class MatchDispute {

    String userId, scoreImage, message, scoreVideo, userName, userProfilePicture;

    public MatchDispute() {

    }

    public MatchDispute(String userId, String scoreImage, String scoreVideo, String message) {
        this.userId = userId;
        this.scoreVideo = scoreVideo;
        this.scoreImage = scoreImage;
        this.message = message;
    }

    public MatchDispute(String userId, String scoreImage, String scoreVideo, String message, String userName, String userProfilePicture) {
        this.userId = userId;
        this.scoreImage = scoreImage;
        this.message = message;
        this.scoreVideo = scoreVideo;
        this.userName = userName;
        this.userProfilePicture = userProfilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScoreImage() {
        return scoreImage;
    }

    public void setScoreImage(String scoreImage) {
        this.scoreImage = scoreImage;
    }

    public String getScoreVideo() {
        return scoreVideo;
    }

    public void setScoreVideo(String scoreVideo) {
        this.scoreVideo = scoreVideo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
