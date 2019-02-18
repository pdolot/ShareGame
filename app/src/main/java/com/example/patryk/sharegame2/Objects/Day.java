package com.example.patryk.sharegame2.Objects;

public class Day {
    private String name;
    private String openHour;
    private String closeHour;
    private String openHours;
    private boolean available = true;

    public Day(String name) {
        this.name = name;
        this.openHour = "00:00";
        this.closeHour = "00:00";
        this.openHours = "00:00-00:00";
    }

    public Day(String name, String openHours) {
        this.name = name;
        this.openHour = openHours.substring(0,5);
        this.closeHour = openHours.substring(6,11);
        this.openHours = openHours;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenHour() {
        return openHour;
    }

    public void setOpenHour(String openHour) {
        this.openHour = openHour;
    }

    public String getCloseHour() {
        return closeHour;
    }

    public void setCloseHour(String closeHour) {
        this.closeHour = closeHour;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours() {
        this.openHours = openHour + "-" + closeHour;
    }
}
