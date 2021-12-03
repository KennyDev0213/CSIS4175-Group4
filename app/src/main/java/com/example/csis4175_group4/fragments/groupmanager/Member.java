package com.example.csis4175_group4.fragments.groupmanager;

import java.util.HashMap;
import java.util.Map;

public class Member {
    private String id;
    private String userid;
    private String role;

    public Member() {
        id = "";
        userid = "";
        role = "";
    }

    public Member(String id, String userId, String role) {
        this.id = id;
        this.userid = userId;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("userid", userid);
        result.put("role", role);

        return result;
    }
}
