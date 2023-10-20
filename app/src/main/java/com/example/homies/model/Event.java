package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import timber.log.Timber;

public class Event {

    private static String householdID, eventTitle, eventDetail;
    private static Timestamp startTime, endTime;

    private static FirebaseFirestore db;


    private static final String TAG = Event.class.getSimpleName();

    public Event(String eventTitle, String eventDetail, Timestamp startTime, Timestamp endTime, String householdID){
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.householdID = householdID;
    }

    public static void createEvent(String eventID, String eventTitle, String eventDetail, Timestamp startTime, Timestamp endTime){

        Event event = new Event(eventTitle, eventDetail, startTime, endTime, householdID);

        db = MyApplication.getDbInstance();
        db.collection("calendar")
                .document(eventID)
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    // User removed from household successfully
                    Timber.tag(TAG).d("Event Added: %s", eventID);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error adding event: %s", eventID);
                });
    }

    public static void deleteEvent(String eventID){
        db = MyApplication.getDbInstance();
        db.collection("calendar")
                .document(eventID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // User removed from household successfully
                    Timber.tag(TAG).d("Event deleted: %s", eventID);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error deleting event: %s", eventID);
                });
    }


    public static void changeEvent(String eventID, String fieldName, Object obj) {
        db = MyApplication.getDbInstance();
        db.collection("calendar")
                .document(eventID)
                .update(fieldName, obj)
                .addOnSuccessListener(aVoid -> {
                    // User removed from household successfully
                    Timber.tag(TAG).d("Event changed: %s", eventID);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error changing event: %s", eventID);
                });
    }

}
