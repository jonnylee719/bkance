package com.simpleastudio.recommendbookapp;

import android.support.v4.app.Fragment;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new BookListFragment();
    }
}
