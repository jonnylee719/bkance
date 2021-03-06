package com.simpleastudio.recommendbookapp.api;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.simpleastudio.recommendbookapp.model.Book;
import com.simpleastudio.recommendbookapp.R;
import com.simpleastudio.recommendbookapp.model.BookLab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public String getThumbnailUrl(JSONObject object, String title){
        String thumbnailLink = "throwexception";
        if(BookLab.get(mAppContext).getThumbnailUrl(title) != null ){
            thumbnailLink = BookLab.get(mAppContext).getThumbnailUrl(title);
            //Log.d(TAG, "URL from storage: " + thumbnailLink);
        }
        else {
            boolean foundThumbnail = false;
            int index = 0;
            int times = 0;
            while(!foundThumbnail && times<20){
                try {
                    //Log.d(TAG, "Number of searches done: " + times);
                    JSONArray items = object.getJSONArray("items");
                    JSONObject item = items.getJSONObject(index);
                    JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                    String bookTitle = volumeInfo.getString("title");
                    if(bookTitle.toLowerCase().equals(title.toLowerCase())){
                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        if(imageLinks != null){
                            thumbnailLink = imageLinks.getString("thumbnail");
                            foundThumbnail = true;
                        }
                    }
                    index++;
                } catch (JSONException e) {
                    //Log.e(TAG, "JSONException: ", e);
                }
                times++;
            }
            //Saving the thumbnail
            BookLab.get(mAppContext).addThumbnailUrl(title, thumbnailLink);
        }
        return thumbnailLink;
    }

    public void setThumbnail(final String bookTitle, final NetworkImageView networkImageView){
        //Getting the thumbnail url using Json request
        String url = searchBookUrl(bookTitle);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String thumbnailUrl = getThumbnailUrl(response, bookTitle);
                //Log.d(TAG, "URL: " + thumbnailUrl);
                if(thumbnailUrl.equals("throwexception")){
                    //Log.d(TAG, "Equals to exception url");
                    networkImageView.setImageResource(R.drawable.default_book_cover);
                    networkImageView.setDefaultImageResId(R.drawable.default_book_cover);
                } else {
                    ImageLoader imageLoader = SingRequestQueue.getInstance(mAppContext).getImageLoader();
                    networkImageView.setImageUrl(thumbnailUrl, imageLoader);
                    networkImageView.setErrorImageResId(R.drawable.default_book_cover);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        jsonRequest.setTag("GET");
        SingRequestQueue.getInstance(mAppContext).addToRequestQueue(jsonRequest);
    }
}
