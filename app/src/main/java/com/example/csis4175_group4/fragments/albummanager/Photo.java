package com.example.csis4175_group4.fragments.albummanager;

import java.util.HashMap;
import java.util.Map;

public class Photo {
    private String id; //photo id
    private String filename;
    private String date;
    private String desc;

    public Photo() {
        id = "";
        filename = "";
        date = "";
        desc = "";
    }

    public Photo(String id, String filename, String date, String desc) {
        this.id = id;
        this.filename = filename;
        this.date = date;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("filename", filename);
        result.put("date", date);
        result.put("desc", desc);

        return result;
    }
}
