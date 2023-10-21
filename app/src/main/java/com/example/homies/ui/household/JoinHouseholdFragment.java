package com.example.homies.ui.household;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.homies.R;
import com.google.firebase.auth.FirebaseAuth;

import timber.log.Timber;

public class JoinHouseholdFragment extends Fragment implements View.OnClickListener {
    HouseholdActivity householdActivity;
    View view;
    FirebaseAuth mAuth;
    private final String TAG = getClass().getSimpleName();

    public JoinHouseholdFragment(HouseholdActivity householdActivity) {
        this.householdActivity = householdActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_join_household, container, false);
        Timber.tag(TAG).d("onCreateView()");

        mAuth = FirebaseAuth.getInstance();


        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
