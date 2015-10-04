package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.net.ConnectivityManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookInfoActivityFragment extends Fragment {
    private Button searchButton;
    private TextView textViewBookPreview;
    private String mSearchResult;

    public BookInfoActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_info, container, false);
        searchButton = (Button) v.findViewById(R.id.button_search_book);
        textViewBookPreview = (TextView) v.findViewById(R.id.textview_preview);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchVolumesTask().execute("Sea");
            }
        });
        return v;
    }

    private class FetchVolumesTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String searchSubject = params[0];
            return new BookFetcher().searchSubject(searchSubject).toString();
        }

        @Override
        protected void onPostExecute(String searchResults){
            mSearchResult = searchResults;
            textViewBookPreview.setText(mSearchResult);
        }
    }
}
