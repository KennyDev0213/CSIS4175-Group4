package com.example.csis4175_group4.fragments.groupmanager;

import java.util.ArrayList;

public class Group {
    private String name;
    private ArrayList<Member> members;

    public Group() {
        name = "";
        members = new ArrayList<>();
    }

    public Group(String name, ArrayList<Member> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }
}
