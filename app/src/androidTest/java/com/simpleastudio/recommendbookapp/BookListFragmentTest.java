package com.simpleastudio.recommendbookapp;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Jonathan on 9/11/2015.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BookListFragmentTest{

    @Rule
    public ActivityTestRule<TestFragmentActivity> mActivityRule =
            new ActivityTestRule(TestFragmentActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withText("Hello World!")).check(matches(isDisplayed()));
    }


}
