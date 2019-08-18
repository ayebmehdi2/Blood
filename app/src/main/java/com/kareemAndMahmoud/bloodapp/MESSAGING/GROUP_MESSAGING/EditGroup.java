package com.kareemAndMahmoud.bloodapp.MESSAGING.GROUP_MESSAGING;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kareemAndMahmoud.bloodapp.PROFILE.AdapterPeople;
import com.kareemAndMahmoud.bloodapp.PROFILE.ProfileActivity;
import com.kareemAndMahmoud.bloodapp.PROFILE.profilData;
import com.kareemAndMahmoud.bloodapp.R;
import com.kareemAndMahmoud.bloodapp.databinding.EditGroupBinding;


import java.util.ArrayList;

public class EditGroup extends AppCompatActivity implements AdapterPeople.clickPrson {

    EditGroupBinding binding;
    private String groupId;
    private AdapterPeople people;
    private ArrayList<profilData> whoAdd;
    private String[] Users;

    private DatabaseReference rf1, rf2;
    private ValueEventListener v1, v2 ;


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(me).child("status").setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(me).child("status").setValue("offline");
    }

    private String me;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.edit_group);


        groupId = getIntent().getStringExtra("grpId");

        if (groupId == null) return;
        if (!(groupId.length() > 0)) return;

        String adminId = groupId.split("--")[1];

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
         me = preferences.getString("uid", "");


        people = new AdapterPeople(this, 23);

        binding.members.setLayoutManager(new LinearLayoutManager(this));
        binding.members.setHasFixedSize(true);
        binding.members.setAdapter(people);

        whoAdd = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();


        v1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profilData data = dataSnapshot.getValue(profilData.class);
                if (data == null) return;
                binding.nameAdmin.setText(data.getName());
                Glide.with(EditGroup.this).load(data.getPhoto()).into(binding.photoAdmin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        rf1 = database.getReference().child("PROFILES").child(adminId);
                rf1.addValueEventListener(v1);


                v2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GroupMessage  groupMessage = dataSnapshot.getValue(GroupMessage.class);
                        if (groupMessage == null) return;
                        binding.nameGrp.setText(groupMessage.getNamegroup());
                        Users = groupMessage.getUsers().split(",");
                        for (String us : Users){
                            if (!(us.equals(adminId))){
                                whoAdd.add(new profilData(us, null, null, null,null, null, us, null, null, null, "no"));
                            }
                        }

                        if (whoAdd.size() > 0){
                            people.swapAdapter(whoAdd);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                };

        rf2 = database.getReference().child("PROFILES").child(adminId).child("GROUP_MESSAGE").child(groupId);
                rf2.addValueEventListener(v2);



        if (me.equals(adminId)){
            binding.upName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newName = binding.nameGrp.getText().toString();
                    if (!(newName.length()>0))return;
                    Toast.makeText(EditGroup.this, "Group name is chaged", Toast.LENGTH_LONG).show();
                    for (String us : Users){
                        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(us).child("GROUP_MESSAGE").child(groupId)
                                .child("namegroup")
                                .setValue(newName);
                        binding.upName.setEnabled(false);
                    }
                }
            });
        }else {
            binding.update.setVisibility(View.GONE);
        }


        binding.admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditGroup.this, ProfileActivity.class);
                i.putExtra("uid", adminId);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        whoAdd.clear();
        rf1.removeEventListener(v1);
        rf2.removeEventListener(v2);
    }

    @Override
    public void select(String uid) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("uid", uid);
        startActivity(i);
    }

    @Override
    public void add(String uid, String d) {

    }
    @Override
    public void msg(String uid) {

    }


}
