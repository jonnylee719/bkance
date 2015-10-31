package com.simpleastudio.recommendbookapp.api;

import android.content.Context;
import android.util.Log;

import com.simpleastudio.recommendbookapp.R;

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
public class TastekBooksFetcher {
    private static final String TAG = "TastekBooksFetcher";
    private Context mAppContext;
    private static final String ENDPOINT = "https://www.tastekid.com/api/similar?q=";
    private static final String PARAM_SEPARATOR = "&";
    private static final String SPACE_ENCODED = "%20";
    //Parameters
    private static final String PARAM_SEARCH = "book:";
    private static final String PARAM_TYPE = "type=";
    private static final String PARAM_INFO = "info=";
    private static final String PARAM_LIMIT = "limit=";
    private static final String PARAM_KEY = "k=";


    private static final String API = "https://www.tastekid.com/api/similar?q=";
    //Default parameters
    private static final String DEFAULT_TYPE = "books";
    private static final String DEFAULT_INFO = "1";
    private static final String DEFAULT_LIMIT = "50";

    public TastekBooksFetcher(Context c){
        mAppContext = c;
    }

    public String getSpaceEncoded(String in){
        return in.replace(" ", SPACE_ENCODED);
    }

    public String getRecommedationUrl(String bookTitle){
        String urlString = ENDPOINT
                + PARAM_SEARCH + getSpaceEncoded(bookTitle) + PARAM_SEPARATOR
                + PARAM_TYPE + DEFAULT_TYPE + PARAM_SEPARATOR
                + PARAM_LIMIT + DEFAULT_LIMIT + PARAM_SEPARATOR
                + PARAM_INFO + DEFAULT_INFO + PARAM_SEPARATOR
                + PARAM_KEY + mAppContext.getResources().getString(R.string.tastekid);
        return urlString;
    }

    public JSONObject getRecommendation(String book){
        String urlString = ENDPOINT
                + PARAM_SEARCH + getSpaceEncoded(book) + PARAM_SEPARATOR
                + PARAM_TYPE + DEFAULT_TYPE + PARAM_SEPARATOR
                + PARAM_LIMIT + DEFAULT_LIMIT + PARAM_SEPARATOR
                + PARAM_INFO + DEFAULT_INFO + PARAM_SEPARATOR
                + PARAM_KEY + mAppContext.getResources().getString(R.string.tastekid);

        try{
            return getResponse(urlString);
        } catch (IOException e) {
            //Log.e(TAG, "IOException: " + e);
        }
        return null;
    }

    public JSONObject getResponse(String urlString) throws IOException {
        URL url = new URL(urlString);
        //Log.d(TAG, "url: " + url);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);                //5 seconds of waiting for input stream read
            connection.setConnectTimeout(5000);             //5 seconds of waiting for connection

            String responseMessage = connection.getResponseMessage();
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                //Log.d(TAG, "TasteKidAPI request failed. Response Code: " + responseCode +
                        //"Response Message: " + responseMessage);
                connection.disconnect();
                return null;
            }
            //Log.d(TAG, "TasteKidAPI request was successful. Response Code: " + responseCode +
                    //"Response Message: " + responseMessage);

            //Read data from response
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            //Log.d(TAG, "Response String: " + responseString);
            return new JSONObject(responseString);
        } catch (JSONException e) {
            //Log.e(TAG, "Json exception: " + e);
        } catch (SocketTimeoutException se){
            //Log.e(TAG, "SocketTimeoutException: " + se);
        }finally {
            connection.disconnect();
        }
        return null;
    }
    
}
