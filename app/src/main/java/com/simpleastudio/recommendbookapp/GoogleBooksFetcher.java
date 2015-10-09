package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Random;

/**
 * Purpose: To search for books results using Google Books API
 * Created by Jonathan on 3/10/2015.
 */
public class GoogleBooksFetcher {
    private static final String TAG = "GoogleBooksFetcher";
    private Context mAppContext;
    private static final String ENDPOINT = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String SEARCH_SUBJECT = "subject:";
    private static final String SPACE_ENCODED = "%20";

    private static final String PARAM_MAX_RESULTS = "&maxResults=";       //the Maximum results at one time is 40
    private static final String NUM_RESULTS = "40";
    private static final String PARAM_START_INDEX = "&startIndex=";
    private static final String PARAM_LANG_RESTRICT = "&langRestrict=";
    private static final String LANG = "en";
    private static final String PARAM_ORDER_BY = "&orderBy=";
    private static final String ORDER = "relevance";
    private static final String PARAM_PRINT_TYPE = "&printType=";
    private static final String API_KEY = "&key=";

    public GoogleBooksFetcher(Context c){
        mAppContext = c;
    }

    public JSONObject searchBook(String bookTitle){
        String url = ENDPOINT
                + getSpaceEncoded(bookTitle) + "+" + SEARCH_SUBJECT + "Fiction"
                + PARAM_MAX_RESULTS + NUM_RESULTS
                + PARAM_ORDER_BY + ORDER         //Order from the most recently published
                + PARAM_PRINT_TYPE + "books"
                + API_KEY + mAppContext.getResources().getString(R.string.googleBooks);
        Log.d(TAG, "URL sent: " + url);
        try {
            JSONObject searchResults = getUrl(url);
            return searchResults;
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e);
        }
        return null;
    }

    public String getSpaceEncoded(String in){
        return in.replace(" ", SPACE_ENCODED);
    }

    public String getThumbnail(String bookTitle){
        JSONObject object = searchBook(bookTitle);
        String thumbnailLink = "";
        boolean foundThumbnail = false;
        int index = 0;
        while(!foundThumbnail){
            try {
                JSONArray items = object.getJSONArray("items");
                JSONObject item = items.getJSONObject(index);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                if(imageLinks != null){
                    thumbnailLink = imageLinks.getString("thumbnail");
                    foundThumbnail = true;
                }
                index++;
            } catch (JSONException e) {
                Log.e(TAG, "JSONException: ", e);
            }
        }
        return thumbnailLink;
    }

    public JSONObject searchSubject(String searchSubject, int pageNum){
        int startIndex = Integer.parseInt(NUM_RESULTS)*(pageNum-1);         //Index starts at 0
        String url = ENDPOINT
                + SEARCH_SUBJECT + searchSubject
                + PARAM_MAX_RESULTS + NUM_RESULTS
                + PARAM_START_INDEX + String.valueOf(startIndex)
                + PARAM_LANG_RESTRICT + LANG
                + PARAM_ORDER_BY + ORDER
                + PARAM_PRINT_TYPE + "books"
                + API_KEY;
        Log.d(TAG, "URL sent: " + url);
        try{
            JSONObject searchResults = getUrl(url);
            return searchResults;
        }catch (IOException e){
            Log.e(TAG, "IOException: " + e);
        }
        return null;
    }

    public JSONObject searchFiction(String genre, int pageNum){
        int startIndex = Integer.parseInt(NUM_RESULTS)*(pageNum-1);         //Index starts at 0
        String url = ENDPOINT
                + genre + "+" + SEARCH_SUBJECT + "Fiction"
                + PARAM_MAX_RESULTS + NUM_RESULTS
                + PARAM_START_INDEX + String.valueOf(startIndex)
                + PARAM_LANG_RESTRICT + LANG
                + PARAM_ORDER_BY + ORDER
                + PARAM_PRINT_TYPE + "books"
                + API_KEY;
        Log.d(TAG, "URL sent: " + url);
        try{
            JSONObject searchResults = getUrl(url);
            return searchResults;
        }catch (IOException e){
            Log.e(TAG, "IOException: " + e);
        }
        return null;
    }

    public JSONObject getUrl(String urlString) throws IOException{
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);                //5 seconds of waiting for input stream read
            connection.setConnectTimeout(5000);             //5 seconds of waiting for connection

            String responseMessage = connection.getResponseMessage();
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                Log.d(TAG, "GoogleBooksAPI request failed. Response Code: " + responseCode +
                        "Response Message: " + responseMessage);
                connection.disconnect();
                return null;
            }
            Log.d(TAG, "GoogleBooksAPI request was successful. Response Code: " + responseCode +
                    "Response Message: " + responseMessage);

            //Read data from response
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            Log.d(TAG, "Response String: " + responseString);
            return new JSONObject(responseString);
        } catch (JSONException e) {
            Log.e(TAG, "Json exception: " + e);
        } catch (SocketTimeoutException se){
            Log.e(TAG, "SocketTimeoutException: " + se);
        }finally {
            connection.disconnect();
        }
        return null;
    }
}
