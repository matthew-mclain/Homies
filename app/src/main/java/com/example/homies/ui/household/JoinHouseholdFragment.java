package com.example.homies.ui.household;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.homies.MainActivity;
import com.example.homies.R;
import com.example.homies.model.Household;
import com.google.firebase.auth.FirebaseAuth;

import timber.log.Timber;

public class JoinHouseholdFragment extends Fragment implements View.OnClickListener {
    HouseholdActivity householdActivity;
    private Button buttonJoin;
    private Button buttonBack;
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

        buttonJoin = view.findViewById(R.id.buttonJoin);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonJoin.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Timber.tag(TAG).d("onClick()");

        if (view.getId() == R.id.buttonJoin) {
            //join
        }
        else if (view.getId() == R.id.buttonBack) {
            householdActivity.showButtons(this.view);
        }
    }
}
