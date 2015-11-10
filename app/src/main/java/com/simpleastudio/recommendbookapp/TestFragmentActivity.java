package com.simpleastudio.recommendbookapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.test.suitebuilder.annotation.MediumTest;

import com.simpleastudio.recommendbookapp.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 9/11/2015.
 */
public class TestFragmentActivity extends AppCompatActivity {
    private static final String TAG = "TestFragmentActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);
    }



}
