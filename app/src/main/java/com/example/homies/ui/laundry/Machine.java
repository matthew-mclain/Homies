package com.example.homies.ui.laundry;

import java.util.ArrayList;

public class Machine {
    String name, usedBy, endAt;
    public Machine(String name, String usedBy, String endAt){
        this.name = name;
        this.usedBy = usedBy;
        this.endAt = endAt;
    }

    public ArrayList<Machine> getLaundryMachinesList(String householdID){
        ArrayList<Machine> result = new ArrayList<Machine>();
        result.add(new Machine("machine 1", null, null));
        result.add(new Machine("machine 2", "Sarah", "12:00 PM"));
        return result;
    }

    //gets
    public String getName(){ return name; }
    public String getUsedBy(){ return usedBy; }
    public String getEndAt(){ return endAt; }
}
