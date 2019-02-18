package com.example.patryk.sharegame2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RememberPasswordActivity extends AppCompatActivity {

    private EditText email;
    private LinearLayout rememberButton;
    private TextView mess;

    private String url = StartActivity.user.getUrl() +  "/rememberPassword";
    private String activityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_password);

        Intent intent = getIntent();
        activityName = intent.getStringExtra("ActivityName");

        email = findViewById(R.id.rememberPasswordEmail);
        rememberButton = findViewById(R.id.rememberPasswordButton);
        mess = findViewById(R.id.rememberMess);

        rememberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rememberPassword();

            }
        });

    }

    private void rememberPassword() {
        JSONObject userEmail = new JSONObject();
        try {
            userEmail.put("email",email.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, userEmail, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String res = response.getString("message");
                    mess.setVisibility(View.VISIBLE);
                    if(res.equals("MAIL_SEND")){

                        mess.setText("Na wskazany adres email zostało wysłane nowe wygenerowane hasło");
                    }else{
                        mess.setText("Coś poszło nie tak");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent login = new Intent(RememberPasswordActivity.this, LoginActivity.class);
                        login.putExtra("ActivityName",activityName);
                        startActivity(login);
                        finish();
                    }
                },2500);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RememberPasswordActivity.this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
            }
        }
        );

        RequestQueue requestQueue =Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }
}
