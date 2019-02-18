package com.example.patryk.sharegame2.Objects;

public class User {

    private String userName;
    private Boolean isLogged = false;
    private Boolean offLine = false;
    private String url = "http://192.168.0.101:8080";

    public String getUrl() {
        return url;
    }

    public Boolean getOffLine() {
        return offLine;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getLogged() {
        return isLogged;
    }

    public void setLogged(Boolean logged) {
        isLogged = logged;
    }
}
