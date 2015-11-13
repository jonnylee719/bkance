package com.simpleastudio.recommendbookapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.simpleastudio.recommendbookapp.api.GoodreadsFetcher;
import com.simpleastudio.recommendbookapp.api.GoogleBooksFetcher;
import com.simpleastudio.recommendbookapp.api.SingRequestQueue;
import com.simpleastudio.recommendbookapp.model.Book;
import com.simpleastudio.recommendbookapp.model.BookLab;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan on 26/10/2015.
 */
public class BookDetailFragment extends Fragment {
    private static final String TAG = "BookDetailFragment";
    public static final String ARGS_BOOK_TITLE = "com.simpleastudio.recommendbookapp.title";
    private Book mBook;

    @Bind(R.id.textview_title) protected TextView mTextViewTitle;
    @Bind(R.id.textview_author) protected TextView mTextViewAuthor;
    @Bind(R.id.textview_date) protected TextView mTextViewDate;
    @Bind(R.id.textView_goodreads_title) protected TextView mTextViewGRTitle;
    @Bind(R.id.textview_rating) protected TextView mTextViewRating;
    @Bind(R.id.textview_rating_count) protected TextView mTextViewRatingCount;
    @Bind(R.id.textview_description) protected TextView mTextViewDescription;
    @Bind(R.id.imageview_thumbnail) protected NetworkImageView mImageView;
    ImageLoader imageLoader = SingRequestQueue.getInstance(getActivity()).getImageLoader();


    public BookDetailFragment(){

    }

    public static BookDetailFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(ARGS_BOOK_TITLE, title);
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        String title = getArguments().getString(ARGS_BOOK_TITLE);
        mBook = BookLab.get(getActivity()).getBookFromTable(BookLab.PAST_REC_TABLE, title);
        Log.d(TAG, "Book retrieved at BookDetailFragment: " + mBook.toString());

        //Retain book instance during runtime configuration
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_book_info, container, false);
        ButterKnife.bind(this, v);

        //Set onCreateView rather than onCreate because on retained fragment situation doesn't go through onCreate
        setHasOptionsMenu(true);
        Toolbar mToolBar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolBar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displaymBook();
        setThumbnailImage();
        //Lock navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                //Unlock navigation drawer
                DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
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
        //Log.i(TAG, "Canceled all request with GET tag");
    }

    public void setThumbnailImage(){
        String url = BookLab.get(getActivity()).getThumbnailUrl(mBook.getmTitle());
        //Log.d(TAG, "url: " + url);
        if(url == null){
            new GoogleBooksFetcher(getActivity()).setThumbnail(mBook.getmTitle(), mImageView);
        } else if(url.equals("throwexception")){
            mImageView.setDefaultImageResId(R.drawable.default_book_cover);
        } else {
            mImageView.setErrorImageResId(R.drawable.default_book_cover);
            mImageView.setImageUrl(url, imageLoader);
        }
    }

    public void displaymBook(){
        mTextViewTitle.setText(mBook.getmTitle());
        mTextViewDescription.setText(paraBreak(mBook.getmDescription()));
        if(mBook.getmAuthors() == null || mBook.getmYear() == 0
                || mBook.getmRatingCount() == 0 || mBook.getmAvgRating() == 0){
            goodreadsStringRequest();
        } else{
            mTextViewAuthor.setText(mBook.getmAuthors());
            String date = String.format(getResources().getString(R.string.book_date), mBook.getmYear());
            mTextViewDate.setText(date);
            String avgRating = String.format(getResources().getString(R.string.book_rating), mBook.getmAvgRating());
            mTextViewRating.setText(avgRating);
            String ratingCount = String.format(getResources().getString(R.string.rating_count), NumberFormat.getInstance(Locale.getDefault()).format(mBook.getmRatingCount()));
            mTextViewRatingCount.setText(ratingCount);
            mTextViewGRTitle.setText(getResources().getString(R.string.Goodreads_title));
        }
    }

    public void goodreadsStringRequest(){
        //Log.d(TAG, "Sending Goodreads String request");
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
                        displayInfoFromGoodreads();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(TAG, "Something went wrong at goodreads StringRequest.");
            }
        });
        request.setTag("GET");
        SingRequestQueue.getInstance(getActivity()).addToRequestQueue(request);
    }

    public void displayInfoFromGoodreads(){
        mTextViewAuthor.setText(mBook.getmAuthors());
        String date = String.format(getResources().getString(R.string.book_date), mBook.getmYear());
        mTextViewDate.setText(date);
        String avgRating = String.format(getResources().getString(R.string.book_rating), mBook.getmAvgRating());
        mTextViewRating.setText(avgRating);
        String ratingCount = String.format(getResources().getString(R.string.rating_count), NumberFormat.getInstance(Locale.getDefault()).format(mBook.getmRatingCount()));
        mTextViewRatingCount.setText(ratingCount);
        mTextViewGRTitle.setText(getResources().getString(R.string.Goodreads_title));
    }

    private String paraBreak(String text){
        String resultText = "";
        //Log.d(TAG, "Text: " + text);
        String[] textArray = text.split("\\. ");
        for(int i = 0; i < textArray.length; i++){
            if(i == 0){
                resultText = resultText + textArray[i];
                //Log.d(TAG, "textArray (first line): " + textArray[i]);
            } else if(i%3 == 0 && i!=(textArray.length-1)){
                resultText = resultText + textArray[i] + ". " + "\n" + "\n";
                //Log.d(TAG, "textArray (with new para): " + textArray[i]);
            } else if(i%3 != 0 && i!=(textArray.length-1)){
                //Log.d(TAG, "textArray (mid body): " + textArray[i]);
                resultText = resultText + textArray[i] + ". ";
            } else if (i == (textArray.length -1)){
                //Log.d(TAG, "textArray (last sentence): " + textArray[i]);
                resultText = resultText + textArray[i];
            }
        }
        return resultText;
    }

}
