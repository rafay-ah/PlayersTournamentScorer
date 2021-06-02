package com.players.nest.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class Scores implements Parcelable {

    String userID;
    boolean isSubmitted;
    int userScore, opponentScore;

    public Scores() {

    }

    public Scores(String userID, int userScore, int opponentScore, boolean isSubmitted) {
        this.userID = userID;
        this.userScore = userScore;
        this.opponentScore = opponentScore;
        this.isSubmitted = isSubmitted;
    }

    protected Scores(Parcel in) {
        userID = in.readString();
        isSubmitted = in.readByte() != 0;
        userScore = in.readInt();
        opponentScore = in.readInt();
    }

    public static final Creator<Scores> CREATOR = new Creator<Scores>() {
        @Override
        public Scores createFromParcel(Parcel in) {
            return new Scores(in);
        }

        @Override
        public Scores[] newArray(int size) {
            return new Scores[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }

    public boolean getSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        this.isSubmitted = submitted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeByte((byte) (isSubmitted ? 1 : 0));
        dest.writeInt(userScore);
        dest.writeInt(opponentScore);
    }
}
