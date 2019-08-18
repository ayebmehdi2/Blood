package com.kareemAndMahmoud.bloodapp.MESSAGING.GROUP_MESSAGING;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kareemAndMahmoud.bloodapp.R;

import java.util.ArrayList;

public class AdapterGroupMessaging  extends RecyclerView.Adapter<AdapterGroupMessaging.holder> {

    private Context context;
    private ArrayList<GroupMessage> dataMessage = null;
    private int type;

    public AdapterGroupMessaging(clickMessagingGroup click, int i) {
        this.click = click;

        type = i;
    }

    public void swapAdapter(ArrayList<GroupMessage> data){
        if (dataMessage == data) return;
        this.dataMessage = data;
        if (data != null){
            this.notifyDataSetChanged();
        }
    }

    public interface clickMessagingGroup{
        void clickAllIte(String chiledName);
        void clickRequestGrp(String s);
    }

    private final clickMessagingGroup click;

    class holder extends RecyclerView.ViewHolder{

        ImageView userI;
        TextView name;
        TextView lastMsg;
        public holder(@NonNull View itemView) {
            super(itemView);
            userI = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.username);
            lastMsg = itemView.findViewById(R.id.lastmsg);

            if (type == 1){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.clickAllIte(dataMessage.get(getAdapterPosition()).getId());
                    }
                });
            }else if (type == 2){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.clickRequestGrp(dataMessage.get(getAdapterPosition()).getId());
                    }
                });
            }

        }
    }



    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        if (type == 1){
            return new holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item, viewGroup, false));
        }else if (type == 2){
            return new holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grp_req, viewGroup, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int i) {
        GroupMessage userInfo = dataMessage.get(i);

        if (userInfo.getPhoto() != null){
            Glide.with(context).load(userInfo.getPhoto()).into(holder.userI);

        }

        if (userInfo.getNamegroup()!=null){
            if (type == 1){
                holder.name.setText(userInfo.getNamegroup());
            }else {
                String  s1 = userInfo.getNamegroup().substring(0, (userInfo.getNamegroup().length() / 2)) + "...";
                holder.name.setText(s1);
            }
        }

        if (type == 1){
            if (userInfo.getLastmsg() != null){
                holder.lastMsg.setText(userInfo.getLastmsg());
            }
        }else if (type == 2){
            if (userInfo.getUsers() != null){
                String[] list =userInfo.getUsers().split(",");
                String s = list.length + " User";
                holder.lastMsg.setText(s);
            }
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
