package com.example.patryk.sharegame2.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.SportFacility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private List<SportFacility> sportFacilities;

    public static final String DATABASE_NAME = "ShareGameDB";
    private static final String TAG = "SFDatabaseHelper";
    private static final String TABLE_NAME = "SportFacilities";

    private static final String COL1 = "ID";
    private static final String COL2 = "NAME";
    private static final String COL3 = "CITY";
    private static final String COL4 = "STREET";
    private static final String COL5 = "LOCAL_NO";
    private static final String COL6 = "ZIP_CODE";
    private static final String COL7 = "ZIP_CODE_CITY";
    private static final String COL8 = "EMAIL";
    private static final String COL9 = "PP_EMAIL";
    private static final String COL10 = "PHONE_NO";
    private static final String COL11 = "WWW";
    private static final String COL12 = "START_HOUR";
    private static final String COL13 = "END_HOUR";
    private static final String COL14 = "RENTAL_PRICE";
    private static final String COL15 = "LATITUDE";
    private static final String COL16 = "LONGITUDE";
    private static final String COL17 = "OWNER_ID";
    private static final String COL18 = "SOCCER";
    private static final String COL19 = "FUTSAL";
    private static final String COL20 = "VOLLEYBALL";
    private static final String COL21= "TENNIS";
    private static final String COL22 = "BASKETBALL";
    private static final String COL23 = "HANDBALL";
    private static final String COL24 = "SQUASH";
    private static final String COL25 = "BADMINTON";

    private static final String COL26= "PARKING";
    private static final String COL27 = "BATH";
    private static final String COL28 = "LOCKER_ROOM";
    private static final String COL29 = "LIGHTING";
    private static final String COL30 = "MAGAZINE";

    private static final String COL31 = "GLOBAL_ID";

    private static final String COL32 = "MONDAY";
    private static final String COL33 = "TUESDAY";
    private static final String COL34 = "WEDNESDAY";
    private static final String COL35 = "THURSDAY";
    private static final String COL36 = "FRIDAY";
    private static final String COL37 = "SATURDAY";
    private static final String COL38 = "SUNDAY";
    private static final String COL39 = "HOLIDAYS";
    private static final String COL40 = "WEEKENDS";

    private static final String USER_TABLE_NAME = "User";

    private static final String USER_COL1 = "LOGGED";
    private static final String USER_COL2 = "USER_ID";
    private static final String USER_COL3 = "USERNAME";
    private static final String USER_COL4 = "PASSWORD";
    private static final String USER_COL5 = "LATITUDE";
    private static final String USER_COL6 = "LONGITUDE";

    private static final String IMAGES_TABLE_NAME = "Images";

    private static final String IMAGES_COL1 = "ID";
    private static final String IMAGES_COL2 = "FACILITY_ID";
    private static final String IMAGES_COL3 = "IMAGE_URL";


    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME  + " (" +
                COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT NOT NULL, "+
                COL3 + " TEXT NOT NULL, "+
                COL4 + " TEXT NOT NULL, "+
                COL5 + " TEXT NOT NULL, "+
                COL6 + " TEXT NOT NULL, " +
                COL7 + " TEXT NOT NULL, " +
                COL8 + " TEXT, " +
                COL9 + " TEXT NOT NULL, " +
                COL10 + " TEXT, " +
                COL11 + " TEXT, " +
                COL12 + " TEXT NOT NULL, " +
                COL13 + " TEXT NOT NULL, " +
                COL14 + " INTEGER NOT NULL, " +
                COL15 + " REAL NOT NULL, " +
                COL16 + " REAL NOT NULL, " +
                COL17 + " INTEGER, " +
                COL18 + " INTEGER, " +
                COL19 + " INTEGER, " +
                COL20 + " INTEGER, " +
                COL21 + " INTEGER, " +
                COL22 + " INTEGER, " +
                COL23 + " INTEGER, " +
                COL24 + " INTEGER, " +
                COL25 + " INTEGER, " +
                COL26 + " INTEGER, " +
                COL27 + " INTEGER, " +
                COL28 + " INTEGER, " +
                COL29 + " INTEGER, " +
                COL30 + " INTEGER, " +
                COL31 + " INTEGER, " +
                COL32 + " TEXT, " +
                COL33 + " TEXT, " +
                COL34 + " TEXT, " +
                COL35 + " TEXT, " +
                COL36 + " TEXT, " +
                COL37 + " TEXT, " +
                COL38 + " TEXT, " +
                COL39 + " INTEGER," +
                COL40 + " INTEGER) ";
        db.execSQL(createTable);

        String createTableUser = "CREATE TABLE " + USER_TABLE_NAME  + " (LOGGED INTEGER, USER_ID INTEGER, USERNAME TEXT, PASSWORD TEXT, LATITUDE REAL, LONGITUDE REAL)";
        db.execSQL(createTableUser);

        String createTableImages = "CREATE TABLE " + IMAGES_TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, FACILITY_ID INTEGER, IMAGE_URL BLOB)";
        db.execSQL(createTableImages);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGES_TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String name, String city,
                           String street, String local_no,
                           String zip_code, String zip_code_city,
                           String email, String pp_email,
                           String phone_no, String www,
                           String start_hour, String end_hour,
                           int rental_price, double latitude,
                           double longitude, int owner_id,
                           int soccer, int futsal,
                           int volleyball, int tennis,
                           int basketball, int handball,
                           int squash, int badminton,
                           int parking, int bath,
                           int locker_room, int lighting,
                           int magazine, int global_id,
                           String monday, String tuesday,
                           String wednesday, String thursday,
                           String friday, String saturday,
                           String sunday, int holidays,
                           int weekends) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, name);
        contentValues.put(COL3, city);
        contentValues.put(COL4, street);
        contentValues.put(COL5, local_no);
        contentValues.put(COL6, zip_code);
        contentValues.put(COL7, zip_code_city);
        contentValues.put(COL8, email);
        contentValues.put(COL9, pp_email);
        contentValues.put(COL10, phone_no);
        contentValues.put(COL11, www);
        contentValues.put(COL12, start_hour);
        contentValues.put(COL13, end_hour);
        contentValues.put(COL14, rental_price);
        contentValues.put(COL15, latitude);
        contentValues.put(COL16, longitude);
        contentValues.put(COL17, owner_id);
        contentValues.put(COL18, soccer);
        contentValues.put(COL19, futsal);
        contentValues.put(COL20, volleyball);
        contentValues.put(COL21, tennis);
        contentValues.put(COL22, basketball);
        contentValues.put(COL23, handball);
        contentValues.put(COL24, squash);
        contentValues.put(COL25, badminton);
        contentValues.put(COL26, parking);
        contentValues.put(COL27, bath);
        contentValues.put(COL28, locker_room);
        contentValues.put(COL29, lighting);
        contentValues.put(COL30, magazine);
        contentValues.put(COL31, global_id);
        contentValues.put(COL32, monday);
        contentValues.put(COL33, tuesday);
        contentValues.put(COL34, wednesday);
        contentValues.put(COL35, thursday);
        contentValues.put(COL36, friday);
        contentValues.put(COL37, saturday);
        contentValues.put(COL38, sunday);
        contentValues.put(COL39, holidays);
        contentValues.put(COL40, weekends);


        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            System.out.println("DATABASE HELPER ERROR");
            return false;
        } else {
            System.out.println("DATABASE HELPER SUCCESS");
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public SportFacility getSportFacility(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL31 + " = '" + id + "'";
        Cursor cursor = db.rawQuery(query, null);

        SportFacility sportFacility = null;
        if(cursor.moveToFirst()){
            return getSportFacility(cursor);
        }
        return sportFacility;
    }

    public String getInfo(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        int fac = 0;
        int img = 0;

        if (cursor.moveToFirst()){
            do{
                fac++;
            }while(cursor.moveToNext());
        }

        cursor.close();
        String query2 = "SELECT * FROM " + IMAGES_TABLE_NAME;
        cursor = db.rawQuery(query2, null);

        if (cursor.moveToFirst()){
            do{
                img++;
            }while(cursor.moveToNext());
        }

        return String.valueOf(fac) + " img:" + String.valueOf(img);
    }

    public List<SportFacility> getSportFacilities(){
        sportFacilities = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do{
                moveCursorVIAFacilities(cursor,sportFacilities);
            }while(cursor.moveToNext());
        }
        cursor.close();

        return sportFacilities;
    }

    public List<SportFacility> getUserSportFacilities(int id){
        List<SportFacility> userSportFacilities = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL17 + " = '" + id + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do{
                moveCursorVIAFacilities(cursor,userSportFacilities);

            }while(cursor.moveToNext());
        }
        cursor.close();

        return userSportFacilities;
    }

    public boolean removeSportFacility(int id){

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,COL31 +"= " + "'" + id +"'",null) > 0;

    }

    public void removeAll(){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME;
        db.execSQL(query);

        String query2 = "DELETE FROM " + IMAGES_TABLE_NAME;
        db.execSQL(query2);

    }

    //======= USER TABLE

    public boolean addUser(int logged, int user_id, String username, String password, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL1, logged);
        contentValues.put(USER_COL2,user_id);
        contentValues.put(USER_COL3, username);
        contentValues.put(USER_COL4, password);
        contentValues.put(USER_COL5, latitude);
        contentValues.put(USER_COL6, longitude);

        long result = db.insert(USER_TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void logOut(){
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "DELETE FROM " + USER_TABLE_NAME;
        String query = "UPDATE " + USER_TABLE_NAME + " SET " + USER_COL1 + " = '0'," + USER_COL2 + " = '0'";

        db.execSQL(query);
    }

    public void logIn(int id, String username, String userpassword){
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "DELETE FROM " + USER_TABLE_NAME;
        String query = "UPDATE " + USER_TABLE_NAME + " SET " + USER_COL1 + " = '1'," + USER_COL2 + " = '" + id + "'," + USER_COL3 + " = '" + username + "',"+ USER_COL4 + " = '" + userpassword + "'";

        db.execSQL(query);
    }

    public void updateUser(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + USER_TABLE_NAME + " SET " + USER_COL3 + " = '" + username +"'";

        db.execSQL(query);
    }

    public void updatePassword(String password){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + USER_TABLE_NAME + " SET " + USER_COL4 + " = '" + password +"'";

        db.execSQL(query);
    }

    public void updateLocation(double latitude, double longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        //String query = "DELETE FROM " + USER_TABLE_NAME;
        String query = "UPDATE " + USER_TABLE_NAME + " SET " + USER_COL5  + " = '" + latitude + "'," + USER_COL6 + " = '" + longitude + "'";

        db.execSQL(query);
    }

    // IMAGES

    public boolean addImage(int id, byte[] image_url){
        SQLiteDatabase db = this.getWritableDatabase();

        // FileInputStream fs = new FileInputStream(image_url);
        //byte[] img = new byte[fs.available()];
        //fs.read(img);
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGES_COL2, id);
        contentValues.put(IMAGES_COL3,image_url);
        db.insert(IMAGES_TABLE_NAME, null, contentValues);

        //fs.close();
        return true;
    }


    public List<FacilityImage> getImages(int id){
        List<FacilityImage> facilityImages = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + IMAGES_TABLE_NAME + " WHERE " + IMAGES_COL2 + " = '" + id + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do{
                FacilityImage fi = new FacilityImage();
                byte[] byteArray = cursor.getBlob(2);
                System.out.println("DATABASE" + byteArray);
                fi.setByteArray(byteArray);
                fi.setAdded(true);
                facilityImages.add(fi);
            }while(cursor.moveToNext());
        }
        cursor.close();

        return facilityImages;
    }

    public boolean removeImages(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(IMAGES_TABLE_NAME,IMAGES_COL2 +"= " + "'" + id +"'",null) > 0;
    }

    //=============================================

    public void moveCursorVIAFacilities(Cursor cursor, List<SportFacility> facilities){
        SportFacility facility = new SportFacility();

        int id = cursor.getInt(0);
        facility.setId(id);

        String name = cursor.getString(1);
        facility.setName(name);

        String city = cursor.getString(2);
        facility.setCity(city);

        String street = cursor.getString(3);
        facility.setStreet(street);

        String local_no = cursor.getString(4);
        facility.setLocal_no(local_no);

        String zip_code = cursor.getString(5);
        facility.setZip_code(zip_code);

        String zip_code_city = cursor.getString(6);
        facility.setZip_code_city(zip_code_city);

        String email = cursor.getString(7);
        facility.setEmail(email);

        String pp_email = cursor.getString(8);
        facility.setPp_email(pp_email);

        String phone_no = cursor.getString(9);
        facility.setPhone_no(phone_no);

        String www = cursor.getString(10);
        facility.setWww(www);

        String start_hour = cursor.getString(11);
        facility.setStart_hour(start_hour);

        String end_hour = cursor.getString(12);
        facility.setEnd_hour(end_hour);

        int rental_price = cursor.getInt(13);
        facility.setRental_price(rental_price);

        double latitude = cursor.getDouble(14);
        facility.setLatitude(latitude);

        double longitude = cursor.getDouble(15);
        facility.setLongitude(longitude);

        int owner_id = cursor.getInt(16);
        facility.setOwner_id(owner_id);

        int soccer = cursor.getInt(17);
        facility.setSoccer((soccer == 1) ? true : false);

        int futsal = cursor.getInt(18);
        facility.setFutsal((futsal == 1) ? true : false);

        int volleyball = cursor.getInt(19);
        facility.setVolleyball((volleyball == 1) ? true : false);

        int tennis = cursor.getInt(20);
        facility.setTennis((tennis == 1) ? true : false);

        int basketball = cursor.getInt(21);
        facility.setBasketball((basketball == 1) ? true : false);

        int handball = cursor.getInt(22);
        facility.setHandball((handball == 1) ? true : false);

        int squash = cursor.getInt(23);
        facility.setSquash((squash == 1) ? true : false);

        int badminton = cursor.getInt(24);
        facility.setBadminton((badminton == 1) ? true : false);

        int parking = cursor.getInt(25);
        facility.setParking((parking == 1) ? true : false);

        int bath = cursor.getInt(26);
        facility.setBath((bath == 1) ? true : false);

        int locker_room = cursor.getInt(27);
        facility.setLocker_room((locker_room == 1) ? true : false);

        int lighting = cursor.getInt(28);
        facility.setLighting((lighting == 1) ? true : false);

        int magazine = cursor.getInt(29);
        facility.setMagazine((magazine == 1) ? true : false);

        int global_id = cursor.getInt(30);
        facility.setGlobal_id(global_id);

        String monday = cursor.getString(31);
        facility.setOh_monday(monday);

        String tuesday = cursor.getString(32);
        facility.setOh_tuesday(tuesday);

        String wednesday = cursor.getString(33);
        facility.setOh_wednesday(wednesday);

        String thursday = cursor.getString(34);
        facility.setOh_thursday(thursday);

        String friday = cursor.getString(35);
        facility.setOh_friday(friday);

        String saturday = cursor.getString(36);
        facility.setOh_saturday(saturday);

        String sunday = cursor.getString(37);
        facility.setOh_sunday(sunday);

        int holidays = cursor.getInt(38);
        facility.setHolidays((holidays == 1) ? true : false);

        int weekends = cursor.getInt(39);
        facility.setWeekends((weekends == 1) ? true : false);

        facilities.add(facility);
    }

    public SportFacility getSportFacility(Cursor cursor){

        SportFacility facility = new SportFacility();

        int id = cursor.getInt(0);
        facility.setId(id);

        String name = cursor.getString(1);
        facility.setName(name);

        String city = cursor.getString(2);
        facility.setCity(city);

        String street = cursor.getString(3);
        facility.setStreet(street);

        String local_no = cursor.getString(4);
        facility.setLocal_no(local_no);

        String zip_code = cursor.getString(5);
        facility.setZip_code(zip_code);

        String zip_code_city = cursor.getString(6);
        facility.setZip_code_city(zip_code_city);

        String email = cursor.getString(7);
        facility.setEmail(email);

        String pp_email = cursor.getString(8);
        facility.setPp_email(pp_email);

        String phone_no = cursor.getString(9);
        facility.setPhone_no(phone_no);

        String www = cursor.getString(10);
        facility.setWww(www);

        String start_hour = cursor.getString(11);
        facility.setStart_hour(start_hour);

        String end_hour = cursor.getString(12);
        facility.setEnd_hour(end_hour);

        int rental_price = cursor.getInt(13);
        facility.setRental_price(rental_price);

        double latitude = cursor.getDouble(14);
        facility.setLatitude(latitude);

        double longitude = cursor.getDouble(15);
        facility.setLongitude(longitude);

        int owner_id = cursor.getInt(16);
        facility.setOwner_id(owner_id);

        int soccer = cursor.getInt(17);
        facility.setSoccer((soccer == 1) ? true : false);

        int futsal = cursor.getInt(18);
        facility.setFutsal((futsal == 1) ? true : false);

        int volleyball = cursor.getInt(19);
        facility.setVolleyball((volleyball == 1) ? true : false);

        int tennis = cursor.getInt(20);
        facility.setTennis((tennis == 1) ? true : false);

        int basketball = cursor.getInt(21);
        facility.setBasketball((basketball == 1) ? true : false);

        int handball = cursor.getInt(22);
        facility.setHandball((handball == 1) ? true : false);

        int squash = cursor.getInt(23);
        facility.setSquash((squash == 1) ? true : false);

        int badminton = cursor.getInt(24);
        facility.setBadminton((badminton == 1) ? true : false);

        int parking = cursor.getInt(25);
        facility.setParking((parking == 1) ? true : false);

        int bath = cursor.getInt(26);
        facility.setBath((bath == 1) ? true : false);

        int locker_room = cursor.getInt(27);
        facility.setLocker_room((locker_room == 1) ? true : false);

        int lighting = cursor.getInt(28);
        facility.setLighting((lighting == 1) ? true : false);

        int magazine = cursor.getInt(29);
        facility.setMagazine((magazine == 1) ? true : false);

        int global_id = cursor.getInt(30);
        facility.setGlobal_id(global_id);

        String monday = cursor.getString(31);
        facility.setOh_monday(monday);

        String tuesday = cursor.getString(32);
        facility.setOh_tuesday(tuesday);

        String wednesday = cursor.getString(33);
        facility.setOh_wednesday(wednesday);

        String thursday = cursor.getString(34);
        facility.setOh_thursday(thursday);

        String friday = cursor.getString(35);
        facility.setOh_friday(friday);

        String saturday = cursor.getString(36);
        facility.setOh_saturday(saturday);

        String sunday = cursor.getString(37);
        facility.setOh_sunday(sunday);

        int holidays = cursor.getInt(38);
        facility.setHolidays((holidays == 1) ? true : false);

        int weekends = cursor.getInt(39);
        facility.setWeekends((weekends == 1) ? true : false);

        return facility;

    }
}

