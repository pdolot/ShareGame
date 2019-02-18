package com.example.patryk.sharegame2.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.patryk.sharegame2.Objects.FacilityImage;
import com.example.patryk.sharegame2.Objects.UserRent;
import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.SQLite.DatabaseHelper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryRVAdapter extends RecyclerView.Adapter<HistoryRVAdapter.MyViewHolder>{


    private static ClickListener clickListener;

    private Context mContext;
    private List<UserRent> mData;
    private DatabaseHelper databaseHelper;


    public HistoryRVAdapter(Context mContext, List<UserRent> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public List<UserRent> getmData() {
        return mData;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.history_row,viewGroup, false);
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
        myViewHolder.rentalPrice.setText(String.format("%.2f",mData.get(i).getAmount())+ " zł");

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
        HistoryRVAdapter.clickListener = clickListener;
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
        TextView rentalPrice;
        LinearLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.uh_image);
            name = itemView.findViewById(R.id.uh_name);
            address = itemView.findViewById(R.id.uh_address);
            date = itemView.findViewById(R.id.uh_date);
            hours = itemView.findViewById(R.id.uh_hours);
            layout =  itemView.findViewById(R.id.user_history_layout);
            rentalPrice = itemView.findViewById(R.id.uh_rentalPrice);

            layout.setOnLongClickListener(this);
            layout.setOnClickListener(this);
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
