package com.simpleastudio.recommendbookapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.junit.After;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

/**
 * Created by Jonathan on 8/11/2015.
 */
public class FragmentTestCase<T> {

    private static final String FRAGMENT_TAG = "fragment";

    private ActivityController controller;
    private TestFragmentActivity activity;
    private T fragment;

    public void startFragment(T fragment){
        this.fragment = fragment;
        controller = Robolectric.buildActivity(TestFragmentActivity.class);
        activity = (TestFragmentActivity) controller.create().start().visible().get();
        FragmentManager fm = activity.getSupportFragmentManager();
        fm.beginTransaction().add((Fragment) fragment, FRAGMENT_TAG).commit();
    }

    @After
    public void destroyFragment(){
        if(fragment!=null){
            FragmentManager fm = activity.getSupportFragmentManager();
            fm.beginTransaction().remove((Fragment) fragment).commit();
            fragment = null;
            activity = null;
        }
    }

    public void pauseAndResumeFragment(){
        controller.pause().resume();
    }

    public T recreateFragment(){
        activity.recreate();
        fragment = (T) activity.getSupportFragmentManager()
                .findFragmentByTag(FRAGMENT_TAG);
        return fragment;
    }

}
