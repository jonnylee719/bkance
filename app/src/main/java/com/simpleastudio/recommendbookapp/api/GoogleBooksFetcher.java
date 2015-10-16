package com.simpleastudio.recommendbookapp.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.simpleastudio.recommendbookapp.Book;
import com.simpleastudio.recommendbookapp.BookLab;
import com.simpleastudio.recommendbookapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
public class GoogleBooksFetcher {
    private static final String TAG = "GoogleBooksFetcher";
    private Context mAppContext;
    private static final String ENDPOINT = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String SEARCH_SUBJECT = "subject:";
    private static final String SPACE_ENCODED = "%20";

    private static final String PARAM_MAX_RESULTS = "&maxResults=";       //the Maximum results at one time is 40
    private static final String NUM_RESULTS = "10";
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

    public String searchBookUrl(String title){
        String url = ENDPOINT
                + getSpaceEncoded(title) + "+" + SEARCH_SUBJECT + "Fiction"
                + PARAM_MAX_RESULTS + NUM_RESULTS
                + PARAM_ORDER_BY + ORDER         //Order from the most recently published
                + PARAM_PRINT_TYPE + "books"
                + API_KEY + mAppContext.getResources().getString(R.string.googleBooks);
        return url;
    }

    public String getSpaceEncoded(String in){
        return in.replace(" ", SPACE_ENCODED);
    }

    public String getThumbnailUrl(JSONObject object){
        String thumbnailLink = "";
        boolean foundThumbnail = false;
        int index = 0;
        int times = 0;
        while(!foundThumbnail && times<20){
            try {
                Log.d(TAG, "Number of searches done: " + times);
                JSONArray items = object.getJSONArray("items");
                JSONObject item = items.getJSONObject(index);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                if(imageLinks != null){
                    thumbnailLink = imageLinks.getString("thumbnail");
                    foundThumbnail = true;
                }
                times++;
                index++;
            } catch (JSONException e) {
                Log.e(TAG, "JSONException: ", e);
            }
        }
        return thumbnailLink;
    }

    public void setThumbnail(final Book b, final ImageView iv){
        String title = b.getmTitle();
        //Getting the thumbnail url using Json request
        String url = searchBookUrl(title);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String thumbnailUrl = getThumbnailUrl(response);
                b.setmThumbnailUrl(thumbnailUrl);
                setImageView(b.getmThumbnailUrl(), iv);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        SingRequestQueue.getInstance(mAppContext).addToRequestQueue(jsonRequest);
    }

    private void setImageView(final String url, final ImageView iv){
        ImageLoader imageLoader = SingRequestQueue.getInstance(mAppContext).getImageLoader();
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                iv.setImageBitmap(response.getBitmap());
                Log.d(TAG, "Image loaded into bitmap.");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                //Should load default bitmap here
                error.printStackTrace();
            }
        });
    }
}
