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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simpleastudio.recommendbookapp.api.GoodreadsFetcher;
import com.simpleastudio.recommendbookapp.api.GoogleBooksFetcher;
import com.simpleastudio.recommendbookapp.api.SingRequestQueue;
import com.simpleastudio.recommendbookapp.api.TastekBooksFetcher;

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
                mBook = BookLab.get(getActivity()).getRandomBook();
                loadRandomBookInfo();
                String url = BookLab.get(getActivity()).getmPastRecList().get(0).getmThumbnailUrl();
                Log.d(TAG, "URL of past book recommendation in BookLab: " + url);
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
        mBook = BookLab.get(getActivity()).getRecommendBook(randomIndex);
        goodreadsStringRequest();
        displaymBook();
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

    @Override
    public void onStop(){
        super.onStop();
        SingRequestQueue.getInstance(getActivity()).getRequestQueue().cancelAll("GET");
        Log.i(TAG, "Canceled all request with GET tag");
    }

    public void loadRandomBookInfo(){
        goodreadsStringRequest();
        new GoogleBooksFetcher(getActivity()).setThumbnail(mBook, mImageView);
        BookLab.get(getActivity()).putToPastRec(mBook.getTag());
    }

    public void goodreadsStringRequest(){
        Log.d(TAG, "Sending Goodreads String request");
        String url = new GoodreadsFetcher(getActivity()).getUrl(mBook.getmTitle());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Book resultBook = GoodreadsFetcher.parseXmlResponse(response);
                        Book book = BookLab.get(getActivity()).getRecommendBook(mBook.getTag());
                        book.setmDay(resultBook.getmDay());
                        book.setmMonth(resultBook.getmMonth());
                        book.setmYear(resultBook.getmYear());
                        book.setmRatingCount(resultBook.getmRatingCount());
                        book.setmAvgRating(resultBook.getmAvgRating());
                        book.setmAuthors(resultBook.getmAuthors());
                        book.setmThumbnailUrl(resultBook.getmThumbnailUrl());
                        book.setmId(resultBook.getmId());
                        mBook = book;

                        BookLab.get(getActivity()).putToPastRec(mBook.getTag());

                        displaymBook();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Something went wrong at goodreads StringRequest.");
            }
        });
        request.setTag("GET");
        SingRequestQueue.getInstance(getActivity()).addToRequestQueue(request);
    }

    public void displaymBook(){
        mTextViewTitle.setText(mBook.getmTitle());
        mTextViewAuthor.setText(mBook.getmAuthors());
        String date = String.format(getResources().getString(R.string.book_date), mBook.getmYear());
        mTextViewDate.setText(date);
        String avgRating = String.format(getResources().getString(R.string.book_rating), mBook.getmAvgRating());
        mTextViewRating.setText(avgRating);
        String ratingCount = String.format(getResources().getString(R.string.rating_count), NumberFormat.getInstance(Locale.getDefault()).format(mBook.getmRatingCount()));
        mTextViewRatingCount.setText(ratingCount);
        mTextViewGRTitle.setText(getResources().getString(R.string.Goodreads_title));
        mImageView.setImageBitmap(mBook.getmBitmap());
        mTextViewDescription.setText(mBook.getmDescription());

    }

}
