package com.example.patryk.sharegame2.MainFragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.patryk.sharegame2.AddFacility;
import com.example.patryk.sharegame2.FacilityView;
import com.example.patryk.sharegame2.LoginActivity;
import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.MyClusterItem;
import com.example.patryk.sharegame2.Objects.OwnIconRendered;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.Options.UserFavorites;
import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;
import com.example.patryk.sharegame2.StartActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

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
import java.util.Timer;


/**
 * A simple {@link Fragment} subclass.
 */
public class mapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ClusterManager.OnClusterItemClickListener<MyClusterItem>, ClusterManager.OnClusterClickListener<MyClusterItem> {



    public mapFragment() {
        // Required empty public constructor
    }

    // maps elements
    private static final int REQUEST_USER_LOCATION_CODE = 99;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private ClusterManager<MyClusterItem> mClusterManager;

    // actionbar elements
    private Button left;
    private Button right;
    private ImageView logo;

    // bottom menu elements
    private Button currLocation;
    private Button mapType;
    private Button addFacility;
    private Button addToFavorites;
    private Button refresh;
    private LinearLayout bottomLayout;

    // layouts
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheet;
    private LinearLayout gallery;
    private LayoutInflater galleryInflanter;
    private LinearLayout ifNotLogged;

    // bottomSheet elements
    private TextView facilityName;
    private TextView facilityAddress;
    private TextView facilityRatingCount;
    private RatingBar facilityRatingBar;
    private TextView facilityOpenHours;
    private TextView facilityIsOpen;
    private TextView facilitySports;
    private TextView facilityEquipment;

    //======================================

    private List<SportFacility> sportsFacilities = new ArrayList<>();
    private DatabaseHelper databaseHelper;

    private Boolean isBlackTheme = false;
    private Boolean isFirstBottom = true;
    private double latitude;
    private double longitude;

    private String url = StartActivity.user.getUrl() + "/addObjectToFav";
    private String urlGetAll = StartActivity.user.getUrl() + "/getAllObjects";
    private String MESSAGE_SUCCESS = "SUCCESS";
    private String MESSAGE_ERROR1 = "EXIST";

    // dialog
    private Dialog loadDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        Cursor c = databaseHelper.getUser();
        if (c.getCount() > 0) {
            c.moveToFirst();
            latitude = c.getDouble(4);
            longitude = c.getDouble(5);
        } else {
            latitude = 51.758649;
            longitude = 19.453125;
        }

        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomSheet = view.findViewById(R.id.bottom_sheet_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        ifNotLogged = view.findViewById(R.id.layout_if_not_logged);


        // setting gallery in bottomsheet
        gallery = view.findViewById(R.id.gallery);


        // bottom menu
        currLocation = view.findViewById(R.id.location);
        mapType = view.findViewById(R.id.mapType);
        addFacility = view.findViewById(R.id.addFacility);
        addToFavorites = view.findViewById(R.id.addToFavorites);
        refresh = view.findViewById(R.id.refresh);

        addToFavorites.setEnabled(false);
        currLocation.setEnabled(false);


        // actionbar elements
        left = view.findViewById(R.id.left_swipe);
        right = view.findViewById(R.id.right_swipe);
        logo = view.findViewById(R.id.logo);

        // bottomSheet elements
        facilityName = view.findViewById(R.id.facilityName);
        facilityAddress = view.findViewById(R.id.facilityAddress);
        facilityRatingCount = view.findViewById(R.id.ratingsCount);
        facilityRatingBar = view.findViewById(R.id.ratingBar);
        facilityOpenHours = view.findViewById(R.id.facilityOpenHours);
        facilityIsOpen = view.findViewById(R.id.isOpen);
        facilitySports = view.findViewById(R.id.facilitySports);
        facilityEquipment = view.findViewById(R.id.facilityOffer);
        bottomLayout = view.findViewById(R.id.bottomLayout);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                if (i == 4 || isFirstBottom == true) {
                    addToFavorites.setEnabled(false);
                } else if (i != 4 || isFirstBottom != true) {
                    addToFavorites.setEnabled(true);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

                LatLng center = mMap.getCameraPosition().target;

                int bottomPadding = (int) (v * 1100);

                mMap.setPadding(0, 140, 0, bottomPadding);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(center));

            }
        });

        // getting viewPager
        final ViewPager viewPager = getActivity().findViewById(R.id.viewPagerContainer);

        // open options fragment
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0, true);
            }
        });

        // open advancedSearch fragment
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2, true);
            }
        });

        // close bottom sheet
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        //======================= Bottom menu =====================

        //moving camera to current user postition
        currLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
            }
        });

        // changing map type between normala and hybrid type
        mapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBlackTheme) {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.normalmap));
                    mapType.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.iconColor)));
                    isBlackTheme = false;
                } else {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.sharegamemapstyle));
                    mapType.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.turquioselv2)));
                    isBlackTheme = true;
                }


            }
        });

        addFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddFacility.class);
                startActivity(intent);
            }
        });

        loadDialog = new Dialog(getContext());

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadDialog.setContentView(R.layout.load_dialog);
                loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                loadDialog.show();
                getAllSportFacilities();
            }
        });


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setPadding(0, 140, 0, 0);
        LatLng latLng = new LatLng(latitude, longitude);

        Cursor c = databaseHelper.getUser();
        if (c.getCount() > 0) {
            c.moveToFirst();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6f));
        }

        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableLoc();
                mMap.setMyLocationEnabled(true);
            } else {
                currLocation.setEnabled(true);
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }

        mClusterManager = new ClusterManager<>(getContext(), mMap);
        mClusterManager.setAnimation(true);

        sportsFacilities = databaseHelper.getSportFacilities();

        for (int i = 0; i < sportsFacilities.size(); i++) {

            addMarker(sportsFacilities.get(i));
        }

        mMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterClickListener(this);

        mMap.setOnCameraIdleListener(mClusterManager);

        mClusterManager.setRenderer(new OwnIconRendered(getContext(), mMap, mClusterManager));

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

                sportsFacilities = databaseHelper.getSportFacilities();

                mMap.clear();
                mClusterManager.clearItems();

                for (int i = 0; i < sportsFacilities.size(); i++) {

                    addMarker(sportsFacilities.get(i));
                }


                float zoom = mMap.getCameraPosition().zoom;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom - (float)0.001));
                loadDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
                loadDialog.dismiss();
            }
        }
        );

        RequestQueue requestQueue =Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private int boolToInt(String value){
        if(value.equals("true")){
            return 1;
        }else{
            return 0;
        }
    }

    private void addImages(int facilityID) {

        List<FacilityImage> facilityImages = new ArrayList<>();
        facilityImages = databaseHelper.getImages(facilityID);

        gallery.removeAllViews();
        galleryInflanter = LayoutInflater.from(getContext());

        if(facilityImages.size() != 0){
            for (int i = 0; i < facilityImages.size(); i++) {
                View view2 = galleryInflanter.inflate(R.layout.gallery_item, gallery, false);
                LinearLayout image = view2.findViewById(R.id.facilityImage);
                image.setBackground(new BitmapDrawable(getResources(),facilityImages.get(i).getBitmap()));
                gallery.addView(view2);
            }
        }else{
            View view2 = galleryInflanter.inflate(R.layout.gallery_item, gallery, false);
            LinearLayout image = view2.findViewById(R.id.facilityImage);
            image.setBackground(getResources().getDrawable(R.drawable.id1img1));
            gallery.addView(view2);
        }

    }

    private void addMarker(SportFacility sportFacility) {

        String sport = sportFacility.getSports();
        int markerIcon;

        if (sport.equals("soccer")) {
            markerIcon = R.drawable.m_soccer;
        } else if (sport.equals("futsal")) {
            markerIcon = R.drawable.m_futsal;
        } else if (sport.equals("volleyball")) {
            markerIcon = R.drawable.m_volleyball;
        } else if (sport.equals("badminton")) {
            markerIcon = R.drawable.m_badminton;
        } else if (sport.equals("basketball")) {
            markerIcon = R.drawable.m_basketball;
        } else if (sport.equals("tennis")) {
            markerIcon = R.drawable.m_tennis;
        } else if (sport.equals("squash")) {
            markerIcon = R.drawable.m_squash;
        } else if (sport.equals("handball")) {
            markerIcon = R.drawable.m_handball;
        } else if (sport.equals("multi")) {
            markerIcon = R.drawable.m_multi;
        }else{
            markerIcon = R.drawable.m_multi;
        }


        double lat = sportFacility.getLatitude();
        double lng = sportFacility.getLongitude();
        int id = sportFacility.getId();

        MyClusterItem offsetItem = new MyClusterItem(lat, lng, bitmapDescriptorFromVector(getActivity(),markerIcon), id);
        mClusterManager.addItem(offsetItem);

    }

    protected synchronized void enableLoc() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            startIntentSenderForResult(status.getResolution().getIntentSender(), REQUEST_USER_LOCATION_CODE, null, 0, 0, 0, null);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        databaseHelper.updateLocation(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_USER_LOCATION_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                currLocation.setEnabled(true);
            }
            if (resultCode == getActivity().RESULT_CANCELED) {
                currLocation.setEnabled(false);
            }
        }
    }

    public double hourToDouble(String hour) {
        String hours, minutes;
        hours = hour.substring(0, 2);
        minutes = hour.substring(3, 5);

        double dHour;
        double dMinutes;

        dHour = Double.parseDouble(hours);
        dMinutes = Double.parseDouble(minutes);

        return dHour + (0.01)*dMinutes;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onClusterItemClick(MyClusterItem myClusterItem) {
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("HH:mm");
        String currentTime = df.format(calendar.getTime());

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        addToFavorites.setEnabled(true);

        isFirstBottom = false;
        ifNotLogged.setVisibility(View.GONE);
        int id = myClusterItem.getId();
        SportFacility facility = new SportFacility();

        for(int i=0; i < sportsFacilities.size();i++){
            if(sportsFacilities.get(i).getId() == id){
                facility = sportsFacilities.get(i);
            }
        }

        String sports = "<b>" + "Dostępne dyscypliny sportowe: " + "</b><br/>" + "\t" + facility.getSportList().substring(0,facility.getSportList().length()-2);
        String extras;
        if(facility.getExtrasList().length() > 2){
            extras = "<b>" + "Obiekt oferuje: " + "</b><br/>" + "\t" + facility.getExtrasList().substring(0,facility.getExtrasList().length()-2);
        }else{
            extras = facility.getExtrasList();
        }

        facilityName.setText(facility.getName());
        facilityAddress.setText(facility.getCity() + ", ul. " + facility.getStreet() + " " + facility.getLocal_no());
        facilityRatingCount.setText("( " + String.valueOf(0) + " )");
        facilityRatingBar.setVisibility(View.VISIBLE);
        facilityRatingBar.setRating(0);
        //facilityOpenHours.setText(facility.getStart_hour() + "-" + facility.getEnd_hour());

        facilitySports.setText(Html.fromHtml(sports));
        facilityEquipment.setText(Html.fromHtml(extras));

        String openHours = setOpenHour(dayOfWeek,facility);
        double cTime, oTime = .0, eTime = .0;
        cTime = hourToDouble(currentTime);

        if(openHours.equals("")){
            facilityOpenHours.setTextColor(getResources().getColor(R.color.sf_close));
            facilityOpenHours.setText("Obiekt nieczynny");
        }else{
            facilityOpenHours.setText(openHours);
            oTime = hourToDouble(openHours.substring(0,5));
            eTime = hourToDouble(openHours.substring(6,11));
        }

        if (cTime >= oTime && cTime <= eTime) {
            facilityIsOpen.setText(" ( Otwarte )");
            facilityIsOpen.setTextColor(getResources().getColor(R.color.turquioselv2));
        } else {
            facilityIsOpen.setText(" ( Zamknięte )");
            facilityIsOpen.setTextColor(getResources().getColor(R.color.magentalv2));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(facility.getLatitude(), facility.getLongitude()), 13f));
        addImages(facility.getGlobal_id());

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        final SportFacility finalFacility = facility;
        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FacilityView.class);
                intent.putExtra("Facility", finalFacility);
                startActivity(intent);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FacilityView.class);
                intent.putExtra("Facility", finalFacility);
                startActivity(intent);
            }
        });

        final int global_id = facility.getGlobal_id();

        addToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("GLOBAL ID " + global_id);
                addToFavorites(global_id);
            }
        });
        return true;
    }

    private String setOpenHour(int dayOfWeek, SportFacility facility) {

        if (Calendar.MONDAY == dayOfWeek) {
            return facility.getOh_monday();
        } else if (Calendar.TUESDAY == dayOfWeek) {
            return facility.getOh_tuesday();
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            return facility.getOh_wednesday();
        } else if (Calendar.THURSDAY == dayOfWeek) {
            return facility.getOh_thursday();
        } else if (Calendar.FRIDAY == dayOfWeek) {
            return facility.getOh_friday();
        } else if (Calendar.SATURDAY == dayOfWeek) {
            if(!facility.getOh_saturday().equals("00:00-00:00")){
                return facility.getOh_saturday();
            }else{
                return "";
            }
        } else if (Calendar.SUNDAY == dayOfWeek) {
            if(!facility.getOh_sunday().equals("00:00-00:00")){
                return facility.getOh_sunday();
            }else{
                return "";
            }
        }
        return "";
    }


    @Override
    public boolean onClusterClick(Cluster<MyClusterItem> cluster) {
        LatLng latLng = cluster.getPosition();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
        return true;
    }


    private void addToFavorites(int id) {

        JSONObject object = new JSONObject();
        try {
            object.put("sportobjectid", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String res = response.getString("message");

                    if(res.equals(MESSAGE_SUCCESS)){
                        Toast.makeText(getContext(), "Dodano do ulubionych", Toast.LENGTH_SHORT).show();
                    }else if(res.equals(MESSAGE_ERROR1)){
                        Toast.makeText(getContext(), "Ten obiekt już polubiłeś", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
            }
        }
        ){
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
        RequestQueue requestQueue =Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);

    }
}
