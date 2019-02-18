package com.example.patryk.sharegame2.Objects;

import android.text.BoringLayout;

public class SportItem {
    private String SportName;
    private int SportIcon;

    private Boolean isChecked;

    public SportItem(String sportName) {
        this.SportName = sportName;
        this.isChecked = false;
    }

    public SportItem(String sportName, Boolean isChecked) {
        this.SportName = sportName;
        this.isChecked = isChecked;
    }

    public SportItem(String sportName, int sportIcon) {
        SportName = sportName;
        SportIcon = sportIcon;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public void setSportName(String sportName) {
        SportName = sportName;
    }

    public void setSportIcon(int sportIcon) {
        SportIcon = sportIcon;
    }

    public String getSportName() {
        return SportName;
    }

    public int getSportIcon() {
        return SportIcon;
    }
}