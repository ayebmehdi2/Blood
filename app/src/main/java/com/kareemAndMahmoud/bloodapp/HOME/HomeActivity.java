package com.kareemAndMahmoud.bloodapp.HOME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.FirebaseDatabase;
import com.kareemAndMahmoud.bloodapp.MESSAGING.CHATROOM.FragMessaging;
import com.kareemAndMahmoud.bloodapp.MESSAGING.CHATROOM.RoomChatActivity;
import com.kareemAndMahmoud.bloodapp.MESSAGING.GROUP_MESSAGING.FragGroupMsging;
import com.kareemAndMahmoud.bloodapp.MESSAGING.GROUP_MESSAGING.GroupMessaging;
import com.kareemAndMahmoud.bloodapp.PROFILE.ProfileActivity;
import com.kareemAndMahmoud.bloodapp.PROFILE.Search;
import com.kareemAndMahmoud.bloodapp.R;


public class HomeActivity extends
        AppCompatActivity implements FragMessaging.clickItem, FragGroupMsging.clickItem {

    private View v1;
    private View v4;
    private static final int NUM_PAGES = 2;
    private ViewPager mPager;


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(id).child("status").setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(id).child("status").setValue("offline");
    }

    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadre);

        //if (savedInstanceState != null) return;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        id = preferences.getString("uid", null);

        v1 = findViewById(R.id.cha);
        v4 = findViewById(R.id.cha_gr);
        ImageView pro = findViewById(R.id.profile);

        mPager = findViewById(R.id.home_pager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),
                new Fragment[] {new FragMessaging(), new FragGroupMsging()});
        mPager.setAdapter(pagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                if (position == 0){

                    v1.setVisibility(View.VISIBLE);
                    v4.setVisibility(View.INVISIBLE);
                }else if (position == 1){
                    v1.setVisibility(View.INVISIBLE);
                    v4.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                i.putExtra("uid", "me");
                startActivity(i);


            }
        });

        ImageView image = findViewById(R.id.main_search);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, Search.class);
                i.putExtra("type", 0);
                startActivity(i);
            }
        });

        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v1.setVisibility(View.VISIBLE);
                v4.setVisibility(View.INVISIBLE);
                mPager.setCurrentItem(0);
            }
        });

        findViewById(R.id.chat_gr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v1.setVisibility(View.INVISIBLE);
                v4.setVisibility(View.VISIBLE);
                mPager.setCurrentItem(1);
            }
        });

    }

    @Override
    public void clickpp(String chN) {
        Intent i = new Intent(this, RoomChatActivity.class);
        i.putExtra("uid", chN);
        startActivity(i);
    }


    @Override
    public void clickp(String chN) {
        Intent i = new Intent(this, GroupMessaging.class);
        i.putExtra("t", "simple");
        i.putExtra("uid", chN);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Fragment[] fragments;
        public ScreenSlidePagerAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


}