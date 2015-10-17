package com.simpleastudio.recommendbookapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jonathan on 10/10/2015.
 */
public class RandomBookService extends IntentService {
    private static final String TAG = "RandomBookService";
    private static final int RAND_INTERVAL = 1000*15;
    public static final String EVENT_NEW_RECOMMENDATION =
            "com.simpleastudio.recommendbookapp.NEW_RANDOM";
    public static final String PERM_PRIVATE =
            "com.simpleastudio.recommendbookapp.PRIVATE";
    public static final String PREF_RANDOM_REC = "randomRecTitle";

    public RandomBookService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an intent: " + intent);

        Book recommendation = BookLab.get(getApplicationContext()).getRandomBook();
        Log.d(TAG, "recommended book: " + recommendation);

        if(recommendation != null){
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .edit()
                    .putString(PREF_RANDOM_REC, recommendation.getmTitle())
                    .commit();
            sendBroadcast(new Intent(EVENT_NEW_RECOMMENDATION), PERM_PRIVATE);
            Log.d(TAG, "Broadcast intent should be sent.");
        }
    }

    public static void setServiceAlarm(Context c, boolean isOn){
        Intent i = new Intent(c, RandomBookService.class);
        PendingIntent pi = PendingIntent.getService(c, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                c.getSystemService(c.ALARM_SERVICE);

        if(isOn){
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), RAND_INTERVAL, pi);
            //Show that random book is turned on
            Toast.makeText(c, "Random Book Service is on", Toast.LENGTH_SHORT).show();
        }
        else {
            alarmManager.cancel(pi);
            pi.cancel();
            //Show that random book is turned off
            Toast.makeText(c, "Random Book Service is off", Toast.LENGTH_SHORT).show();
        }
    }
}
