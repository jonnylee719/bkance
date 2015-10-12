package com.simpleastudio.recommendbookapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.simpleastudio.recommendbookapp.api.GoodreadsFetcher;
import com.simpleastudio.recommendbookapp.api.TastekBooksFetcher;
import com.simpleastudio.recommendbookapp.api.ThumbnailAsyncTasker;
import com.simpleastudio.recommendbookapp.services.BookSearchService;
import com.simpleastudio.recommendbookapp.services.RandomBookService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookInfoFragment extends VisibleFragment {
    private static final String TAG = "BookInfoFragment";
    public static final int INPUT_BOOK_REQUEST = 1;
    @Bind(R.id.toolbar_search_button) protected Button mSearchButton;
    @Bind(R.id.textview_title) protected TextView mTextViewTitle;
    @Bind(R.id.textview_author) protected TextView mTextViewAuthor;
    @Bind(R.id.textview_date) protected TextView mTextViewDate;
    @Bind(R.id.textView_goodreads_title) protected TextView mTextViewGRTitle;
    @Bind(R.id.textview_rating) protected TextView mTextViewRating;
    @Bind(R.id.textview_rating_count) protected TextView mTextViewRatingCount;
    @Bind(R.id.textview_description) protected TextView mTextViewDescription;
    @Bind(R.id.imageview_thumbnail) protected ImageView mImageView;
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
            startActivityForResult(i, INPUT_BOOK_REQUEST);
        }
        else {
            mSearchTerm = storedTitle;
        }

        Intent i = new Intent(getActivity(), BookSearchService.class);
        getActivity().startService(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_info, container, false);
        ButterKnife.bind(this, v);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTextviews();
                mImageView.setImageBitmap(null);
                new FetchVolumesTask().execute(mSearchTerm);
            }
        });

        mImageView.setImageBitmap(null);
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == INPUT_BOOK_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                String storedTitle = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(BookInputFragment.PREF_INITIAL_BOOK, null);
                if(storedTitle != null){
                    mSearchTerm = storedTitle.replace(" ", "%20");
                }
            }
        }
    }

    @Override
    public void actionOnReceive(){
        int randomIndex = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(RandomBookService.PREF_RANDOM_BOOK, 0);
        Book b = BookLab.get(getActivity()).getRecommendBook(randomIndex);
        mTextViewTitle.setText(b.getmTitle());
        mTextViewAuthor.setText(b.getmAuthors());
        String date = String.format(getResources().getString(R.string.book_date), b.getmYear());
        mTextViewDate.setText(date);
        String avgRating = String.format(getResources().getString(R.string.book_rating), b.getmAvgRating());
        mTextViewRating.setText(avgRating);
        String ratingCount = String.format(getResources().getString(R.string.rating_count), NumberFormat.getInstance(Locale.getDefault()).format(b.getmRatingCount()));
        mTextViewRatingCount.setText(ratingCount);
        mTextViewGRTitle.setText(getResources().getString(R.string.Goodreads_title));
        mImageView.setImageBitmap(b.getmBitmap());
        mTextViewDescription.setText(b.getmDescription());
    }

    protected void clearTextviews(){
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

                //Loading thumbnail using Picasso
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
