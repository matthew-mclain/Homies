package com.example.homies.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.homies.R;
import com.example.homies.model.CalendarEvent;
import com.google.firebase.Timestamp;

import timber.log.Timber;

public class CalendarFragment extends Fragment implements View.OnClickListener {

    View view;

    EditText eventTitleET, eventDetailET, eventStartTimeET, eventStartDateET, eventEndTimeET, eventEndDateET;

    private final String TAG = getClass().getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        Timber.tag(TAG).d("onCreateView()");

        eventTitleET = view.findViewById(R.id.editTextEventTitle);
        eventDetailET = view.findViewById(R.id.editTextEventDetail);
        eventStartDateET = view.findViewById(R.id.editTextStartDate);
        eventStartTimeET = view.findViewById(R.id.editTextStartTime);
        eventEndDateET = view.findViewById(R.id.editTextEndDate);
        eventEndTimeET = view.findViewById(R.id.editTextEndTime);

        final Button addEventButton = view.findViewById(R.id.addEventButton);
        if(addEventButton != null){
            addEventButton.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Timber.tag(TAG).d("onClick()");

        if(v.getId() == R.id.addEventButton){
            Timber.tag(TAG).d("add Event");
            String eventTitle = eventTitleET.getText().toString();
            String eventDetail = eventDetailET.getText().toString();
            CalendarEvent event = new CalendarEvent(eventTitle, eventDetail, null, null, "123");
            event.createEvent("123", eventTitle, eventDetail, null, null);
        }
    }
}
