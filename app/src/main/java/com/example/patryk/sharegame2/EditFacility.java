package com.example.patryk.sharegame2;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.Objects.SportItem;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditFacility extends AppCompatActivity {

    private String url = StartActivity.user.getUrl() + "/updateObject";
    private String MESSAGE_SUCCESS = "UPDATED";
    private String MESSAGE_ERROR1 = "ERROR";

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

    private CheckBox holidays;

    private List<Day> dayList;
    private OpenHoursAdapter openHoursAdapter;
    private RecyclerView rv_days;


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

    private DatabaseHelper databaseHelper;
    private LinearLayout edit;

    private boolean result = false;

    // Images
    public static final int PERMISSION_REQUEST = 0;
    public static final int RESULT_LOAD_IMAGE = 1;

    private List<FacilityImage> imageList;
    private ImageRVAdapter imageAdapter;
    private RecyclerView rv_images;
    private int adapterPosition;

    private SportFacility sportFacility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_facility);

        databaseHelper = new DatabaseHelper(this);
        final Calendar calendar = Calendar.getInstance();

        // INTENT

        Intent intent = getIntent();
        sportFacility = intent.getParcelableExtra("Facility");

        ScrollView sv = findViewById(R.id.editScrollView);
        sv.requestFocus();

        // IMAGES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }

        imageList = new ArrayList<>();

        if(databaseHelper.getImages(sportFacility.getGlobal_id())!= null){
            imageList = databaseHelper.getImages(sportFacility.getGlobal_id());
        }

        imageList.add(new FacilityImage());

        imageAdapter = new ImageRVAdapter(this, imageList);
        rv_images = findViewById(R.id.rv_edit_images);

        rv_images.setLayoutManager(new GridLayoutManager(this, 2));
        rv_images.setAdapter(imageAdapter);

        imageAdapter.setOnItemClickListener(new ImageRVAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (!imageAdapter.getmData().get(position).isAdded()) {
                    adapterPosition = position;
                    Intent add = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(add, RESULT_LOAD_IMAGE);
                } else {
                    imageList.remove(position);
                    imageAdapter.notifyItemRemoved(position);
                }

            }
        });
        //======

        // Facilities info
        facilityName = findViewById(R.id.ef_facility_name);
        facilityName.setText(sportFacility.getName());

        facilityCity = findViewById(R.id.ef_facility_address_city);
        facilityCity.setText(sportFacility.getCity());

        facilityStreet = findViewById(R.id.ef_facility_address_street);
        facilityStreet.setText(sportFacility.getStreet());

        facilityStreetNr = findViewById(R.id.ef_facility_address_streetNo);
        facilityStreetNr.setText(sportFacility.getLocal_no());

        facilityZip = findViewById(R.id.ef_facility_address_zip);
        facilityZip.setText(sportFacility.getZip_code().substring(0,2));

        facilityCode = findViewById(R.id.ef_facility_address_code);
        facilityCode.setText(sportFacility.getZip_code().substring(3,6));

        facilityZCCity = findViewById(R.id.ef_facility_address_zccity);
        facilityZCCity.setText(sportFacility.getZip_code_city());

        facilityEmail = findViewById(R.id.ef_facility_email);
        facilityEmail.setText(sportFacility.getEmail());

        facilityPaypalEmail = findViewById(R.id.ef_facility_email_paypal);
        facilityPaypalEmail.setText(sportFacility.getPp_email());

        facilityPhoneNo = findViewById(R.id.ef_facility_phone_no);
        facilityPhoneNo.addTextChangedListener(new MaskWatcher("### ### ###"));
        facilityPhoneNo.setText(sportFacility.getPhone_no());

        facilityWWW = findViewById(R.id.ef_facility_www);
        facilityWWW.setText(sportFacility.getWww());


        // MAP DIALOG
        checkInMap = findViewById(R.id.ef_check_in_map);

        latitude = sportFacility.getLatitude();
        longitude = sportFacility.getLongitude();
        mapDialog = new Dialog(this);

        // EDIT BUTTON
        edit = findViewById(R.id.edit_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkCorrectness()) {

                    if (!StartActivity.user.getOffLine()) {
                        editFacility();
                    }
                }
            }
        });

        // TIMER PICKER
        facilityOpenHour = findViewById(R.id.ef_facility_start_hour);
        facilityOpenHour.setText(sportFacility.getStart_hour());

        facilityEndHour = findViewById(R.id.ef_facility_end_hour);
        facilityEndHour.setText(sportFacility.getEnd_hour());

        timepickerstart = findViewById(R.id.ef_timepickerstart);
        timepickerend = findViewById(R.id.ef_timepickerend);

        // TimePicker

        //starthour

        timepickerstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(EditFacility.this,
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
                initHours(facilityOpenHour.getText().toString(), facilityEndHour.getText().toString());
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

                CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(EditFacility.this,
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
                initHours(facilityOpenHour.getText().toString(), facilityEndHour.getText().toString());
            }
        };

        // TIMETABLE

        initDayList();

        openHoursAdapter = new OpenHoursAdapter(this, dayList);
        rv_days = findViewById(R.id.rv_ef_DayOpenHours);
        rv_days.setAdapter(openHoursAdapter);
        rv_days.setLayoutManager(new LinearLayoutManager(this));

        holidays = findViewById(R.id.ef_isOpenHolidays);

        if(sportFacility.getHolidays()){
            holidays.setChecked(true);
        }

        initSportList();
        initExstrasList();

        // SportCategories
        sports = findViewById(R.id.edit_recyclerView);
        recyclerViewAdapter = new CheckableRecyclerViewAdapter(this, rv_sports);

        sports.setAdapter(recyclerViewAdapter);
        sports.setLayoutManager(new LinearLayoutManager(this));

        addSport = findViewById(R.id.edit_sportCategory);
        addSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditFacility.this, "Ta funkcja chwilowo niedostępna", Toast.LENGTH_SHORT).show();
            }
        });

        // EXTRAS

        extras = findViewById(R.id.edit_extrasRecyclerView);
        extrasViewAdapter = new CheckableRecyclerViewAdapter(this, rv_extras);
        extras.setAdapter(extrasViewAdapter);
        extras.setLayoutManager(new LinearLayoutManager(this));

        // RENTAL PRICE
        seekBar = findViewById(R.id.ef_facility_seekbar);
        facilityRentalPrice = findViewById(R.id.ef_facility_rental_price);

        seekBar.setMax(maxValue / stepValue);
        seekBar.setProgress(sportFacility.getRental_price());

        currentValue = sportFacility.getRental_price();

        facilityRentalPrice.setText( sportFacility.getRental_price() + " zł");

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

    }

    private void initHours(String open, String close) {
        for (int i = 0; i < dayList.size(); i++) {
            dayList.get(i).setOpenHour(open);
            dayList.get(i).setCloseHour(close);
            dayList.get(i).setOpenHours();
        }

        openHoursAdapter.notifyDataSetChanged();
    }


    private void initDayList() {
        dayList = new ArrayList<>();
        dayList.add(new Day("Poniedziałek", sportFacility.getOh_monday()));
        dayList.add(new Day("Wtorek",sportFacility.getOh_tuesday()));
        dayList.add(new Day("Środa",sportFacility.getOh_wednesday()));
        dayList.add(new Day("Czwartek",sportFacility.getOh_thursday()));
        dayList.add(new Day("Piątek",sportFacility.getOh_friday()));
        dayList.add(new Day("Sobota", sportFacility.getOh_saturday()));
        dayList.add(new Day("Niedziela", sportFacility.getOh_sunday()));
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

        String saturday = dayList.get(5).getOpenHours();

        String sunday = dayList.get(6).getOpenHours();

        int isHolidays;

        if (holidays.isChecked()) {
            isHolidays = 1;
        } else {
            isHolidays = 0;
        }

        int isWeekends;

        if (saturday.equals("00:00-00:00") && sunday.equals("00:00-00:00")) {
            isWeekends = 1;
        } else {
            isWeekends = 0;
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

    private void editFacility() {

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

        boolean soccer = rv_sports.get(0).getChecked();

        boolean futsal = rv_sports.get(1).getChecked();

        boolean volleyball = rv_sports.get(2).getChecked();

        boolean tennis = rv_sports.get(3).getChecked();

        boolean basketball = rv_sports.get(4).getChecked();

        boolean handball = rv_sports.get(5).getChecked();

        boolean squash = rv_sports.get(6).getChecked();

        boolean badminton = rv_sports.get(7).getChecked();

        boolean parking = rv_extras.get(0).getChecked();

        boolean bath = rv_extras.get(1).getChecked();

        boolean locker_room = rv_extras.get(2).getChecked();

        boolean lighting = rv_extras.get(3).getChecked();

        boolean magazine = rv_extras.get(4).getChecked();

        String monday = dayList.get(0).getOpenHours();

        String tuesday = dayList.get(1).getOpenHours();

        String wednesday = dayList.get(2).getOpenHours();

        String thursday = dayList.get(3).getOpenHours();

        String friday = dayList.get(4).getOpenHours();

        String saturday = dayList.get(5).getOpenHours();

        String sunday = dayList.get(6).getOpenHours() ;

        int isHolidays;

        if (holidays.isChecked()) {
            isHolidays = 1;
        } else {
            isHolidays = 0;
        }

        final JSONObject sportObject = new JSONObject();

        try{
            sportObject.put("id",sportFacility.getGlobal_id());
            sportObject.put("name", name);
            sportObject.put("city", city);
            sportObject.put("street", street);
            sportObject.put("localno", local_no);
            sportObject.put("zipcode", zip_code);
            sportObject.put("zipcodecity", zip_code_city);
            sportObject.put("email", email);
            sportObject.put("ppmail", pp_email);
            sportObject.put("phoneno", phone_no);
            sportObject.put("siteaddress", www);
            sportObject.put("open", start_hour);
            sportObject.put("close", end_hour);
            sportObject.put("rentprice", String.valueOf(rental_price));
            sportObject.put("latitude", String.valueOf(lati));
            sportObject.put("longitude", String.valueOf(longi));
            sportObject.put("ownid", String.valueOf(owner_id));
            sportObject.put("soccer", soccer);
            sportObject.put("futsal", futsal);
            sportObject.put("volleyball", volleyball);
            sportObject.put("tennis", tennis);
            sportObject.put("basketball", basketball);
            sportObject.put("handball", handball);
            sportObject.put("squash", squash);
            sportObject.put("badminton", badminton);
        }catch (JSONException e) {
            e.printStackTrace();
        }


        // extras
        JSONObject extras = new JSONObject();

        try {
            extras.put("parking", parking);
            extras.put("bathroom", bath);
            extras.put("artificiallighting", lighting);
            extras.put("lockerroom", locker_room);
            extras.put("equipment", magazine);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // openhours
        JSONObject openHours = new JSONObject();
        try {
            openHours.put("mondayHours", monday);
            openHours.put("tusedayHours", tuesday);
            openHours.put("wensdayHours", wednesday);
            openHours.put("thrusdayHours", thursday);
            openHours.put("fridayHours", friday);
            openHours.put("saturdayHours", saturday);
            openHours.put("sundayHours", sunday);
            openHours.put("openInBankHolidays", String.valueOf(isHolidays));
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


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject json = response;
                    String res = json.getString("status");

                    if (res.equals(MESSAGE_SUCCESS)) {
                        databaseHelper.removeImages(sportFacility.getGlobal_id());
                        databaseHelper.removeSportFacility(sportFacility.getGlobal_id());

                        addFacilityToSQLite(sportFacility.getGlobal_id());
                        Toast.makeText(EditFacility.this, "Obiekt zedytowany pomyślnie", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditFacility.this, MainActivity.class);
                        intent.putExtra("PageNo", 1);
                        startActivity(intent);
                    } else if (res.equals(MESSAGE_ERROR1)) {
                        Toast.makeText(EditFacility.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
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

                System.out.println(username + ":" + password);
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

    private void initExstrasList() {
        rv_extras = new ArrayList<>();
        rv_extras.add(new SportItem("Parking",sportFacility.getParking()));
        rv_extras.add(new SportItem("Łazienka", sportFacility.getBath()));
        rv_extras.add(new SportItem("Szatnia", sportFacility.getLocker_room()));
        rv_extras.add(new SportItem("Sztuczne oświetlenie", sportFacility.getLighting()));
        rv_extras.add(new SportItem("Dostęp do magazynku", sportFacility.getMagazine()));
    }

    private void initSportList() {
        rv_sports = new ArrayList<>();
        rv_sports.add(new SportItem("Piłka nożna",sportFacility.getSoccer()));
        rv_sports.add(new SportItem("Futsal",sportFacility.getFutsal()));
        rv_sports.add(new SportItem("Siatkówka",sportFacility.getVolleyball()));
        rv_sports.add(new SportItem("Tenis",sportFacility.getTennis()));
        rv_sports.add(new SportItem("Koszykówka",sportFacility.getBasketball()));
        rv_sports.add(new SportItem("Piłka ręczna",sportFacility.getHandball()));
        rv_sports.add(new SportItem("Squash",sportFacility.getSquash()));
        rv_sports.add(new SportItem("Badminton",sportFacility.getBadminton()));
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

                MarkerOptions mOptions = new MarkerOptions();
                mOptions.position(latLng);
                mMap.addMarker(mOptions);

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


    //==================


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
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

                    System.out.println(imageList.get(adapterPosition).byteToString().length());
                    imageList.add(new FacilityImage());
                    imageAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
