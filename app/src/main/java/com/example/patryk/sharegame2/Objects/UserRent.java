package com.example.patryk.sharegame2.Objects;

public class UserRent {

    public UserRent(SportFacility sportFacility, String date, String timeStart, String timeEnd, double amount, boolean statusOfPayment) {
        this.sportFacility = sportFacility;
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.amount = amount;
        this.statusOfPayment = statusOfPayment;
    }

    private SportFacility sportFacility;
    private String date;
    private String timeStart;
    private String timeEnd;
    private double amount;
    private boolean statusOfPayment;

    public boolean isStatusOfPayment() {
        return statusOfPayment;
    }

    public void setStatusOfPayment(boolean statusOfPayment) {
        this.statusOfPayment = statusOfPayment;
    }



    public SportFacility getSportFacility() {
        return sportFacility;
    }

    public void setSportFacility(SportFacility sportFacility) {
        this.sportFacility = sportFacility;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
