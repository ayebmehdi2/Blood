package com.kareemAndMahmoud.bloodapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ShowImage extends AppCompatActivity {

    private String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image);


         url = getIntent().getStringExtra("url");
         /*
         uid = getIntent().getStringExtra("uid");
         type = getIntent().getStringExtra("type");
        */

        if (url == null) return;

        ImageView view = findViewById(R.id.img_show);

        Glide.with(this).load(url).into(view);

        findViewById(R.id.back_show_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
