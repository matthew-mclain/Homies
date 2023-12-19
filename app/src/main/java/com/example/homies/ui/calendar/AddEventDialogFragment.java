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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.homies.R;

public class AddEventDialogFragment extends DialogFragment {
    private static final String PREFERENCES = "MyPreferences";
    private static final String PREF_THEME_KEY = "theme";

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

        builder.setView(view)
                .setTitle(R.string.add_event)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle save button click
                        // Extract data from the views (eventNameEditText, datePicker, timePicker)
                        // and save the event
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        AddEventDialogFragment.this.getDialog().cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();

        // Apply text color based on the theme when the dialog is shown
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
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
}
