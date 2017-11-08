package com.WalkLiveApp;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private String contact;
    private String nickname;
    private List<String> friendId = new ArrayList<>();
    private Date createdOn = new Date();

    public User(String username, String password, String contact) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.nickname = null;
        this.friendId = null;
        this.createdOn = null;
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

    public List<String> getFriendId() {
        return this.friendId;
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
        return "";
    }
}
