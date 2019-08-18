package com.kareemAndMahmoud.bloodapp.MESSAGING.CHATROOM;

public class PersonMessage {

    private String id, name, photo, msg;

    public PersonMessage(){

    }

    public PersonMessage(String i, String n, String p, String m){
        id = i;
        name = n;
        photo = p;
        msg = m;
    }



    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
