package com.simpleastudio.recommendbookapp;

import android.os.AsyncTask;

import org.json.JSONObject;

/**
 * Created by Jonathan on 8/10/2015.
 */
public class GoogleBAsyncTasker extends AsyncTask<Book, Void, Book > {

    @Override
    protected Book doInBackground(Book... params) {
        Book book = params[0];
        JSONObject object = new GoogleBooksFetcher().searchBook(book.getmTitle());

        return null;
    }
}
