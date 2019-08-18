package com.kareemAndMahmoud.bloodapp.HOME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.kareemAndMahmoud.bloodapp.AUTH.LoginOrSignupActivity;
import com.kareemAndMahmoud.bloodapp.R;


public class MainActivity extends AppCompatActivity implements FragmentStarts.click {

    private static final int NUM_PAGES = 3;
    private ViewPager mPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is = preferences.getBoolean("first", true);

        if (is) {
            mPager = findViewById(R.id.pager);
            mPager.setVisibility(View.VISIBLE);
            PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(pagerAdapter);
            SharedPreferences.Editor preference = PreferenceManager.getDefaultSharedPreferences(this).edit();
            preference.putBoolean("first", false);
            preference.apply();
        }else {
            startActivity(new Intent(this, LoginOrSignupActivity.class));
        }



    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void next() {
        mPager.setVisibility(View.GONE);
        SharedPreferences.Editor preference = PreferenceManager.getDefaultSharedPreferences(this).edit();
        preference.putBoolean("first", false);
        preference.apply();
         startActivity(new Intent(this, LoginOrSignupActivity.class));
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new FragmentStarts(position + 1);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
