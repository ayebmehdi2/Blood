package com.kareemAndMahmoud.bloodapp.MESSAGING.CHATROOM;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kareemAndMahmoud.bloodapp.R;

import java.util.ArrayList;

public class AdapterPersonMessaging extends RecyclerView.Adapter<AdapterPersonMessaging.holder> {

private ArrayList<PersonMessage> dataMessage = null;

public AdapterPersonMessaging(clickMessagingPerson click) {
        this.click = click;
        }

public void swapAdapter(ArrayList<PersonMessage> data){
        if (dataMessage == data) return;
        this.dataMessage = data;
        if (data != null){
        this.notifyDataSetChanged();
        }
        }

public interface clickMessagingPerson{
    void clickAllItem(String chiledName);
}

    private final clickMessagingPerson click;

class holder extends RecyclerView.ViewHolder{

    ImageView userI;
    TextView name;
    TextView lastMsg;
    ImageView on;
    ImageView off;
    public holder(@NonNull View itemView) {
        super(itemView);
        userI = itemView.findViewById(R.id.img);
        name = itemView.findViewById(R.id.username);
        lastMsg = itemView.findViewById(R.id.lastmsg);
        on = itemView.findViewById(R.id.on);
        off = itemView.findViewById(R.id.off);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.clickAllItem(dataMessage.get(getAdapterPosition()).getId());
            }
        });
    }
}

Context context;
    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int i) {
        PersonMessage userInfo = dataMessage.get(i);

        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(userInfo.getId()).child("status").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue(String.class);
                        if (status == null) return;
                        if (status.equals("online")){
                            holder.on.setVisibility(View.VISIBLE);
                            holder.off.setVisibility(View.GONE);
                        }else if (status.equals("offline")){
                            holder.on.setVisibility(View.GONE);
                            holder.off.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        if (userInfo.getPhoto() != null){
            Glide.with(context).load(userInfo.getPhoto()).into(holder.userI);
        }

        if (userInfo.getName()!=null){
            holder.name.setText(userInfo.getName());
        }

        if (userInfo.getMsg() != null){
            holder.lastMsg.setText(userInfo.getMsg());
        }

    }

    @Override
    public int getItemCount() {
        if (dataMessage == null){
            return 0;
        }
        return dataMessage.size();
    }

}
