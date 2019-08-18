package com.kareemAndMahmoud.bloodapp.MESSAGING.GROUP_MESSAGING;

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
import android.widget.ImageView;
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
import com.kareemAndMahmoud.bloodapp.HOME.HomeActivity;
import com.kareemAndMahmoud.bloodapp.PROFILE.Search;
import com.kareemAndMahmoud.bloodapp.R;


import java.util.ArrayList;

public class FragGroupMsging extends Fragment implements  AdapterGroupMessaging.clickMessagingGroup {

    private AdapterGroupMessaging perMes;
    private AdapterGroupMessaging reqGrp;
    private DatabaseReference reference;
    private DatabaseReference referenceRequst;
    private ArrayList<GroupMessage> listReqGrp;
    private ValueEventListener valueEventListener;
    private ValueEventListener reqLisitner;
    private ArrayList<GroupMessage> messageList;
    private ProgressBar prg;
    private LinearLayout emptyLayout;
    private TextView problem;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.layout_chat_grp, container, false);

        LinearLayout reqLayout = view.findViewById(R.id.rrr);
        ImageView top=view.findViewById(R.id.toppp);
        ImageView buttom=view.findViewById(R.id.buttommm);
        TextView t = view.findViewById(R.id.infoshow);

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqLayout.setVisibility(View.GONE);
                top.setVisibility(View.GONE);
                buttom.setVisibility(View.VISIBLE);
                t.setText("show request group");
            }
        });

        buttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqLayout.setVisibility(View.VISIBLE);
                top.setVisibility(View.VISIBLE);
                buttom.setVisibility(View.GONE);
                t.setText("hide request group");
            }
        });

        prg = view.findViewById(R.id.prg);

        problem = view.findViewById(R.id.problem);

        emptyLayout = view.findViewById(R.id.empty);
        RecyclerView recyclerView = view.findViewById(R.id.rec);
        perMes = new AdapterGroupMessaging(this, 1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(perMes);

        RecyclerView rec_req_grp =view.findViewById(R.id.rec_grp_req);
        reqGrp = new AdapterGroupMessaging(this, 2);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        layoutManager1.setOrientation(RecyclerView.HORIZONTAL);
        rec_req_grp.setLayoutManager(layoutManager1);
        rec_req_grp.setHasFixedSize(true);
        rec_req_grp.setAdapter(reqGrp);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        reference = database.getReference().child("PROFILES").child(preferences.getString("uid", "")).child("GROUP_MESSAGE");
        messageList = new ArrayList<>();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupMessage info = snapshot.getValue(GroupMessage.class);
                    if (info == null) return;
                    messageList.add(new GroupMessage(info.getId(), info.getUsers(), info.getNamegroup(), info.getLastmsg(), info.getPhoto()));
                }

                prg.setVisibility(View.GONE);

                if (!(messageList.size() > 0)){
                    emptyLayout.setVisibility(View.VISIBLE);
                    problem.setText("No group here ! \n connect with sommone and create a group");
                    problem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getContext(), HomeActivity.class));
                        }
                    });
                }

                perMes.swapAdapter(messageList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        view.findViewById(R.id.create_grp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Search.class);
                i.putExtra("type", 1);
                i.putExtra("create_group", true);
                startActivity(i);
            }
        });


        referenceRequst = database.getReference().child("PROFILES").child(preferences.getString("uid", "")).child("GROUP_REQUEST");
        listReqGrp = new ArrayList<>();
        reqLisitner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listReqGrp.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupMessage info = snapshot.getValue(GroupMessage.class);
                    if (info == null) return;
                    listReqGrp.add(new GroupMessage(info.getId(), info.getUsers(), info.getNamegroup(), info.getLastmsg(), info.getPhoto()));
                }

                prg.setVisibility(View.GONE);
                reqGrp.swapAdapter(listReqGrp);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        vide();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageList.clear();
        listReqGrp.clear();
        reference.removeEventListener(valueEventListener);
        referenceRequst.removeEventListener(reqLisitner);
    }

    private void vide(){
        if (isNetworkOnline()){
            emptyLayout.setVisibility(View.GONE);
            reference.addValueEventListener(valueEventListener);
            referenceRequst.addValueEventListener(reqLisitner);
        }else {
            emptyLayout.setVisibility(View.VISIBLE);
            problem.setText("No internet connection \n click to try again");
            problem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vide();
                }
            });
        }
    }


    public boolean isNetworkOnline() {
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


    public interface clickItem{ void clickp(String chN);}

    private clickItem click;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try { click = (clickItem) context;
        }catch (ClassCastException e){
        }
    }

    @Override
    public void clickAllIte(String chiledName) {
        click.clickp(chiledName);
    }

    @Override
    public void clickRequestGrp(String s) {
        Intent i = new Intent(getContext(), GroupRequst.class);
        i.putExtra("groupId", s);
        startActivity(i);
    }


}
