package com.simpleastudio.recommendbookapp.model;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.simpleastudio.recommendbookapp.FileWriter;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookLab implements Serializable {
    private static final String TAG = "BookLab";
    private static BookLab mLab;
    private Context mAppContext;
    private static final String FILENAME_REC_LIST = "recommendationList.json";
    private static final String FILENAME_REC_LIST_PAST = "pastRecommendationList.json";
    private static final String FILENAME_THUMBNAIL_URL = "thumbnailurlList.json";
    public static final String PREF_REC = "randomRecTitle";

    private Hashtable<String, Book> mRecTable;
    private Hashtable<String, Book> mPastRecTable;
    private Hashtable<String, String> mThumbnailUrlTable;

    private BookLab(Context c){
        mAppContext = c;
        mRecTable = loadTable(FILENAME_REC_LIST);
        mPastRecTable = loadTable(FILENAME_REC_LIST_PAST);
        mThumbnailUrlTable = loadThumbnailUrl(FILENAME_THUMBNAIL_URL);
    }

    public static BookLab get(Context c){
        if(mLab == null){
            //Log.d(TAG, "Trying to load bookLab file");
            mLab = new BookLab(c.getApplicationContext());
        }
        return mLab;
    }

    public void addBook(Book b){
        //Use book title as key
        mRecTable.put(b.getmTitle(), b);
    }

    public Hashtable<String, String> getmThumbnailUrlTable(){
        return mThumbnailUrlTable;
    }

    public void addThumbnailUrl(String title, String url){
        mThumbnailUrlTable.put(title, url);
    }

    public String getThumbnailUrl(String title){
        String url;
        if(mThumbnailUrlTable.containsKey(title)){
            url = mThumbnailUrlTable.get(title);
        }
        else{
            url = null;
        }
        return url;
    }

    public boolean putToPastRecTable(String title){
        if(mRecTable.containsKey(title)){
            Book b = mRecTable.get(title);
            mPastRecTable.put(title, b);
            mRecTable.remove(title);
            return true;
        }
        else {
            return false;
        }
    }

    private Hashtable<String, Book> loadTable(String fileName){
        Hashtable<String, Book> hashtable;
        try {
            hashtable = new FileWriter(mAppContext).loadBooks(fileName);
        } catch (Exception e) {
            hashtable = new Hashtable<String, Book>();
            //Log.e(TAG, "Error loading booklists.", e);
        }
        return hashtable;
    }

    private Hashtable<String, String> loadThumbnailUrl(String fileName){
        Hashtable<String, String> hashtable;
        try{
            hashtable = new FileWriter(mAppContext).loadThumbnailURL(fileName);
        }catch (Exception e){
            hashtable = new Hashtable<String, String>();
            //Log.e(TAG, "Error loading booklists.", e);
        }
        return hashtable;
   }

    public void clearRecTable(){
        mRecTable.clear();
    }

    public Book getRecommendedBook(String title){
        if(mPastRecTable.containsKey(title)){
            return mPastRecTable.get(title);
        }
        else {
            return null;
        }
    }

    public boolean clearPastRecTable(){
        mPastRecTable.clear();
        return mPastRecTable.isEmpty();
    }

    public boolean removeItemPastRec(Book book){
        boolean removed;
        if(mPastRecTable.contains(book)){
            mPastRecTable.remove(book);
            removed = true;
        } else {
            removed = false;
        }
        return removed;
    }

    public void addPastRec(Book book){
        mPastRecTable.put(book.getmTitle(), book);
    }

    public Book getRandomBook(){
        //Get recommendation items in order
        Book bookRec = null;
        if(!mRecTable.isEmpty()){
            Enumeration e = mRecTable.elements();
            while(e.hasMoreElements()){
                bookRec = (Book) e.nextElement();
            }

            //Everytime random book is fetched, immediately update Shared Preference
            PreferenceManager.getDefaultSharedPreferences(mAppContext.getApplicationContext())
                    .edit()
                    .putString(BookLab.PREF_REC, bookRec.getmTitle())
                    .commit();

            //Puts the book to PastRecList immediately to prevent multi-thread problem
            this.putToPastRecTable(bookRec.getmTitle());
        }
        return bookRec;
    }

    public boolean save(){
        FileWriter mFileWriter = new FileWriter(mAppContext);
        try{
            mFileWriter.saveBooks(mRecTable, FILENAME_REC_LIST);
            mFileWriter.saveBooks(mPastRecTable, FILENAME_REC_LIST_PAST);
            mFileWriter.saveThumbnailURL(mThumbnailUrlTable, FILENAME_THUMBNAIL_URL);
            //Log.d(TAG, "Hashtables are saved.");
            return true;
        }catch (Exception e){
            //Log.e(TAG, "Error in saving tables: ", e);
            return false;
        }
    }

    public Hashtable<String, Book> getmPastRecTable(){
        return mPastRecTable;
    }

}
