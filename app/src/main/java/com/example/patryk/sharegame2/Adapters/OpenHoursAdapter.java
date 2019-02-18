package com.example.patryk.sharegame2.Adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.patryk.sharegame2.AddFacility;
import com.example.patryk.sharegame2.Objects.Day;
import com.example.patryk.sharegame2.Objects.SportItem;
import com.example.patryk.sharegame2.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OpenHoursAdapter extends RecyclerView.Adapter<OpenHoursAdapter.MyViewHolder> {

    Context mContext;
    private List<Day> mData;
    private ArrayList<SportItem> removedSportItem;
    private int minHour;

    public OpenHoursAdapter(Context mContext, List<Day> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.removedSportItem = new ArrayList<>();
    }

    public List<Day> getmData() {
        return mData;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.open_hour_row,viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {


        myViewHolder.rv_day_name.setText(mData.get(i).getName());
        myViewHolder.rv_day_open.setText(mData.get(i).getOpenHour());
        myViewHolder.rv_day_close.setText(mData.get(i).getCloseHour());

        final int pos = i;

        if(i == getItemCount()-1){
            myViewHolder.divide.setVisibility(View.GONE);
        }

        myViewHolder.rv_day_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = Integer.parseInt(mData.get(pos).getOpenHour().substring(0,2));
                int minute = Integer.parseInt(mData.get(pos).getOpenHour().substring(3,5));

                CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(mContext,
                        myViewHolder.timeStartSetListener, hour, minute, true);
                timeDialog.setMaxHour(23);
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timeDialog.show();

            }
        });

        myViewHolder.timeStartSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                mData.get(pos).setOpenHour(hour);
                mData.get(pos).setOpenHours();
                notifyDataSetChanged();
                minHour = hourOfDay;
            }
        };

        //endhour

        myViewHolder.rv_day_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = (String) myViewHolder.rv_day_open.getText().toString();
                int hour = Integer.parseInt(time.substring(0, 2));
                int minute = Integer.parseInt(time.substring(3, 5));

                CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(mContext,
                        myViewHolder.timeEndSetListener, hour, minute, true);

                if (minute > 0) {
                    timeDialog.setMinHour(minHour + 1);
                } else {
                    timeDialog.setMinHour(minHour);
                }

                timeDialog.setMaxHour(24);
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timeDialog.show();
            }
        });

        myViewHolder.timeEndSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hour = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                mData.get(pos).setCloseHour(hour);
                mData.get(pos).setOpenHours();
                notifyDataSetChanged();
            }
        };

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TimePickerDialog.OnTimeSetListener timeStartSetListener;
        TimePickerDialog.OnTimeSetListener timeEndSetListener;
        TextView rv_day_name;
        TextView rv_day_open;
        TextView rv_day_close;
        LinearLayout rv_day_layout;
        TextView divide;

        public MyViewHolder(View itemView) {
            super(itemView);

            divide = itemView.findViewById(R.id.day_divide);
            rv_day_name = itemView.findViewById(R.id.day_name);
            rv_day_open = itemView.findViewById(R.id.day_timestart);
            rv_day_close = itemView.findViewById(R.id.day_timeend);
            rv_day_layout = itemView.findViewById(R.id.day_layout);
        }
    }
}
