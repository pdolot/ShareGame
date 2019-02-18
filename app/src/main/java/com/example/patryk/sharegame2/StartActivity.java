package com.example.patryk.sharegame2;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.patryk.sharegame2.MainFragments.MainActivity;
import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.User;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    private static final int REQUEST_USER_LOCATION_CODE = 99;
    public static User user = new User();
    private String urlGetAll = StartActivity.user.getUrl() + "/getAllObjects";

    DatabaseHelper databaseHelper;

    Dialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        databaseHelper = new DatabaseHelper(this);
        loadDialog = new Dialog(this);

        loadDialog.setContentView(R.layout.load_dialog);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadDialog.show();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            while(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                checkUserLocationPermission();
            }
            getAllSportFacilities();

        }
    }

    public boolean checkUserLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_USER_LOCATION_CODE);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_USER_LOCATION_CODE);
            }
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_USER_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED){
                       // Toast.makeText(this,"Permission allowed..",Toast.LENGTH_SHORT).show();
                    }
                }else{
                   // Toast.makeText(this,"Permission denied..",Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private void getAllSportFacilities() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlGetAll,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                databaseHelper.removeAll();
                try{
                    for(int i=0;i<response.length();i++){
                        JSONObject sportFacilities = response.getJSONObject(i);


                        String name = sportFacilities.getString("name");

                        String city = sportFacilities.getString("city");

                        String street = sportFacilities.getString("street");

                        String local_no = sportFacilities.getString("localno");

                        String zip_code = sportFacilities.getString("zipcode");

                        String zip_code_city = sportFacilities.getString("zipcodecity");

                        String email = sportFacilities.getString("email");

                        String pp_email = sportFacilities.getString("ppmail");

                        String phone_no = sportFacilities.getString("phoneno");

                        String www = sportFacilities.getString("siteaddress");

                        String start_hour = sportFacilities.getString("open");

                        String end_hour = sportFacilities.getString("close");

                        int rental_price = Integer.valueOf(sportFacilities.getString("rentprice"));

                        double lati = Double.valueOf(sportFacilities.getString("latitude"));

                        double longi = Double.valueOf(sportFacilities.getString("longitude"));

                        int owner_id = Integer.valueOf(sportFacilities.getString("ownid"));

                        int soccer = boolToInt(sportFacilities.getString("soccer"));

                        int futsal = boolToInt(sportFacilities.getString("futsal"));

                        int volleyball = boolToInt(sportFacilities.getString("volleyball"));

                        int tennis = boolToInt(sportFacilities.getString("tennis"));

                        int basketball = boolToInt(sportFacilities.getString("basketball"));

                        int handball = boolToInt(sportFacilities.getString("handball"));

                        int squash = boolToInt(sportFacilities.getString("squash"));

                        int badminton = boolToInt(sportFacilities.getString("badminton"));

                        //
                        JSONObject extras = sportFacilities.getJSONObject("objectExtras");

                        int parking = boolToInt(extras.getString("parking"));

                        int bath = boolToInt(extras.getString("bathroom"));

                        int locker_room = boolToInt(extras.getString("artificiallighting"));

                        int lighting = boolToInt(extras.getString("lockerroom"));

                        int magazine = boolToInt(extras.getString("equipment"));

                        //
                        JSONObject openHours = sportFacilities.getJSONObject("openHours");

                        String monday = openHours.getString("mondayHours");

                        String tuesday = openHours.getString("tusedayHours");

                        String wednesday = openHours.getString("wensdayHours");

                        String thursday = openHours.getString("thrusdayHours");

                        String friday = openHours.getString("fridayHours");

                        String saturday = openHours.getString("saturdayHours");

                        String sunday = openHours.getString("sundayHours");

                        int isHolidays = boolToInt(openHours.getString("openInBankHolidays"));

                        int isWeekends;

                        int globalID = Integer.valueOf(sportFacilities.getString("globalID"));

                        if(saturday.equals("00:00-00:00") && sunday.equals("00:00-00:00")){
                            isWeekends = 0;
                        }else{
                            isWeekends = 1;
                        }

                        List<FacilityImage> images = new ArrayList<>();

                        JSONArray photos = sportFacilities.getJSONArray("photos");
                        for(int j = 0 ; j < photos.length();j++ ){
                            JSONObject photo = photos.getJSONObject(j);
                            String url = photo.getString("photo");
                            FacilityImage fi = new FacilityImage();
                            fi.setByteArray(url);
                            images.add(fi);
                            databaseHelper.addImage(globalID,images.get(j).getByteArray());
                        }

                        databaseHelper.addData(name, city, street, local_no,
                                zip_code, zip_code_city, email, pp_email,
                                phone_no, www,
                                start_hour, end_hour, rental_price, lati,
                                longi, owner_id,
                                soccer, futsal,
                                volleyball, tennis,
                                basketball, handball,
                                squash, badminton,
                                parking, bath,
                                locker_room, lighting,
                                magazine, globalID,
                                monday, tuesday,
                                wednesday, thursday,
                                friday, saturday,
                                sunday, isHolidays,
                                isWeekends);


                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                loadDialog.dismiss();

                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                intent.putExtra("PageNo", 1);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadDialog.dismiss();
                Toast.makeText(StartActivity.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                intent.putExtra("PageNo", 1);
                startActivity(intent);
                finish();
            }
        }
        );
        RequestQueue requestQueue =Volley.newRequestQueue(StartActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    private int boolToInt(String value){
        if(value.equals("true")){
            return 1;
        }else{
            return 0;
        }
    }


}
