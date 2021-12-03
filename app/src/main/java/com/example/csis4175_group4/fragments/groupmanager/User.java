package com.example.csis4175_group4.fragments.groupmanager;

import java.util.HashMap;

public class User {
    private String uid;
    private String email;
    private String username;
    private HashMap<String, String> groups;

    public User() {
        uid = "";
        email = "";
        username = "";
        groups = new HashMap<>();
    }

    public User(String uid, String email, String username, HashMap<String, String> groups) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.groups = groups;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HashMap<String, String> getGroups() {
        return groups;
    }

    public void setGroups(HashMap<String, String> groups) {
        this.groups = groups;
    }
}
