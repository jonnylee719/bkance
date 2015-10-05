package com.simpleastudio.recommendbookapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Purpose: To search for books results using Google Books API
 * Created by Jonathan on 3/10/2015.
 */
public class BookFetcher {
    private static final String TAG = "BookFetcher";
    private static final String ENDPOINT = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String SEARCH_SUBJECT = "subject:";

    private static final String PARAM_MAX_RESULTS = "&maxResults=40";       //the Maximum results at one time is 40
    private static final String PARAM_LANG_RESTRICT = "&langRestrict=en";
    private static final String PARAM_ORDER_BY = "&orderBy=relevance";
    private static final String PARAM_PRINT_TYPE = "&printType=books";
    private static final String API_KEY = "&key=AIzaSyC00WZv8MZ9P0gsl4VTTIl09LIyGtjN1gI";

    public JSONObject searchTerm(String searchTerm){
        String url = ENDPOINT
                + searchTerm
                + PARAM_MAX_RESULTS + PARAM_LANG_RESTRICT + PARAM_ORDER_BY + PARAM_PRINT_TYPE + API_KEY;
        Log.d(TAG, "URL sent: " + url);
        try {
            JSONObject searchResults = getUrl(url);
            return searchResults;
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e);
        }
        return null;
    }

    public JSONObject searchSubject(String searchSubject){
        String url = ENDPOINT
                + SEARCH_SUBJECT + searchSubject
                + PARAM_MAX_RESULTS + PARAM_LANG_RESTRICT + PARAM_ORDER_BY + PARAM_PRINT_TYPE + API_KEY;
        Log.d(TAG, "URL sent: " + url);
        try{
            JSONObject searchResults = getUrl(url);
            return searchResults;
        }catch (IOException e){
            Log.e(TAG, "IOException: " + e);
        }
        return null;
    }

    public JSONObject searchFiction(String genre){
        String url = ENDPOINT
                + SEARCH_SUBJECT + "\"" + "fiction" + "+" + genre + "\""
                + PARAM_MAX_RESULTS + PARAM_LANG_RESTRICT + PARAM_ORDER_BY + PARAM_PRINT_TYPE + API_KEY;
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
