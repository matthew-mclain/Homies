package com.example.homies.model.viewmodel;

import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homies.MyApplication;
import com.example.homies.model.LaundryList;
import com.example.homies.model.Machine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LaundryViewModel extends ViewModel {

    MutableLiveData<LaundryList> selectedLaundry = new MutableLiveData<>();
    MutableLiveData<List<Machine>> selectedLaundryMachines = new MutableLiveData<>();
    private static FirebaseFirestore db;
    private static final String TAG  = LaundryViewModel.class.getSimpleName();

    public void getLaundryMachines(String householdId){
        db = MyApplication.getDbInstance();
        db.collection("laundry_list")
                .whereEqualTo("householdId", householdId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            LaundryList laundryList = doc.toObject(LaundryList.class);
                            setSelectedLaundryList(laundryList);
                            String laundryListId = doc.getId();
                            Timber.tag(TAG).d("Laundry list found with ID: %s", laundryListId);
                            fetchLaundryItems(laundryListId);
                        } else {
                            Timber.tag(TAG).e(task.getException(), "Error fetching laundry by householdId: %s", householdId);
                        }
                    }
                });

    }

    public void setSelectedLaundryList(LaundryList laundryList){
        selectedLaundry.setValue(laundryList);
    }

    private void fetchLaundryItems(String laundryListId){
        db.collection("laundry_list")
                .document(laundryListId)
                .collection("laundry_machines")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            List<Machine> items = new ArrayList<>();
                            for (DocumentSnapshot d : list){
                                Timber.tag(TAG).d("Laundry Machine: "+d.getData().get("name"));
                                Machine item = d.toObject(Machine.class);
                                items.add(item);
                            }
                            setSelectedMachines(items);
                        } else{
                            Timber.tag(TAG).e("No data found in Database");
                            selectedLaundryMachines.setValue(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.tag(TAG).e("Fail to load data from laundry list: %s", laundryListId);
                    }
                });
    }

    private void setSelectedMachines(List<Machine> items) {
        selectedLaundryMachines.setValue(items);
    }

    public void addLaundryMachine(String machineName){
        LaundryList laundryList = selectedLaundry.getValue();
        if(laundryList != null){
            laundryList.addLaundryMachine(machineName);
            Timber.tag(TAG).d(machineName + " added.");
        } else{
            Timber.tag(TAG).e("No Laundry List Selected");
        }
    }

    public void deleteMachine(String machineName){
        LaundryList laundryList = selectedLaundry.getValue();
        if (laundryList != null){
            laundryList.deleteMachine(machineName);
            Timber.tag(TAG).d("%s laundry machine deleted", machineName);
        } else{
            Timber.tag(TAG).d("No Laundry List Selected");
        }
    }

    public void updateMachineName(String originalName, String newName) {
        LaundryList laundryList = selectedLaundry.getValue();
        if(laundryList != null){
            laundryList.updateMachineName(originalName, newName);
            Timber.tag(TAG).d("laundry machine name changed from %s to %s", originalName, newName);
        } else{
            Timber.tag(TAG).d("No Laundry List Selected");
        }
    }


    public void updateMachineStatus(String machineName, int duration) {
        LaundryList laundryList = selectedLaundry.getValue();
        if(laundryList != null){
            if (duration != -1){
                String endTime = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime time = LocalDateTime.now();
                    int hour = time.getHour();
                    int min = time.getMinute() + duration;
                    if (min >= 60){
                        hour = (hour + 1) % 24;
                        min %= 60;
                    }
                    endTime += hour + ":" + min;
                } else{
                    Timber.tag(TAG).e("API version not correct for time values");
                }
                laundryList.updateMachineStatus(machineName, endTime);
            } else {
                laundryList.updateMachineStatus(machineName, null);
            }

        }
    }

    //gets
    public LiveData<List<Machine>> getLaundryMachines(){
        return selectedLaundryMachines;
    }

    public LiveData<LaundryList> getLaundryList(){
        return selectedLaundry;
    }

}
