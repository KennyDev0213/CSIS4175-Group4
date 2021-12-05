package com.example.csis4175_group4.fragments.groupmanager;

import java.util.HashMap;
import java.util.Map;

public class Member {
    private String uid; //member key, uid
    private String email;
    private String role;

    public Member() {
        uid = "";
        email = "";
        role = "";
    }

    public Member(String uid, String email, String role) {
        this.uid = uid;
        this.email = email;
        this.role = role;
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

    public void setEmail(String userId) {
        this.email = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("email", email);
        result.put("role", role);

        return result;
    }
}
