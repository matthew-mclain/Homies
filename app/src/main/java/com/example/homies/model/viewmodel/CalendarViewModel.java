package com.example.homies.model.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homies.MyApplication;
import com.example.homies.model.Calendar;
import com.example.homies.model.CalendarEvent;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CalendarViewModel extends ViewModel {
    private MutableLiveData<Calendar> selectedCalendar = new MutableLiveData<>();
    private MutableLiveData<List<CalendarEvent>> selectedEvents = new MutableLiveData<>();
    private static FirebaseFirestore db;
    private static final String TAG = CalendarViewModel.class.getSimpleName();

    public LiveData<List<CalendarEvent>> getSelectedEvents() {
        return selectedEvents;
    }

    public void setSelectedCalendar(Calendar calendar) {
        selectedCalendar.setValue(calendar);
    }

    public void setSelectedEvents(List<CalendarEvent> events) {
        selectedEvents.setValue(events);
    }

    public void getEventsForCalendar(String householdId) {
        db = MyApplication.getDbInstance();
        db.collection("calendars")
                .whereEqualTo("householdId", householdId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Calendar document found
                            Calendar calendar = document.toObject(Calendar.class);
                            setSelectedCalendar(calendar);
                            String calendarId = document.getId();
                            Timber.tag(TAG).d("Calendar found with ID: %s", calendarId);
                            fetchCalendarEventsFromSubcollection(calendarId);
                            return;
                        }
                    } else {
                        // Handle failures
                        Timber.tag(TAG).e(task.getException(), "Error fetching calendar for householdId: %s", householdId);
                    }
                });
    }

    private void fetchCalendarEventsFromSubcollection(String calendarId) {
        db.collection("calendars")
                .document(calendarId)
                .collection("calendar_events")
                .get()
                .addOnCompleteListener(eventsTask -> {
                    if (eventsTask.isSuccessful()) {
                        // Events fetched successfully, handle them
                        Timber.tag(TAG).d("Events fetched successfully for calendar ID: %s", calendarId);
                        List<CalendarEvent> events = new ArrayList<>();
                        for (QueryDocumentSnapshot document : eventsTask.getResult()) {
                            CalendarEvent event = document.toObject(CalendarEvent.class);
                            events.add(event);
                        }
                        setSelectedEvents(events);
                    } else {
                        // Handle failures
                        Timber.tag(TAG).e(eventsTask.getException(), "Error fetching calendar events for calendarId: %s", calendarId);
                        selectedEvents.setValue(null);
                    }
                });
    }

    public void addCalendarEvent(String eventName, Timestamp eventDateTime) {
        Timber.tag(TAG).d("addCalendarEvent: %s, %s", eventName, eventDateTime);

        Calendar calendar = selectedCalendar.getValue();
        String householdId = calendar.getHouseholdId();
        if (householdId != null) {
            CalendarEvent calendarEvent = new CalendarEvent(eventName, eventDateTime);

            db = MyApplication.getDbInstance();
            db.collection("calendars")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String calendarId = documentSnapshot.getId();

                            // Add the new event to the "calendar_events" subcollection
                            db.collection("calendars")
                                    .document(calendarId)
                                    .collection("calendar_events")
                                    .add(calendarEvent)
                                    .addOnSuccessListener(documentReference -> {
                                        // Event added to the subcollection successfully
                                        Timber.tag(TAG).d("Event added to subcollection successfully: %s", documentReference.getId());

                                        // Update selectedEvents LiveData with the new message
                                        List<CalendarEvent> currentEvents = selectedEvents.getValue();
                                        if (currentEvents == null) {
                                            currentEvents = new ArrayList<>();
                                        }
                                        currentEvents.add(calendarEvent);
                                        selectedEvents.setValue(currentEvents);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle errors
                                        Timber.tag(TAG).e(e, "Failed to add event to subcollection: %s", householdId);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Timber.tag(TAG).e(e, "Failed to query calendars: %s", householdId);
                    });

        } else {
            // Handle the case where no household is selected
            Timber.tag(TAG).d("No household selected.");
        }
    }

    public void deleteCalendarEvent(String eventName) {
        Calendar calendar = selectedCalendar.getValue();
        String householdId = calendar.getHouseholdId();
        if (householdId != null) {
            db = MyApplication.getDbInstance();
            db.collection("calendars")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            if (!documents.isEmpty()) {
                                DocumentSnapshot doc = documents.get(0);
                                db.collection("calendars")
                                        .document(doc.getId())
                                        .collection("calendar_events")
                                        .whereEqualTo("eventName", eventName)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                List<DocumentSnapshot> items = task1.getResult().getDocuments();
                                                if (!items.isEmpty()) {
                                                    DocumentSnapshot item = items.get(0);
                                                    db.collection("calendars")
                                                            .document(doc.getId())
                                                            .collection("calendar_events")
                                                            .document(item.getId())
                                                            .delete()
                                                            .addOnSuccessListener(aVoid ->
                                                                    Timber.tag(TAG).d("Event deleted successfully")
                                                            )
                                                            .addOnFailureListener(e ->
                                                                    Timber.tag(TAG).e(e, "Failed to delete event")
                                                            );
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    public void updateCalendarEvent(String oldEventName, String newEventName, Timestamp newEventDateTime) {
        Calendar calendar = selectedCalendar.getValue();
        String householdId = calendar.getHouseholdId();
        if (householdId != null) {
            db = MyApplication.getDbInstance();
            db.collection("calendars")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            if (!documents.isEmpty()) {
                                DocumentSnapshot doc = documents.get(0);
                                db.collection("calendars")
                                        .document(doc.getId())
                                        .collection("calendar_events")
                                        .whereEqualTo("eventName", oldEventName)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                List<DocumentSnapshot> items = task1.getResult().getDocuments();
                                                if (!items.isEmpty()) {
                                                    DocumentSnapshot item = items.get(0);

                                                    // Update both eventName and eventDateTime
                                                    Map<String, Object> updates = new HashMap<>();
                                                    updates.put("eventName", newEventName);
                                                    updates.put("eventDateTime", newEventDateTime);

                                                    db.collection("calendars")
                                                            .document(doc.getId())
                                                            .collection("calendar_events")
                                                            .document(item.getId())
                                                            .update(updates)
                                                            .addOnSuccessListener(aVoid ->
                                                                    Timber.tag(TAG).d("Event updated successfully")
                                                            )
                                                            .addOnFailureListener(e ->
                                                                    Timber.tag(TAG).e(e, "Failed to update event")
                                                            );
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

}
