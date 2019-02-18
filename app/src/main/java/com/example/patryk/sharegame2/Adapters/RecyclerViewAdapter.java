package com.example.patryk.sharegame2.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.Objects.SportItem;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<SportItem> mData;
    private ArrayList<SportItem> removedSportItem;

    public RecyclerViewAdapter(Context mContext, List<SportItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.removedSportItem = new ArrayList<>();
    }

    public List<SportItem> getmData() {
        return mData;
    }

    public List<SportItem> getRemovedSportItem() {
        return removedSportItem;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.sports_recyclerview_row,viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {

        myViewHolder.rv_sport_name.setText(mData.get(i).getSportName());
        myViewHolder.rv_sport_icon.setBackgroundResource(mData.get(i).getSportIcon());
        myViewHolder.rv_remove_icon.setBackgroundResource(R.drawable.ic_remove);

        myViewHolder.rv_remove_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removedSportItem.add(mData.get(i));
                mData.remove(i);

                notifyDataSetChanged();
            }
        });
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView rv_sport_name;
        Button rv_sport_icon;
        Button rv_remove_icon;

        public MyViewHolder(View itemView) {
            super(itemView);

            rv_sport_name = itemView.findViewById(R.id.rv_sport_name);
            rv_sport_icon = itemView.findViewById(R.id.rv_sport_icon);
            rv_remove_icon = itemView.findViewById(R.id.rv_remove_icon);
        }
    }
}
