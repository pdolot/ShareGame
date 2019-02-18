package com.example.patryk.sharegame2.Options;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.patryk.sharegame2.Adapters.FacilityRVAdapter;
import com.example.patryk.sharegame2.Adapters.RentRVAdapter;
import com.example.patryk.sharegame2.FacilityView;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.Objects.UserRent;
import com.example.patryk.sharegame2.PaymentActivity;
import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;
import com.example.patryk.sharegame2.StartActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserRentalSportFacilitiesFragment extends Fragment {

    private RecyclerView rv_rents;
    private RentRVAdapter rentAdapter;
    private List<UserRent> userRents;
    private List<Integer> selected;

    private Button add;
    private Button edit;
    private Button delete;


    public UserRentalSportFacilitiesFragment() {
        // Required empty public constructor
    }

    private String url = StartActivity.user.getUrl() +  "/getActiveReserv";
    private String urlRemove = StartActivity.user.getUrl() +  "/DeleteReservation";
    private DatabaseHelper databaseHelper;

    private Dialog previewDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_rental_sport_facilities, container, false);
        databaseHelper = new DatabaseHelper(getContext());
        selected = new ArrayList<>();

        previewDialog = new Dialog(getContext());

        // Buttons
        add = view.findViewById(R.id.rfv_add);
        edit = view.findViewById(R.id.rfv_edit);
        edit.setEnabled(false);
        delete = view.findViewById(R.id.rfv_delete);
        delete.setEnabled(false);

        initRents();
        rv_rents = view.findViewById(R.id.rv_userRentalFacilities);
        getReservations();

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

    private void initRents() {
        userRents = new ArrayList<>();
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

                        userRents.add(userRent);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                createAdapter();
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
        requestQueue.add(jsonArrayRequest);

    }

    private void createAdapter() {

        rentAdapter = new RentRVAdapter(getContext(), userRents);
        rv_rents.setAdapter(rentAdapter);
        rv_rents.setLayoutManager(new LinearLayoutManager(getContext()));

        rentAdapter.setOnItemClickListener(new RentRVAdapter.ClickListener() {
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
                    previewDialog.setContentView(R.layout.reservation_preview);
                    Button close = previewDialog.findViewById(R.id.close_dialog);
                    TextView fName = previewDialog.findViewById(R.id.res_prev_facilityName);
                    TextView address = previewDialog.findViewById(R.id.res_prev_facilityAddress);
                    TextView date = previewDialog.findViewById(R.id.res_prev_ddate);
                    TextView hours = previewDialog.findViewById(R.id.res_prev_hours);
                    TextView price = previewDialog.findViewById(R.id.res_prev_price);
                    RelativeLayout pay = previewDialog.findViewById(R.id.res_view_pay);
                    RelativeLayout view = previewDialog.findViewById(R.id.res_view);

                    final UserRent ur = userRents.get(position);
                    if(ur.isStatusOfPayment()){
                        pay.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                    }else{
                        pay.setVisibility(View.VISIBLE);
                        view.setVisibility(View.GONE);
                    }

                    fName.setText(ur.getSportFacility().getName());
                    address.setText("ul. " + ur.getSportFacility().getStreet() + " " + ur.getSportFacility().getLocal_no() + ", " + ur.getSportFacility().getCity());
                    date.setText(ur.getDate());
                    hours.setText(ur.getTimeStart() + "-" + ur.getTimeEnd());
                    price.setText(String.format("%.2f",ur.getAmount()) + " zł");
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            previewDialog.dismiss();
                        }
                    });

                    pay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            previewDialog.dismiss();
                            Intent intent = new Intent(getContext(),PaymentActivity.class);
                            intent.putExtra("price",ur.getAmount());
                            intent.putExtra("id",ur.getSportFacility().getGlobal_id());
                            intent.putExtra("start", ur.getDate() + " " + ur.getTimeStart() + ":00.0");
                            intent.putExtra("end",ur.getDate() + " " + ur.getTimeEnd() + ":00.0");
                            intent.putExtra("date",ur.getDate());

                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                    previewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    previewDialog.show();
                }

            }

            @Override
            public void onItemLongClick(final int position, View v) {
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
                        removeReservation(userRents.get(index).getSportFacility().getGlobal_id(),
                                userRents.get(index).getTimeStart(),
                                userRents.get(index).getTimeEnd(),
                                userRents.get(index).getDate());
                        rentAdapter.removeItem(selected.get(i));
                    }
                }
                selected.clear();

                // reset Adapter, without it, adapter select another items after remove
                rv_rents.setAdapter(null);
                rv_rents.setLayoutManager(null);
                rv_rents.getRecycledViewPool().clear();
                rv_rents.swapAdapter(rentAdapter, false);
                rv_rents.setLayoutManager(new LinearLayoutManager(getContext()));

                //==============
                switchButtons();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = "";
                for (int i = 0; i < selected.size(); i++) {
                    tmp += String.valueOf(selected.get(i)) + ",";
                }

                Toast.makeText(getContext(), tmp, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Rezerwacja usunięta", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                createAdapter();
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
