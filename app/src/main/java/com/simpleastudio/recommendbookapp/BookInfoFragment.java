package com.simpleastudio.recommendbookapp;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simpleastudio.recommendbookapp.api.GoodreadsFetcher;
import com.simpleastudio.recommendbookapp.api.GoogleBooksFetcher;
import com.simpleastudio.recommendbookapp.api.SingRequestQueue;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookInfoFragment extends VisibleFragment {
    private static final String TAG = "BookInfoFragment";
    public static final int INPUT_BOOK_REQUEST = 1;
    private static final int SETTING_REQUEST = 2;
    @Bind(R.id.toolbar_search_button) protected Button mSearchButton;
    @Bind(R.id.textview_title) protected TextView mTextViewTitle;
    @Bind(R.id.textview_author) protected TextView mTextViewAuthor;
    @Bind(R.id.textview_date) protected TextView mTextViewDate;
    @Bind(R.id.textView_goodreads_title) protected TextView mTextViewGRTitle;
    @Bind(R.id.textview_rating) protected TextView mTextViewRating;
    @Bind(R.id.textview_rating_count) protected TextView mTextViewRatingCount;
    @Bind(R.id.textview_description) protected TextView mTextViewDescription;
    @Bind(R.id.imageview_thumbnail) protected ImageView mImageView;
    @Bind(R.id.toolbar_setting_button) protected Button mSettingButton;
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

        //Initiate mBook
        String recBookTitle = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(BookLab.PREF_REC, null);
        if(recBookTitle == null){
            Book newRecBook = BookLab.get(getActivity()).getRandomBook();
            if(newRecBook != null){
                mBook = newRecBook;
            }
            else {
                //Hashtable of recommendations is 0
                //Make a dialogue message
                Toast.makeText(getActivity(),
                        "There are no more recommendations for this particular book.",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else{
            //Get recommendation stored in Shared Pref to start with
            mBook = BookLab.get(getActivity()).getRecommendedBook(recBookTitle);
        }

        /*int randomBookIndex = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getInt(RandomBookService.PREF_RANDOM_BOOK, -1);
        if(randomBookIndex == -1){
            Book newRandomBook = BookLab.get(getActivity()).getRandomBook();
            if(newRandomBook != null){
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putInt(RandomBookService.PREF_RANDOM_BOOK, newRandomBook.getTag()).commit();
                mBook = newRandomBook;
            }
            else{                       //ArrayList of recommendations is 0
                //Make a dialogue message
                Toast.makeText(getActivity(),
                        "There are no more recommendations for this particular book.",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else {
            mBook = BookLab.get(getActivity()).getook(randomBookIndex);
            if(mBook == null){      //This could be because arraylist of recommendations is 0 or random index is outside the size of the arraylit
                mBook = BookLab.get(getActivity()).getRandomBook();
                if(mBook == null){
                    //Make a dialogue message
                    Toast.makeText(getActivity(),
                            "There are no more recommendations for this particular book.",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView.");
        View v = inflater.inflate(R.layout.fragment_book_info, container, false);
        ButterKnife.bind(this, v);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Think about if the person keeps pressing search button
                //TODO prevent him from clicking again until book is loaded?
                SingRequestQueue.getInstance(getActivity()).getRequestQueue().cancelAll("GET");

                mBook = BookLab.get(getActivity()).getRandomBook();
                if (mBook != null) {
                    clearTextviews();
                    mImageView.setImageBitmap(null);
                    loadRandomBookInfo();
                    String url = mBook.getmThumbnailUrl();
                    Log.d(TAG, "URL of past book recommendation in BookLab: " + url);
                } else {
                    //Make a dialogue message
                    Toast.makeText(getActivity(),
                            "There are no more recommendations for this particular book.",
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
        
        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(i, SETTING_REQUEST);
            }
        });

        //Load mBook book info
        if(mBook != null){
            loadRandomBookInfo();
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "OnActivityResult.");
    }

    @Override
    public void actionOnReceive(){
        String recBookTitle = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(RandomBookService.PREF_RANDOM_REC, null);
        Log.d(TAG, "RecBookTitle: " + recBookTitle);

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putString(BookLab.PREF_REC, recBookTitle)
                .commit();

        mBook = BookLab.get(getActivity()).getRecommendedBook(recBookTitle);
        Log.d(TAG, "Book fetched: " + mBook.getmTitle());
        loadRandomBookInfo();
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
    public void onPause(){
        super.onPause();
        BookLab.get(getActivity()).save();
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
    }

    public void goodreadsStringRequest(){
        Log.d(TAG, "Sending Goodreads String request");
        String url = new GoodreadsFetcher(getActivity()).getUrl(mBook.getmTitle());
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Book resultBook = GoodreadsFetcher.parseXmlResponse(response);
                        mBook.setmDay(resultBook.getmDay());
                        mBook.setmMonth(resultBook.getmMonth());
                        mBook.setmYear(resultBook.getmYear());
                        mBook.setmRatingCount(resultBook.getmRatingCount());
                        mBook.setmAvgRating(resultBook.getmAvgRating());
                        mBook.setmAuthors(resultBook.getmAuthors());
                        mBook.setmThumbnailUrl(resultBook.getmThumbnailUrl());
                        mBook.setmId(resultBook.getmId());
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
        mTextViewDescription.setText(mBook.getmDescription());
    }

}
