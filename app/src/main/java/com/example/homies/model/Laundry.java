package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;

public class Laundry {
    // machines format
    // (dryer/washer)(householdID)(# assigned in order)

    private static FirebaseFirestore db;

    private static String householdID;

    public Laundry(String householdID){
        this.householdID = householdID;
    }

    public static void getMachine(String machineID){
        db = MyApplication.getDbInstance();
        db.collection("laundry")
                .document(machineID)
                .get();
    }

    public static void addDryer(){
        String machineID = "dryer"+householdID+2;

        LaundryMachine machine = new LaundryMachine(60, null, false, null);

        db = MyApplication.getDbInstance();
        db.collection("laundry")
                .document(machineID)
                .set(machine);
    }

    public static void addWasher(){
        String machineID = "washer"+householdID+3;

        LaundryMachine machine = new LaundryMachine(60, null, false, null);

        db = MyApplication.getDbInstance();
        db.collection("laundry")
                .document(machineID)
                .set(machine);
    }

    public static void deleteDryer(){
        // TO DO: check if dryer is being used
        db = MyApplication.getDbInstance();
        db.collection("laundry")
                .document("dryer"+householdID+2)
                .delete();
    }

    public static void deleteWasher(){
        // TO DO: check if washer is being used
        db = MyApplication.getDbInstance();
        db.collection("laundry")
                .document("washer"+householdID+2)
                .delete();
    }

}
