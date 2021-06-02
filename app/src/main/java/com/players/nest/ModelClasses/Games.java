package com.players.nest.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class Games implements Parcelable {

    String gameID;
    String gameImg, name;

    public Games() {

    }

    public Games(String gameID, String gameImg, String name) {
        this.gameID = gameID;
        this.gameImg = gameImg;
        this.name = name;
    }

    protected Games(Parcel in) {
        gameID = in.readString();
        gameImg = in.readString();
        name = in.readString();
    }

    public static final Creator<Games> CREATOR = new Creator<Games>() {
        @Override
        public Games createFromParcel(Parcel in) {
            return new Games(in);
        }

        @Override
        public Games[] newArray(int size) {
            return new Games[size];
        }
    };

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getGameImg() {
        return gameImg;
    }

    public void setGameImg(String gameImg) {
        this.gameImg = gameImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(gameID);
        parcel.writeString(gameImg);
        parcel.writeString(name);
    }
}
