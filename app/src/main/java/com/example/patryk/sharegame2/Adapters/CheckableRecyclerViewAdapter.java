package com.example.patryk.sharegame2.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.Objects.SportItem;

import java.util.List;

public class CheckableRecyclerViewAdapter extends RecyclerView.Adapter<CheckableRecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<SportItem> mData;

    public CheckableRecyclerViewAdapter(Context mContext, List<SportItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.sports_recyclerview_row2,viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        myViewHolder.rv_sport_name.setText(mData.get(i).getSportName());
        myViewHolder.rv_sport_check.setChecked(mData.get(i).getChecked());

        myViewHolder.rv_sport_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mData.get(i).setChecked(true);
                }else{
                    mData.get(i).setChecked(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CheckBox rv_sport_check;
        TextView rv_sport_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            rv_sport_check = itemView.findViewById(R.id.rv2_sport_check);
            rv_sport_name = itemView.findViewById(R.id.rv2_sport_name);

        }
    }
}
