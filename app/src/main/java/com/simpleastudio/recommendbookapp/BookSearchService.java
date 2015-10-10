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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jonathan on 10/10/2015.
 */
public class BookSearchService extends IntentService {
    private static final String TAG = "BookSearchService";
    public static final String PREF_SEARCHED_TITLE = "searchedTitle";

    public BookSearchService(){
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

        String searchedTitle = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PREF_SEARCHED_TITLE, null);
        String inputTitle = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(BookInputFragment.PREF_INITIAL_BOOK, null);
        if(inputTitle == null){
            return;
        }
        else{
            //if(searchedTitle==null || !searchedTitle.equals(inputTitle)){
                //Get search results using TastekBooksFetcher
                JSONObject results = new TastekBooksFetcher(this).getRecommendation(inputTitle);
                ArrayList<Book> newRecList = parseJsonResult(results);
                //Store results in BookLab
                BookLab.get(this).setmRecommendList(newRecList);
                //Change searched title to the newly input title
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putString(PREF_SEARCHED_TITLE, inputTitle)
                        .commit();
                Log.d(TAG, "Searched title of current book list: " + inputTitle);
            //}
        }

        RandomBookService.setServiceAlarm(this, true);
    }

    private ArrayList<Book> parseJsonResult(JSONObject object) {
        ArrayList<Book> bookList = new ArrayList<Book>();
        try{
            JSONObject similar = object.getJSONObject("Similar");
            JSONArray results = similar.getJSONArray("Results");
            int totalItems = results.length();
            int parseItems = 0;
            if(totalItems >= 50){
                parseItems = 50;
            }
            else{
                parseItems = 50;
            }

            //Parsing a maximum of 50 searched results
            for(int i = 0; i< parseItems; i++){
                JSONObject item = results.getJSONObject(i);
                Book b = new Book(item.getString("Name"));
                b.setmDescription(item.getString("wTeaser"));
                Log.d(TAG, "Book description: " + b.getmDescription());
                bookList.add(b);
            }

        } catch (JSONException e) {
            Log.e(TAG, "JsonException: ", e);
        }
        return bookList;
    }





}
