package com.example.patryk.sharegame2.Options;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.patryk.sharegame2.AddFacility;
import com.example.patryk.sharegame2.LoginActivity;
import com.example.patryk.sharegame2.MainFragments.MainActivity;
import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;
import com.example.patryk.sharegame2.StartActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity{

    private CircleImageView useravatar;
    private Button edit;
    private Button remove;

    private TextView userName;
    private TextView userFirstName;
    private TextView userLastName;
    private TextView userEmail;
    private TextView userPassword;
    private TextView userPayPalEmail;
    private TextView userPhoneNo;
    private TextView userBirthDate;

    private String photo;

    private String dateOfBirth;

    private LinearLayout saveChanges;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    private String url = StartActivity.user.getUrl() + "/updateProfile";
    private String urlGetUser = StartActivity.user.getUrl() + "/getUser";
    private String urlChangePassword = StartActivity.user.getUrl() + "/resetPassword";
    private String urlPhoto = StartActivity.user.getUrl() + "/addPhoto";
    private DatabaseHelper databaseHelper;

    public static final int PERMISSION_REQUEST = 0;
    public static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST);
        }

        databaseHelper = new DatabaseHelper(this);

        final Calendar calendar = Calendar.getInstance();

        useravatar = findViewById(R.id.user_avatar);
        edit = findViewById(R.id.edit_edit);
        remove = findViewById(R.id.edit_delete);
        edit.setEnabled(false);
        remove.setEnabled(false);

        userName = findViewById(R.id.user_name);
        userFirstName = findViewById(R.id.user_firstname);
        userLastName = findViewById(R.id.user_lastname);
        userEmail = findViewById(R.id.user_mail);
        userPassword = findViewById(R.id.user_password);
        userPayPalEmail = findViewById(R.id.user_paypalmail);
        userBirthDate = findViewById(R.id.userBirthDate);
        userPhoneNo = findViewById(R.id.user_phoneno);

        saveChanges = findViewById(R.id.save_button);

        Cursor c = databaseHelper.getUser();
        c.moveToFirst();
        final String password = c.getString(3);
        String pass ="";
        for(int i=0;i<password.length();i++){
            pass += "*";
        }

        userPassword.setText(pass);

        getUser();

        useravatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.VISIBLE);
                remove.setVisibility(View.VISIBLE);
                edit.setEnabled(true);
                remove.setEnabled(true);

                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                edit.setVisibility(View.INVISIBLE);
                                remove.setVisibility(View.INVISIBLE);
                                edit.setEnabled(false);
                                remove.setEnabled(false);
                            }
                        });
                    }
                }, 5000);
            }
        });

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(userName,"Nazwa użytkownika");
            }
        });

        userFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(userFirstName,"Imie");
            }
        });

        userLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(userLastName,"Nazwisko");
            }
        });

        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(userEmail,"Email");
            }
        });

        userPayPalEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(userPayPalEmail,"PayPal email");
            }
        });

        userPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(userPhoneNo,"Numer telefonu");
            }
        });

        userBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(EditProfile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);

                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();

            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year_, int month_, int dayOfMonth_) {
                month_ += 1;

                userBirthDate.setText(String.format("%02d",dayOfMonth_)+"/"+String.format("%02d",month_)+"/"+String.valueOf(year_));
            }
        };

        userPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(EditProfile.this,R.style.customDialog));

                View view = LayoutInflater.from(EditProfile.this).inflate(R.layout.insert_password_dialog, null);
                final EditText old_password = view.findViewById(R.id.input_password);
                final EditText new_password = view.findViewById(R.id.input_newpassword);

                builder.setView(view)
                        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Zatwierdź", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changePassword(new_password.getText().toString(),old_password.getText().toString());
                            }
                        });


                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                new_password.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Cursor c = databaseHelper.getUser();
                        c.moveToFirst();
                        String password = c.getString(3);

                        if(old_password.getText().toString().equals(password)){
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }else{
                            Toast.makeText(EditProfile.this, "Stare hasło jest nieprawdiłowe", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
                addPhoto();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(add,RESULT_LOAD_IMAGE);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo = "";
                useravatar.setImageResource(R.drawable.useravatar);
            }
        });
    }

    private void changePassword(final String newpass, final String oldPass) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, urlChangePassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String res = json.getString("message");
                    if(res.equals("PASSWORD_CHANGED")){
                        Toast.makeText(EditProfile.this, "Hasło zostało zmienione", Toast.LENGTH_SHORT).show();
                        StartActivity.user.setLogged(false);
                        databaseHelper.logOut();
                    }else{
                        Toast.makeText(EditProfile.this, "Hasło nie zostało zmienione", Toast.LENGTH_SHORT).show();
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

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<>();


                params.put("newPassword",newpass);
                params.put("oldPassword",oldPass);
                return params;
            }


        };
        RequestQueue requestQueue =Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getUser() {
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.POST, urlGetUser, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    userBirthDate.setText(response.getString("dateofbirth"));
                    userName.setText(response.getString("username"));
                    userEmail.setText(response.getString("email"));
                    userFirstName.setText(response.getString("firstname"));
                    userLastName.setText(response.getString("lastname"));
                    userPhoneNo.setText(response.getString("phoneno"));
                    userPayPalEmail.setText(response.getString("paypalemail"));
                    photo = response.getString("photo");

                    if(!photo.equals("")){
                        byte[] imageLink = Base64.decode(response.getString("photo"),0);

                        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageLink, 0, imageLink.length);
                        useravatar.setImageBitmap(decodedImage);
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

    public void openDialog(final TextView textView, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.customDialog));

        View view = LayoutInflater.from(this).inflate(R.layout.insert_dialog, null);
        final EditText input = view.findViewById(R.id.dialog_input);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(title);

        input.setText(textView.getText());
        input.setHint(title);

        builder.setView(view)
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Zatwierdź", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String in = input.getText().toString();
                        textView.setText(in);
                    }
                });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateProfile() {

        JSONObject profile = new JSONObject();

        try {
            profile.put("username",userName.getText().toString());
            profile.put("firstname",userFirstName.getText().toString());
            profile.put("lastname",userLastName.getText().toString());
            profile.put("email",userEmail.getText().toString());
            profile.put("paypalemail",userPayPalEmail.getText().toString());
            profile.put("phoneno",userPhoneNo.getText().toString());
            profile.put("dateofbirth",userBirthDate.getText().toString());
            profile.put("password"," ");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url,profile, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    String res = response.getString("message");

                    if(res.equals("UPDATE_SUCCESS")) {
                        Toast.makeText(EditProfile.this, "Zmiany zostały zapisane", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("UPDATE_ERROR")){
                        Toast.makeText(EditProfile.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
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

    private void addPhoto() {

        JSONObject avatar = new JSONObject();
        try {
            avatar.put("photo",photo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, urlPhoto,avatar, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),"Spróbuj ponownie za chwilę",Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("PageNo", 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,filePathColumn,null,null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picPath = cursor.getString(columnIndex);
                    cursor.close();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                    Bitmap b;
                    if(bitmap.getWidth()/8 < 300 || bitmap.getHeight()/8 < 300){
                        b = Bitmap.createScaledBitmap(bitmap,300,300,false);
                    }else{
                        b = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/4,bitmap.getHeight()/4,false);
                    }


                    b.compress(Bitmap.CompressFormat.PNG,40,stream);

                    byte[] byteArray = stream.toByteArray();
                    Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

                    useravatar.setImageBitmap(compressedBitmap);
                    FacilityImage fi = new FacilityImage();
                    fi.setByteArray(byteArray);
                    photo = fi.byteToString();
                    Toast.makeText(this, String.valueOf(photo.length()), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
