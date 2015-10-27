package com.simpleastudio.recommendbookapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Jonathan on 26/10/2015.
 */
public class BookDetailActivity extends SingleFragmentActivity {
    private static final String TAG = "BookDetailActivity";

    @Override
    protected Fragment createFragment() {
        String title = getIntent().getStringExtra(BookDetailFragment.ARGS_BOOK_TITLE);
        return BookDetailFragment.newInstance(title);
    }

}
