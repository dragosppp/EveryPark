package com.example.park.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    private String email;
    private String userId;
    private String username;
    private String avatar;

    public User(String email, String userId, String username, String avatar) {
        this.email = email;
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
    }

    public User() {

    }

    protected User(Parcel parcel) {
        email = parcel.readString();
        userId = parcel.readString();
        username = parcel.readString();
        avatar = parcel.readString();
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", user_id='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(avatar);
    }
}

