package com.example.patryk.sharegame2.Adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.R;

import java.util.ArrayList;
import java.util.List;

public class ImageSlider extends PagerAdapter {

    private int imageCount;
    private List<FacilityImage> images;
    private Context context;
    private LayoutInflater layoutInflater;


    public ImageSlider(Context context) {
        this.context = context;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public void setImages(List<FacilityImage> images) {
        this.images = images;
    }

    @Override
    public int getCount() {
        return imageCount;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {

        return view == (LinearLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view =layoutInflater.inflate(R.layout.image_layout,container,false);

        //view.setBackgroundResource(images.get(position).getBitmap());
        view.setBackground(new BitmapDrawable(context.getResources(),images.get(position).getBitmap()));
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
