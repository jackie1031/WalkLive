package com.WalkLiveApp;

import java.util.Set;
import java.util.List;

public class User implements UserInterface {
    private String username;
    private String password;
    private String nickname;
    private Set<String> friendId;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<String> getLog() {
        return null;
    }

    public void manageLog() {

    }

    public int initiateTrip() {
        return 0;
    }

    public int modifyContact() {
        return 0;
    }

    public int sendTrackRequest() {
        return 0;
    }

    public int sendFriendRequest() {
        return 0;
    }

    @Override
    public String toString() {
        return "";
    }
}
