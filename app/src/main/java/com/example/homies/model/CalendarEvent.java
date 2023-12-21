package com.example.homies.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class CalendarEvent implements Serializable {
    private String eventName;
    private Timestamp eventDateTime;

    public CalendarEvent(){}

    public CalendarEvent(String eventName, Timestamp eventDateTime){
        this.eventName = eventName;
        this.eventDateTime = eventDateTime;
    }

    public String getEventName() {
        return eventName;
    }

    public Timestamp getEventDateTime() {
        return eventDateTime;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDateTime(Timestamp eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
}
