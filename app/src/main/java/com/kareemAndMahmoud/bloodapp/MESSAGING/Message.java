package com.kareemAndMahmoud.bloodapp.MESSAGING;

public class Message {


    private String name;
    private String id;
    private String hisId;
    private String userImage;
    private String conStr;
    private String conImg, type, location;
    private int time;


    public Message(){}

    public Message(String id,String hisId, String name, String userImage, String conStr, String conImg, int time, String type, String l){
        this.name = name;
        this.hisId = hisId;
        this.id = id;
        this.userImage = userImage;
        this.conStr = conStr;
        this.conImg = conImg;
        this.type = type;
        this.time = time;
        location = l;
    }

    public String getHisId() {
        return hisId;
    }

    public void setHisId(String hisId) {
        this.hisId = hisId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getConImg() {
        return conImg;
    }

    public int getTime() {
        return time;
    }

    public String getConStr() {
        return conStr;
    }

    public String getUserImage() {
        return userImage;
    }

}
