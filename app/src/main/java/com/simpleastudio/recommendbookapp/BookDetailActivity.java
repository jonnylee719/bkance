package com.simpleastudio.recommendbookapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Jonathan on 26/10/2015.
 */
public class BookDetailActivity extends AppCompatActivity {
    private static final String TAG = "BookDetailActivity";
    private BookDetailFragment fragment;

    protected BookDetailFragment createFragment() {
        String title = getIntent().getStringExtra(BookDetailFragment.ARGS_BOOK_TITLE);
        return BookDetailFragment.newInstance(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //Get retained fragment
        FragmentManager fm = getSupportFragmentManager();
        fragment = (BookDetailFragment) fm.findFragmentByTag("book");

        if(fragment==null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment, "book")
                    .commit();
        }
    }

}
