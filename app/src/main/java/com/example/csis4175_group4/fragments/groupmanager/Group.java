package com.example.csis4175_group4.fragments.groupmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {
    private String id;
    private String name;
    private HashMap<String, Member> members;

    public Group() {
        id = "";
        name = "";
        members = new HashMap<>();
    }

    public Group(String id, String name, HashMap<String, Member> members) {
        this.id = id;
        this.name = name;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Member> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Member> members) {
        this.members = members;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("members", members);

        return result;
    }
}
