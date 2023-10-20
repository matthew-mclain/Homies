package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;

public class Laundry {
    private static FirebaseFirestore db;

    private static String householdID;
    private static String machineName;
    private static String usedBy;
    private static Timestamp startTime;
    private static Number durationMin;

    public Laundry(String householdID, String machineName){
        this.householdID = householdID;
        this.machineName = machineName;
        startTime = null;
        durationMin = 60;
        usedBy = null;
    }

    public static void getMachine(String machineID){
        db = MyApplication.getDbInstance();
        db.collection("laundry")
                .document(machineID)
                .get();
    }

    public static void addMachine(String machineID, String machineName){
        Laundry machine = new Laundry(householdID, machineName);

        db = MyApplication.getDbInstance();
        db.collection("laundry")
                .document(machineID)
                .set(machine);
    }

    public static void deleteMachine(String machineID){
        // TO DO: check if dryer is being used
        db = MyApplication.getDbInstance();
        db.collection("laundry")
                .document(machineID)
                .delete();
    }
}
