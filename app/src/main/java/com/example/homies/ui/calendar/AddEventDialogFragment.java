package com.example.homies.ui.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.homies.R;
import com.example.homies.model.CalendarEvent;
import com.google.firebase.Timestamp;

import java.util.Date;

import timber.log.Timber;

public class AddEventDialogFragment extends DialogFragment {
    private final String TAG = AddEventDialogFragment.class.getSimpleName();
    private static final String PREFERENCES = "MyPreferences";
    private static final String PREF_THEME_KEY = "theme";
    private static final String ARG_EVENT = "event";
    private static final String ARG_POSITION = "position";
    private OnEventAddedListener addedListener;
    private OnEventUpdatedListener updatedListener;

    public interface OnEventAddedListener {
        void onEventAdded(String eventName, Timestamp eventDateTime);
    }

    public interface OnEventUpdatedListener {
        void onEventUpdated(String oldEventName, String newEventName, Timestamp newDateTime, int position);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Retrieve the current theme preference
        boolean isDarkModeEnabled = isDarkModeEnabled(requireContext());
        int themeId = isDarkModeEnabled ? R.style.PickerThemeDark : R.style.PickerThemeLight;

        // Create a themed context
        Context themedContext = new ContextThemeWrapper(requireContext(), themeId);

        // Create a themed AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(themedContext);

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(themedContext);
        View view = inflater.inflate(R.layout.dialog_add_event, null);

        final EditText eventNameEditText = view.findViewById(R.id.editTextEventName);
        final DatePicker datePicker = view.findViewById(R.id.datePicker);
        final TimePicker timePicker = view.findViewById(R.id.timePicker);

        // Check if this is an edit operation
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_EVENT) && args.containsKey(ARG_POSITION)) {
            CalendarEvent event = (CalendarEvent) args.getSerializable(ARG_EVENT);
            int position = args.getInt(ARG_POSITION);

            if (event != null) {
                eventNameEditText.setText(event.getEventName());

                // Convert Firebase Timestamp to Date
                Date eventDate = event.getEventDateTime().toDate();

                // Extract year, month, day, hour, minute from the Date
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(eventDate);

                datePicker.updateDate(cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH));
                timePicker.setHour(cal.get(java.util.Calendar.HOUR_OF_DAY));
                timePicker.setMinute(cal.get(java.util.Calendar.MINUTE));
            }

            builder.setTitle(R.string.edit_event)
                    .setPositiveButton(R.string.save, (dialog, id) -> {
                        // Get updated event name from EditText
                        String newEventName = eventNameEditText.getText().toString();

                        // Get updated date from DatePicker
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();

                        // Get updated time from TimePicker
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();

                        // Create a Calendar instance for updated date and time
                        java.util.Calendar updatedDateTimeCalendar = java.util.Calendar.getInstance();
                        updatedDateTimeCalendar.set(year, month, day, hour, minute);

                        // Convert Calendar instance to Date object
                        Date newEventDate = updatedDateTimeCalendar.getTime();

                        // Convert Date object to Firebase Timestamp
                        Timestamp newEventDateTime = new Timestamp(newEventDate);

                        // Notify the listener
                        if (updatedListener != null) {
                            updatedListener.onEventUpdated(event.getEventName(), newEventName, newEventDateTime, position);
                        }
                    });
        } else {
            // This is an add operation
            builder.setTitle(R.string.add_event)
                    .setPositiveButton(R.string.save, (dialog, id) -> {
                        // Get event name from EditText
                        String eventName = eventNameEditText.getText().toString();

                        // Get selected date from DatePicker
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();

                        // Get selected time from TimePicker
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();

                        // Create a Calendar instance for date and time
                        java.util.Calendar dateTimeCalendar = java.util.Calendar.getInstance();
                        dateTimeCalendar.set(year, month, day, hour, minute);

                        // Convert Calendar instance to Date object
                        Date dateTime = dateTimeCalendar.getTime();

                        // Convert Date object to Firebase Timestamp
                        Timestamp eventDateTime = new Timestamp(dateTime);

                        // Notify the listener (CalendarFragment)
                        if (addedListener != null) {
                            addedListener.onEventAdded(eventName, eventDateTime);
                        }
                    });
        }

        builder.setView(view)
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                    Timber.tag(TAG).d("Dialog cancelled");
                    AddEventDialogFragment.this.getDialog().cancel();
                });

        AlertDialog alertDialog = builder.create();

        // Apply text color based on the theme when the dialog is shown
        alertDialog.setOnShowListener(dialogInterface -> {
            // Get the positive and negative buttons and set the text color
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            if (positiveButton != null && negativeButton != null) {
                int textColor = isDarkModeEnabled
                        ? ContextCompat.getColor(requireContext(), android.R.color.white)
                        : ContextCompat.getColor(requireContext(), android.R.color.black);

                positiveButton.setTextColor(textColor);
                negativeButton.setTextColor(textColor);
            }
        });

        return alertDialog;
    }

    private boolean isDarkModeEnabled(Context context) {
        // Retrieve the current theme preference
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        int themeId = preferences.getInt(PREF_THEME_KEY, R.style.Theme_Homies_Light);
        return themeId == R.style.Theme_Homies_Dark;
    }

    // Method to create a new instance of the dialog for editing
    public static AddEventDialogFragment newInstanceForEdit(CalendarEvent event, int position) {
        AddEventDialogFragment fragment = new AddEventDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnEventAddedListener(OnEventAddedListener listener) {
        this.addedListener = listener;
    }

    public void setOnEventUpdatedListener(OnEventUpdatedListener listener) {
        this.updatedListener = listener;
    }
}
