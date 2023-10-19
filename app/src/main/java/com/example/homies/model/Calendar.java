package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Calendar {

    private static String householdID;

    private static FirebaseFirestore db;

    public Calendar(String householdID){
        this.householdID = householdID;
    }

    public static void createEvent(String eventTitle, String eventDetail, Timestamp startTime, Timestamp endTime){
        String eventID = "event"+householdID+new Date().getTime();

        CalendarEvent event = new CalendarEvent(eventID, eventTitle, eventDetail, startTime, endTime, householdID);

        db = MyApplication.getDbInstance();
        db.collection("calendar")
                .document(eventID)
                .set(event);
    }

}
