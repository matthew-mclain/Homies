package com.example.homies.ui.household;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.example.homies.MainActivity;
import com.example.homies.R;
import com.example.homies.model.Household;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import timber.log.Timber;

public class CreateHouseholdFragment extends Fragment implements View.OnClickListener{
    HouseholdActivity householdActivity;
    View view;
    private EditText editTextHouseholdName;
    private Button buttonCreate;
    private Button buttonBack;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private final String TAG = getClass().getSimpleName();

    public CreateHouseholdFragment(HouseholdActivity householdActivity) {
        this.householdActivity = householdActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_household, container, false);
        Timber.tag(TAG).d("onCreateView()");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        editTextHouseholdName = view.findViewById(R.id.editTextHouseholdName);

        buttonCreate = view.findViewById(R.id.buttonCreate);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonCreate.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Timber.tag(TAG).d("onClick()");

        if (view.getId() == R.id.buttonCreate) {
            String householdName = String.valueOf(editTextHouseholdName.getText());

            if (!TextUtils.isEmpty(householdName) && currentUser != null) {
                String userId = currentUser.getUid();
                Household.createHousehold(householdName, userId);
            } else {
                    // Handle the case where user data is not available (user not found or error occurred)
                    // You might want to show an error message to the user in this case.
            }
        }
        else if (view.getId() == R.id.buttonBack) {
            householdActivity.showButtons(this.view);
        }
    }
}
