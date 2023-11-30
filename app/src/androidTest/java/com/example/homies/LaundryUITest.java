package com.example.homies;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LaundryUITest {

    @Rule
    public ActivityScenarioRule<SplashActivity> mActivityScenarioRule = new ActivityScenarioRule<>(SplashActivity.class);

    @Test
    public void laundryUITest(){

        try{
            Thread.sleep(5215);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //move to laundry page
        ViewInteraction laundryButton = onView(
                allOf(withId(R.id.navigation_laundry), isDisplayed())
        );
        laundryButton.perform(click());

        //open add laundry page
        ViewInteraction addPageOpenButton = onView(
                allOf(withId(R.id.openAddLaundryFragment), withText("Add Laundry Machine"), isDisplayed()));
        addPageOpenButton.perform(click());

        //press back to come back to laundry page
        ViewInteraction cancelAddPageOpenButton = onView(
                allOf(withId(R.id.cancelAddMachineButton), withText("Cancel"), isDisplayed()));
        cancelAddPageOpenButton.perform(click());

        //press add button again
        addPageOpenButton.perform(click());

        //enter a machine name to edit text
        ViewInteraction editTextMachineNameToAdd = onView(
                allOf(withId(R.id.editTextMachineNameToAdd), isDisplayed()));
        editTextMachineNameToAdd.perform(typeText("UI TEST"));

        //click add button to add machine
        ViewInteraction addMachineButton = onView(
                allOf(withId(R.id.addMachineButton), withText("Add"), isDisplayed()));
        addMachineButton.perform(click());

        //short break to check result
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //open start laundry page
        ViewInteraction startButton = onView(
                allOf(withId(R.id.useLaundryButton), withText("Start Laundry"), isDisplayed()));
        startButton.perform(click());

        //press back to come back to laundry page
        ViewInteraction backFromUseLaundryButton = onView(
                allOf(withId(R.id.cancelLaundryRun), withText("Cancel"), isDisplayed()));
        backFromUseLaundryButton.perform(click());

        //open start laundry page again
        startButton.perform(click());

        //press start to start laundry
        ViewInteraction runButton = onView(
                allOf(withId(R.id.startMachineButton), withText("Start"), isDisplayed()));
        runButton.perform(click());

        //short break to check result
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //press stop to stop laundry
        ViewInteraction stopButton = onView(
                allOf(withId(R.id.laundryStopButton), withText("Stop"), isDisplayed()));
        stopButton.perform(click());

        //short break to check result
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //press edit to edit machine
        ViewInteraction editButton = onView(
                allOf(withId(R.id.machineEditButton), withText("Edit"), isDisplayed()));
        editButton.perform(click());

        //press cancel
        ViewInteraction cancelEditButton = onView(
                allOf(withId(R.id.cancelMachineEditButton), withText("Cancel"), isDisplayed()));
        cancelEditButton.perform(click());

        //Press edit again
        editButton.perform(click());

        //change machine name
        ViewInteraction editTextMachineNameToChange = onView(
                allOf(withId(R.id.editTextMachineNameToEdit), isDisplayed()));
        editTextMachineNameToChange.perform(typeText(" CHANGED"));

        //press apply
        ViewInteraction applyChangeButton = onView(
                allOf(withId(R.id.applyMachineEditButton), withText("Apply"), isDisplayed()));
        applyChangeButton.perform(click());

        //short break to check result
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //press edit again
        editButton.perform(click());

        //press delete machine
        ViewInteraction deleteButton = onView(
                allOf(withId(R.id.deleteMachineButton), withText("Delete Machine"), isDisplayed()));
        deleteButton.perform(click());

        //short break to check result
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
