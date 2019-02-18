package com.example.patryk.sharegame2;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.patryk.sharegame2.MainFragments.MainActivity;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MakeReservation extends AppCompatActivity {

    //date swipper
    private Button prev_date;
    private Button next_date;
    private TextView date;
    private TextView day_of_week;

    //schedule
    private TextView dayStatus;
    private LinearLayout schedule;
    private LayoutInflater hourInflanter;

    private String facilitySchedule = "000";
    private String startHourS;
    private String endHourS;
    private String[] hours;
    private TextView choosedHours;

    private String choosedStartHour;
    private String choosedEndHour;
    private String selectedDate;
    private int facilityID;

    private String url = StartActivity.user.getUrl() +  "/returnTimetableOfSportObject";
    private String urlMakeReservation = StartActivity.user.getUrl() +  "/ReserveHall";
    private DatabaseHelper databaseHelper;
    private SportFacility sportFacility;

    // buttons

    private LinearLayout reserve;
    private LinearLayout reserveAndPay;

    private double payCost;

    private boolean islogged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_reservation);

        choosedStartHour = "23:59";

        // buttons

        reserve = findViewById(R.id.b_reserve);
        reserveAndPay = findViewById(R.id.b_reserve_and_pay);

        //getting values from intent
        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("DATE");
        facilityID = intent.getIntExtra("ID",1);

        //databaseHelper
        databaseHelper = new DatabaseHelper(this);
        sportFacility = databaseHelper.getSportFacility(facilityID);
        Cursor c = databaseHelper.getUser();
        c.moveToFirst();
        int log = c.getInt(0);

        if(log == 0){
            islogged = false;
        }else{
            islogged = true;
        }

        // date swipper
        prev_date = findViewById(R.id.res_prev_date);
        next_date = findViewById(R.id.res_next_date);
        date = findViewById(R.id.res_day);
        day_of_week = findViewById(R.id.day_of_week);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.valueOf(selectedDate.substring(0,4)),Integer.valueOf(selectedDate.substring(5,7))-1,Integer.valueOf(selectedDate.substring(8,10)));
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        date.setText(selectedDate);

        next_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_YEAR,1);
                selectedDate = df.format(calendar.getTime());
                setHours(calendar.get(Calendar.DAY_OF_WEEK));
                choosedHours.setText("*zaznacz na terminarzu godziny");
                getSchedule();
                date.setText(selectedDate);
            }
        });

        prev_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_YEAR,-1);
                selectedDate = df.format(calendar.getTime());
                setHours(calendar.get(Calendar.DAY_OF_WEEK));
                choosedHours.setText("*zaznacz na terminarzu godziny");
                getSchedule();
                date.setText(selectedDate);
            }
        });

        // schedule
        schedule = findViewById(R.id.res_hours);
        dayStatus = findViewById(R.id.res_dayStatus);
        choosedHours = findViewById(R.id.choosedHours);
        setHours(calendar.get(Calendar.DAY_OF_WEEK));
        getSchedule();

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar current = Calendar.getInstance();
                Date date = current.getTime();

                Calendar selected = Calendar.getInstance();
                selected.set(Integer.valueOf(selectedDate.substring(0,4)),Integer.valueOf(selectedDate.substring(5,7))-1,Integer.valueOf(selectedDate.substring(8,10)),
                        Integer.valueOf(choosedStartHour.substring(0,2)),Integer.valueOf(choosedStartHour.substring(3,5)));
                Date sDate = selected.getTime();

                System.out.println(selectedDate);
                if(!islogged){
                    Toast.makeText(MakeReservation.this, "Musisz się zalogować", Toast.LENGTH_SHORT).show();
                }else if(date.compareTo(sDate) > 0){
                    Toast.makeText(MakeReservation.this, "Nie możesz zarezerwować w ten dzień", Toast.LENGTH_SHORT).show();
                }else{
                    if(!choosedHours.getText().equals("*zaznacz na terminarzu godziny")){
                        makeReservation();
                    }else{
                        Toast.makeText(MakeReservation.this, "Zaznacz na terminarzu godziny", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        reserveAndPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar current = Calendar.getInstance();
                Date date = current.getTime();

                Calendar selected = Calendar.getInstance();
                selected.set(Integer.valueOf(selectedDate.substring(0,4)),Integer.valueOf(selectedDate.substring(5,7))-1,Integer.valueOf(selectedDate.substring(8,10)),
                        Integer.valueOf(choosedStartHour.substring(0,2)),Integer.valueOf(choosedStartHour.substring(3,5)));
                Date sDate = selected.getTime();

                if(!islogged){
                    Toast.makeText(MakeReservation.this, "Musisz się zalogować", Toast.LENGTH_SHORT).show();
                }else if(date.compareTo(sDate) > 0){
                    Toast.makeText(MakeReservation.this, "Nie możesz zarezerwować w ten dzień", Toast.LENGTH_SHORT).show();
                }else{
                        if(!choosedHours.getText().equals("*zaznacz na terminarzu godziny")){
                            //makeReservation("true");
                            Intent intent = new Intent(MakeReservation.this, PaymentActivity.class);
                            intent.putExtra("price",payCost);
                            intent.putExtra("id",facilityID);
                            intent.putExtra("start", selectedDate + " " + choosedStartHour + ":00.0");
                            intent.putExtra("end",selectedDate + " " + choosedEndHour + ":00.0");
                            intent.putExtra("date",selectedDate);

                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(MakeReservation.this, "Zaznacz na terminarzu godziny", Toast.LENGTH_SHORT).show();
                        }
                }
            }
        });

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

    public void setSchedule(){

        double startHour, endHour;

        startHour = hourToDouble(startHourS);
        endHour = hourToDouble(endHourS);


        int hoursCount = facilitySchedule.length() + 1;

        hours = getHoursTab(hoursCount, startHour);

        hourInflanter = LayoutInflater.from(this);
        schedule.removeAllViews();


        if(endHour - startHour == 0){
            dayStatus.setText("Obiekt w ten dzień jest nieczynny");
            schedule.setVisibility(View.GONE);
        }else{
            schedule.setVisibility(View.VISIBLE);
            dayStatus.setText("");
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
            final TextView h_1 = v.findViewById(R.id.h_1);

            if(i%2==0){
                hour.setText(hoursTab[i]);
            }else{
                hour.setText("");
            }


            if(i != hoursCount - 1){
                if(facilitySchedule.charAt(i) == '1'){
                    h_1.setBackgroundColor(getResources().getColor(R.color.turquiose));
                }else if(facilitySchedule.charAt(i) == '2'){
                    h_1.setBackgroundColor(getResources().getColor(R.color.magenta));
                }else if(facilitySchedule.charAt(i) == '4'){
                    h_1.setBackgroundColor(getResources().getColor(R.color.turquioselv2));
                }

                final int pos = i;

                h_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder schedule_ = new StringBuilder(facilitySchedule);

                        if(facilitySchedule.charAt(pos) == '0'){
                            schedule_.setCharAt(pos,'2');
                            facilitySchedule = schedule_.toString();
                            h_1.setBackgroundColor(getResources().getColor(R.color.magenta));
                            lockSchedule(pos);
                            checkSchedule(pos);
                        }

                        else if(facilitySchedule.charAt(pos) == '2'){
                            schedule_.setCharAt(pos,'0');
                            facilitySchedule = schedule_.toString();
                            if(!unlockSchedule()){
                                checkScheduleAtUnclick(pos);
                            }
                            h_1.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                        }
                        setChoosedHours();
                        // SCHEDULE 0 - free, 1 - reserved, 2-checked, 3- locked
                        System.out.println(facilitySchedule + "\t " + pos);
                    }
                });
            }

            schedule.addView(v);
        }
    }

    private void setChoosedHours(){
        int counter = 0;
        int first = 0;
        int last = 0;
        for(int i = 0; i < facilitySchedule.length();i++){

            if(facilitySchedule.charAt(i) == '2'){
                counter++;
                first = i;
                last = i;
                break;
            }
        }

        for(int i = first; i < facilitySchedule.length();i++){

            if(facilitySchedule.charAt(i) == '2'){
                counter++;
                last = i;
            }
        }

        if(counter == 0){
            reserveAndPay.setClickable(false);
            reserve.setClickable(false);
            choosedHours.setText("*zaznacz na terminarzu godziny");
        }else{
            if(first == last){
                choosedStartHour = hours[first];
                choosedEndHour = hours[first+1];
            }else{
                choosedStartHour = hours[first];
                choosedEndHour = hours[last + 1];
            }

            reserveAndPay.setClickable(true);
            reserve.setClickable(true);

            payCost = (hourToDouble(choosedEndHour)-hourToDouble(choosedStartHour)) * sportFacility.getRental_price();
            choosedHours.setText("Wybrane godziny:\n" + choosedStartHour + " - " + choosedEndHour+"\n"+"Cena:\n"+String.format("%.2f",payCost) + " zł");
        }
    }

    private boolean unlockSchedule(){
        int counter = 0;
        for(int i = 0; i < facilitySchedule.length();i++){
            if(facilitySchedule.charAt(i) == '2'){
                counter++;
            }
        }

        StringBuilder schedule_ = new StringBuilder(facilitySchedule);

        if(counter == 0){
            for(int i = 0; i < facilitySchedule.length();i++){
                if(facilitySchedule.charAt(i) == '3'){
                    schedule_.setCharAt(i,'0');
                }
            }

            facilitySchedule = schedule_.toString();
            return true;
        }

        return false;
    }

    private void checkScheduleAtUnclick(int pos){
        int first = 0;
        int last = 0;
        for(int i = 0; i < facilitySchedule.length();i++){

            if(facilitySchedule.charAt(i) == '2'){
                first = i;
                last = i;
                break;
            }
        }

        for(int i = first; i < facilitySchedule.length();i++){

            if(facilitySchedule.charAt(i) == '2'){
                last = i;
            }
        }


        if(pos != first && pos != last && first!=last){
            StringBuilder schedule_ = new StringBuilder(facilitySchedule);

            if(pos - first >= last - pos){
                for(int i = pos; i<=last;i++){
                    schedule_.setCharAt(i,'0');
                }
            }else if(pos - first < last - pos){
                for(int i = first; i<=pos;i++){
                    schedule_.setCharAt(i,'0');
                }
            }

            facilitySchedule = schedule_.toString();
            setSchedule();
        }
    }

    private void checkSchedule(int pos){
        int first = 0;
        boolean moreThanOne = false;
        for(int i = 0; i < facilitySchedule.length();i++){

            if(facilitySchedule.charAt(i) == '2' && i != pos){
                first = i;
                moreThanOne = true;
                break;
            }
        }
        StringBuilder schedule_ = new StringBuilder(facilitySchedule);

        if(moreThanOne){
            if(pos > first){
                for(int i = first; i <= pos; i++){
                    schedule_.setCharAt(i,'2');
                }
            }else{
                for(int i = pos; i <= first; i++){
                    schedule_.setCharAt(i,'2');
                }
            }

            facilitySchedule = schedule_.toString();
            setSchedule();
        }


    }

    private void lockSchedule(int pos){

        for(int i = pos; i < facilitySchedule.length(); i++){

        }
        int i = pos;
        int j = pos;

        int lastPos = facilitySchedule.length()-1;
        int firstPos = 0;

        while(facilitySchedule.charAt(i) != '1' && facilitySchedule.charAt(i) != '4' && i < facilitySchedule.length()){
            lastPos = i;
            i++;
            if(i == facilitySchedule.length()){
                break;
            }
        }

        while(facilitySchedule.charAt(j) != '1' && facilitySchedule.charAt(j) != '4' && j > 0){
            j--;
            firstPos = j;
            if(j == 0){
                break;
            }
        }

        StringBuilder schedule_ = new StringBuilder(facilitySchedule);
        System.out.println(firstPos + " - " + lastPos);

        for(int k = 0; k < firstPos; k++){
            if(facilitySchedule.charAt(k) != '1' && facilitySchedule.charAt(k) != '4'){
                schedule_.setCharAt(k,'3');
            }
        }

        if(lastPos + 1 != facilitySchedule.length() -1 ){
            for(int k = lastPos + 1; k <= facilitySchedule.length()-1; k++){
                if(facilitySchedule.charAt(k) != '1' && facilitySchedule.charAt(k) != '4'){
                    schedule_.setCharAt(k,'3');
                }
            }
        }

        facilitySchedule = schedule_.toString();
    }

    // VOLLEY METHODS

    private void getSchedule() {

        JSONObject schedule = new JSONObject();
        try {
            schedule.put("sportobjectid", sportFacility.getGlobal_id());
            schedule.put("start",selectedDate + " 00:00:00.0");
            schedule.put("end",selectedDate + " 23:59:00.0");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, schedule, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    facilitySchedule = response.getString("schedule");
                    setSchedule();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MakeReservation.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue requestQueue =Volley.newRequestQueue(MakeReservation.this);
        requestQueue.add(jsonObjectRequest);
    }

    private void makeReservation() {

        JSONObject reservation = new JSONObject();
        try {
            reservation.put("sportobjectid",facilityID);
            reservation.put("start",selectedDate + " " + choosedStartHour + ":00.0");
            reservation.put("end",selectedDate + " " + choosedEndHour + ":00.0");
            reservation.put("statusOfPayment",false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, urlMakeReservation, reservation, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String res = response.getString("status");

                    if(res.equals("SPORTOBJECT_IS_NOT_AVALAVILE")){
                        Toast.makeText(MakeReservation.this, "Nie można zarezerować w tych godzinach", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("REQUEST_FAILED")){
                        Toast.makeText(MakeReservation.this, "Nie można zarezerować w tych godzinach", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("RESERVATION_CONFRIMED")){
                        Toast.makeText(MakeReservation.this, "Zarezerwowano", Toast.LENGTH_SHORT).show();
                        Intent facilityView = new Intent(MakeReservation.this,FacilityView.class);
                        facilityView.putExtra("Facility",sportFacility);
                        startActivity(facilityView);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MakeReservation.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
            }
        }
        ){
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
        RequestQueue requestQueue =Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    // SCHEDULE METHODS

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

    private String[] getHoursTab(int hoursCount, double start) {
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
}
