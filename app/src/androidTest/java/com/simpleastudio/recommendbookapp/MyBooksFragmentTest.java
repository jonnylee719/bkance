package com.simpleastudio.recommendbookapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.EditText;

import com.simpleastudio.recommendbookapp.model.Book;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.simpleastudio.recommendbookapp.matchers.TestUtils.withRecyclerView;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by Jonathan on 12/11/2015.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MyBooksFragmentTest {
    private List<Book> testBookList = new ArrayList<>();
    protected MyBooksFragment fragment;
    protected int testListSize;

    @Rule
    public ActivityTestRule mActivityRule =
            new ActivityTestRule(TestFragmentActivity.class);

    @Before
    public void set_up_activity_with_test_book_list(){
        testListSize = 50;
        for(int i = 0; i < testListSize; i++){
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
        fragment = new MyBooksFragment();
        FragmentManager fm = ((TestFragmentActivity)mActivityRule.getActivity()).getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
        getInstrumentation().waitForIdleSync();
        //Setting fragment book list to be testBookList
        fragment.mBookList = testBookList;
    }

    @Test
    public void searchview_filters_booklist(){
        onView(withId(R.id.booklist_search_bar))
                .perform(click());

        onView(isAssignableFrom(EditText.class)).perform(typeText("Fountainhead"));

        List<Book> searchedList = ((MyBooksFragment.BookCardAdaptor)fragment.mAdapter).mList;
        int listSize = searchedList.size();
        //Should be half the size of testBookList
        assertThat(listSize, is(equalTo(25)));
        //Should not contain James and the giant peach on odd item
        onView(withRecyclerView(R.id.book_recycler_view).atPositionOnView(1, R.id.card_textview_title))
                .check(matches(withText("Fountainhead")));


    }
}