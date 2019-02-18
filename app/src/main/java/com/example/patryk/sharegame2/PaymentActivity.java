package com.example.patryk.sharegame2;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
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
import com.example.patryk.sharegame2.Adapters.MaskWatcher;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private LinearLayout pay;
    private EditText cardNumber;
    private EditText cardValidDate;
    private EditText cardCVC;

    private TextView resDate;
    private TextView resHours;
    private TextView resPrice;

    private String startHour;
    private String endHour;
    private int facilityid;
    private double price;
    private String date;

    private Stripe stripe;

    private int cardMonth;
    private int cardYear;
    private String cardNo;
    private String cvc;

    private String urlMakeReservation = StartActivity.user.getUrl() +  "/ReserveHall";
    private String urlPayment = StartActivity.user.getUrl() +  "/charge";

    private DatabaseHelper databaseHelper;
    //private TokenCallback tokenCallback;
    private String tokenid;

    private SportFacility sportFacility;
    private int reservationId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        databaseHelper = new DatabaseHelper(this);

        stripe = new Stripe(this,"pk_test_LqwS35HxVYhAuIy28LXE7qv5");

        resDate = findViewById(R.id.reservationDate);
        resHours = findViewById(R.id.reservationHours);
        resPrice = findViewById(R.id.reservationPrice);


        cardNumber = findViewById(R.id.creditCard);
        cardValidDate = findViewById(R.id.creditValidDate);
        cardCVC = findViewById(R.id.CVC);
        pay = findViewById(R.id.pay);

        Intent intent = getIntent();
        startHour = intent.getStringExtra("start");
        endHour = intent.getStringExtra("end");
        facilityid = intent.getIntExtra("id",1);
        price = intent.getDoubleExtra("price",0.0);
        date = intent.getStringExtra("date");

        resDate.setText(date);
        resHours.setText(startHour.substring(11,16) + "-" + endHour.substring(11,16));
        resPrice.setText(String.format("%.2f",price) + " zł");

        sportFacility = databaseHelper.getSportFacility(facilityid);

        cardNumber.addTextChangedListener(new MaskWatcher("#### #### #### ####"));
        cardValidDate.addTextChangedListener(new MaskWatcher("##/##"));



        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCardValid()){
                    Toast.makeText(PaymentActivity.this, "Błędny numer karty", Toast.LENGTH_SHORT).show();
                }else if(!isCardValidDate()){
                    Toast.makeText(PaymentActivity.this, "Nieprawidłowa data karty", Toast.LENGTH_SHORT).show();
                }else if(!isValidCVC()){
                    Toast.makeText(PaymentActivity.this, "Nieprawidłowy kod CVC", Toast.LENGTH_SHORT).show();
                }else{
                    cardNo = cardNumber.getText().toString();
                    cardNo = cardNo.replaceAll("\\s","");
                    generateCardDate();
                    cvc = cardCVC.getText().toString();

                    //stripe.createToke
                    //token = tokenCallback.;

                    Card card = new Card(cardNo,cardMonth,cardYear,cvc);

                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your own web service
                                    tokenid = token.getId();
                                    //System.out.println("TOKEN : " + tokenid);
                                    makeReservation();
                                }
                                public void onError(Exception error) {
                                    System.out.println("ERROR");
                                    System.out.println(error.toString());
                                    Toast.makeText(PaymentActivity.this, "Token nie został wygenerowany", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );


                }
            }
        });
    }

    public boolean isCardValid(){
        String cardNo = cardNumber.getText().toString();
        cardNo = cardNo.replaceAll("\\s","");
        System.out.println("NUMER KARTY " + cardNo + " --- " + cardNo.length());
        if(cardNo.length() == 16){
            return true;
        }

        return false;
    }

    public boolean isCardValidDate(){
        String m = cardValidDate.getText().toString().substring(0,2);
        String y = "20" + cardValidDate.getText().toString().substring(3,5);
        int im = Integer.valueOf(m);
        int iy = Integer.valueOf(y);

        if(im > 0 && im <=12 && iy > 1999 && iy < 3000){
            return true;
        }
        return false;
    }

    public boolean isValidCVC(){
        if(cardCVC.getText().toString().length() == 3){
            return true;
        }else{
            return false;
        }
    }

    private void generateCardDate(){
        String m = cardValidDate.getText().toString().substring(0,2);
        String y = "20" + cardValidDate.getText().toString().substring(3,5);
        cardMonth = Integer.valueOf(m);
        cardYear = Integer.valueOf(y);
    }


    private void makeReservation() {

        JSONObject reservation = new JSONObject();
        try {
            reservation.put("sportobjectid",facilityid);
            reservation.put("start",startHour);
            reservation.put("end",endHour);
            reservation.put("statusOfPayment",true);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlMakeReservation,reservation, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String res = response.getString("status");
                    reservationId = response.getInt("payment_id");

                    if(res.equals("SPORTOBJECT_IS_NOT_AVALAVILE")){
                        Toast.makeText(PaymentActivity.this, "Nie można zarezerować w tych godzinach", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("REQUEST_FAILED")){
                        Toast.makeText(PaymentActivity.this, "Nie można zarezerować w tych godzinach", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("RESERVATION_CONFRIMED") || res.equals("lets_pay")){
                        pay();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
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

    private void pay() {
        int amount = (int) (2 * price);

        JSONObject pay = new JSONObject();
        try {
            pay.put("stripeToken",tokenid);
            pay.put("price",amount);
            pay.put("stripeEmail","admin@admin.pl");
            pay.put("id",reservationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, urlPayment,pay, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String res = response.getString("status");

                    if(res.equals("succeeded")){
                        Toast.makeText(PaymentActivity.this, "Zarezerwowano", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(PaymentActivity.this, "Spróbuj ponownie później", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent facilityView = new Intent(PaymentActivity.this,FacilityView.class);
                facilityView.putExtra("Facility",sportFacility);
                startActivity(facilityView);
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PaymentActivity.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue requestQueue =Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }
}
