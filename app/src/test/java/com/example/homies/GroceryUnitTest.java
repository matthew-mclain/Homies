package com.example.homies;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.homies.model.GroceryItem;

import java.util.ArrayList;

public class GroceryUnitTest {
    @Test
    public void getGroceryItemName_isCorrect(){
        GroceryItem groceryItem = new GroceryItem("egg");
        assertEquals("egg", groceryItem.getItemName());
    }
}
