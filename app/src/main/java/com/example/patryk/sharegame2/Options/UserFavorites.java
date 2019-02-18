package com.example.patryk.sharegame2.Options;

import android.content.Intent;
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
import com.example.patryk.sharegame2.Adapters.FacilityRVAdapter;
import com.example.patryk.sharegame2.AddFacility;
import com.example.patryk.sharegame2.FacilityView;
import com.example.patryk.sharegame2.Objects.SportFacility;
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

public class UserFavorites extends AppCompatActivity {

    private RecyclerView rv_favorites;
    private FacilityRVAdapter facilitiesAdapter;
    private List<SportFacility> sportFavorites;
    private List<Integer> selected;

    private Button delete;

    private String url = StartActivity.user.getUrl() + "/getFavorites";
    private String urlRemove = StartActivity.user.getUrl() + "/deleteFromFavorites";

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorites);

        databaseHelper = new DatabaseHelper(this);
        selected = new ArrayList<>();

        // Buttons
        delete = findViewById(R.id.uf_delete);
        delete.setEnabled(false);
        sportFavorites = new ArrayList<>();

        getFavorites();
        rv_favorites = findViewById(R.id.rv_userFavorites);
    }

    public void createAdapter(){

        facilitiesAdapter = new FacilityRVAdapter(this,sportFavorites);
        rv_favorites.setAdapter(facilitiesAdapter);
        rv_favorites.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        facilitiesAdapter.setOnItemClickListener(new FacilityRVAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                boolean isSelected = false;
                for (int i = 0; i < selected.size(); i++) {
                    if (selected.get(i) == position) {
                        isSelected = true;
                        selected.remove(i);
                        break;
                    }
                }
                switchButtons();

                if(isSelected == false){
                    Intent facilityView = new Intent(UserFavorites.this,FacilityView.class);
                    facilityView.putExtra("Facility",sportFavorites.get(position));
                    startActivity(facilityView);
                }
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
                        removeFromFavorites(sportFavorites.get(selected.get(i)).getGlobal_id());
                        facilitiesAdapter.removeItem(selected.get(i));
                    }
                }
                selected.clear();

                // reset Adapter, without it, adapter select another items after remove
                rv_favorites.setAdapter(null);
                rv_favorites.setLayoutManager(null);
                rv_favorites.getRecycledViewPool().clear();
                rv_favorites.swapAdapter(facilitiesAdapter, false);
                rv_favorites.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                //==============
                switchButtons();
            }
        });
    }

    public void switchButtons(){
        if(selected.size() == 0){
            delete.setEnabled(false);
        }else{
            delete.setEnabled(true);
        }
    }

    private void getFavorites() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try{
                    for(int i=0;i<response.length();i++){
                        JSONObject sportFacilities = response.getJSONObject(i);
                        int id = sportFacilities.getInt("id");

                        SportFacility sf = databaseHelper.getSportFacility(id);
                        sportFavorites.add(sf);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                createAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserFavorites.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
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

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<>();
                return params;
            }

        };
        RequestQueue requestQueue =Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void removeFromFavorites(int id) {

        JSONObject fav = new JSONObject();
        try {
            fav.put("sportobjectid",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlRemove,fav, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserFavorites.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue =Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
