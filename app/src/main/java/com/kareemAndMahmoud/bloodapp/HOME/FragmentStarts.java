package com.kareemAndMahmoud.bloodapp.HOME;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kareemAndMahmoud.bloodapp.R;


public class FragmentStarts extends Fragment {


    private int i = 0;
    public FragmentStarts(int i){
        this.i = i;
    }

    public interface click{
        void next();
    }

    private click c;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        c = (click) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.start_info, container, false);

        ImageView view = rootView.findViewById(R.id.page);
        TextView next = rootView.findViewById(R.id.next);
        if (i == 1){
            view.setImageResource(R.drawable.g1);
            next.setVisibility(View.GONE);
        }else if (i == 2){
            view.setImageResource(R.drawable.g2);
            next.setVisibility(View.GONE);
        }else if (i == 3){
            view.setImageResource(R.drawable.g3);
            next.setVisibility(View.VISIBLE);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    c.next();
                }
            });
        }
        return rootView;
    }
}
