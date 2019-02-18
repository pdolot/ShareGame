package com.example.patryk.sharegame2.Options;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.patryk.sharegame2.Adapters.ViewPagerAdapter;
import com.example.patryk.sharegame2.MainFragments.MainActivity;
import com.example.patryk.sharegame2.R;

public class UserSportFacilities extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPagerAdapter pagerAdapter;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sport_facilities);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.userSportFacilitiesPager);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new UserRentalSportFacilitiesFragment(),"Wynajmowane");
        pagerAdapter.addFragment(new UserSportFacilitiesFragment(),"Twoje");

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserSportFacilities.this, MainActivity.class);
        intent.putExtra("PageNo", 0);
        startActivity(intent);
    }
}
