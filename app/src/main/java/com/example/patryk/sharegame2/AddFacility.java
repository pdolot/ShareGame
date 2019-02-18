package com.example.patryk.sharegame2;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.patryk.sharegame2.Adapters.CheckableRecyclerViewAdapter;
import com.example.patryk.sharegame2.Adapters.CustomTimePickerDialog;
import com.example.patryk.sharegame2.Adapters.ImageRVAdapter;
import com.example.patryk.sharegame2.Adapters.MaskWatcher;
import com.example.patryk.sharegame2.Adapters.OpenHoursAdapter;
import com.example.patryk.sharegame2.MainFragments.MainActivity;
import com.example.patryk.sharegame2.Objects.Day;
import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.SportItem;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class AddFacility extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private String url = StartActivity.user.getUrl() + "/addNewObject";
    private String MESSAGE_SUCCESS = "SUCCESS";
    private String MESSAGE_ERROR1 = "FAILED";

    private EditText facilityName;
    private EditText facilityCity;
    private EditText facilityStreet;
    private EditText facilityStreetNr;
    private EditText facilityZip;
    private EditText facilityCode;
    private EditText facilityZCCity;
    private EditText facilityEmail;
    private EditText facilityPaypalEmail;
    private EditText facilityPhoneNo;
    private EditText facilityWWW;
    private TextView facilityOpenHour;
    private TextView facilityEndHour;
    private TextView checkInMap;
    private TextView isCheckInMap;

    private CheckBox setYes;
    private CheckBox setNo;
    private CheckBox weekends;
    private CheckBox holidays;
    private ExpandableRelativeLayout days;
    private List<Day> dayList;
    private OpenHoursAdapter openHoursAdapter;
    private RecyclerView rv_days;

    private ExpandableRelativeLayout weekend;
    private List<Day> weekendDays;
    private OpenHoursAdapter openWeekendHoursAdapter;
    private RecyclerView rv_weekend;

    private String lastChar = " ";
    private int minHour = 0;

    private TimePickerDialog.OnTimeSetListener timeStartSetListener;
    private TimePickerDialog.OnTimeSetListener timeEndSetListener;

    private LinearLayout timepickerstart;
    private LinearLayout timepickerend;


    // choose sport
    private RecyclerView sports;
    private CheckableRecyclerViewAdapter recyclerViewAdapter;
    private List<SportItem> rv_sports;
    private TextView addSport;

    // choose extras

    private RecyclerView extras;
    private CheckableRecyclerViewAdapter extrasViewAdapter;
    private List<SportItem> rv_extras;

    // SeekBar
    private SeekBar seekBar;
    TextView facilityRentalPrice;
    private int maxValue = 200;
    private int stepValue = 1;
    private int currentValue;

    // Map
    private GoogleMap mMap;
    private Dialog mapDialog;
    private double latitude;
    private double longitude;
    private Boolean isCheckedInMap = false;

    // Not logged section
    private ImageView c1;
    private ImageView c2;
    private ImageView c3;
    private ImageView c4;
    private ImageView circle;
    private RelativeLayout relativeLayout;
    private TextView login;
    private TextView signUp;

    private Boolean isLogged = false;

    private LinearLayout add;
    private DatabaseHelper databaseHelper;

    private boolean result = false;


    public static final int PERMISSION_REQUEST = 0;
    public static final int RESULT_LOAD_IMAGE = 1;

    private List<FacilityImage> imageList;
    private ImageRVAdapter imageAdapter;
    private RecyclerView rv_images;
    private int adapterPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_facility);

        relativeLayout = findViewById(R.id.not_logged_layout);

        ScrollView sv = findViewById(R.id.mainLayout);

        databaseHelper = new DatabaseHelper(this);
        Cursor c = databaseHelper.getUser();
        if (c.getCount() > 0) {
            c.moveToFirst();

            isLogged = (c.getInt(0) == 1) ? true : false;
        }

        if (!isLogged) {
            relativeLayout.setVisibility(View.VISIBLE);
            sv.setVisibility(View.GONE);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x - 60;

            c1 = findViewById(R.id.c_one);
            c1.getLayoutParams().height = width - 40;
            c1.getLayoutParams().width = width - 40;
            c2 = findViewById(R.id.c_second);
            c2.getLayoutParams().height = width - 140;
            c2.getLayoutParams().width = width - 140;
            c3 = findViewById(R.id.c_third);
            c3.getLayoutParams().height = width - 240;
            c3.getLayoutParams().width = width - 240;
            c4 = findViewById(R.id.c_fourth);
            c4.getLayoutParams().height = width - 340;
            c4.getLayoutParams().width = width - 340;

            circle = findViewById(R.id.c_fifth);
            circle.getLayoutParams().height = width - 440;
            circle.getLayoutParams().width = width - 440;
            setAnimation(c1, 20000);
            setAnimation(c2, 19000);
            setAnimation(c3, 18000);
            setAnimation(c4, 17000);

            login = findViewById(R.id.add_login);
            signUp = findViewById(R.id.add_signin);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent login = new Intent(AddFacility.this, LoginActivity.class);
                    login.putExtra("ActivityName", "Add");
                    startActivity(login);
                    finish();
                }
            });

            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signin = new Intent(AddFacility.this, SignUpActivity.class);
                    signin.putExtra("ActivityName", "Add");
                    startActivity(signin);
                    finish();
                }
            });

        } else {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST);
            }

            //images
            imageList = new ArrayList<>();
            imageList.add(new FacilityImage());

            imageAdapter = new ImageRVAdapter(this,imageList);
            rv_images = findViewById(R.id.rv_images);
            rv_images.setLayoutManager(new GridLayoutManager(this,2));
            rv_images.setAdapter(imageAdapter);

            imageAdapter.setOnItemClickListener(new ImageRVAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if(!imageAdapter.getmData().get(position).isAdded()){
                        adapterPosition = position;
                        Intent add = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(add,RESULT_LOAD_IMAGE);
                    }else{
                        imageList.remove(position);
                        imageAdapter.notifyItemRemoved(position);
                    }

                }
            });

            //======
            relativeLayout.setVisibility(View.GONE);
            sv.setVisibility(View.VISIBLE);
            sv.requestFocus();

            latitude = 51.760499;
            longitude = 19.453120;
            mapDialog = new Dialog(this);

            final Calendar calendar = Calendar.getInstance();

            timepickerstart = findViewById(R.id.timepickerstart);
            timepickerend = findViewById(R.id.timepickerend);

            facilityName = findViewById(R.id.af_facility_name);
            facilityCity = findViewById(R.id.af_facility_address_city);
            facilityStreet = findViewById(R.id.af_facility_address_street);
            facilityStreetNr = findViewById(R.id.af_facility_address_streetNo);
            facilityZip = findViewById(R.id.af_facility_address_zip);
            facilityCode = findViewById(R.id.af_facility_address_code);
            facilityZCCity = findViewById(R.id.af_facility_address_zccity);
            facilityEmail = findViewById(R.id.af_facility_email);
            facilityPaypalEmail = findViewById(R.id.af_facility_email_paypal);
            facilityPhoneNo = findViewById(R.id.af_facility_phone_no);
            facilityPhoneNo.addTextChangedListener(new MaskWatcher("### ### ###"));
            facilityWWW = findViewById(R.id.af_facility_www);
            facilityOpenHour = findViewById(R.id.af_facility_start_hour);
            facilityEndHour = findViewById(R.id.af_facility_end_hour);
            checkInMap = findViewById(R.id.check_in_map);
            isCheckInMap = findViewById(R.id.is_checked);
            add = findViewById(R.id.add_button);


            days = findViewById(R.id.erl_dayOpenHours);
            days.collapse();

            weekend = findViewById(R.id.erl_weekend);
            weekend.collapse();

            setYes = findViewById(R.id.isOpenPnPt);
            setNo = findViewById(R.id.isNotOpenPnPt);
            weekends = findViewById(R.id.isOpenWeekend);
            holidays = findViewById(R.id.isOpenHolidays);
            initDayList();
            initWeekend();

            openHoursAdapter = new OpenHoursAdapter(this,dayList);
            rv_days = findViewById(R.id.rv_DayOpenHours);
            rv_days.setAdapter(openHoursAdapter);
            rv_days.setLayoutManager(new LinearLayoutManager(this));

            openWeekendHoursAdapter = new OpenHoursAdapter(this,weekendDays);
            rv_weekend = findViewById(R.id.rv_weekend);
            rv_weekend.setAdapter(openWeekendHoursAdapter);
            rv_weekend.setLayoutManager(new LinearLayoutManager(this));

            setYes.setOnCheckedChangeListener(this);
            setNo.setOnCheckedChangeListener(this);
            weekends.setOnCheckedChangeListener(this);
            holidays.setOnCheckedChangeListener(this);

            initSportList();
            initExstrasList();

            sports = findViewById(R.id.add_recyclerView);
            recyclerViewAdapter = new CheckableRecyclerViewAdapter(this, rv_sports);

            sports.setAdapter(recyclerViewAdapter);
            sports.setLayoutManager(new LinearLayoutManager(this));

            extras = findViewById(R.id.add_extrasRecyclerView);
            extrasViewAdapter = new CheckableRecyclerViewAdapter(this, rv_extras);
            extras.setAdapter(extrasViewAdapter);
            extras.setLayoutManager(new LinearLayoutManager(this));

            addSport = findViewById(R.id.add_sportCategory);
            addSport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(AddFacility.this, dayList.get(0).getOpenHours(), Toast.LENGTH_SHORT).show();

                }
            });


            seekBar = findViewById(R.id.af_facility_seekbar);
            facilityRentalPrice = findViewById(R.id.af_facility_rental_price);

            seekBar.setMax(maxValue / stepValue);
            seekBar.setProgress(maxValue / stepValue / 2);

            currentValue = (maxValue / stepValue / 2) * stepValue;
            facilityRentalPrice.setText(String.valueOf((maxValue / stepValue / 2) * stepValue) + " zł");

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    currentValue = progress * stepValue;
                    facilityRentalPrice.setText(String.valueOf(currentValue) + " zł");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            // TimePicker

            //starthour

            timepickerstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(AddFacility.this,
                            timeStartSetListener, hour, minute, true);
                    timeDialog.setMaxHour(23);
                    timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    timeDialog.show();

                }
            });

            timeStartSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    facilityOpenHour.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    facilityEndHour.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    initHours(facilityOpenHour.getText().toString(),facilityEndHour.getText().toString());
                    minHour = hourOfDay;
                }
            };

            //endhour

            timepickerend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String time = (String) facilityOpenHour.getText();
                    int hour = Integer.parseInt(time.substring(0, 2));
                    int minute = Integer.parseInt(time.substring(3, 5));

                    CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(AddFacility.this,
                            timeEndSetListener, hour, minute, true);

                    if (minute > 0) {
                        timeDialog.setMinHour(minHour + 1);
                    } else {
                        timeDialog.setMinHour(minHour);
                    }

                    timeDialog.setMaxHour(24);
                    timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    timeDialog.show();
                }
            });

            timeEndSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    facilityEndHour.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    initHours(facilityOpenHour.getText().toString(),facilityEndHour.getText().toString());
                }
            };

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkCorrectness()) {

                        if (!StartActivity.user.getOffLine()) {
                            addFacility();
                        }else{
                            addFacility();
                        }
                    }
                }
            });
        }

    }

    private void initHours(String open, String close) {
        for(int i = 0; i < dayList.size(); i++){
            dayList.get(i).setOpenHour(open);
            dayList.get(i).setCloseHour(close);
            dayList.get(i).setOpenHours();
        }

        openHoursAdapter.notifyDataSetChanged();
    }

    private void resetWeekend() {
        for(int i = 0; i < weekendDays.size(); i++){
            weekendDays.get(i).setOpenHour("00:00");
            weekendDays.get(i).setCloseHour("00:00");
            weekendDays.get(i).setOpenHours();
        }
        openWeekendHoursAdapter.notifyDataSetChanged();
    }

    private void initWeekend() {
        weekendDays = new ArrayList<>();
        weekendDays.add(new Day("Sobota"));
        weekendDays.add(new Day("Niedziela"));
    }


    private void initDayList() {
        dayList = new ArrayList<>();
        dayList.add(new Day("Poniedziałek"));
        dayList.add(new Day("Wtorek"));
        dayList.add(new Day("Środa"));
        dayList.add(new Day("Czwartek"));
        dayList.add(new Day("Piątek"));
    }

    private void initExstrasList() {
        rv_extras = new ArrayList<>();
        rv_extras.add(new SportItem("Parking"));
        rv_extras.add(new SportItem("Łazienka"));
        rv_extras.add(new SportItem("Szatnia"));
        rv_extras.add(new SportItem("Sztuczne oświetlenie"));
        rv_extras.add(new SportItem("Dostęp do magazynku"));
    }

    private void addFacilityToSQLite(int id) {
        String name = facilityName.getText().toString();

        String city = facilityCity.getText().toString();

        String street = facilityStreet.getText().toString();

        String local_no = facilityStreetNr.getText().toString();

        String zip_code = facilityZip.getText().toString() + "-" + facilityCode.getText().toString();

        String zip_code_city = facilityZCCity.getText().toString();

        String email;
        if (facilityEmail.getText().length() == 0) {
            email = "-";
        } else {
            email = facilityEmail.getText().toString();
        }

        String pp_email = facilityPaypalEmail.getText().toString();

        String phone_no;

        if (facilityPhoneNo.getText().length() == 0) {
            phone_no = "-";
        } else {
            phone_no = facilityPhoneNo.getText().toString();
        }

        String www;

        if (facilityWWW.getText().length() == 0) {
            www = "-";
        } else {
            www = facilityWWW.getText().toString();
        }

        String start_hour = facilityOpenHour.getText().toString();

        String end_hour = facilityEndHour.getText().toString();

        int rental_price = currentValue;

        double lati = latitude;

        double longi = longitude;

        Cursor cursor = databaseHelper.getUser();
        cursor.moveToFirst();

        int owner_id = cursor.getInt(1);

        int soccer = (rv_sports.get(0).getChecked() == true) ? 1 : 0;

        int futsal = (rv_sports.get(1).getChecked() == true) ? 1 : 0;

        int volleyball = (rv_sports.get(2).getChecked() == true) ? 1 : 0;

        int tennis = (rv_sports.get(3).getChecked() == true) ? 1 : 0;

        int basketball = (rv_sports.get(4).getChecked() == true) ? 1 : 0;

        int handball = (rv_sports.get(5).getChecked() == true) ? 1 : 0;

        int squash = (rv_sports.get(6).getChecked() == true) ? 1 : 0;

        int badminton = (rv_sports.get(7).getChecked() == true) ? 1 : 0;

        int parking = (rv_extras.get(0).getChecked() == true) ? 1 : 0;

        int bath = (rv_extras.get(1).getChecked() == true) ? 1 : 0;

        int locker_room = (rv_extras.get(2).getChecked() == true) ? 1 : 0;

        int lighting = (rv_extras.get(3).getChecked() == true) ? 1 : 0;

        int magazine = (rv_extras.get(4).getChecked() == true) ? 1 : 0;


        String monday = dayList.get(0).getOpenHours();

        String tuesday = dayList.get(1).getOpenHours();

        String wednesday = dayList.get(2).getOpenHours();

        String thursday = dayList.get(3).getOpenHours();

        String friday = dayList.get(4).getOpenHours();

        String saturday;

        String sunday;

        int isHolidays;

        if(holidays.isChecked()){
            isHolidays = 1;
        }else{
            isHolidays = 0;
        }

        int isWeekends;

        if(weekends.isChecked()){
            isWeekends = 1;
            saturday = weekendDays.get(0).getOpenHours();
            sunday = weekendDays.get(1).getOpenHours();
        }else{
            isWeekends = 0;
            saturday = "00:00-00:00";
            sunday = "00:00-00:00";
        }

        result = databaseHelper.addData(name, city, street, local_no,
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
                magazine, id,
                monday, tuesday,
                wednesday, thursday,
                friday, saturday,
                sunday, isHolidays,
                isWeekends);


        for(int i = 0; i < imageList.size() - 1; i++){
            databaseHelper.addImage(id,imageList.get(i).getByteArray());
        }
    }

    private void addFacility() {

        String start_hour = facilityOpenHour.getText().toString();

        String end_hour = facilityEndHour.getText().toString();

        Cursor cursor = databaseHelper.getUser();
        cursor.moveToFirst();
        int owner_id = cursor.getInt(1);


        String saturday;

        String sunday;

        if (weekends.isChecked()) {
            saturday = weekendDays.get(0).getOpenHours();
            sunday = weekendDays.get(1).getOpenHours();
        } else {
            saturday = "00:00-00:00";
            sunday = "00:00-00:00";
        }

        // making JSON

        JSONObject sportObject = new JSONObject();

        try{
            sportObject.put("name", facilityName.getText().toString());
            sportObject.put("city", facilityCity.getText().toString());
            sportObject.put("street", facilityStreet.getText().toString());
            sportObject.put("localno", facilityStreetNr.getText().toString());
            sportObject.put("zipcode", facilityZip.getText().toString() + "-" + facilityCode.getText().toString());
            sportObject.put("zipcodecity", facilityZCCity.getText().toString());
            sportObject.put("email", facilityEmail.getText().length());
            sportObject.put("ppmail", facilityPaypalEmail.getText().toString());
            sportObject.put("phoneno", facilityPhoneNo.getText().toString());
            sportObject.put("siteaddress", facilityWWW.getText().length());
            sportObject.put("open", start_hour);
            sportObject.put("close", end_hour);
            sportObject.put("rentprice", currentValue);
            sportObject.put("latitude", latitude);
            sportObject.put("longitude", longitude);
            sportObject.put("ownid", owner_id);
            sportObject.put("soccer", rv_sports.get(0).getChecked());
            sportObject.put("futsal", rv_sports.get(1).getChecked());
            sportObject.put("volleyball", rv_sports.get(2).getChecked());
            sportObject.put("tennis", rv_sports.get(3).getChecked());
            sportObject.put("basketball", rv_sports.get(4).getChecked());
            sportObject.put("handball", rv_sports.get(5).getChecked());
            sportObject.put("squash", rv_sports.get(6).getChecked());
            sportObject.put("badminton", rv_sports.get(7).getChecked());
        }catch (JSONException e) {
            e.printStackTrace();
        }


        // extras
        JSONObject extras = new JSONObject();

        try {
            extras.put("parking", rv_extras.get(0).getChecked());
            extras.put("bathroom", rv_extras.get(1).getChecked());
            extras.put("artificiallighting", rv_extras.get(2).getChecked());
            extras.put("lockerroom", rv_extras.get(3).getChecked());
            extras.put("equipment", rv_extras.get(4).getChecked());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // openhours
        JSONObject openHours = new JSONObject();
        try {
            openHours.put("mondayHours", dayList.get(0).getOpenHours());
            openHours.put("tusedayHours", dayList.get(1).getOpenHours());
            openHours.put("wensdayHours", dayList.get(2).getOpenHours());
            openHours.put("thrusdayHours", dayList.get(3).getOpenHours());
            openHours.put("fridayHours", dayList.get(4).getOpenHours());
            openHours.put("saturdayHours", saturday);
            openHours.put("sundayHours", sunday);
            openHours.put("openInBankHolidays", holidays.isChecked());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        //photos

        JSONArray photos = new JSONArray();
        for(int i = 0; i < imageList.size() - 1 ; i++){
            photos.put(imageList.get(i).byteToString());
        }

        JSONObject params = new JSONObject();
        try {
            params.put("sportObject",sportObject);
            params.put("openHours",openHours);
            params.put("objectExstras",extras);
            params.put("photos",photos);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //System.out.println(params.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject json = response;
                    String res = json.getString("message");
                    int id = json.getInt("id");

                    if (res.equals(MESSAGE_SUCCESS)) {
                        addFacilityToSQLite(id);
                        Toast.makeText(AddFacility.this, "Obiekt dodany pomyślnie", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddFacility.this, MainActivity.class);
                        intent.putExtra("PageNo", 1);
                        startActivity(intent);
                    } else if (res.equals(MESSAGE_ERROR1)) {
                        Toast.makeText(AddFacility.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Cursor cursor = databaseHelper.getUser();
                cursor.moveToFirst();

                String username = cursor.getString(2);
                String password = cursor.getString(3);

                String credentials = username + ":" + password;
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private void initSportList() {
        rv_sports = new ArrayList<SportItem>();
        rv_sports.add(new SportItem("Piłka nożna"));
        rv_sports.add(new SportItem("Futsal"));
        rv_sports.add(new SportItem("Siatkówka"));
        rv_sports.add(new SportItem("Tenis"));
        rv_sports.add(new SportItem("Koszykówka"));
        rv_sports.add(new SportItem("Piłka ręczna"));
        rv_sports.add(new SportItem("Squash"));
        rv_sports.add(new SportItem("Badminton"));
    }

    public void setAnimation(ImageView imageView, int duration) {
        RotateAnimation animation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        imageView.setAnimation(animation);
        imageView.startAnimation(animation);
    }

    // showing dialog with map
    public void ShowPopup(View v) throws IOException {

        final Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());

        mapDialog.setContentView(R.layout.map_dialog);
        TextView close = mapDialog.findViewById(R.id.map_close);
        final TextView accept = mapDialog.findViewById(R.id.map_accept);
        accept.setEnabled(false);

        MapView mapView = mapDialog.findViewById(R.id.mapInDialog);
        MapsInitializer.initialize(getApplicationContext());

        mapView.onCreate(mapDialog.onSaveInstanceState());
        mapView.onResume();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);

                LatLng latLng = new LatLng(latitude, longitude);


                if (isCheckedInMap) {
                    MarkerOptions mOptions = new MarkerOptions();
                    mOptions.position(latLng);
                    mMap.addMarker(mOptions);
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        // Creating a marker
                        MarkerOptions markerOptions = new MarkerOptions();

                        // Setting the position for the marker
                        markerOptions.position(latLng);

                        // Clears the previously touched position
                        mMap.clear();

                        // Animating to the touched position
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                        // Placing a marker on the touched position
                        mMap.addMarker(markerOptions);

                        latitude = latLng.latitude;
                        longitude = latLng.longitude;

                        accept.setTextColor(getResources().getColor(R.color.turquioselv2));
                        accept.setEnabled(true);
                    }
                });

            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapDialog.dismiss();
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckedInMap = true;
                isCheckInMap.setText("Zaznaczono!");
                isCheckInMap.setTextColor(getResources().getColor(R.color.turquioselv2));

                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String city = addresses.get(0).getLocality();
                String zipcode = addresses.get(0).getPostalCode();
                String street = addresses.get(0).getThoroughfare();

                if (city != null) {
                    facilityCity.setText(city);
                }

                if (zipcode.length() == 6) {

                    facilityZip.setText(zipcode.substring(0, 2));
                    facilityCode.setText(zipcode.substring(3, 6));
                }

                if (addresses.get(0).getThoroughfare() != null) {

                    facilityStreet.setText(street);
                }


                mapDialog.dismiss();
            }
        });

        mapDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // usuwanie przyciemnienia tła (nachodziło na mape)
        mapDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mapDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("PageNo", 1);
        startActivity(intent);
        finish();
    }


    public Boolean checkCorrectness() {

        Boolean checked = false;

        for (int i = 0; i < rv_sports.size(); i++) {

            if (rv_sports.get(i).getChecked() == true) {
                checked = true;
                break;
            }
        }

        if (facilityName.getText().length() < 3) {
            Toast.makeText(this, "Nazwa obiektu musi zawierać co najmniej 3 znaki", Toast.LENGTH_SHORT).show();
            return false;
        } else if (facilityCity.getText().length() < 2) {
            Toast.makeText(this, "Nazwa miasta musi zawierać co najmniej 2 znaki", Toast.LENGTH_SHORT).show();
            return false;
        } else if (facilityStreet.getText().length() < 2) {
            Toast.makeText(this, "Nazwa ulicy musi zawierać co najmniej 2 znaki", Toast.LENGTH_SHORT).show();
            return false;
        } else if (facilityZip.getText().length() == 0 || facilityCode.getText().length() == 0 || facilityZCCity.getText().length() < 2) {
            Toast.makeText(this, "Nieprawidłowy kod pocztowy", Toast.LENGTH_SHORT).show();
            return false;
        } else if (facilityStreetNr.getText().length() == 0) {
            Toast.makeText(this, "Nieprawidłowy numer lokalu", Toast.LENGTH_SHORT).show();
            return false;
        } else if (isCheckedInMap == false) {
            Toast.makeText(this, "Musisz zaznaczyć obiekt na mapie", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!facilityPaypalEmail.getText().toString().contains("@") || facilityPaypalEmail.getText().length() < 6) {
            Toast.makeText(this, "Nieprawidłowy PayPal adres email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (facilityOpenHour.getText().toString().equals(facilityEndHour.getText().toString())) {
            Toast.makeText(this, "Godziny otwarcia nie mogą być takie same", Toast.LENGTH_SHORT).show();
            return false;
        } else if (checked == false) {
            Toast.makeText(this, "Wybierz chociaż jedną kategorię sportową", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.isOpenPnPt:
                if(isChecked){
                    setNo.setChecked(false);
                    days.toggle();
                }else{
                    setNo.setChecked(true);
                    days.toggle();
                }
                break;
            case R.id.isNotOpenPnPt:
                if(isChecked){
                    initHours(facilityOpenHour.getText().toString(),facilityEndHour.getText().toString());
                    setYes.setChecked(false);
                    days.toggle();
                }else{
                    setYes.setChecked(true);
                    days.toggle();
                }
                break;
            case R.id.isOpenWeekend:
                if(isChecked){

                    weekend.toggle();
                }else{
                    resetWeekend();
                    weekend.toggle();
                }
                break;
            case R.id.isOpenHolidays:
                break;
        }
    }


    //==================


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,filePathColumn,null,null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picPath = cursor.getString(columnIndex);
                    cursor.close();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                    Bitmap b = Bitmap.createScaledBitmap(bitmap,480,360,false);

                    b.compress(Bitmap.CompressFormat.PNG,40,stream);

                    byte[] byteArray = stream.toByteArray();
                    Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

                    imageList.get(adapterPosition).setByteArray(byteArray);
                    imageList.get(adapterPosition).setBitmap(compressedBitmap);
                    imageList.get(adapterPosition).setImg_path(picPath);
                    imageList.get(adapterPosition).setAdded(true);

                    imageList.add(new FacilityImage());
                    imageAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
