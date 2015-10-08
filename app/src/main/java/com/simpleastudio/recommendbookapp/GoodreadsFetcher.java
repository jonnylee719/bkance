package com.simpleastudio.recommendbookapp;

import android.content.Context;
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
 * Created by Jonathan on 7/10/2015.
 */
public class GoodreadsFetcher {
    private static final String TAG = "GoodreadsFetcher";
    private Context mAppContext;
    private Book mBook;
    private static final String ENDPOINT = "https://www.goodreads.com/search.xml?";
    private static final String PARAM_SEPARATOR = "&";
    private static final String SPACE_ENCODED = "%20";
    //Parameters
    private static final String PARAM_FORMAT = "format=";
    private static final String PARAM_TITLE = "title=";
    private static final String PARAM_AUTHOR = "author=";
    private static final String PARAM_RATING = "rating=";
    private static final String PARAM_API = "key=";
    //Default parameters
    private static final String DEFAULT_FORMAT = "xml";

    public GoodreadsFetcher(Context c){
        mAppContext = c;
    }

    public Book getBookInfo(Book book){
        mBook = book;
        String title = book.getmTitle();
        String urlString = ENDPOINT + PARAM_API + mAppContext.getResources().getString(R.string.goodreads)
                + PARAM_SEPARATOR + "q=" + getSpaceEncoded(title);

        try{
            String responseString = getResponse(urlString);
            Log.d(TAG, "responseString: " + responseString);
            //Log.d(TAG, "Results string: " + results.toString());
            //Book editedBook = addBookInfo(results);
            return mBook;
        }catch (IOException ie){
            Log.e(TAG, "IOException: " + ie);
        }
        return mBook;
    }

    public String getSpaceEncoded(String in){
        return in.replace(" ", SPACE_ENCODED);
    }

    public Book addBookInfo(JSONObject info){

        return null;
    }

    public JSONObject parseResponse(String responseString){
        try{
            JSONObject results = new JSONObject(responseString);
            return results;
        } catch (JSONException je) {
            Log.e(TAG, "JsonException: " + je);
        }
        return null;
    }

    public String getResponse(String urlString) throws IOException {
        URL url = new URL(urlString);
        Log.d(TAG, "url: " + url);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);                //5 seconds of waiting for input stream read
            connection.setConnectTimeout(5000);             //5 seconds of waiting for connection

            String responseMessage = connection.getResponseMessage();
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                Log.d(TAG, "TasteKidAPI request failed. Response Code: " + responseCode +
                        "Response Message: " + responseMessage);
                connection.disconnect();
                return null;
            }
            Log.d(TAG, "TasteKidAPI request was successful. Response Code: " + responseCode +
                    "Response Message: " + responseMessage);

            //Read data from response
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            return builder.toString();
        } catch (SocketTimeoutException se){
            Log.e(TAG, "SocketTimeoutException: " + se);
        }finally {
            connection.disconnect();
        }
        return null;
    }

}
