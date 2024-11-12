package com.ersinkoc.hopol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TableLayout;

import com.ersinkoc.hopol.adapter.ViewPagerAdapter;
import com.ersinkoc.hopol.fragment.search;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;

public class MainActivity extends AppCompatActivity implements search.OnDataPass {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    ViewPagerAdapter pagerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();

        addTabs();
    }

    private void init(){

        viewPager = findViewById(R.id.viewPager1);
        tabLayout = findViewById(R.id.tableLayout1);

    }
    private void addTabs(){

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.baseline_home_24_white));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.baseline_search_24_white));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.baseline_add_24_white));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.baseline_play_circle_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.baseline_person_24_white));



        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        pagerAdapter= new ViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_home_24_white);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){


                    case 0 :
                        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_home_24);
                        break;
                    case 1 :
                        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_search_24);
                        break;
                    case 2 :
                        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_add_24);
                        break;
                    case 3 :
                        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_play_circle_24_1);
                        break;
                    case 4 :
                        tabLayout.getTabAt(4).setIcon(R.drawable.baseline_person_24);
                        break;


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){


                    case 0 :
                        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_home_24_white);
                        break;
                    case 1 :
                        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_search_24_white);
                        break;
                    case 2 :
                        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_add_24_white);
                        break;
                    case 3 :
                        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_play_circle_24);
                        break;
                    case 4 :
                        tabLayout.getTabAt(4).setIcon(R.drawable.baseline_person_24_white);
                        break;


                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){


                    case 0 :
                        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_home_24);
                        break;
                    case 1 :
                        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_search_24);
                        break;
                    case 2 :
                        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_add_24);
                        break;
                    case 3 :
                        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_play_circle_24_1);
                        break;
                    case 4 :
                        tabLayout.getTabAt(4).setIcon(R.drawable.baseline_person_24);
                        break;

                }

            }
        });

    }


    private Bitmap loadProfileImage(String directory) {

        try {
            File file = new File(directory, "profile.png");

            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }
    public static String USER_ID;
    public static boolean IS_SEARCHED_USER = false;

    @Override
    public void onChange(String uid) {
        USER_ID = uid;
        IS_SEARCHED_USER =true;
        viewPager.setCurrentItem(4);
    }




    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem()==4) {
            viewPager.setCurrentItem(0);
            IS_SEARCHED_USER=false;

        }
        else
            super.onBackPressed();
    }
}