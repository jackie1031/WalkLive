package com.WalkLiveApp;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private String contact;
    private String nickname;
    private Date createdOn = new Date();
    private String emergencyId;
    private String emergencyContact;

    public User(String username, String password, String contact) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.nickname = null;
        this.createdOn = null;
        this.emergencyId = null;
        this.emergencyContact = null;
    }


    public User(String username, String password, String contact, String nickname, Date createdOn, String emergencyId, String emergencyContact) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.nickname = nickname;
        this.createdOn = createdOn;
        this.emergencyId = emergencyId;
        this.emergencyContact = emergencyContact;
    }

    //add setters

    public String getUsername() {
        return this.username;
    }

    public String getPassword() { return this.password; }

    public String getContact() { return this.contact; }

    public String getNickname() {
        return this.nickname;
    }

    public Date getCreatedOn() {
        return this.createdOn;
    }

//    public List<String> getLog() {
//        return null;
//    }
//
//    public void manageLog() {
//
//    }
//
//    public int initiateTrip() {
//        return 0;
//    }
//
//    public int modifyContact() {
//        return 0;
//    }
//
//    public int sendTrackRequest() {
//        return 0;
//    }
//
//    public int sendFriendRequest() {
//        return 0;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (username != user.username) return false;
        if (nickname != null ? !nickname.equals(user.nickname) : user.nickname != null) return false;
        return !(createdOn != null ? !createdOn.equals(user.createdOn) : user.createdOn != null);
    }

    public String toString() {

        String str = this.username + " " + this.password;

        return str;
    }
}
