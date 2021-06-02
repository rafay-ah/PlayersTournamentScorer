package com.players.nest.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.appcompat.widget.AppCompatButton;

import java.util.HashMap;

public class    TournamentMatches implements Parcelable {

    String  matchFinishedTime, matchWinner;
    boolean userOneAccepted, userOneRejected,userTwoAccepted,userTwoRejected, timerFinished,
            submitResultTimerFinished;
    HashMap<String, Object> rejectedUserIds;
    String match_ID,userOneId,userTwoId;
    private Scores scores;
    String matchStatus;
    int sectionNumber;

    public TournamentMatches() {
    }

    public TournamentMatches(String matchFinishedTime, String matchWinner, boolean userOneAccepted,
                             boolean userOneRejected, boolean userTwoAccepted,
                             boolean userTwoRejected, boolean timerFinished, boolean submitResultTimerFinished,
                             HashMap<String, Object> rejectedUserIds, String match_ID, String userOneId,String userTwoId,
                             String matchStatus, int sectionNumber,Scores scores) {
        this.matchFinishedTime = matchFinishedTime;
        this.matchWinner = matchWinner;
        this.userOneRejected = userOneRejected;
        this.userTwoRejected = userTwoRejected;
        this.userOneAccepted = userOneAccepted;
        this.userTwoAccepted = userTwoAccepted;
        this.timerFinished = timerFinished;
        this.submitResultTimerFinished = submitResultTimerFinished;
        this.rejectedUserIds = rejectedUserIds;
        this.match_ID = match_ID;
        this.userOneId = userOneId;
        this.userTwoId = userTwoId;
        this.matchStatus = matchStatus;
        this.sectionNumber = sectionNumber;
        this.scores = scores;
    }



    protected TournamentMatches(Parcel in) {
        matchFinishedTime = in.readString();
        matchWinner = in.readString();
        userOneAccepted = in.readByte() != 0;
        userTwoAccepted = in.readByte() != 0;
        userOneRejected = in.readByte() != 0;
        userTwoRejected = in.readByte() != 0;
        timerFinished = in.readByte() != 0;
        submitResultTimerFinished = in.readByte() != 0;
        match_ID = in.readString();
        userOneId = in.readString();
        userTwoId = in.readString();
        matchStatus = in.readString();
        sectionNumber = in.readInt();
        scores = in.readParcelable(Scores.class.getClassLoader());
    }

    public static final Creator<TournamentMatches> CREATOR = new Creator<TournamentMatches>() {
        @Override
        public TournamentMatches createFromParcel(Parcel in) {
            return new TournamentMatches(in);
        }

        @Override
        public TournamentMatches[] newArray(int size) {
            return new TournamentMatches[size];
        }
    };


    public String getMatchFinishedTime() {
        return matchFinishedTime;
    }

    public void setMatchFinishedTime(String matchFinishedTime) {
        this.matchFinishedTime = matchFinishedTime;
    }

    public String getMatchWinner() {
        return matchWinner;
    }

    public void setMatchWinner(String matchWinner) {
        this.matchWinner = matchWinner;
    }


    public boolean isTimerFinished() {
        return timerFinished;
    }

    public void setTimerFinished(boolean timerFinished) {
        this.timerFinished = timerFinished;
    }

    public boolean isSubmitResultTimerFinished() {
        return submitResultTimerFinished;
    }

    public void setSubmitResultTimerFinished(boolean submitResultTimerFinished) {
        this.submitResultTimerFinished = submitResultTimerFinished;
    }

    public HashMap<String, Object> getRejectedUserIds() {
        return rejectedUserIds;
    }

    public void setRejectedUserIds(HashMap<String, Object> rejectedUserIds) {
        this.rejectedUserIds = rejectedUserIds;
    }

    public String getMatch_ID() {
        return match_ID;
    }

    public void setMatch_ID(String match_ID) {
        this.match_ID = match_ID;
    }

    public boolean isUserOneAccepted() {
        return userOneAccepted;
    }

    public void setUserOneAccepted(boolean userOneAccepted) {
        this.userOneAccepted = userOneAccepted;
    }

    public boolean isUserOneRejected() {
        return userOneRejected;
    }

    public void setUserOneRejected(boolean userOneRejected) {
        this.userOneRejected = userOneRejected;
    }

    public boolean isUserTwoAccepted() {
        return userTwoAccepted;
    }

    public void setUserTwoAccepted(boolean userTwoAccepted) {
        this.userTwoAccepted = userTwoAccepted;
    }

    public boolean isUserTwoRejected() {
        return userTwoRejected;
    }

    public void setUserTwoRejected(boolean userTwoRejected) {
        this.userTwoRejected = userTwoRejected;
    }

    public String getUserOneId() {
        return userOneId;
    }

    public void setUserOneId(String userOneId) {
        this.userOneId = userOneId;
    }

    public String getUserTwoId() {
        return userTwoId;
    }

    public void setUserTwoId(String userTwoId) {
        this.userTwoId = userTwoId;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(matchFinishedTime);
        dest.writeString(matchWinner);
        dest.writeByte((byte) (userOneAccepted ? 1 : 0));
        dest.writeByte((byte) (userTwoAccepted ? 1 : 0));
        dest.writeByte((byte) (userOneRejected ? 1 : 0));
        dest.writeByte((byte) (userTwoRejected ? 1 : 0));
        dest.writeByte((byte) (timerFinished ? 1 : 0));
        dest.writeByte((byte) (submitResultTimerFinished ? 1 : 0));
        dest.writeString(match_ID);
        dest.writeString(userOneId);
        dest.writeString(userTwoId);
        dest.writeString(matchStatus);
        dest.writeInt(sectionNumber);
        dest.writeParcelable(scores,flags);

    }
}
