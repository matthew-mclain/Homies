package com.example.homies.model;

import java.util.ArrayList;

public class Machine {
    String name, usedBy, endAt;

    public Machine(){}
    public Machine(String name, String usedBy, String endAt){
        this.name = name;
        this.usedBy = usedBy;
        this.endAt = endAt;
    }

    //gets
    public String getName(){ return name; }
    public String getUsedBy(){ return usedBy; }
    public String getEndAt(){ return endAt; }
}
