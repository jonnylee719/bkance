package com.simpleastudio.recommendbookapp.api;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Jonathan on 5/10/2015.
 */
public class ThumbnailAsyncTasker extends AsyncTask<String, Void, String> {
    private static final String TAG = "ThumbnailAsyncTasker";
    private Context mAppContext;
    private ImageView mImageView;

    public ThumbnailAsyncTasker(ImageView imageView, Context c){
        mImageView = imageView;
        mAppContext = c;
    }

    @Override
    protected String doInBackground(String... params) {
        String bookTitle = params[0];
            //String urlString = new GoogleBooksFetcher(mAppContext).getThumbnailUrl(bookTitle);
        String urlString = "";
        return urlString;
    }

    @Override
    protected void onPostExecute(String urlString){
        Picasso.with(mAppContext).load(Uri.parse(urlString)).into(mImageView);
    }
}
