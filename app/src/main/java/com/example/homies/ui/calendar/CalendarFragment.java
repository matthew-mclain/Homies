package com.example.homies.ui.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.homies.R;
import com.example.homies.model.CalendarEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import timber.log.Timber;

public class CalendarFragment extends Fragment implements View.OnClickListener {
    View view;
    private final String TAG = getClass().getSimpleName();
    private static final String PREFERENCES = "MyPreferences";
    private static final String PREF_THEME_KEY = "theme";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        Timber.tag(TAG).d("onCreateView()");

        // Assuming you have a button to add events
        FloatingActionButton actionButton = view.findViewById(R.id.fabAddEvent);
        actionButton.setOnClickListener(this);

        // Retrieve the current theme preference
        boolean isDarkModeEnabled = isDarkModeEnabled(requireContext());

        // Choose the background tint color based on the theme
        int backgroundColor = isDarkModeEnabled
                ? ContextCompat.getColor(requireContext(), R.color.purple_200)
                : ContextCompat.getColor(requireContext(), R.color.purple_500);

        // Choose the icon color based on the theme
        int iconColor = isDarkModeEnabled
                ? ContextCompat.getColor(requireContext(), android.R.color.black)
                : ContextCompat.getColor(requireContext(), android.R.color.white);

        // Set the background tint dynamically
        actionButton.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        // Set the icon color dynamically
        actionButton.setImageTintList(ColorStateList.valueOf(iconColor));

        return view;
    }

    @Override
    public void onClick(View v) {
        Timber.tag(TAG).d("onClick()");

        if (v.getId() == R.id.fabAddEvent) {
            AddEventDialogFragment addEventDialogFragment = new AddEventDialogFragment();
            addEventDialogFragment.show(getFragmentManager(), "AddEventDialogFragment");
        }
    }

    private boolean isDarkModeEnabled(Context context) {
        // Retrieve the current theme preference
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        int themeId = preferences.getInt(PREF_THEME_KEY, R.style.Theme_Homies_Light);
        return themeId == R.style.Theme_Homies_Dark;
    }
}
