package com.example.homies.model;

import androidx.annotation.NonNull;

import com.example.homies.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import timber.log.Timber;

public class LaundryList {

    private String householdId;
    private static FirebaseFirestore db;
    private static final String TAG = LaundryList.class.getSimpleName();

    public LaundryList(){}
    public LaundryList(String householdId) { this.householdId = householdId; }

    public String getHouseholdId() {return householdId;}

    public static void createLaundryList(String householdID) {
        LaundryList laundryList = new LaundryList(householdID);

        db = MyApplication.getDbInstance();
        db.collection("laundry_managers")
                .add(laundryList)
                .addOnSuccessListener(documentReference -> {
                    Timber.tag(TAG).d("Laundry List Created Successfully: %s", documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Timber.tag(TAG).e("Error creating laundry list");
                });
    }

    public void addLaundryMachine(String machineName){
        if (householdId != null){
            db = MyApplication.getDbInstance();
            Machine machine = new Machine(machineName, null, null);
            Timber.tag(TAG).d("New Laundry Machine Adding in %s: ", householdId);

            db.collection("laundry_managers")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                db.collection("laundry_managers")
                                        .document(doc.getId())
                                        .collection("laundry_machines")
                                        .add(machine);
                            } else{
                                Timber.tag(TAG).e("Error adding laundry machine in laundry list in household %s", householdId);
                            }
                        }
                    });
        }
    }

    public void deleteMachine(String machineName){
        if (householdId != null) {
            // TO DO: check if dryer is being used
            db = MyApplication.getDbInstance();
            db.collection("laundry_managers")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            db.collection("laundry_managers")
                                    .document(doc.getId())
                                    .collection("laundry_machines")
                                    .whereEqualTo("name", machineName)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot machine = task.getResult().getDocuments().get(0);
                                            db.collection("laundry_managers")
                                                    .document(doc.getId())
                                                    .collection("laundry_machines")
                                                    .document(machine.getId())
                                                    .delete();
                                            Timber.tag(TAG).d("machine delete success");
                                        }
                                    });

                        }
                    });
        }
    }

    public void updateMachineName(String originalName, String newName) {
        if (householdId != null)
            // TO DO: check if dryer is being used
            db = MyApplication.getDbInstance();
        db.collection("laundry_managers")
                .whereEqualTo("householdId", householdId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        db.collection("laundry_managers")
                                .document(doc.getId())
                                .collection("laundry_machines")
                                .whereEqualTo("name", originalName)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        DocumentSnapshot machine = task.getResult().getDocuments().get(0);
                                        db.collection("laundry_managers")
                                                .document(doc.getId())
                                                .collection("laundry_machines")
                                                .document(machine.getId())
                                                .update("name", newName);
                                        Timber.tag(TAG).d("machine name change success");
                                    }
                                });

                    }
                });
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Handle the case where the user is not signed in
            Timber.tag(TAG).d("Current user is null. User not signed in.");
            return null;
        }
    }

    public void updateMachineStatus(String machineName, String endTime) {
        if (householdId != null && endTime != null){
            // TO DO: check if dryer is being used
            db = MyApplication.getDbInstance();
            db.collection("laundry_managers")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            db.collection("laundry_managers")
                                    .document(doc.getId())
                                    .collection("laundry_machines")
                                    .whereEqualTo("name", machineName)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot machine = task.getResult().getDocuments().get(0);
                                            db.collection("laundry_managers")
                                                    .document(doc.getId())
                                                    .collection("laundry_machines")
                                                    .document(machine.getId())
                                                    .update("usedBy", getCurrentUserId(), "endAt", endTime);
                                            Timber.tag(TAG).d("machine status change success");
                                        }
                                    });

                        }
                    });
        } else if (householdId != null && endTime == null){
            db = MyApplication.getDbInstance();
            db.collection("laundry_managers")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            db.collection("laundry_managers")
                                    .document(doc.getId())
                                    .collection("laundry_machines")
                                    .whereEqualTo("name", machineName)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot machine = task.getResult().getDocuments().get(0);
                                            db.collection("laundry_managers")
                                                    .document(doc.getId())
                                                    .collection("laundry_machines")
                                                    .document(machine.getId())
                                                    .update("usedBy", null, "endAt", null);
                                            Timber.tag(TAG).d("machine status change success");
                                        }
                                    });

                        }
                    });
        }

    }
}
