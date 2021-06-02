package com.players.nest.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class MatchDetail implements Parcelable {

    Games game;
    double entryFee, winningAmt;
    String timeCreated, matchFinishedTime, matchWinner,
            customRules;
    boolean hostAccepted, hostRejected, timerFinished,
            submitResultTimerFinished;
    HashMap<String, Object> rejectedUserIds;
    String match_ID, hostUserId, joinedUserID;
    String format, rules, console, matchStatus;

    MatchDetail() {

    }

    public MatchDetail(Games games, double entryFee, double winningAmt, String timeCreated, String match_ID,
                       String hostUserId, String joinedUserID, String format, String rules, String customRules,
                       String console, boolean hostAccepted, boolean hostRejected, String matchStatus, HashMap<String,
            Object> rejectedUserIds, boolean timerFinished, boolean submitResultTimerFinished,
                       String matchFinishedTime, String matchWinner) {
        this.game = games;
        this.rules = rules;
        this.format = format;
        this.console = console;
        this.entryFee = entryFee;
        this.match_ID = match_ID;
        this.winningAmt = winningAmt;
        this.hostUserId = hostUserId;
        this.matchStatus = matchStatus;
        this.timeCreated = timeCreated;
        this.customRules = customRules;
        this.matchWinner = matchWinner;
        this.hostAccepted = hostAccepted;
        this.hostRejected = hostRejected;
        this.joinedUserID = joinedUserID;
        this.timerFinished = timerFinished;
        this.rejectedUserIds = rejectedUserIds;
        this.matchFinishedTime = matchFinishedTime;
        this.submitResultTimerFinished = submitResultTimerFinished;
    }

    protected MatchDetail(Parcel in) {
        game = in.readParcelable(Games.class.getClassLoader());
        entryFee = in.readDouble();
        winningAmt = in.readDouble();
        timeCreated = in.readString();
        matchFinishedTime = in.readString();
        hostAccepted = in.readByte() != 0;
        hostRejected = in.readByte() != 0;
        timerFinished = in.readByte() != 0;
        submitResultTimerFinished = in.readByte() != 0;
        match_ID = in.readString();
        hostUserId = in.readString();
        joinedUserID = in.readString();
        format = in.readString();
        rules = in.readString();
        console = in.readString();
        customRules = in.readString();
        matchStatus = in.readString();
        matchWinner = in.readString();
    }

    public static final Creator<MatchDetail> CREATOR = new Creator<MatchDetail>() {
        @Override
        public MatchDetail createFromParcel(Parcel in) {
            return new MatchDetail(in);
        }

        @Override
        public MatchDetail[] newArray(int size) {
            return new MatchDetail[size];
        }
    };

    public Games getGame() {
        return game;
    }

    public void setGame(Games game) {
        this.game = game;
    }

    public double getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(double entryFee) {
        this.entryFee = entryFee;
    }

    public double getWinningAmt() {
        return winningAmt;
    }

    public void setWinningAmt(double winningAmt) {
        this.winningAmt = winningAmt;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getMatch_ID() {
        return match_ID;
    }

    public void setMatch_ID(String match_ID) {
        this.match_ID = match_ID;
    }

    public String getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(String hostUserId) {
        this.hostUserId = hostUserId;
    }

    public String getJoinedUserID() {
        return joinedUserID;
    }

    public void setJoinedUserID(String joinedUserID) {
        this.joinedUserID = joinedUserID;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getConsole() {
        return console;
    }

    public void setConsole(String console) {
        this.console = console;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public HashMap<String, Object> getRejectedUserIds() {
        return rejectedUserIds;
    }

    public void setRejectedUserIds(HashMap<String, Object> rejectedUserIds) {
        this.rejectedUserIds = rejectedUserIds;
    }

    public boolean isHostAccepted() {
        return hostAccepted;
    }

    public void setHostAccepted(boolean hostAccepted) {
        this.hostAccepted = hostAccepted;
    }

    public boolean isHostRejected() {
        return hostRejected;
    }

    public void setHostRejected(boolean hostRejected) {
        this.hostRejected = hostRejected;
    }

    public String getCustomRules() {
        return customRules;
    }

    public void setCustomRules(String customRules) {
        this.customRules = customRules;
    }

    public boolean isTimerFinished() {
        return timerFinished;
    }

    public void setTimerFinished(boolean timerFinished) {
        this.timerFinished = timerFinished;
    }

    public String getMatchFinishedTime() {
        return matchFinishedTime;
    }

    public void setMatchFinishedTime(String matchFinishedTime) {
        this.matchFinishedTime = matchFinishedTime;
    }

    public boolean isSubmitResultTimerFinished() {
        return submitResultTimerFinished;
    }

    public void setSubmitResultTimerFinished(boolean submitResultTimerFinished) {
        this.submitResultTimerFinished = submitResultTimerFinished;
    }

    public String getMatchWinner() {
        return matchWinner;
    }

    public void setMatchWinner(String matchWinner) {
        this.matchWinner = matchWinner;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(game, flags);
        dest.writeDouble(entryFee);
        dest.writeDouble(winningAmt);
        dest.writeString(timeCreated);
        dest.writeString(matchFinishedTime);
        dest.writeByte((byte) (hostAccepted ? 1 : 0));
        dest.writeByte((byte) (hostRejected ? 1 : 0));
        dest.writeByte((byte) (timerFinished ? 1 : 0));
        dest.writeByte((byte) (submitResultTimerFinished ? 1 : 0));
        dest.writeString(match_ID);
        dest.writeString(hostUserId);
        dest.writeString(joinedUserID);
        dest.writeString(format);
        dest.writeString(rules);
        dest.writeString(console);
        dest.writeString(customRules);
        dest.writeString(matchStatus);
        dest.writeString(matchWinner);
    }
}
