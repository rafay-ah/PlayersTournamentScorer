package com.players.nest.ModelClasses;

public class Comment {

    private String commentID, comment;
    private String user_id;
    private long date_created;

    public Comment() {

    }

    public Comment(String commentID, String comment, String user_id, long date_created) {
        this.commentID = commentID;
        this.comment = comment;
        this.user_id = user_id;
        this.date_created = date_created;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getDate_created() {
        return date_created;
    }

    public void setDate_created(long date_created) {
        this.date_created = date_created;
    }
}
