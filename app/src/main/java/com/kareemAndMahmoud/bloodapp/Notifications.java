package com.kareemAndMahmoud.bloodapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kareemAndMahmoud.bloodapp.MESSAGING.Message;

import java.util.HashMap;
import java.util.Map;

public class Notifications {

    public  void sendNotificationToUser(Message message) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference()
                .child("notifications")
                .child("messages")
                .push()
                .setValue(message);
    }

}
