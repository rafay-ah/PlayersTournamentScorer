package com.players.nest.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class UsersPosts implements Parcelable {

    String caption;
    String dateCreated;
    String imageUri;
    String postId;
    String userId;
    String tags;
    String postType;
    boolean turnOffComments;
    HashMap<String, Object> Likes;

    //Only for passing user object from Home Fragment to ViewProfile Fragment
    User user;

    public UsersPosts() {

    }

    public UsersPosts(String caption, String dateCreated, String imageUri, String postId,
                      String userId, String tags, String postType, boolean turnOffComments,
                      HashMap<String, Object> likes) {
        this.caption = caption;
        this.dateCreated = dateCreated;
        this.imageUri = imageUri;
        this.postId = postId;
        this.userId = userId;
        this.tags = tags;
        this.postType = postType;
        this.turnOffComments = turnOffComments;
        Likes = likes;
    }

    protected UsersPosts(Parcel in) {
        caption = in.readString();
        dateCreated = in.readString();
        imageUri = in.readString();
        postId = in.readString();
        userId = in.readString();
        tags = in.readString();
        postType = in.readString();
        turnOffComments = in.readByte() != 0;
//        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<UsersPosts> CREATOR = new Creator<UsersPosts>() {
        @Override
        public UsersPosts createFromParcel(Parcel in) {
            return new UsersPosts(in);
        }

        @Override
        public UsersPosts[] newArray(int size) {
            return new UsersPosts[size];
        }
    };

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public HashMap<String, Object> getLikes() {
        return Likes;
    }

    public void setLikes(HashMap<String, Object> likes) {
        this.Likes = likes;
    }

    public boolean isTurnOffComments() {
        return turnOffComments;
    }

    public void setTurnOffComments(boolean turnOffComments) {
        this.turnOffComments = turnOffComments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caption);
        dest.writeString(dateCreated);
        dest.writeString(imageUri);
        dest.writeString(postId);
        dest.writeString(userId);
        dest.writeString(tags);
        dest.writeString(postType);
        dest.writeByte((byte) (turnOffComments ? 1 : 0));
//        dest.writeParcelable(user, flags);
    }
}
