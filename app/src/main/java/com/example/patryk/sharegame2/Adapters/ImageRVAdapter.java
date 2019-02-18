package com.example.patryk.sharegame2.Adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageRVAdapter extends RecyclerView.Adapter<ImageRVAdapter.MyViewHolder> {

    private static ImageRVAdapter.ClickListener clickListener;

    Context mContext;
    List<FacilityImage> mData;

    public ImageRVAdapter(Context mContext, List<FacilityImage> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public List<FacilityImage> getmData() {
        return mData;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.facility_image_item,viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        if(mData.get(i).isAdded()){
            myViewHolder.image.setBackground(new BitmapDrawable(mContext.getResources(),mData.get(i).getBitmap()));
            myViewHolder.add_image.setBackground(mContext.getDrawable(R.drawable.ic_removepic));
            myViewHolder.image.setClickable(false);
        }else{
            myViewHolder.image.setBackground(mContext.getDrawable(R.drawable.ic_picture));
            myViewHolder.add_image.setBackground(mContext.getDrawable(R.drawable.ic_addpic));
            myViewHolder.image.setClickable(true);
        }

    }

    public void setOnItemClickListener(ImageRVAdapter.ClickListener clickListener) {
        ImageRVAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        Button add_image;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.facilityImage);
            add_image = itemView.findViewById(R.id.add_image);

            image.setOnClickListener(this);
            add_image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

    }
}
