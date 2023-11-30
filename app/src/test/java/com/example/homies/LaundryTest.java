package com.example.homies;

import static org.junit.Assert.assertEquals;

import com.example.homies.model.Machine;

import org.junit.Before;
import org.junit.Test;

public class LaundryTest {

    private static final String name = "TEST NAME", usedBy = "USER", endAt = "0000";

    private Machine machine;

     @Before
    public void setUp(){
         machine = new Machine(name, usedBy, endAt);
     }

     @Test
    public void testGetters(){
         assertEquals(name, machine.getName());
         assertEquals(usedBy, machine.getUsedBy());
         assertEquals(endAt, machine.getEndAt());
     }

}
