package com.example.patryk.sharegame2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.patryk.sharegame2.Adapters.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements TextWatcher {

    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText userConfirmPassword;
    private TextView errorMess;
    private LinearLayout signUp;

    private String url = StartActivity.user.getUrl() + "/register";

    private String USER_MESSAGE_SUCCESS = "SUCCESS";
    private String USER_MESSAGE_ERROR1 = "EXIST";
    private String USER_MESSAGE_ERROR2 = "INCORRECT";
    private String USER_MESSAGE_ERROR3 = "SERVER_ERROR";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        final String activityName = intent.getStringExtra("ActivityName");

        userName = findViewById(R.id.signUpUsername);
        userEmail = findViewById(R.id.signUpEmail);
        userPassword = findViewById(R.id.signUpPassword);
        userConfirmPassword = findViewById(R.id.signUpConfirmPassword);
        signUp = findViewById(R.id.signUpButton);
        errorMess = findViewById(R.id.signUpError);

        userName.addTextChangedListener(this);
        userEmail.addTextChangedListener(this);
        userPassword.addTextChangedListener(this);
        userConfirmPassword.addTextChangedListener(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(checkCorrectness()){
                    signIn(activityName);

                }else{
                    errorMess.setVisibility(View.VISIBLE);
                    errorMess.setText("Nieprawidłowe dane");
                }
            }
        });
    }

    private void signIn(final String activityName) {

        JSONObject signUp = new JSONObject();
        try {
            signUp.put("username",userName.getText().toString());
            signUp.put("email",userEmail.getText().toString());
            signUp.put("password",userPassword.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,signUp, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    String res = response.getString("message");

                    errorMess.setVisibility(View.VISIBLE);
                    if(res.equals(USER_MESSAGE_ERROR1)){
                        errorMess.setText("Użytkownik już istnieje");
                    }else if(res.equals(USER_MESSAGE_ERROR2) || res.equals(USER_MESSAGE_ERROR3)){
                        errorMess.setText("Nieprawidłowe dane");
                    }else if(res.equals(USER_MESSAGE_SUCCESS)){
                        errorMess.setTextColor(getResources().getColor(R.color.turquioselv2));
                        errorMess.setText("Zarejestrowano pomyślnie");
                        Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
                        login.putExtra("ActivityName",activityName);
                        startActivity(login);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplication(),"Spróbuj ponownie za chwilę",Toast.LENGTH_LONG).show();
            }
        }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private Boolean checkCorrectness(){
        String username = userName.getText().toString();
        String useremail = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmpassword = userConfirmPassword.getText().toString();

        if(username.length() >= 5 && useremail.length() >= 6 && useremail.contains("@")
                && password.length() >= 5 && password.equals(confirmpassword)){
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        errorMess.setVisibility(View.GONE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
