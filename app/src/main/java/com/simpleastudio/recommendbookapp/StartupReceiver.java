package com.simpleastudio.recommendbookapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.simpleastudio.recommendbookapp.service.RandomBookService;

/**
 * Created by Jonathan on 28/10/2015.
 */
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isOn = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(RandomBookService.PREF_IS_ALARM_ON, false);
        RandomBookService.setServiceAlarm(context, isOn);
    }
}
