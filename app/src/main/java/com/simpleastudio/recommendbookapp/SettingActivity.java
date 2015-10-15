package com.simpleastudio.recommendbookapp;

import android.support.v4.app.Fragment;

/**
 * Created by Jonathan on 15/10/2015.
 */
public class SettingActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SettingFragment();
    }
}
