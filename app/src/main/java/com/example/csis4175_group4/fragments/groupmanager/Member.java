package com.example.csis4175_group4.fragments.groupmanager;

public class Member {
    private String userid;
    private String role;

    public Member() {
    }

    public Member(String userId, String role) {
        this.userid = userId;
        this.role = role;
    }

    public String getUserId() {
        return userid;
    }

    public void setUserId(String userId) {
        this.userid = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
