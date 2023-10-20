package com.example.homies.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.homies.R;
import com.google.firebase.firestore.FirebaseFirestore;

import timber.log.Timber;

public class CalendarFragment extends Fragment implements View.OnClickListener {

    View view;
    private static FirebaseFirestore db;
    private final String TAG = getClass().getSimpleName();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        Timber.tag(TAG).d("onCreateView()");

        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
