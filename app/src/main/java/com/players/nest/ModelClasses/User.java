package com.players.nest.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    int ratings;
    long lastActiveTime;
    long phone_number;
    double account_balance;
    String user_id, email, username, fullName, description,
            profilePic, status, deviceToken;
    int role = 0;

    public User() {

    }

    public User(String user_id, long phone_number, String email, String fullName, String username,
                String description, String profilePic, String status, String deviceToken, int ratings,
                double account_balance) {
        this.email = email;
        this.ratings = ratings;
        this.status = status;
        this.user_id = user_id;
        this.fullName = fullName;
        this.username = username;
        this.phone_number = phone_number;
        this.description = description;
        this.profilePic = profilePic;
        this.deviceToken = deviceToken;
        this.account_balance = account_balance;
    }


    protected User(Parcel in) {
        ratings = in.readInt();
        lastActiveTime = in.readLong();
        phone_number = in.readLong();
        account_balance = in.readLong();
        user_id = in.readString();
        email = in.readString();
        username = in.readString();
        fullName = in.readString();
        description = in.readString();
        profilePic = in.readString();
        status = in.readString();
        deviceToken = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public double getAccount_balance() {
        return account_balance;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    public void setAccount_balance(double account_balance) {
        this.account_balance = account_balance;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ratings);
        dest.writeLong(lastActiveTime);
        dest.writeLong(phone_number);
        dest.writeDouble(account_balance);
        dest.writeString(user_id);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeString(description);
        dest.writeString(profilePic);
        dest.writeString(status);
        dest.writeString(deviceToken);
    }
}
