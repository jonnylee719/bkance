package com.simpleastudio.recommendbookapp.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.simpleastudio.recommendbookapp.model.Book;
import com.simpleastudio.recommendbookapp.BookInputFragment;
import com.simpleastudio.recommendbookapp.model.BookLab;
import com.simpleastudio.recommendbookapp.api.SingRequestQueue;
import com.simpleastudio.recommendbookapp.api.TastekBooksFetcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(PREF_SEARCHED_TITLE, false)
                .commit();

        boolean searchedTitle = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_SEARCHED_TITLE, false);
        String inputTitle = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(BookInputFragment.PREF_INITIAL_BOOK, null);

        if(!searchedTitle){
            //Get search results using SingRequestQueue
            String url = new TastekBooksFetcher(this).getRecommedationUrl(inputTitle);
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ArrayList<Book> newRecList = parseJsonResult(response);
                    //Clears recommendations of the previous book
                    BookLab.get(getApplicationContext()).clearRecTable();
                    //Store results in BookLab
                    for(int i = 0; i< newRecList.size(); i++){
                        Book b = newRecList.get(i);
                        BookLab.get(getApplicationContext()).addBook(b);
                    }
                    //Saving searched book list at internal storage
                    BookLab.get(getApplicationContext()).save();

                    Log.d(TAG, "Finished searching for similar book list");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            SingRequestQueue.getInstance(this).addToRequestQueue(jsonRequest);

            //Change searched title boolean to true
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(PREF_SEARCHED_TITLE, true)
                    .commit();
        }
    }

    private ArrayList<Book> parseJsonResult(JSONObject object) {
        ArrayList<Book> bookList = new ArrayList<Book>();
        try{
            JSONObject similar = object.getJSONObject("Similar");
            JSONArray results = similar.getJSONArray("Results");
            int totalItems = results.length();
            int parseItems;
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
                bookList.add(b);
            }

        } catch (JSONException e) {
            Log.e(TAG, "JsonException: ", e);
        }
        return bookList;
    }





}
