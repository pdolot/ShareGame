package com.example.patryk.sharegame2.Adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.Objects.SportItem;
import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FacilityRVAdapter extends RecyclerView.Adapter<FacilityRVAdapter.MyViewHolder> {

    private static ClickListener clickListener;

    Context mContext;
    List<SportFacility> mData;
    DatabaseHelper databaseHelper;

    public FacilityRVAdapter(Context mContext, List<SportFacility> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public List<SportFacility> getmData() {
        return mData;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.facility_view_row,viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {

        databaseHelper = new DatabaseHelper(mContext);
        List<FacilityImage> images = databaseHelper.getImages(mData.get(i).getGlobal_id());
        if(images.size() == 0){
            myViewHolder.imageView.setImageResource(R.drawable.ic_noimage);
        }else{
            myViewHolder.imageView.setImageBitmap(images.get(0).getBitmap());
        }

        myViewHolder.name.setText(mData.get(i).getName());
        myViewHolder.address.setText(mData.get(i).getCity() + ", ul. " +mData.get(i).getStreet() + " "
                + mData.get(i).getLocal_no() + ", " + mData.get(i).getZip_code() + " " + mData.get(i).getZip_code_city());

    }


    public void removeItem(int pos){

        mData.remove(pos);
        notifyItemRemoved(pos);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        FacilityRVAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        CircleImageView imageView;
        TextView name;
        TextView address;
        LinearLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            imageView = itemView.findViewById(R.id.fv_image);
            name = itemView.findViewById(R.id.fv_name);
            address = itemView.findViewById(R.id.fv_address);
            layout =  itemView.findViewById(R.id.facility_view_layout);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
            layout.setBackgroundResource(R.drawable.item_outline_dark);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            layout.setBackgroundResource(R.drawable.item_outline);

            return true;
        }
    }
}
