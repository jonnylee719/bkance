package com.simpleastudio.recommendbookapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;
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
    private TextView mTextViewGRTitle;
    private TextView mTextViewRating;
    private TextView mTextViewRatingCount;
    private TextView mTextViewDescription;
    private ImageView mImageView;
    private String mSearchTerm;
    private String mNewSearchTerm;
    private JSONObject mSearchResults;

    public void setmBook(Book mBook) {
        this.mBook = mBook;
    }

    private Book mBook;

    public BookInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Get search term from shared preference
        String storedTitle = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(BookInputFragment.PREF_INITIAL_BOOK, null);
        if(storedTitle == null || storedTitle.equals("")){
            Intent i = new Intent(getActivity(), BookInputActivity.class);
            startActivity(i);
        }
        else {
            mSearchTerm = storedTitle.replace(" ", "%20");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_info, container, false);
        searchButton = (Button) v.findViewById(R.id.button_search_book);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTextviews();
                mImageView.setImageBitmap(null);
                new FetchVolumesTask().execute(mSearchTerm);
            }
        });

        mTextViewTitle = (TextView) v.findViewById(R.id.textview_title);
        mTextViewAuthor = (TextView) v.findViewById(R.id.textview_author);
        mTextViewDate = (TextView) v.findViewById(R.id.textview_date);
        mTextViewRating = (TextView) v.findViewById(R.id.textview_rating);
        mTextViewRatingCount = (TextView) v.findViewById(R.id.textview_rating_count);
        mTextViewDescription = (TextView) v.findViewById(R.id.textview_description);
        mImageView = (ImageView) v.findViewById(R.id.imageview_thumbnail);
        mImageView.setImageBitmap(null);
        mTextViewGRTitle = (TextView) v.findViewById(R.id.textView_goodreads_title);

        return v;
    }

    private void clearTextviews(){
        mTextViewTitle.setText("");
        mTextViewAuthor.setText("");
        mTextViewDate.setText("");
        mTextViewRating.setText("");
        mTextViewRatingCount.setText("");
        mTextViewDescription.setText("");
        mTextViewGRTitle.setText("");
    }

    private class FetchVolumesTask extends AsyncTask<String, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(String... params) {
            String searchSubject = params[0];
            if(mSearchTerm.equals(searchSubject)){
                Log.d(TAG, "Looking up the same term again.");
                if(mSearchResults==null){
                    Log.d(TAG, "SearchResults JSONObject is null");
                    mSearchResults = new TastekBooksFetcher(getActivity()).getRecommendation(mSearchTerm);
                }
            }
            else {      //New search term
                Log.d(TAG, "New search term");
                mSearchTerm = searchSubject;
                mSearchResults = new TastekBooksFetcher(getActivity()).getRecommendation(mSearchTerm);
            }
            return mSearchResults;
        }

        @Override
        protected void onPostExecute(JSONObject searchResults){
            JSONObject randItem;
            try {
                randItem = getRandomVolume(searchResults);

                //Set up as a new Book
                mBook = new Book(randItem.getString("Name"));
                mBook.setmDescription(randItem.getString("wTeaser"));
                new GoodReadsAsyncTasker().execute(mBook);
                new ThumbnailAsyncTasker(mImageView, getActivity()).execute(mBook.getmTitle());

            } catch (JSONException e) {
                Log.e(TAG, "JSONException: " + e);
            }

            mTextViewTitle.setText(mBook.getmTitle());
            mTextViewDescription.setText(mBook.getmDescription());
        }

        public JSONObject getRandomVolume(JSONObject searchResults) throws JSONException{
            JSONObject similar = searchResults.getJSONObject("Similar");
            JSONArray results = similar.getJSONArray("Results");
            int totalItems = results.length();
            int randomItemIndex;
            if(totalItems >= 50){
                randomItemIndex = new Random().nextInt(50);
            }
            else{
                randomItemIndex = new Random().nextInt(totalItems);
            }
            return results.getJSONObject(randomItemIndex);
        }
    }

    public class GoodReadsAsyncTasker extends AsyncTask<Book, Void, Book> {
        private static final String TAG = "GoodReadsAsyncTasker";

        public GoodReadsAsyncTasker(){
            Log.d(TAG, "Initiated GoodReadsAsyncTasker.");
        }

        @Override
        protected Book doInBackground(Book... params) {
            Book book = params[0];
            return new GoodreadsFetcher(getActivity()).getBookInfo(book);
        }

        @Override
        protected void onPostExecute(Book book){
            mBook = book;
            String date = String.format(getResources().getString(R.string.book_date), mBook.getmYear());
            mTextViewDate.setText(date);
            String avgRating = String.format(getResources().getString(R.string.book_rating), mBook.getmAvgRating());
            mTextViewRating.setText(avgRating);
            String ratingCount = String.format(getResources().getString(R.string.rating_count), NumberFormat.getInstance(Locale.getDefault()).format(mBook.getmRatingCount()));
            mTextViewRatingCount.setText(ratingCount);
            mTextViewAuthor.setText(book.getmAuthors());
            mTextViewGRTitle.setText(getResources().getString(R.string.Goodreads_title));


        }
    }
}
