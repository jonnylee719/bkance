package com.simpleastudio.recommendbookapp;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;

import com.simpleastudio.recommendbookapp.model.Book;

import java.util.ArrayList;
import java.util.List;

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

    private List<Book> testBookList = new ArrayList<>();


    @Rule
    public ActivityTestRule<TestFragmentActivity> mActivityRule =
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

    }

    public void all_books_from_list_are_included_in_adaptor(){

    }

    @Test
    public void listGoesOverTheFold() {
        onView(withText("Hello World!")).check(matches(isDisplayed()));
    }


}