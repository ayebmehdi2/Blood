package com.kareemAndMahmoud.bloodapp.PROFILE;

public class profilData {

    private String name, email, password, number, location, photo, uid, bloodType, bloodLastTime, diseases, status;

    public profilData() {}

    public profilData(String name, String email, String password, String number, String location
            , String photo, String id, String bt, String bl,String di, String status){
        this.name = name;
        this.email = email;
        this.password = password;
        this.number = number;
        this.location = location;
        this.photo = photo;
        uid = id;
        bloodType = bt;
        bloodLastTime = bl;
        diseases = di;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBloodLastTime() {
        return bloodLastTime;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getDiseases() {
        return diseases;
    }

    public void setBloodLastTime(String bloodLastTime) {
        this.bloodLastTime = bloodLastTime;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setDiseases(String diseases) {
        this.diseases = diseases;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getNumber() {
        return number;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
