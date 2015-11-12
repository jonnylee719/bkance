package com.simpleastudio.recommendbookapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenuItemView;
import android.support.design.internal.NavigationMenuView;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.simpleastudio.recommendbookapp.model.BookLab;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Matcher;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.assertion.ViewAssertions.selectedDescendantsMatch;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitle;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitleText;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.android.apps.common.testing.deps.guava.base.CharMatcher.is;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by Jonathan on 11/11/2015.
 */
@RunWith(AndroidJUnit4.class)
public class NavigationActivityTest {

    private static final String TEST_BOOK_TITLE = "Fountainhead";
    @Rule
    public ActivityTestRule activityTestRule =
            new ActivityTestRule(BookNavigationActivity.class);


    //Need to set up default book if first time running test
    public void set_test_book_in_booklistfragment(){
        onView(withId(R.id.editText_book)).perform(typeText(TEST_BOOK_TITLE));
        //Verify text is typed
        onView(withId(R.id.editText_book)).check(matches(withText(TEST_BOOK_TITLE)));
        //Click Enter button
        onView(withId(R.id.button_input_book)).perform(click());
    }

    @Test
    public void launch_fragments_with_navigation_drawer(){
        //Check setting fragment
        open_fragment_from_navigation_drawer("Setting", "Setting");
        //Check BookInfo fragment
        open_fragment_from_navigation_drawer("Books", "Books");
        //Check BookList fragment
        open_fragment_from_navigation_drawer("Previous", "Previous");
        //Check BookInput fragment
        open_fragment_from_navigation_drawer("Enter book title", "Enter book title");
    }

    public void open_fragment_from_navigation_drawer(String navigation_item_label, String fragment_title){
        //Check that fragment title is not displayed
        ViewInteraction fragment_title_text = onView(allOf(isDescendantOfA(withId(R.id.toolbar)), withText(fragment_title)));
        fragment_title_text.check(doesNotExist());

        //Open drawerlayout
        DrawerActions.openDrawer(R.id.drawer_layout);

        //First check if the naviagation view is display
        onView(withId(R.id.navigation_view)).check(matches(isDisplayed()));

        //Check that there's a navigation menu item view with navigation item label
        ViewInteraction navigationDrawerItem = onView(allOf(withParent(isDescendantOfA(withId(R.id.navigation_view))), withText(navigation_item_label)));
        navigationDrawerItem.check(matches(isDisplayed()));
        navigationDrawerItem.perform((click()));

        //Verifying fragment title text through action bar title
        fragment_title_text.check(matches(isDisplayed()));
    }
}


