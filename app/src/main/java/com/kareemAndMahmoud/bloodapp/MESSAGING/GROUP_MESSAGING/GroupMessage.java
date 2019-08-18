package com.kareemAndMahmoud.bloodapp.MESSAGING.GROUP_MESSAGING;

public class GroupMessage{


    private String id, users, namegroup, lastmsg, photo ;

    public GroupMessage(){

    }

    public GroupMessage(String i, String u, String n, String l, String p){
        users = u;
        namegroup = n;
        lastmsg = l;
        photo = p;
        id= i;
    }

    public String getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public String getNamegroup() {
        return namegroup;
    }

    public String getUsers() {
        return users;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    public void setNamegroup(String namegroup) {
        this.namegroup = namegroup;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
