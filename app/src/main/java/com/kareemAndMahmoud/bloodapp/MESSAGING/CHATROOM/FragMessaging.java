package com.kareemAndMahmoud.bloodapp.MESSAGING.CHATROOM;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kareemAndMahmoud.bloodapp.PROFILE.Search;
import com.kareemAndMahmoud.bloodapp.R;


import java.util.ArrayList;

public class FragMessaging extends Fragment  implements AdapterPersonMessaging.clickMessagingPerson {

    private AdapterPersonMessaging perMes;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private ArrayList<PersonMessage> messageList;
    private ProgressBar prg;
    private LinearLayout emptyLayout;
    private TextView problem;

    FirebaseDatabase database;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_chat, container, false);



        prg = view.findViewById(R.id.prg);
        problem = view.findViewById(R.id.problem);

        emptyLayout = view.findViewById(R.id.empty);
        RecyclerView recyclerView = view.findViewById(R.id.rec);
        perMes = new AdapterPersonMessaging(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(perMes);

        database  = FirebaseDatabase.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        reference = database.getReference().child("PROFILES").child(preferences.getString("uid", "")).child("PERSON_MESSAGE");

        messageList = new ArrayList<>();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prg.setVisibility(View.GONE);

                messageList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PersonMessage info = snapshot.getValue(PersonMessage.class);
                    if (info == null) return;
                    messageList.add(new PersonMessage(info.getId(), info.getName(), info.getPhoto(), info.getMsg()));
                }

                if (!(messageList.size() > 0)){
                    emptyLayout.setVisibility(View.VISIBLE);
                    problem.setText("No message here ! \n click to talk with sommone");
                    problem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getContext(), Search.class);
                            i.putExtra("type", 0);
                            startActivity(i);
                        }
                    });
                }

                perMes.swapAdapter(messageList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        vide();


        return view;
    }



    public void vide(){
        if (isNetworkOnline()){
            emptyLayout.setVisibility(View.GONE);
            reference.addValueEventListener(valueEventListener);
        }else {
            emptyLayout.setVisibility(View.VISIBLE);
            problem.setText("No internet connection :|");
            problem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vide();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageList.clear();
        reference.removeEventListener(valueEventListener);
    }



    public interface clickItem{
        void clickpp(String chN);
    }

    private clickItem click;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            click = (clickItem) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void clickAllItem(String chiledName) {
        click.clickpp(chiledName);
    }


    private boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

}
