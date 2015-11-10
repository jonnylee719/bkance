package com.simpleastudio.recommendbookapp;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;

import com.simpleastudio.recommendbookapp.model.Book;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Jonathan on 9/11/2015.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BookListFragmentTest{

    private List<Book> testBookList = new ArrayList<>();
    protected BookListFragment fragment;

    @Rule
    public ActivityTestRule mActivityRule =
            new ActivityTestRule(TestFragmentActivity.class);

    @Before
    public void set_up_activity_with_test_book_list(){
        for(int i = 0; i < 50; i++){
            Book b;
            if(i % 2 == 0){
                b = new Book("Fountainhead");
                b.setmRatingCount(500);
                b.setmAvgRating(3.00);
            } else {
                b = new Book("James and the giant peach");
                b.setmAvgRating(3.50);
                b.setmRatingCount(1000);
            }
            testBookList.add(b);
        }
        fragment = new BookListFragment();
        FragmentManager fm = ((TestFragmentActivity)mActivityRule.getActivity()).getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        getInstrumentation().waitForIdleSync();
        ((BookListFragment.BookCardAdaptor)fragment.mAdapter).animateTo(testBookList);
    }

    @Test
    public void searchview_is_setup(){
        onView(withId(R.id.booklist_search_bar)).check(matches(isDisplayed()));
    }



}
