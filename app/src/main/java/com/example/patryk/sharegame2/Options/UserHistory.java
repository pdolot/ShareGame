package com.example.patryk.sharegame2.Options;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
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
import com.example.patryk.sharegame2.Adapters.HistoryRVAdapter;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.Objects.UserRent;
import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;
import com.example.patryk.sharegame2.StartActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHistory extends AppCompatActivity {

    private RecyclerView rv_rentsHistory;
    private HistoryRVAdapter historyAdapter;
    private List<UserRent> userRentsHistory;
    private List<Integer> selected;

    private Button delete;

    private String url = StartActivity.user.getUrl() +  "/getHistoryReserv";
    private String urlRemove = StartActivity.user.getUrl() +  "/DeleteReservation";
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        databaseHelper = new DatabaseHelper(this);
        selected = new ArrayList<>();

        // Buttons
        delete = findViewById(R.id.uh_delete);
        delete.setEnabled(false);

        initRentsHistory();
        rv_rentsHistory = findViewById(R.id.rv_userHistory);

        getReservations();

    }

    public void switchButtons(){
        if(selected.size() == 0){
            delete.setEnabled(false);
        }else{
            delete.setEnabled(true);
        }
    }

    private void initRentsHistory() {
        userRentsHistory = new ArrayList<>();

    }

    private void getReservations() {
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try{
                    for(int i = 0 ; i<response.length();i++){
                        JSONObject jsonObject = response.getJSONObject(i);
                        int id = jsonObject.getInt("sportobjectid");
                        String start = jsonObject.getString("startrent");
                        String end = jsonObject.getString("exprrent");
                        double rentalPrice =jsonObject.getDouble("cost");
                        boolean statusOfPayment = jsonObject.getBoolean("statusOfPaymnet");

                        String date = start.substring(0,10);
                        start = start.substring(11,16);
                        end = end.substring(11,16);

                        SportFacility sportFacility = databaseHelper.getSportFacility(id);
                        UserRent userRent= new UserRent(sportFacility,date,start,end,rentalPrice,statusOfPayment);

                        userRentsHistory.add(userRent);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                createAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserHistory.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue =Volley.newRequestQueue(UserHistory.this);
        requestQueue.add(jsonArrayRequest);

    }

    private void createAdapter() {

        historyAdapter = new HistoryRVAdapter(this, userRentsHistory);
        rv_rentsHistory.setAdapter(historyAdapter);
        rv_rentsHistory.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        historyAdapter.setOnItemClickListener(new HistoryRVAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                for (int i = 0; i < selected.size(); i++) {
                    if (selected.get(i) == position) {
                        selected.remove(i);
                        break;
                    }
                }
                switchButtons();
            }

            @Override
            public void onItemLongClick(int position, View v) {
                for (int i = 0; i < selected.size(); i++) {

                    if (selected.get(i) == position) {
                        selected.remove(i);
                    }
                }
                selected.add(position);
                switchButtons();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!selected.isEmpty()) {
                    Collections.sort(selected, Collections.<Integer>reverseOrder());
                    for (int i = 0; i < selected.size(); i++) {


                        int index = selected.get(i);
                        removeReservation(userRentsHistory.get(index).getSportFacility().getGlobal_id(),
                                userRentsHistory.get(index).getTimeStart(),
                                userRentsHistory.get(index).getTimeEnd(),
                                userRentsHistory.get(index).getDate());
                        historyAdapter.removeItem(selected.get(i));
                    }
                }
                selected.clear();

                // reset Adapter, without it, adapter select another items after remove
                rv_rentsHistory.setAdapter(null);
                rv_rentsHistory.setLayoutManager(null);
                rv_rentsHistory.getRecycledViewPool().clear();
                rv_rentsHistory.swapAdapter(historyAdapter, false);
                rv_rentsHistory.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                //==============
                switchButtons();
            }
        });
    }

    private void removeReservation(int facilityid, String startHour, String endHour, String date) {
        JSONObject reservation = new JSONObject();

        try {
            reservation.put("sportobjectid",facilityid);
            reservation.put("start",date + " " + startHour + ":00.0");
            reservation.put("end",date + " " + endHour + ":00.0");
        } catch (JSONException e) {
            e.printStackTrace();
        }


         JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlRemove,reservation, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    String res = response.getString("message");

                    if(res.equals("Delete_Success")){
                        Toast.makeText(UserHistory.this, "Rezerwacja usunięta", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(UserHistory.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                createAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserHistory.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue =Volley.newRequestQueue(UserHistory.this);
        requestQueue.add(jsonObjectRequest);
    }
}
