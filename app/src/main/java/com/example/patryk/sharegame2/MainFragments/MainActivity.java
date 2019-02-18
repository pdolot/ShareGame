package com.example.patryk.sharegame2.MainFragments;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.patryk.sharegame2.Adapters.SectionsPagerAdapter;
import com.example.patryk.sharegame2.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPagerContainer);

        Intent intent = getIntent();
        int position = intent.getIntExtra("PageNo",1);

        setupViewPager(position);
    }

    private void setupViewPager(int position){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new optionsFragment());

        if(isServicesOK()){
            adapter.addFragment(new mapFragment());
        }
        adapter.addFragment(new advancedSearchFragment());

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    private boolean isServicesOK(){
        Log.d(TAG,"isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"We can't make map request",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()!= 1){
            viewPager.setCurrentItem(1);
        }else{
            super.onBackPressed();
        }
    }
}
