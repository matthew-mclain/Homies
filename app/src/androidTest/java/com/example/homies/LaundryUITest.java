package com.example.homies;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LaundryUITest {

    @Rule
    public ActivityScenarioRule<SplashActivity> mActivityScenarioRule = new ActivityScenarioRule<>(SplashActivity.class);

    @Test
    public void laundryUITest(){

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.buttonSignIn), withText("Sign In"),
                        childAtPosition(
                                allOf(withId(R.id.fragment_container),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTextSignInEmail),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_container),
                                        3),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("liao.563@osu.edu"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editTextSignInPassword),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_container),
                                        3),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("1234567"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.buttonSignInSubmit), withText("Sign In"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_container),
                                        3),
                                2),
                        isDisplayed()));
        materialButton2.perform(click());

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.action_bar),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                                allOf(withId(com.google.android.material.R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.nav_view),
                                                0)),
                                5),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

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
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
