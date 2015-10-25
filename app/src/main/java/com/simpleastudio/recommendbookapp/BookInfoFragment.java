package com.simpleastudio.recommendbookapp;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.simpleastudio.recommendbookapp.api.GoodreadsFetcher;
import com.simpleastudio.recommendbookapp.api.GoogleBooksFetcher;
import com.simpleastudio.recommendbookapp.api.SingRequestQueue;
import com.simpleastudio.recommendbookapp.model.Book;
import com.simpleastudio.recommendbookapp.model.BookLab;
import com.simpleastudio.recommendbookapp.service.RandomBookService;

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
    private static final int LIST_REQUEST = 3;
    @Bind(R.id.toolbar_search_button) protected Button mSearchButton;
    @Bind(R.id.textview_title) protected TextView mTextViewTitle;
    @Bind(R.id.textview_author) protected TextView mTextViewAuthor;
    @Bind(R.id.textview_date) protected TextView mTextViewDate;
    @Bind(R.id.textView_goodreads_title) protected TextView mTextViewGRTitle;
    @Bind(R.id.textview_rating) protected TextView mTextViewRating;
    @Bind(R.id.textview_rating_count) protected TextView mTextViewRatingCount;
    @Bind(R.id.textview_description) protected TextView mTextViewDescription;
    @Bind(R.id.imageview_thumbnail) protected NetworkImageView mImageView;
    @Bind(R.id.toolbar_setting_button) protected Button mSettingButton;
    @Bind(R.id.toolbar_list_button) protected Button mListButton;
    private String mNewSearchTerm;
    private JSONObject mSearchResults;
    protected FloatingActionButton mFAB;

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

        setHasOptionsMenu(true);

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bookinfo_actionbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment;
        switch (item.getItemId()){
            case R.id.action_previous_info:
                fragment = new BookListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment).commit();
                return true;
            case R.id.action_setting_info:
                fragment = new SettingFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView.");
        View v = inflater.inflate(R.layout.fragment_book_info, container, false);
        ButterKnife.bind(this, v);

        //FAB
        mFAB = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        mFAB.show();
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cancel currently loading thumbnail image
                SingRequestQueue.getInstance(getActivity()).getRequestQueue().cancelAll("THUMBNAIL");

                //Stops the user from clicking continuously
                //TODO Add animation to show loading
                mSearchButton.setClickable(false);

                mBook = BookLab.get(getActivity()).getRandomBook();
                if (mBook != null) {
                    clearTextviews();
                    mImageView.setImageBitmap(null);
                    loadRandomBookInfo();
                    //For checking flow
                    String url = mBook.getmThumbnailUrl();
                    Log.d(TAG, "URL of past book recommendation in BookLab: " + url);
                } else {
                    //Make a dialogue message
                    Toast.makeText(getActivity(),
                            "There are no more recommendations for this particular book.",
                            Toast.LENGTH_SHORT)
                            .show();

                    //Enable search button to be clickable if it's not currently
                    if (!mSearchButton.isClickable()) {
                        mSearchButton.setClickable(true);
                    }
                }
            }
        });

        //Making title as Book info
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.navigation_main);

        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(i, SETTING_REQUEST);
            }
        });

        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO add if on result to display the content of the past recommendation
                Intent i = new Intent(getActivity(), BookListActivity.class);
                startActivityForResult(i, LIST_REQUEST);
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
        SingRequestQueue.getInstance(getActivity()).getRequestQueue().cancelAll("THUMBNAIL");
        Log.i(TAG, "Canceled all request with GET and THUMBNAIL tag");
    }

    public void loadRandomBookInfo(){
        //TODO Try to put image fetching request here, so that thumbnail is always loaded after the info
        new GoogleBooksFetcher(getActivity()).setThumbnail(mBook.getmTitle(), mImageView);
        goodreadsStringRequest();
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

        //Enable search button to be clickable if it's not currently
        if(!mSearchButton.isClickable()){
            mSearchButton.setClickable(true);
        }
    }

}
