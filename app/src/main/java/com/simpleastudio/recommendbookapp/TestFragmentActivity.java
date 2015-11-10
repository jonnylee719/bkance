package com.simpleastudio.recommendbookapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.MenuItem;

import com.simpleastudio.recommendbookapp.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 9/11/2015.
 */
public class TestFragmentActivity extends NavigationActivityBase {
    private static final String TAG = "TestFragmentActivity";

    @Override
    protected Fragment setStartFragment() {
        return new Fragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_navigation;
    }

    @Override
    protected int setNavigationDrawerMenu() {
        return R.menu.drawer;
    }

    @Override
    protected void selectDrawerItem(MenuItem menuItem) {

    }

}
