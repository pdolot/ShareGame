package com.example.patryk.sharegame2.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import java.io.ByteArrayOutputStream;

public class FacilityImage {

    private boolean isAdded = false;
    private Bitmap bitmap;
    private byte[] byteArray;
    private String img_path;

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
        ByteArrayInputStream imageStream = new ByteArrayInputStream(byteArray);
        this.bitmap = BitmapFactory.decodeStream(imageStream);
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public byte[] bitmapToByteArray()
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
        byte[] byteArray = stream.toByteArray();
        this.byteArray = byteArray;
        return byteArray;
    }

    public String byteToString(){
        String byteArrayStr= new String(Base64.getEncoder().encodeToString(bitmapToByteArray()));
        return byteArrayStr;
    }

    public void setByteArray(String img_url){
        this.byteArray = android.util.Base64.decode(img_url,0);
    }
}
