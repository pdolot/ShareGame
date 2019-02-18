package com.example.patryk.sharegame2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.patryk.sharegame2.MainFragments.MainActivity;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements TextWatcher{

    private EditText userName;
    private EditText userPassword;
    private LinearLayout loginButton;
    private TextView signUp;
    private TextView rememberPassword;
    private TextView mess;

    private String url = StartActivity.user.getUrl() + "/login";
    private String USER_MESSAGE_SUCCESS = "SUCCESS";
    private String USER_MESSAGE_ERROR1 = "WRONG_PASSWORD_OR_USERNAME";
    private String USER_MESSAGE_ERROR2 = "SERVER_ERROR";

    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        final String activityName = intent.getStringExtra("ActivityName");

        userName = findViewById(R.id.loginUserName);
        userPassword = findViewById(R.id.loginUserPassword);
        loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.login_signIn);
        rememberPassword = findViewById(R.id.login_rememberPassword);
        mess = findViewById(R.id.errorMess);

        userName.addTextChangedListener(this);
        userPassword.addTextChangedListener(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(StartActivity.user.getOffLine()){

                    Cursor c = databaseHelper.getUser();
                    if(c.getCount() == 0){
                        databaseHelper.addUser(1, 1,  userName.getText().toString(), userPassword.getText().toString(),51.758649, 19.453125);
                    }else{
                        databaseHelper.logIn(1, userName.getText().toString(), userPassword.getText().toString());
                    }

                    if(activityName.equals("Add")){
                        Intent add = new Intent(LoginActivity.this, AddFacility.class);
                        startActivity(add);
                        finish();
                    }else if(activityName.equals("Options")){
                        Intent options = new Intent(LoginActivity.this, MainActivity.class);
                        options.putExtra("PageNo",0);
                        startActivity(options);
                        finish();
                    }
                }else{
                    Login(activityName);
                }

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(LoginActivity.this, SignUpActivity.class);
                login.putExtra("ActivityName",activityName);
                startActivity(login);
                finish();
            }
        });

        rememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rememberPass = new Intent(LoginActivity.this, RememberPasswordActivity.class);
                rememberPass.putExtra("ActivityName",activityName);
                startActivity(rememberPass);
                finish();
            }
        });
    }

    private void Login(final String activityName) {

        JSONObject login = new JSONObject();
        try {
            login.put("username", userName.getText().toString());
            login.put("password", userPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.POST, url, login, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String res = response.getString("message");
                    mess.setVisibility(View.VISIBLE);

                    if(res.equals(USER_MESSAGE_SUCCESS)){
                        mess.setTextColor(getResources().getColor(R.color.turquioselv2));
                        mess.setText("Zalogowano pomyślnie");

                        int id = response.getInt("userId");

                        Cursor c = databaseHelper.getUser();

                        if(c.getCount() == 0){
                            databaseHelper.addUser(1, id,  userName.getText().toString(), userPassword.getText().toString(),51.758649, 19.453125);
                        }else{
                            databaseHelper.logIn(id, userName.getText().toString(), userPassword.getText().toString());
                        }

                        StartActivity.user.setLogged(true);
                        StartActivity.user.setUserName(userName.getText().toString());

                        if(activityName.equals("Add")){
                            Intent add = new Intent(LoginActivity.this, AddFacility.class);
                            startActivity(add);
                            finish();
                        }else if(activityName.equals("Options")){
                            Intent options = new Intent(LoginActivity.this, MainActivity.class);
                            options.putExtra("PageNo",0);
                            startActivity(options);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mess.setVisibility(View.VISIBLE);
                mess.setText("Nieprawidłowa nazwa użytkownika lub hasło");
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String credentials =  userName.getText().toString() + ":" + userPassword.getText().toString();
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };
        RequestQueue requestQueue =Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mess.setVisibility(View.GONE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
