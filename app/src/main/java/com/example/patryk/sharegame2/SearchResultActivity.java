package com.example.patryk.sharegame2;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.patryk.sharegame2.Adapters.FacilityRVAdapter;
import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity {

    private String city;
    private String[] categories;
    private String date;
    private String start;
    private String end;
    private int amount;
    private List<SportFacility> allFacilities;
    private int day;

    private FacilityRVAdapter adapter;
    private List<SportFacility> facilities;
    private RecyclerView rv_facilities;

    private SwipeRefreshLayout refreshLayout;
    private DatabaseHelper databaseHelper;

    private String urlGetAll = StartActivity.user.getUrl() + "/getAllObjects";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        categories = intent.getStringArrayExtra("categories");
        date = intent.getStringExtra("date");
        start = intent.getStringExtra("start");
        end = intent.getStringExtra("end");
        amount = intent.getIntExtra("amount",0);

        Calendar calendar = getDate(date);
        int day = setDay(calendar.get(Calendar.DAY_OF_WEEK));

        databaseHelper = new DatabaseHelper(this);

        // init facilites
        facilities = new ArrayList<>();
        allFacilities = databaseHelper.getSportFacilities();

        for(SportFacility sf : allFacilities){
            if(!city.equals("")){
                if( sf.getCity().equals(city) && checkIsOpen(sf,day)
                        && sf.getRental_price() <= amount && checkCategories(categories,sf) ){
                    facilities.add(sf);
                }
            }else{
                if(checkIsOpen(sf,day) && sf.getRental_price() <= amount
                        && checkCategories(categories,sf) ){
                    facilities.add(sf);
                }
            }
        }

        rv_facilities = findViewById(R.id.rv_searchresult);
        refreshLayout = findViewById(R.id.swipeRefreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllSportFacilities();
            }
        });


        adapter = new FacilityRVAdapter(this,facilities);
        rv_facilities.setLayoutManager(new LinearLayoutManager(this));
        rv_facilities.setAdapter(adapter);

        adapter.setOnItemClickListener(new FacilityRVAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent facilityView = new Intent(SearchResultActivity.this, FacilityView.class);
                facilityView.putExtra("Facility",facilities.get(position));
                startActivity(facilityView);
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });

    }

    public void refreshSearch(){
        facilities.clear();
        allFacilities.clear();
        allFacilities = databaseHelper.getSportFacilities();

        for(SportFacility sf : allFacilities){
            if(!city.equals("")){
                if( sf.getCity().equals(city) && checkIsOpen(sf,day)
                        && sf.getRental_price() <= amount && checkCategories(categories,sf) ){
                    facilities.add(sf);
                }
            }else{
                if(checkIsOpen(sf,day) && sf.getRental_price() <= amount
                        && checkCategories(categories,sf) ){
                    facilities.add(sf);
                }
            }
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

                refreshSearch();
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(SearchResultActivity.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue requestQueue =Volley.newRequestQueue(SearchResultActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    private int boolToInt(String value){
        if(value.equals("true")){
            return 1;
        }else{
            return 0;
        }
    }

    private boolean checkIsOpen(SportFacility sf, int day){
        double startH = hourToDouble(start);
        double endH = hourToDouble(end);
        double s = 0;
        double e = 0;

        if( day == 0){
            String hours = sf.getOh_monday();
            s = hourToDouble(hours.substring(0,5));
            e = hourToDouble(hours.substring(6,11));
        }else if( day == 1){
            String hours = sf.getOh_tuesday();
             s = hourToDouble(hours.substring(0,5));
             e = hourToDouble(hours.substring(6,11));
        }else if( day == 2){
            String hours = sf.getOh_wednesday();
             s = hourToDouble(hours.substring(0,5));
             e = hourToDouble(hours.substring(6,11));
        }else if( day == 3){
            String hours = sf.getOh_thursday();
             s = hourToDouble(hours.substring(0,5));
             e = hourToDouble(hours.substring(6,11));
        }else if( day == 4){
            String hours = sf.getOh_friday();
             s = hourToDouble(hours.substring(0,5));
             e = hourToDouble(hours.substring(6,11));
        }else if( day == 5){
            String hours = sf.getOh_saturday();
             s = hourToDouble(hours.substring(0,5));
             e = hourToDouble(hours.substring(6,11));
        }else if( day == 6){
            String hours = sf.getOh_sunday();
             s = hourToDouble(hours.substring(0,5));
             e = hourToDouble(hours.substring(6,11));
        }

        if(s == 0 && e == 0){
            return false;
        }
        if(s >= startH && e <=endH){

            return true;
        }else{
            return false;
        }
    }

    private int setDay(int dayOfWeek) {

        if (Calendar.MONDAY == dayOfWeek) {
            return 0;

        } else if (Calendar.TUESDAY == dayOfWeek) {
            return 1;

        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            return 2;

        } else if (Calendar.THURSDAY == dayOfWeek) {
            return 3;

        } else if (Calendar.FRIDAY == dayOfWeek) {
            return 4;

        } else if (Calendar.SATURDAY == dayOfWeek) {
            return 5;

        } else if (Calendar.SUNDAY == dayOfWeek) {
            return 6;
        }
        return 0;
    }

    private Calendar getDate(String date){
        int day = Integer.valueOf(date.substring(0,2));
        int month = Integer.valueOf(date.substring(3,5));
        int year = Integer.valueOf(date.substring(6,10));

        Calendar c = Calendar.getInstance();
        c.set(year,month - 1,day);

        return c;
    }

    private double hourToDouble(String hour) {
        String hours, minutes;
        hours = hour.substring(0, 2);
        minutes = hour.substring(3, 5);

        double dHour;
        double dMinutes;

        dHour = Double.parseDouble(hours);
        if (minutes.equals("00")) {
            dMinutes = 0.0;
        } else {
            dMinutes = 0.5;
        }

        return dHour + dMinutes;
    }

    private boolean checkCategories(String[] categories, SportFacility sf){

        int counter = 0;

        if(categories.length == 0){
            return true;
        }

        for(int i = 0; i < categories.length; i++){
            if(checkFacilityCategories(categories[i],sf)){
                counter++;
            }
        }
        if(counter > 0){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkFacilityCategories(String category, SportFacility sf){
        if(category.equals("Piłka nożna")){
            if(sf.getSoccer()){
                return true;
            }else{
                return false;
            }
        }else if(category.equals("Futsal")){
            if(sf.getFutsal()){
                return true;
            }else{
                return false;
            }
        }else if(category.equals("Siatkówka")){
            if(sf.getVolleyball()){
                return true;
            }else{
                return false;
            }
        }else if(category.equals("Koszykówka")){
            if(sf.getBasketball()){
                return true;
            }else{
                return false;
            }
        }else if(category.equals("Tenis")){
            if(sf.getTennis()){
                return true;
            }else{
                return false;
            }
        }else if(category.equals("Squash")){
            if(sf.getSquash()){
                return true;
            }else{
                return false;
            }

        }else if(category.equals("Piłka ręczna")){
            if(sf.getHandball()){
                return true;
            }else{
                return false;
            }

        }else if(category.equals("Badminton")){
            if(sf.getBadminton()){
                return true;
            }else{
                return false;
            }
        }
        return true;
    }
}
