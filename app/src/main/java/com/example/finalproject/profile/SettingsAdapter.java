package com.example.finalproject.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalproject.R;

public class SettingsAdapter extends ArrayAdapter<String> {

        Context context;
        String[] titles;
        int[] icons;

        public SettingsAdapter(Context c, String[] t, int[] i) {
            super(c, R.layout.settings_row, t);
            context = c;
            titles = t;
            icons = i;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View row = inflater.inflate(R.layout.settings_row, parent, false);

            ImageView icon = row.findViewById(R.id.setting_icon);
            TextView title = row.findViewById(R.id.setting_title);

            title.setText(titles[position]);
            icon.setImageResource(icons[position]);

            return row;
        }
    }

