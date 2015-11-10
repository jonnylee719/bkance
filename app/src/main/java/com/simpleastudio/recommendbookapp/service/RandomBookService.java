package com.simpleastudio.recommendbookapp.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.simpleastudio.recommendbookapp.BookNavigationActivity;
import com.simpleastudio.recommendbookapp.R;
import com.simpleastudio.recommendbookapp.model.Book;
import com.simpleastudio.recommendbookapp.model.BookLab;

/**
 * Created by Jonathan on 10/10/2015.
 */
public class RandomBookService extends IntentService {
    private static final String TAG = "RandomBookService";
    private static final int RAND_INTERVAL = 1000*60*60*24;
    public static final String EVENT_NEW_RECOMMENDATION =
            "com.simpleastudio.recommendbookapp.NEW_RANDOM";
    public static final String PERM_PRIVATE =
            "com.simpleastudio.recommendbookapp.PRIVATE";
    public static final String PREF_RANDOM_REC = "randomRecTitle";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";

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
            Notification notification = buildNotification(recommendation.getmTitle());
            showBackgroundNotification(NOTIFICATION_ID, notification);
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
            Toast.makeText(c, "Daily recommendation is on", Toast.LENGTH_SHORT).show();
        }
        else {
            alarmManager.cancel(pi);
            pi.cancel();
            //Show that random book is turned off
            Toast.makeText(c, "Daily recommendation is off", Toast.LENGTH_SHORT).show();
        }

        //Saving if alarm manager should be on or off
        PreferenceManager.getDefaultSharedPreferences(c)
                .edit()
                .putBoolean(RandomBookService.PREF_IS_ALARM_ON, isOn)
                .commit();
    }

    void showBackgroundNotification(int requestCode, Notification notification){
        Intent i = new Intent(EVENT_NEW_RECOMMENDATION);
        i.putExtra("REQUEST_CODE", requestCode);
        i.putExtra("NOTIFICATION", notification);

        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }

    public Notification buildNotification(String title){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.notification_new_book))
                .setContentText(String.format(getString(R.string.notification_content_text), title))
                .setAutoCancel(true);
        Intent resultIntent = new Intent(this, BookNavigationActivity.class);

        //Task stack builder allows backward navigation from opened activity to home
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(BookNavigationActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        return notification;
    }
}
