package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class LaundryMachine {

    private static Number durationMin;
    private static Timestamp startTime;
    private static boolean used;
    private static String usedBy;
    private static FirebaseFirestore db;

    public LaundryMachine(Number durationMin, Timestamp startTime, boolean used, String usedBy){
        this.durationMin = durationMin;
        this.startTime = startTime;
        this.used = used;
        this.usedBy = usedBy;
    }

    public static void getMachine(String machineID){
        db = MyApplication.getDbInstance();
        db.collection("laundry")
                .document(machineID)
                .get();
    }

    //gets for global variables
    public String getUsedBy() {
        return usedBy;
    }
    public Number getDurationMin() {return durationMin;}
    public Timestamp getStartTime() {return startTime;}
    public boolean getUsed() {return used;}
}
