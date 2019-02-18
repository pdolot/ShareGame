package com.example.patryk.sharegame2;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.patryk.sharegame2.Adapters.ImageSlider;
import com.example.patryk.sharegame2.MainFragments.MainActivity;
import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacilityView extends AppCompatActivity {

    private SportFacility sportFacility;

    // content
    private TextView facilityName;
    private TextView facilityAddress;
    private TextView facilityOpenHours;
    private TextView facilityOpenDays;
    private TextView facilityRentalPrice;
    private TextView facilityEquipment;
    private TextView facilityPhoneNo;
    private TextView facilityEmail;
    private TextView facilityWebsite;
    private TextView facilityRatingCount;
    private RatingBar facilityRatingBar;
    private TextView facilitySports;

    // top menu

    private Button phone;
    private Button mess;
    private Button reservation;
    private Button map;
    private Button www;

    // imagePager

    private ViewPager imagePager;
    private int imageCount;
    private ImageSlider imageSlider;
    private TextView[] imageDots;
    private LinearLayout dotsLayout;

    private TextView date;
    private Button prev_date;
    private Button next_date;
    private LinearLayout schedule;
    private LayoutInflater hourInflanter;
    private TextView day_of_week;

    public String[] daysName = {"ND", "PN", "WT", "ŚR", "CZ", "PT", "SB"};

    private ExpandableRelativeLayout expandableLayout;
    private TextView expend_schedule;
    private TextView dayStatus;

    private int hoursCount;
    private double startHour;
    private double endHour;
    private String[] hours;
    private String facilitySchedule = "00000000";
    private String startHourS = "00:00";
    private String endHourS = "00:00";
    private String currentDate;
    private String selectedDate;

    private LinearLayout makeReservation;

    // Map
    private GoogleMap mMap;
    private Dialog mapDialog;
    private String url = StartActivity.user.getUrl() + "/returnTimetableOfSportObject";

    private DatabaseHelper databaseHelper;

    List<FacilityImage> imageList;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_view);

        databaseHelper = new DatabaseHelper(this);
        scrollView = findViewById(R.id.scroll);


        day_of_week = findViewById(R.id.fv_day_of_week);
        final Calendar calendar = Calendar.getInstance();

        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(Calendar.getInstance().getTime());

        sportFacility = new SportFacility();

        final Intent intent = getIntent();
        sportFacility = intent.getParcelableExtra("Facility");

        setHours(calendar.get(Calendar.DAY_OF_WEEK));

        mapDialog = new Dialog(this);

        // content

        facilityName = findViewById(R.id.facility_name);
        facilityName.setText(sportFacility.getName());

        facilityAddress = findViewById(R.id.facility_address);
        facilityAddress.setText(sportFacility.getCity() + ", ul. " +sportFacility.getStreet() + " "
                + sportFacility.getLocal_no() + ", " + sportFacility.getZip_code() + " " + sportFacility.getZip_code_city());

        String openDay = "Poniedziałek\nWtorek\nŚroda\nCzwartek\nPiątek\nSobota\nNiedziela";
        String openHours = "";

        if(!sportFacility.getOh_monday().equals("00:00-00:00")){
            openHours += sportFacility.getOh_monday() + "\n";
        }else{
            openHours += "Nieczynne" + "\n";
        }

        if(!sportFacility.getOh_tuesday().equals("00:00-00:00")){
            openHours += sportFacility.getOh_tuesday() + "\n";
        }else{
            openHours += "Nieczynne" + "\n";
        }

        if(!sportFacility.getOh_wednesday().equals("00:00-00:00")){
            openHours += sportFacility.getOh_wednesday() + "\n";
        }else{
            openHours += "Nieczynne" + "\n";
        }

        if(!sportFacility.getOh_thursday().equals("00:00-00:00")){
            openHours += sportFacility.getOh_thursday() + "\n";
        }else{
            openHours += "Nieczynne" + "\n";
        }

        if(!sportFacility.getOh_friday().equals("00:00-00:00")){
            openHours += sportFacility.getOh_friday() + "\n";
        }else{
            openHours += "Nieczynne" + "\n";
        }

        if(!sportFacility.getOh_saturday().equals("00:00-00:00")){
            openHours += sportFacility.getOh_saturday() + "\n";
        }else{
            openHours += "Nieczynne" + "\n";
        }

        if(!sportFacility.getOh_sunday().equals("00:00-00:00")){
            openHours += sportFacility.getOh_sunday();
        }else{
            openHours += "Nieczynne";
        }

        facilityOpenDays = findViewById(R.id.facility_open_hours);
        facilityOpenHours = findViewById(R.id.facility_open_hours_hours);
        facilityOpenDays.setText(openDay);
        facilityOpenHours.setText(openHours);

        facilityRentalPrice = findViewById(R.id.facility_rental_price);
        facilityRentalPrice.setText(String.valueOf(sportFacility.getRental_price()) + " zł");

        facilitySports = findViewById(R.id.facility_sports);
        facilitySports.setText(sportFacility.getSportList().substring(0,sportFacility.getSportList().length()-2));

        facilityEquipment = findViewById(R.id.facility_equipment);

        if(sportFacility.getExtrasList().length() > 2){
            facilityEquipment.setText(sportFacility.getExtrasList().substring(0,sportFacility.getExtrasList().length()-2));
        }else{
            facilityEquipment.setText("-");
        }

        facilityPhoneNo = findViewById(R.id.facility_phone);
        facilityPhoneNo.setText(sportFacility.getPhone_no());
        //facilityPhoneNo.setText(sportFacility.getPhone_no().replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1 $2 $3"));

        facilityEmail = findViewById(R.id.facility_email);
        facilityEmail.setText(sportFacility.getEmail());

        facilityWebsite = findViewById(R.id.facility_www);
        facilityWebsite.setText(sportFacility.getWww());

        facilityRatingCount = findViewById(R.id.fv__ratingsCount);
        facilityRatingCount.setText(String.valueOf("( 0 )"));

        facilityRatingBar = findViewById(R.id.fv_ratingBar);
        facilityRatingBar.setRating(0);

        // top menu

        phone = findViewById(R.id.call);
        mess = findViewById(R.id.writemess);
        reservation = findViewById(R.id.makereservation);
        map = findViewById(R.id.show_in_map);
        www = findViewById(R.id.website);

        makeReservation = findViewById(R.id.b_makeReservation);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callintent = new Intent(Intent.ACTION_DIAL);
                callintent.setData(Uri.parse("tel:" + sportFacility.getPhone_no()));
                startActivity(callintent);
            }
        });

        mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Wysyłam wiadomość na " + sportFacility.getEmail(), Toast.LENGTH_SHORT).show();
            }
        });

        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Rezerwuje", Toast.LENGTH_SHORT).show();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapDialog.setContentView(R.layout.map_dialog_only_show);
                TextView close = mapDialog.findViewById(R.id.os_map_close);

                MapView mapView = mapDialog.findViewById(R.id.os_mapInDialog);
                MapsInitializer.initialize(getApplicationContext());

                mapView.onCreate(mapDialog.onSaveInstanceState());
                mapView.onResume();

                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        mMap = googleMap;
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        mMap.getUiSettings().setCompassEnabled(false);
                        mMap.getUiSettings().setMapToolbarEnabled(false);

                        LatLng latLng = new LatLng(sportFacility.getLatitude(), sportFacility.getLongitude());

                        MarkerOptions mOptions = new MarkerOptions();
                        mOptions.position(latLng);
                        mMap.addMarker(mOptions);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapDialog.dismiss();
                    }
                });

                mapDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // usuwanie przyciemnienia tła (nachodziło na mape)
                mapDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mapDialog.show();
            }
        });

        www.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wwwintent = new Intent(Intent.ACTION_VIEW);
                wwwintent.setData(Uri.parse("https://" + sportFacility.getWww()));
                startActivity(wwwintent);
            }
        });


        expandableLayout = findViewById(R.id.schedule);

        expandableLayout.collapse();

        expend_schedule = findViewById(R.id.expand_schedule);
        dayStatus = findViewById(R.id.dayStatus);

        imagePager = findViewById(R.id.facilityImagePager);
        dotsLayout = findViewById(R.id.dots_layout);

        imageList = new ArrayList<>();
        imageList = databaseHelper.getImages(sportFacility.getGlobal_id());

        if(imageList.size() == 0){
            FacilityImage fi = new FacilityImage();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.id1img1);
            fi.setBitmap(bitmap);
            fi.setAdded(true);
            imageList.add(fi);
        }
        imageCount = imageList.size();
        imageSlider = new ImageSlider(this);
        imageSlider.setImageCount(imageCount);
        imageSlider.setImages(imageList);
        imagePager.setAdapter(imageSlider);


        date = findViewById(R.id.day);
        prev_date = findViewById(R.id.prev_date);
        next_date = findViewById(R.id.next_date);

        date.setText(currentDate);
        selectedDate = currentDate;

        next_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_YEAR,1);
                String nextDate = df.format(calendar.getTime());
                setHours(calendar.get(Calendar.DAY_OF_WEEK));
                getSchedule(nextDate);
                selectedDate = nextDate;
                date.setText(nextDate);
            }
        });

        prev_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_YEAR,-1);
                String previousDate = df.format(calendar.getTime());
                setHours(calendar.get(Calendar.DAY_OF_WEEK));
                getSchedule(previousDate);
                selectedDate = previousDate;
                date.setText(previousDate);
            }
        });

        makeReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reservation = new Intent(FacilityView.this, MakeReservation.class);
                reservation.putExtra("ID",sportFacility.getGlobal_id());
                reservation.putExtra("DATE",selectedDate);
                startActivity(reservation);
            }
        });

        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reservation = new Intent(FacilityView.this, MakeReservation.class);
                reservation.putExtra("ID",sportFacility.getGlobal_id());
                reservation.putExtra("DATE",currentDate);
                startActivity(reservation);
            }
        });

        expend_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expandableLayout.toggle();
            }
        });

        schedule = findViewById(R.id.hours);

        setSchedule(false);
        //expandableLayout.initLayout();

        getSchedule(currentDate);

        addDotsIndicator(0);

        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                addDotsIndicator(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        scrollView.smoothScrollTo(0,0);
    }

    private void setHours(int dayOfWeek) {

        if (Calendar.MONDAY == dayOfWeek) {
            startHourS = sportFacility.getOh_monday().substring(0,5);
            endHourS = sportFacility.getOh_monday().substring(6,11);
            day_of_week.setText("Poniedziałek");
        } else if (Calendar.TUESDAY == dayOfWeek) {
            startHourS = sportFacility.getOh_tuesday().substring(0,5);
            endHourS = sportFacility.getOh_tuesday().substring(6,11);
            day_of_week.setText("Wtorek");
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            startHourS = sportFacility.getOh_wednesday().substring(0,5);
            endHourS = sportFacility.getOh_wednesday().substring(6,11);
            day_of_week.setText("Środa");
        } else if (Calendar.THURSDAY == dayOfWeek) {
            startHourS = sportFacility.getOh_thursday().substring(0,5);
            endHourS = sportFacility.getOh_thursday().substring(6,11);
            day_of_week.setText("Czwartek");
        } else if (Calendar.FRIDAY == dayOfWeek) {
            startHourS = sportFacility.getOh_friday().substring(0,5);
            endHourS = sportFacility.getOh_friday().substring(6,11);
            day_of_week.setText("Piątek");
        } else if (Calendar.SATURDAY == dayOfWeek) {
            startHourS = sportFacility.getOh_saturday().substring(0,5);
            endHourS = sportFacility.getOh_saturday().substring(6,11);
            day_of_week.setText("Sobota");
        } else if (Calendar.SUNDAY == dayOfWeek) {
            startHourS = sportFacility.getOh_sunday().substring(0,5);
            endHourS = sportFacility.getOh_sunday().substring(6,11);
            day_of_week.setText("Niedziela");
        }
    }

    public void setSchedule(boolean condition){
        if(condition){
            startHour = hourToDouble(startHourS);
            endHour = hourToDouble(endHourS);
        }else{
            startHour = hourToDouble("00:00");
            endHour = hourToDouble("01:00");
        }

        hoursCount = (int) ((endHour - startHour) * 2) + 1;
        hours = getHoursTab(hoursCount, startHour);

        hourInflanter = LayoutInflater.from(this);
        schedule.removeAllViews();


        if(endHour - startHour == 0){
            dayStatus.setText("Obiekt w ten dzień jest nieczynny");
            schedule.setVisibility(View.GONE);
            makeReservation.setClickable(false);
            makeReservation.setBackgroundResource(R.drawable.item_outline_dark);
        }else{
            schedule.setVisibility(View.VISIBLE);
            dayStatus.setText("");
            makeReservation.setClickable(true);
            makeReservation.setBackgroundResource(R.drawable.item_outline);
        }
        final String[] hoursTab = getHoursTab(hoursCount,startHour);

        for(int i=0;i<hoursCount;i++)
        {
            View v;
            if( i == 0){
                v = hourInflanter.inflate(R.layout.hour_layout, schedule, false);
            }else{
                v = hourInflanter.inflate(R.layout.hour_layout2, schedule, false);
            }

            TextView hour = v.findViewById(R.id.hour);
            TextView h_1 = v.findViewById(R.id.h_1);

            if(i%2==0){
                hour.setText(hoursTab[i]);
            }else{
                hour.setText("");
            }

            if(i != hoursCount - 1){
                if(facilitySchedule.charAt(i) == '1'){
                    h_1.setBackgroundColor(getResources().getColor(R.color.turquiose));
                }else if(facilitySchedule.charAt(i) == '4'){
                    h_1.setBackgroundColor(getResources().getColor(R.color.turquioselv2));
                }

                final int pos = i;

                h_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(FacilityView.this, hoursTab[pos] + " - " + hoursTab[pos+1] , Toast.LENGTH_SHORT).show();
                    }
                });
            }

            schedule.addView(v);
        }
    }

    public String[] getHoursTab(int hoursCount, double start) {
        String[] hs = new String[hoursCount];

        for (int i = 0; i < hoursCount; i++) {

            double hour = start + (0.5 * i);

            int hr = (int) hour;
            int min;

            if (hour % 2 != 0 && hour % 2 != 1) {
                min = 30;
            } else {
                min = 0;
            }

            String str = String.format("%02d:%02d", hr, min);

            hs[i] = str;
        }

        return hs;
    }

    public double hourToDouble(String hour) {
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


    public ArrayList<Integer> setFacilityImages(int facilityId) {

        ArrayList<Integer> images = new ArrayList<>();

        for (int i = 0; i < imageCount; i++) {
            int id = getResources().getIdentifier("id" + String.valueOf(facilityId) + "img" + String.valueOf(i + 1), "drawable", this.getPackageName());
            images.add(id);
        }

        return images;
    }

    public void addDotsIndicator(int position) {

        imageDots = new TextView[imageCount];
        dotsLayout.removeAllViews();

        for (int i = 0; i < imageCount; i++) {

            imageDots[i] = new TextView(this);
            imageDots[i].setText(Html.fromHtml("&#8226"));
            imageDots[i].setTextColor(getResources().getColor(R.color.transparentWhite));
            imageDots[i].setTextSize(35);
            dotsLayout.addView(imageDots[i]);

        }

        if (imageDots.length > 0) {
            imageDots[position].setTextSize(40);
            imageDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void getSchedule(String day) {

        JSONObject schedule = new JSONObject();
        try {
            schedule.put("sportobjectid", sportFacility.getGlobal_id());
            schedule.put("start",day + " 00:00:00.0");
            schedule.put("end",day + " 23:59:00.0");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //System.out.println(schedule.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, schedule, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    facilitySchedule = response.getString("schedule");

                    setSchedule(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FacilityView.this, "Nie można pobrać terminarza", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue requestQueue =Volley.newRequestQueue(FacilityView.this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("PageNo", 1);
        startActivity(intent);
        finish();
    }
}
