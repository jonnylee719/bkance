package com.simpleastudio.recommendbookapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.simpleastudio.recommendbookapp.api.GoodreadsFetcher;
import com.simpleastudio.recommendbookapp.api.GoogleBooksFetcher;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jonathan on 10/10/2015.
 */
public class RandomBookService extends IntentService {
    private static final String TAG = "RandomBookService";
    public static final String PREF_RANDOM_BOOK = "randomBookIndex";
    private static final int RAND_INTERVAL = 1000*15;
    public static final String EVENT_NEW_RECOMMENDATION =
            "com.simpleastudio.recommendbookapp.NEW_RANDOM";

    public RandomBookService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        boolean isNetworkAvailable = cm.getBackgroundDataSetting() &&
                cm.getActiveNetworkInfo() != null;
        if(!isNetworkAvailable) return;

        Log.i(TAG, "Received an intent: " + intent);

        int randomIndex = getRandomRec();
        Log.d(TAG, "Random index: " + randomIndex);
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt(PREF_RANDOM_BOOK, randomIndex)
                .commit();
        sendBroadcast(new Intent(EVENT_NEW_RECOMMENDATION));
        Log.d(TAG, "Broadcast intent should be sent.");
    }


    public int getRandomRec(){
        ArrayList<Book> mBookList = BookLab.get(this).getmRecommendList();
        int randIndex = new Random().nextInt(mBookList.size());
        Book b = BookLab.get(this).getRecommendBook(randIndex);

        //Getting additional results
        new GoodreadsFetcher(this).getBookInfo(b);
        //Bitmap thumbnail = new GoogleBooksFetcher(this).getThumbnailBitmap(b.getmTitle());
        Bitmap thumbnail = new GoogleBooksFetcher(this).loadThumbnailBitmap(b.getmTitle());
        b.setmBitmap(thumbnail);

        return randIndex;
    }

    public static void setServiceAlarm(Context c, boolean isOn){
        Intent i = new Intent(c, RandomBookService.class);
        PendingIntent pi = PendingIntent.getService(c, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                c.getSystemService(c.ALARM_SERVICE);

        if(isOn){
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), RAND_INTERVAL, pi);
        }
        else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }
}
