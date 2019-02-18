package com.example.patryk.sharegame2.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.SportFacility;
import com.example.patryk.sharegame2.Objects.UserRent;
import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RentRVAdapter extends RecyclerView.Adapter<RentRVAdapter.MyViewHolder>{


    private static ClickListener clickListener;

    private Context mContext;
    private List<UserRent> mData;
    private DatabaseHelper databaseHelper;


    public RentRVAdapter(Context mContext, List<UserRent> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public List<UserRent> getmData() {
        return mData;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.rental_facility_view_row,viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {

        databaseHelper = new DatabaseHelper(mContext);
        List<FacilityImage> images = databaseHelper.getImages(mData.get(i).getSportFacility().getGlobal_id());
        if(images.size() == 0){
            myViewHolder.imageView.setImageResource(R.drawable.ic_noimage);
        }else{
            myViewHolder.imageView.setImageBitmap(images.get(0).getBitmap());
        }
        myViewHolder.name.setText(mData.get(i).getSportFacility().getName());
        myViewHolder.address.setText(mData.get(i).getSportFacility().getCity() + ", ul. " +mData.get(i).getSportFacility().getStreet() + " "
                + mData.get(i).getSportFacility().getLocal_no() + ", " +mData.get(i).getSportFacility().getZip_code() + " " + mData.get(i).getSportFacility().getZip_code_city());
        myViewHolder.date.setText(mData.get(i).getDate());
        myViewHolder.hours.setText(mData.get(i).getTimeStart() + "-" + mData.get(i).getTimeEnd());
        if(mData.get(i).isStatusOfPayment()){
            myViewHolder.setPayment(true);
            myViewHolder.layout.setBackgroundResource(R.drawable.item_outline_green);
        }else{
            myViewHolder.setPayment(false);
            myViewHolder.layout.setBackgroundResource(R.drawable.item_outline_dark);
        }

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
        RentRVAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        CircleImageView imageView;
        TextView name;
        TextView address;
        TextView date;
        TextView hours;
        LinearLayout layout;

        public void setPayment(boolean payment) {
            this.payment = payment;
        }

        boolean payment = false;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.rfv_image);
            name = itemView.findViewById(R.id.rfv_name);
            address = itemView.findViewById(R.id.rfv_address);
            date = itemView.findViewById(R.id.rfv_date);
            hours = itemView.findViewById(R.id.rfv_hours);
            layout =  itemView.findViewById(R.id.rental_facility_view_layout);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);

            if(payment){
                layout.setBackgroundResource(R.drawable.item_outline_green);
            }else{
                layout.setBackgroundResource(R.drawable.item_outline_dark);
            }

        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);

            layout.setBackgroundResource(R.drawable.item_outline);

            return true;
        }
    }
}
