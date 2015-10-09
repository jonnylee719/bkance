package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Jonathan on 8/10/2015.
 */
public class GoodReadsAsyncTasker extends AsyncTask<Book, Void, Book> {
    private static final String TAG = "GoodReadsAsyncTasker";
    private Context mAppContext;

    public GoodReadsAsyncTasker(Context c){
        Log.d(TAG, "Initiated GoodReadsAsyncTasker.");
        mAppContext = c;
    }

    @Override
    protected Book doInBackground(Book... params) {
        Book book = params[0];
        return new GoodreadsFetcher(mAppContext).getBookInfo(book);
    }

}
