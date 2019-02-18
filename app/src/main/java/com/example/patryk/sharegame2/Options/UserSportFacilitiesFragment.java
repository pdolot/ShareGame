package com.example.patryk.sharegame2.Options;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.patryk.sharegame2.Adapters.FacilityRVAdapter;
import com.example.patryk.sharegame2.Adapters.RecyclerViewAdapter;
import com.example.patryk.sharegame2.AddFacility;
import com.example.patryk.sharegame2.EditFacility;
import com.example.patryk.sharegame2.FacilityView;
import com.example.patryk.sharegame2.MainFragments.MainActivity;
import com.example.patryk.sharegame2.MakeReservation;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.Objects.SportItem;
import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;
import com.example.patryk.sharegame2.StartActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserSportFacilitiesFragment extends Fragment {

    private RecyclerView rv_facilities;
    private FacilityRVAdapter facilitiesAdapter;
    private List<SportFacility> sportFacilities;
    private List<Integer> selected;

    private Button add;
    private Button edit;
    private Button delete;

    public UserSportFacilitiesFragment() {
        // Required empty public constructor
    }

    private DatabaseHelper databaseHelper;

    private String url = StartActivity.user.getUrl() +  "/deleteObject";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_sport_facilities, container, false);

        databaseHelper = new DatabaseHelper(getContext());

        selected = new ArrayList<>();

        // Buttons
        add = view.findViewById(R.id.fv_add);
        edit = view.findViewById(R.id.fv_edit);
        edit.setEnabled(false);
        delete = view.findViewById(R.id.fv_delete);
        delete.setEnabled(false);

        initFacilities();

        rv_facilities = view.findViewById(R.id.rv_userFacilities);
        facilitiesAdapter = new FacilityRVAdapter(getContext(),sportFacilities);
        rv_facilities.setAdapter(facilitiesAdapter);
        rv_facilities.setLayoutManager(new LinearLayoutManager(getContext()));

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
                    Intent facilityView = new Intent(getContext(),FacilityView.class);
                    facilityView.putExtra("Facility",sportFacilities.get(position));
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

                        int index = selected.get(i);
                        databaseHelper.removeSportFacility(sportFacilities.get(index).getGlobal_id());
                        databaseHelper.removeImages(sportFacilities.get(index).getGlobal_id());
                        removeObject(sportFacilities.get(index).getGlobal_id());
                        facilitiesAdapter.removeItem(index);

                    }
                }
                selected.clear();

                // reset Adapter, without it, adapter select another items after remove
                rv_facilities.setAdapter(null);
                rv_facilities.setLayoutManager(null);
                rv_facilities.getRecycledViewPool().clear();
                rv_facilities.swapAdapter(facilitiesAdapter, false);
                rv_facilities.setLayoutManager(new LinearLayoutManager(getContext()));

                //==============
                switchButtons();
                //Toast.makeText(getContext(), String.valueOf(sportFacilities.size()), Toast.LENGTH_SHORT).show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddFacility.class);
                startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = selected.get(0);
                Intent intent = new Intent(getContext(), EditFacility.class);
                intent.putExtra("Facility",sportFacilities.get(index));
                startActivity(intent);
            }
        });

        return view;
    }

    public void switchButtons(){
        if(selected.size() == 0){
            edit.setEnabled(false);
            delete.setEnabled(false);
        }else if(selected.size() == 1){
            edit.setEnabled(true);
            delete.setEnabled(true);
        }else{
            edit.setEnabled(false);
            delete.setEnabled(true);
        }
    }

    private void initFacilities(){
        sportFacilities = new ArrayList<>();

        Cursor cursor = databaseHelper.getUser();
        cursor.moveToFirst();
        sportFacilities = databaseHelper.getUserSportFacilities(cursor.getInt(1));

    }

    private void removeObject(int facilityID) {

        JSONObject facility = new JSONObject();
        try {
            facility.put("sportobjectid",facilityID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,facility, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String res = response.getString("status");

                    if(res.equals("DELETED")){
                        Toast.makeText(getContext(), "Obiekt usunięty", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("Error")){
                        Toast.makeText(getContext(), "Coś poszło nie tak", Toast.LENGTH_SHORT).show();


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
