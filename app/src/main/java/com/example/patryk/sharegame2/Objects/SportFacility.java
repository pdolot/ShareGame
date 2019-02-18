package com.example.patryk.sharegame2.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class SportFacility implements Parcelable {

    public SportFacility() {
    }

    private int id;
    private String name;
    private String city;
    private String street;
    private String local_no;
    private String zip_code;
    private String zip_code_city;
    private String email;
    private String pp_email;
    private String phone_no;
    private String www;
    private String start_hour;
    private String end_hour;
    private int rental_price;
    private double latitude;
    private double longitude;
    private int owner_id;

    private Boolean soccer;
    private Boolean futsal;
    private Boolean volleyball;
    private Boolean tennis;
    private Boolean basketball;
    private Boolean handball;
    private Boolean squash;
    private Boolean badminton;

    private Boolean parking;
    private Boolean bath;
    private Boolean locker_room;
    private Boolean lighting;
    private Boolean magazine;
    private int global_id;

    // openhours
    private String oh_monday;
    private String oh_tuesday;
    private String oh_wednesday;
    private String oh_thursday;
    private String oh_friday;
    private String oh_saturday;
    private String oh_sunday;
    private Boolean holidays;
    private Boolean weekends;


    private String sports = "";
    private String extras = "";

    protected SportFacility(Parcel in) {
        id = in.readInt();
        name = in.readString();
        city = in.readString();
        street = in.readString();
        local_no = in.readString();
        zip_code = in.readString();
        zip_code_city = in.readString();
        email = in.readString();
        pp_email = in.readString();
        phone_no = in.readString();
        www = in.readString();
        start_hour = in.readString();
        end_hour = in.readString();
        rental_price = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        owner_id = in.readInt();
        byte tmpSoccer = in.readByte();
        soccer = tmpSoccer == 0 ? null : tmpSoccer == 1;
        byte tmpFutsal = in.readByte();
        futsal = tmpFutsal == 0 ? null : tmpFutsal == 1;
        byte tmpVolleyball = in.readByte();
        volleyball = tmpVolleyball == 0 ? null : tmpVolleyball == 1;
        byte tmpTennis = in.readByte();
        tennis = tmpTennis == 0 ? null : tmpTennis == 1;
        byte tmpBasketball = in.readByte();
        basketball = tmpBasketball == 0 ? null : tmpBasketball == 1;
        byte tmpHandball = in.readByte();
        handball = tmpHandball == 0 ? null : tmpHandball == 1;
        byte tmpSquash = in.readByte();
        squash = tmpSquash == 0 ? null : tmpSquash == 1;
        byte tmpBadminton = in.readByte();
        badminton = tmpBadminton == 0 ? null : tmpBadminton == 1;
        byte tmpParking = in.readByte();
        parking = tmpParking == 0 ? null : tmpParking == 1;
        byte tmpBath = in.readByte();
        bath = tmpBath == 0 ? null : tmpBath == 1;
        byte tmpLocker_room = in.readByte();
        locker_room = tmpLocker_room == 0 ? null : tmpLocker_room == 1;
        byte tmpLighting = in.readByte();
        lighting = tmpLighting == 0 ? null : tmpLighting == 1;
        byte tmpMagazine = in.readByte();
        magazine = tmpMagazine == 0 ? null : tmpMagazine == 1;
        global_id = in.readInt();
        oh_monday = in.readString();
        oh_tuesday = in.readString();
        oh_wednesday = in.readString();
        oh_thursday = in.readString();
        oh_friday = in.readString();
        oh_saturday = in.readString();
        oh_sunday = in.readString();
        byte tmpHolidays = in.readByte();
        holidays = tmpHolidays == 0 ? null : tmpHolidays == 1;
        byte tmpWeekends = in.readByte();
        weekends = tmpWeekends == 0 ? null : tmpWeekends == 1;
        sports = in.readString();
        extras = in.readString();
    }

    public static final Creator<SportFacility> CREATOR = new Creator<SportFacility>() {
        @Override
        public SportFacility createFromParcel(Parcel in) {
            return new SportFacility(in);
        }

        @Override
        public SportFacility[] newArray(int size) {
            return new SportFacility[size];
        }
    };

    public String getOh_monday() {
        return oh_monday;
    }

    public void setOh_monday(String oh_monday) {
        this.oh_monday = oh_monday;
    }

    public String getOh_tuesday() {
        return oh_tuesday;
    }

    public void setOh_tuesday(String oh_tuesday) {
        this.oh_tuesday = oh_tuesday;
    }

    public String getOh_wednesday() {
        return oh_wednesday;
    }

    public void setOh_wednesday(String oh_wednesday) {
        this.oh_wednesday = oh_wednesday;
    }

    public String getOh_thursday() {
        return oh_thursday;
    }

    public void setOh_thursday(String oh_thursday) {
        this.oh_thursday = oh_thursday;
    }

    public String getOh_friday() {
        return oh_friday;
    }

    public void setOh_friday(String oh_friday) {
        this.oh_friday = oh_friday;
    }

    public String getOh_saturday() {
        return oh_saturday;
    }

    public void setOh_saturday(String oh_saturday) {
        this.oh_saturday = oh_saturday;
    }

    public String getOh_sunday() {
        return oh_sunday;
    }

    public void setOh_sunday(String oh_sunday) {
        this.oh_sunday = oh_sunday;
    }

    public Boolean getHolidays() {
        return holidays;
    }

    public void setHolidays(Boolean holidays) {
        this.holidays = holidays;
    }

    public Boolean getWeekends() {
        return weekends;
    }

    public void setWeekends(Boolean weekends) {
        this.weekends = weekends;
    }

    public Boolean getParking() {
        return parking;
    }

    public void setParking(Boolean parking) {
        this.parking = parking;
    }

    public Boolean getBath() {
        return bath;
    }

    public void setBath(Boolean bath) {
        this.bath = bath;
    }

    public Boolean getLocker_room() {
        return locker_room;
    }

    public void setLocker_room(Boolean locker_room) {
        this.locker_room = locker_room;
    }

    public Boolean getLighting() {
        return lighting;
    }

    public void setLighting(Boolean lighting) {
        this.lighting = lighting;
    }

    public Boolean getMagazine() {
        return magazine;
    }

    public void setMagazine(Boolean magazine) {
        this.magazine = magazine;
    }

    public int getGlobal_id() {
        return global_id;
    }

    public void setGlobal_id(int global_id) {
        this.global_id = global_id;
    }

    public String getExtrasList(){
        extras = "";

        if (getParking()) {
            extras += "parking, ";
        }
        if (getBath()) {
            extras += "łazienki, ";
        }
        if (getLocker_room()) {
            extras += "szatnia, ";
        }
        if (getLighting()) {
            extras += "sztuczne oświetlenie, ";
        }
        if (getMagazine()) {
            extras += "dostęp do magazynku, ";
        }

        return extras;
    }

    public String getSportList(){
        sports = "";

        if (getSoccer()) {
            sports += "piłka nożna, ";
        }
        if (getFutsal()) {
            sports += "futsal, ";
        }
        if (getVolleyball()) {
            sports += "siatkówka, ";
        }
        if (getBadminton()) {
            sports += "badminton, ";
        }
        if (getBasketball()) {
            sports += "koszykówka, ";
        }
        if (getTennis()) {
            sports += "tenis, ";
        }
        if (getSquash()) {
            sports += "squash, ";
        }
        if (getHandball()) {
            sports += "piłka ręczna, ";
        }

        return sports;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLocal_no() {
        return local_no;
    }

    public void setLocal_no(String local_no) {
        this.local_no = local_no;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getZip_code_city() {
        return zip_code_city;
    }

    public void setZip_code_city(String zip_code_city) {
        this.zip_code_city = zip_code_city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPp_email() {
        return pp_email;
    }

    public void setPp_email(String pp_email) {
        this.pp_email = pp_email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }

    public String getStart_hour() {
        return start_hour;
    }

    public void setStart_hour(String start_hour) {
        this.start_hour = start_hour;
    }

    public String getEnd_hour() {
        return end_hour;
    }

    public void setEnd_hour(String end_hour) {
        this.end_hour = end_hour;
    }

    public int getRental_price() {
        return rental_price;
    }

    public void setRental_price(int rental_price) {
        this.rental_price = rental_price;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public Boolean getSoccer() {
        return soccer;
    }

    public void setSoccer(Boolean soccer) {
        this.soccer = soccer;
    }

    public Boolean getFutsal() {
        return futsal;
    }

    public void setFutsal(Boolean futsal) {
        this.futsal = futsal;
    }

    public Boolean getVolleyball() {
        return volleyball;
    }

    public void setVolleyball(Boolean volleyball) {
        this.volleyball = volleyball;
    }

    public Boolean getTennis() {
        return tennis;
    }

    public void setTennis(Boolean tennis) {
        this.tennis = tennis;
    }

    public Boolean getBasketball() {
        return basketball;
    }

    public void setBasketball(Boolean basketball) {
        this.basketball = basketball;
    }

    public Boolean getHandball() {
        return handball;
    }

    public void setHandball(Boolean handball) {
        this.handball = handball;
    }

    public Boolean getSquash() {
        return squash;
    }

    public void setSquash(Boolean squash) {
        this.squash = squash;
    }

    public Boolean getBadminton() {
        return badminton;
    }

    public void setBadminton(Boolean badminton) {
        this.badminton = badminton;
    }

    public String getSports() {
        int trueCount = 0;
        if (getSoccer()) {
            trueCount++;
        }
        if (getFutsal()) {
            trueCount++;
        }
        if (getVolleyball()) {
            trueCount++;
        }
        if (getBadminton()) {
            trueCount++;
        }
        if (getBasketball()) {
            trueCount++;
        }
        if (getTennis()) {
            trueCount++;
        }
        if (getSquash()) {
            trueCount++;
        }
        if (getHandball()) {
            trueCount++;
        }

        if (trueCount > 1) {
            return "multi";
        } else {
            if (getSoccer()) {
                return "soccer";
            }
            if (getFutsal()) {
                return "futsal";
            }
            if (getVolleyball()) {
                return "volleyball";
            }
            if (getBadminton()) {
                return "badminton";
            }
            if (getBasketball()) {
                return "basketball";
            }
            if (getTennis()) {
                return "tennis";
            }
            if (getSquash()) {
                return "squash";
            }
            if (getHandball()) {
                return "handball";
            }
        }

        return "multi";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(city);
        dest.writeString(street);
        dest.writeString(local_no);
        dest.writeString(zip_code);
        dest.writeString(zip_code_city);
        dest.writeString(email);
        dest.writeString(pp_email);
        dest.writeString(phone_no);
        dest.writeString(www);
        dest.writeString(start_hour);
        dest.writeString(end_hour);
        dest.writeInt(rental_price);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(owner_id);
        dest.writeByte((byte) (soccer == null ? 0 : soccer ? 1 : 2));
        dest.writeByte((byte) (futsal == null ? 0 : futsal ? 1 : 2));
        dest.writeByte((byte) (volleyball == null ? 0 : volleyball ? 1 : 2));
        dest.writeByte((byte) (tennis == null ? 0 : tennis ? 1 : 2));
        dest.writeByte((byte) (basketball == null ? 0 : basketball ? 1 : 2));
        dest.writeByte((byte) (handball == null ? 0 : handball ? 1 : 2));
        dest.writeByte((byte) (squash == null ? 0 : squash ? 1 : 2));
        dest.writeByte((byte) (badminton == null ? 0 : badminton ? 1 : 2));
        dest.writeByte((byte) (parking == null ? 0 : parking ? 1 : 2));
        dest.writeByte((byte) (bath == null ? 0 : bath ? 1 : 2));
        dest.writeByte((byte) (locker_room == null ? 0 : locker_room ? 1 : 2));
        dest.writeByte((byte) (lighting == null ? 0 : lighting ? 1 : 2));
        dest.writeByte((byte) (magazine == null ? 0 : magazine ? 1 : 2));
        dest.writeInt(global_id);
        dest.writeString(oh_monday);
        dest.writeString(oh_tuesday);
        dest.writeString(oh_wednesday);
        dest.writeString(oh_thursday);
        dest.writeString(oh_friday);
        dest.writeString(oh_saturday);
        dest.writeString(oh_sunday);
        dest.writeByte((byte) (holidays == null ? 0 : holidays ? 1 : 2));
        dest.writeByte((byte) (weekends == null ? 0 : weekends ? 1 : 2));
        dest.writeString(sports);
        dest.writeString(extras);
    }
}
