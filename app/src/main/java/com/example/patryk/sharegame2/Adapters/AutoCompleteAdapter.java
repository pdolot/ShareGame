package com.example.patryk.sharegame2.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.patryk.sharegame2.R;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private List<String> citiesList;

    public AutoCompleteAdapter(@NonNull Context context, @NonNull List<String> cityList) {
        super(context, 0, cityList);
        citiesList = new ArrayList<>(cityList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return cityFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                   R.layout.city_row, parent, false
            );
        }

        TextView city_name = convertView.findViewById(R.id.city_name);

        String city = getItem(position);

        if (city != null) {
            city_name.setText(city);
        }

        return convertView;
    }

    private Filter cityFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(citiesList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (String city : citiesList) {
                    if (city.toLowerCase().contains(filterPattern)) {
                        suggestions.add(city);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((String) resultValue);
        }
    };
}
