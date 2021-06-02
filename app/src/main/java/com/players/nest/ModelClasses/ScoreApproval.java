package com.players.nest.ModelClasses;

public class ScoreApproval {

    String userId, approvedStatus;

    public ScoreApproval() {

    }

    public ScoreApproval(String userId, String approvedStatus) {
        this.userId = userId;
        this.approvedStatus = approvedStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApprovedStatus() {
        return approvedStatus;
    }

    public void setApprovedStatus(String approvedStatus) {
        this.approvedStatus = approvedStatus;
    }
}
