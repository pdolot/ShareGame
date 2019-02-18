package com.example.patryk.sharegame2.MainFragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.patryk.sharegame2.Adapters.AutoCompleteAdapter;
import com.example.patryk.sharegame2.Adapters.RecyclerViewAdapter;
import com.example.patryk.sharegame2.Adapters.CustomTimePickerDialog;
import com.example.patryk.sharegame2.IntroActivity;
import com.example.patryk.sharegame2.Objects.SportItem;
import com.example.patryk.sharegame2.R;
import com.example.patryk.sharegame2.SearchResultActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class advancedSearchFragment extends Fragment {

    private ArrayList<SportItem> sports;
    private SportCategoryAdapter sportCategoryAdapter;
    private Spinner sportSpinner;

    private RecyclerView sportRecyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<SportItem> rv_sports;

    private List<String> citiesList;

    // data

    private TextView date;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    // start hour

    private TextView timestart;
    // end hour

    private TextView timeend;
    private int minHour = 0;

    // SeekBar
    private SeekBar amount;
    private TextView amountValue;
    private int maxValue = 200;
    private int stepValue = 5;
    private int currentValue;

    private TimePickerDialog.OnTimeSetListener timeStartSetListener;
    private TimePickerDialog.OnTimeSetListener timeEndSetListener;

    private LinearLayout search;

    public advancedSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);
        initSports();
        initCitiesName();

        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDay = dateFormat.format(calendar.getTime());

        // findView
        search = view.findViewById(R.id.searchbutton);

        amount = view.findViewById(R.id.amount);
        amountValue = view.findViewById(R.id.amountvalue);

        date = view.findViewById(R.id.date);
        LinearLayout dateLayout = view.findViewById(R.id.datepicker);

        timestart = view.findViewById(R.id.timestart);
        final LinearLayout timePicker = view.findViewById(R.id.timepicker);

        timeend = view.findViewById(R.id.timeend);

        final AutoCompleteTextView searchCity = view.findViewById(R.id.search_city);
        LinearLayout layout = view.findViewById(R.id.searchLayout);

        sportSpinner = view.findViewById(R.id.choose_sport);
        sportRecyclerView = view.findViewById(R.id.rv_choosed_sport);

        // ================================


        // Seekbar
        amount.setMax(maxValue / stepValue);
        amount.setProgress(maxValue / stepValue / 2);

        currentValue =( maxValue / stepValue / 2) * stepValue;
        amountValue.setText(String.valueOf((maxValue / stepValue / 2) * stepValue)+" zł/h");

        amount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                currentValue = progress * stepValue;
                amountValue.setText(String.valueOf(currentValue) + " zł/h");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // DatePicker
        date.setText(currentDay);

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                dateDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();

            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year_, int month_, int dayOfMonth_) {
                month_ += 1;

                date.setText(String.format("%02d",dayOfMonth_)+"/"+String.format("%02d",month_)+"/"+String.valueOf(year_));
            }
        };

        // TimePicker

        //starthour

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(getContext(),
                        timeStartSetListener,hour,minute,true);
                timeDialog.setMaxHour(23);
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timeDialog.show();

            }
        });

        timeStartSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                timestart.setText(String.format("%02d",hourOfDay)+":"+ String.format("%02d",minute));
                timeend.setText(String.format("%02d",hourOfDay)+":"+String.format("%02d",minute));
                minHour = hourOfDay;
            }
        };

        //endhour

        timeend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String time = (String) timestart.getText();
                int hour = Integer.parseInt(time.substring(0,2));
                int minute = Integer.parseInt(time.substring(3,5));

                CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(getContext(),
                        timeEndSetListener,hour,minute,true);

                if(minute>0){
                    timeDialog.setMinHour(minHour+1);
                }else{
                    timeDialog.setMinHour(minHour);
                }

                timeDialog.setMaxHour(24);
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timeDialog.show();
            }
        });

        timeEndSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                timeend.setText(String.format("%02d",hourOfDay)+":"+String.format("%02d",minute));
            }
        };


        // Searching city
        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getContext(), citiesList);
        searchCity.setAdapter(adapter);

        // Setting sport categories
        sportCategoryAdapter = new SportCategoryAdapter(getContext(),sports);
        sportSpinner.setAdapter(sportCategoryAdapter);

        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),rv_sports);

        sportRecyclerView.setAdapter(recyclerViewAdapter);
        sportRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sportSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sports.get(0).setSportIcon(R.drawable.ic_arrowtop);
                sports.get(0).setSportName("Zwiń listę");

                if(recyclerViewAdapter.getRemovedSportItem().size() != 0){
                    for(int i=0;i<recyclerViewAdapter.getRemovedSportItem().size();i++)
                    {
                        sports.add(recyclerViewAdapter.getRemovedSportItem().get(i));
                    }

                    recyclerViewAdapter.getRemovedSportItem().clear();
                }

                searchCity.setFocusable(false);
                searchCity.setFocusableInTouchMode(false);
                searchCity.setFocusable(true);
                searchCity.setFocusableInTouchMode(true);
                return false;

            }
        });

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        sports.get(position).setSportIcon(R.drawable.ic_arrowdown);
                        sports.get(position).setSportName("Wybierz sport");
                        sportSpinner.setSelection(0);
                        break;
                    default:
                        rv_sports.add(sports.get(position));
                        recyclerViewAdapter.notifyDataSetChanged();
                        sports.remove(position);
                        sports.get(0).setSportIcon(R.drawable.ic_arrowdown);
                        sports.get(0).setSportName("Wybierz sport");
                        sportSpinner.setSelection(0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCity.setFocusable(false);
                searchCity.setFocusableInTouchMode(false);
                searchCity.setFocusable(true);
                searchCity.setFocusableInTouchMode(true);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = String.valueOf(searchCity.getText());
                String[] categories = new String[recyclerViewAdapter.getmData().size()];

                for(int i=0;i<recyclerViewAdapter.getmData().size();i++){
                    categories[i] = recyclerViewAdapter.getmData().get(i).getSportName();
                }
                String getdate = String.valueOf(date.getText());
                String start = String.valueOf(timestart.getText());
                String end= String.valueOf(timeend.getText());
                int amount = currentValue;

                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra("city", city);
                intent.putExtra("categories",categories);
                intent.putExtra("date", getdate);
                intent.putExtra("start",start);
                intent.putExtra("end",end);
                intent.putExtra("amount",amount);
                startActivity(intent);
            }
        });


        return view;
    }

    public void initCitiesName(){
        citiesList = new ArrayList<>();
        citiesList.add("Łódź");
        citiesList.add("Warszawa");
        citiesList.add("Kraków");
        citiesList.add("Katowice");
        citiesList.add("Gdańsk");
    }

    public void initSports(){
        sports = new ArrayList<>();
        sports.add(new SportItem("Wybierz sport",R.drawable.ic_arrowdown));
        sports.add(new SportItem("Piłka nożna",R.drawable.ic_soccer));
        sports.add(new SportItem("Futsal",R.drawable.ic_futsal));
        sports.add(new SportItem("Siatkówka",R.drawable.ic_voleyball));
        sports.add(new SportItem("Koszykówka",R.drawable.ic_basketball));
        sports.add(new SportItem("Tenis",R.drawable.ic_tenis));
        sports.add(new SportItem("Squash",R.drawable.ic_squash));
        sports.add(new SportItem("Piłka ręczna",R.drawable.ic_handball));
        sports.add(new SportItem("Badminton",R.drawable.ic_badminton));

        rv_sports = new ArrayList<>();

    }


    public class SportCategoryAdapter extends ArrayAdapter<SportItem> {

        public SportCategoryAdapter(Context context, ArrayList<SportItem> countryList) {
            super(context, 0, countryList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        private View initView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.sport_spinner_row, parent, false
                );
            }

            ImageView sportIcon = convertView.findViewById(R.id.sport_icon);
            TextView sportName = convertView.findViewById(R.id.sport_name);

            SportItem currentItem = getItem(position);

            if (currentItem != null) {
                sportIcon.setImageResource(currentItem.getSportIcon());
                sportName.setText(currentItem.getSportName());
            }

            return convertView;
        }
    }


}
