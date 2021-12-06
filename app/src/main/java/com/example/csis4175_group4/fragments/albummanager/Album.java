package com.example.csis4175_group4.fragments.albummanager;

import com.example.csis4175_group4.fragments.groupmanager.Member;

import java.util.HashMap;
import java.util.Map;

public class Album {
    private String id;
    private String title;
    private String contents;
    private String groupid;
    private HashMap<String, Photo> photos;

    public Album() {
        id = "";
        title = "";
        contents = "";
        groupid = "";
        photos = new HashMap<>();
    }

    public Album(String id, String title, String contents, String groupid, HashMap<String, Photo> photos) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.groupid = groupid;
        this.photos = photos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getGroupId() {
        return groupid;
    }

    public void setGroupId(String groupid) {
        this.groupid = groupid;
    }

    public HashMap<String, Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(HashMap<String, Photo> photos) {
        this.photos = photos;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("contents", contents);
        result.put("groupid", groupid);
        result.put("members", photos);

        return result;
    }
}
