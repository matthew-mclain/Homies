package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;

public class CalendarEvent {

    //eventID format
    //event(household ID)(time when it was created)

    private static String eventID, eventTitle, eventDetail, householdID;
    private static Timestamp startTime, endTime;
    private static FirebaseFirestore db;

    public CalendarEvent(String eventID, String eventTitle, String eventDetail,
                         Timestamp startTime, Timestamp endTime, String householdID) {
        this.eventID = eventID;
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.householdID = householdID;
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

    //gets for global variables
    public String getEventID() { return eventID; }

    public String getEventTitle() { return eventTitle; }

    public String getEventDetail() { return eventDetail; }

    public String getHouseholdID() { return householdID; }

    public Timestamp getStartTime() { return startTime; }

    public Timestamp getEndTime() { return endTime; }
}
