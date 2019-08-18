package com.kareemAndMahmoud.bloodapp.MESSAGING;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kareemAndMahmoud.bloodapp.R;

import java.util.ArrayList;

public class AdapMessage extends RecyclerView.Adapter<AdapMessage.holder> {

    private ArrayList<Message> dataMessage = null;


    private Context context;
    public AdapMessage(Context context, ClickMsgs clickMsgs){
        this.context = context;
        this.clickMsgs = clickMsgs;
    }

    public void swapAdapter(ArrayList<Message> data){
        if (dataMessage == data) return;
        this.dataMessage = data;
        if (data != null){
            this.notifyDataSetChanged();
        }
    }

    class holder extends RecyclerView.ViewHolder{
        ImageView userI, userI1;
        ImageView cI, cI1;
        TextView msg, msg1;
        RelativeLayout me;
        RelativeLayout you;
        LinearLayout location, location1;
        public holder(@NonNull View itemView) {
            super(itemView);
            me = itemView.findViewById(R.id.me);
            you = itemView.findViewById(R.id.you);
            userI = itemView.findViewById(R.id.userimg);
            cI = itemView.findViewById(R.id.cimage);
            msg = itemView.findViewById(R.id.msg);
            location = itemView.findViewById(R.id.location);

            userI1 = itemView.findViewById(R.id.userimg1);
            cI1 = itemView.findViewById(R.id.cimage1);
            msg1 = itemView.findViewById(R.id.msg1);
            location1 = itemView.findViewById(R.id.location1);


        }
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.msg_for_me, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int i) {
        Message message = dataMessage.get(i);

        if (message == null) return;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (message.getType().equals(preferences.getString("uid", ""))) {
            holder.me.setVisibility(View.VISIBLE);
            holder.you.setVisibility(View.GONE);


            if (message.getUserImage() != null && message.getUserImage().length() > 0){
                try {
                    Glide.with(context).load(message.getUserImage()).into(holder.userI);

                }catch (Exception e){

                }
            }

            if (message.getConStr() != null && message.getConStr().length() > 0){
                holder.msg.setVisibility(View.VISIBLE);
                holder.msg.setText(message.getConStr());
            }else{
                holder.msg.setVisibility(View.GONE);
            }

            if (message.getConImg() != null && message.getConImg().length() > 0){
                holder.cI.setVisibility(View.VISIBLE);
                try {
                    Glide.with(context).load(message.getConImg()).into(holder.cI);
                }catch (Exception e){
                }

                holder.cI.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickMsgs.clickImage(message.getConImg());
                    }
                });
            }else{
                holder.cI.setVisibility(View.GONE);
            }

            if (message.getLocation() != null && message.getLocation().length() > 0){
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickMsgs.openLocation(message.getLocation());
                    }
                });
            }else{
                holder.location.setVisibility(View.GONE);
            }


        }
        else {

            holder.me.setVisibility(View.GONE);
            holder.you.setVisibility(View.VISIBLE);


            if (message.getUserImage() != null && message.getUserImage().length() > 0){
                Glide.with(context).load(message.getUserImage()).into(holder.userI1);
            }

            if (message.getConStr() != null && message.getConStr().length() > 0){
                holder.msg1.setVisibility(View.VISIBLE);
                holder.msg1.setText(message.getConStr());
            }else{
                holder.msg1.setVisibility(View.GONE);
            }

            if (message.getConImg() != null && message.getConImg().length() > 0){
                holder.cI1.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getConImg()).into(holder.cI1);
                holder.cI1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickMsgs.clickImage(message.getConImg());
                    }
                });
            }else{
                holder.cI1.setVisibility(View.GONE);
            }

            if (message.getLocation() != null && message.getLocation().length() > 0){
                holder.location1.setVisibility(View.VISIBLE);
                holder.location1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickMsgs.openLocation(message.getLocation());
                    }
                });
            }else{
                holder.location1.setVisibility(View.GONE);
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

    public interface ClickMsgs{
        void openLocation(String uri);
        void clickImage(String source);
    }

    private final ClickMsgs clickMsgs;
}