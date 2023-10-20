package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Calendar {

    private static String householdID, eventTitle, eventDetail;
    private static Timestamp startTime, endTime;

    private static FirebaseFirestore db;

    public Calendar(String eventTitle, String eventDetail, Timestamp startTime, Timestamp endTime, String householdID){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.householdID = householdID;
    }

    public static void createEvent(String eventID, String eventTitle, String eventDetail, Timestamp startTime, Timestamp endTime){

        Calendar event = new Calendar(eventTitle, eventDetail, startTime, endTime, householdID);

        db = MyApplication.getDbInstance();
        db.collection("calendar")
                .document(eventID)
                .set(event);
    }

    public static void deleteEvent(String eventID){
        db = MyApplication.getDbInstance();
        db.collection("calendar")
                .document(eventID)
                .delete();
    }

    public static void changeEvent(String eventID, String fieldName, Object obj) {
        db = MyApplication.getDbInstance();
        db.collection("calendar")
                .document(eventID)
                .update(fieldName, obj);
    }

}
