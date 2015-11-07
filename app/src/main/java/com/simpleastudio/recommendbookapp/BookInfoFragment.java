package com.simpleastudio.recommendbookapp;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    @Bind(R.id.textview_title) protected TextView mTextViewTitle;
    @Bind(R.id.textview_author) protected TextView mTextViewAuthor;
    @Bind(R.id.textview_date) protected TextView mTextViewDate;
    @Bind(R.id.textView_goodreads_title) protected TextView mTextViewGRTitle;
    @Bind(R.id.textview_rating) protected TextView mTextViewRating;
    @Bind(R.id.textview_rating_count) protected TextView mTextViewRatingCount;
    @Bind(R.id.textview_description) protected TextView mTextViewDescription;
    @Bind(R.id.imageview_thumbnail) protected NetworkImageView mImageView;

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
        String storedTitle = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getString(BookInputFragment.PREF_INITIAL_BOOK, null);
        if(storedTitle == null || storedTitle.equals("")){
            BookInputFragment fragment = new BookInputFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }

        setHasOptionsMenu(true);

        //Initiate mBook
        String recBookTitle = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getString(BookLab.PREF_REC, null);
        if(recBookTitle == null){
            Book newRecBook =  BookLab.get(getActivity()).getRandomBook();
            if(newRecBook != null){
                mBook = newRecBook;
            }
            else {
                if(storedTitle == null || storedTitle.equals("")){
                    Toast.makeText(getActivity(),
                            getString(R.string.no_search_title_message),
                            Toast.LENGTH_SHORT).show();
                } else {
                    //Hashtable of recommendations is 0
                    //Make a dialogue message
                    Toast.makeText(getActivity(),
                            getString(R.string.no_recommendations_message),
                            Toast.LENGTH_SHORT)
                            .show();
                }

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
            case R.id.action_refresh:
                getRecommendation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getRecommendation(){
        //Stops the user from clicking continuously
        //TODO Add animation to show loading
        //Cancel all current GET request from volley
        SingRequestQueue.getInstance(getActivity()).getRequestQueue().cancelAll("GET");

        mBook = BookLab.get(getActivity()).getRandomBook();
        if (mBook != null) {
            loadRandomBookInfo();
            //For checking flow
            //String url = mBook.getmThumbnailUrl();
            //Log.d(TAG, "URL of past book recommendation in BookLab: " + url);
        } else {
            //Make a dialogue message
            Toast.makeText(getActivity(),
                    getString(R.string.no_recommendations_message),
                    Toast.LENGTH_SHORT)
                    .show();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.d(TAG, "onCreateView.");
        View v = inflater.inflate(R.layout.fragment_book_info, container, false);
        ButterKnife.bind(this, v);

        //Making title as Book info
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.navigation_main);
        NavigationView nv = (NavigationView) getActivity().findViewById(R.id.navigation_view);
        nv.getMenu().getItem(0).setChecked(true);

        //Load mBook book info
        if(mBook != null){
            loadRandomBookInfo();
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d(TAG, "OnActivityResult.");
    }

    @Override
    public void actionOnReceive(){
        String recBookTitle = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getString(RandomBookService.PREF_RANDOM_REC, null);
        //Log.d(TAG, "RecBookTitle: " + recBookTitle);

        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .edit()
                .putString(BookLab.PREF_REC, recBookTitle)
                .commit();

        mBook = BookLab.get(getActivity()).getRecommendedBook(recBookTitle);
        //Log.d(TAG, "Book fetched: " + mBook.getmTitle());
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
        //Log.i(TAG, "Canceled all request with GET tag");
    }

    public void loadRandomBookInfo(){
        //Clear current view
        clearTextviews();
        mImageView.setImageBitmap(null);
        //Load title and description first, then do the fetching
        mTextViewTitle.setText(mBook.getmTitle());
        mTextViewDescription.setText(paraBreak(mBook.getmDescription()));
        goodreadsStringRequest();
        setThumbnailImage();
    }

    public void setThumbnailImage(){
        String url = BookLab.get(getActivity()).getThumbnailUrl(mBook.getmTitle());
        //Log.d(TAG, "url: " + url);
        if(url == null){
            new GoogleBooksFetcher(getActivity()).setThumbnail(mBook.getmTitle(), mImageView);
        } else if(url.equals("www.throwexception.com")){
            //Log.d(TAG, "Equals to throwexception.com");
            mImageView.setDefaultImageResId(R.drawable.default_book_cover);
        } else{
            new GoogleBooksFetcher(getActivity()).setThumbnail(mBook.getmTitle(), mImageView);
        }
    }

    public void goodreadsStringRequest(){
        //Log.d(TAG, "Sending Goodreads String request");
        if(mBook.getmRatingCount() != -1 && mBook.getmYear() != -1 &&
                mBook.getmAvgRating() != -1 && mBook.getmAuthors() != null &&
                mBook.getmId() != null){
            //if data for mBook has already been fetched from goodreads
            displayInfoFromGoodreads();
        } else {
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
