package com.simpleastudio.recommendbookapp;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookInfoFragment extends Fragment {
    private static final String TAG = "BookInfoFragment";
    private Button searchButton;
    private TextView mTextViewTitle;
    private TextView mTextViewAuthor;
    private TextView mTextViewDate;
    private TextView mTextViewRating;
    private TextView mTextViewDescription;
    private String mSearchTerm;
    private String mNewSearchTerm;
    private JSONObject mSearchResults;
    private Book mBook;

    public BookInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSearchTerm = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_info, container, false);
        searchButton = (Button) v.findViewById(R.id.button_search_book);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewSearchTerm = "adventure";
                new FetchVolumesTask().execute(mNewSearchTerm);
            }
        });

        mTextViewTitle = (TextView) v.findViewById(R.id.textview_title);
        mTextViewAuthor = (TextView) v.findViewById(R.id.textview_author);
        mTextViewDate = (TextView) v.findViewById(R.id.textview_date);
        mTextViewRating = (TextView) v.findViewById(R.id.textview_rating);
        mTextViewDescription = (TextView) v.findViewById(R.id.textview_description);

        return v;
    }

    private class FetchVolumesTask extends AsyncTask<String, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(String... params) {
            String searchSubject = params[0];
            if(mSearchTerm.equals(searchSubject)){
                Log.d(TAG, "Looking up the same term again.");
                if(mSearchResults==null){
                    Log.d(TAG, "SearchResults JSONObject is null");
                    mSearchResults = new BookFetcher().searchFiction(mSearchTerm, 1);
                }
            }
            else {      //New search term
                Log.d(TAG, "New search term");
                mSearchTerm = searchSubject;
                mSearchResults = new BookFetcher().searchFiction(mSearchTerm, 1);
            }
            return mSearchResults;
        }

        @Override
        protected void onPostExecute(JSONObject searchResults){
            JSONObject randItem;
            try {
                while(!checkVolumeReq(randItem = new BookFetcher().getRandomVolume(searchResults))){
                }

                JSONObject volumeInfo = randItem.getJSONObject("volumeInfo");
                JSONArray authors = volumeInfo.getJSONArray("authors");
                JSONArray industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                JSONObject isbn13 = industryIdentifiers.getJSONObject(0);

                //Set up as a new Book
                mBook = new Book(volumeInfo.getString("title"));
                ArrayList<String> authorList = new ArrayList<>();
                for(int i = 0; i < authors.length(); i++){
                    String author = authors.getString(i);
                    authorList.add(author);
                }
                mBook.setmIsbn(isbn13.getString("identifier"));
                mBook.setmAuthors(authorList);
                mBook.setmPublishDate(volumeInfo.getString("publishedDate"));
                mBook.setmDescription(volumeInfo.getString("description"));
            } catch (JSONException e) {
                Log.e(TAG, "JSONException: " + e);
            }

            mTextViewTitle.setText(mBook.getmTitle());
            mTextViewDate.setText(mBook.getmPublishDate());
            mTextViewAuthor.setText(mBook.getmAuthors());
            mTextViewDescription.setText(mBook.getmDescription());
        }

        private boolean checkVolumeReq(JSONObject item){
            boolean haveAllReq = false;
            try {
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                JSONArray authors = volumeInfo.getJSONArray("authors");
                JSONArray industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                String description = volumeInfo.getString("description");
                //Able to pass all the requisites
                haveAllReq = true;
            } catch (JSONException e) {
                Log.e(TAG, "Random item did not pass check volume requisites", e);
            } finally {
                return haveAllReq;
            }
        }
    }
}
