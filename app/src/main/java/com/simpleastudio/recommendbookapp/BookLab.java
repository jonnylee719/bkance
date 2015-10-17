package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
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
    public static final String PREF_REC = "randomRecTitle";


    private ArrayList<Book> mRecommendList;
    private ArrayList<Book> mPastRecList;

    private Hashtable<String, Book> mRecTable;
    private Hashtable<String, Book> mPastRecTable;

    private BookLab(Context c){
        mAppContext = c;
        mRecTable = loadTable(FILENAME_REC_LIST);
        mPastRecTable = loadTable(FILENAME_REC_LIST_PAST);
    }

    public static BookLab get(Context c){
        if(mLab == null){
            Log.d(TAG, "Trying to load bookLab file");
            mLab = new BookLab(c.getApplicationContext());
        }
        return mLab;
    }

    public void addBook(Book b){
        //Use book title as key
        mRecTable.put(b.getmTitle(), b);
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
            Log.e(TAG, "Error loading booklists.", e);
        }
        return hashtable;
    }

    public Book getRecommendedBook(String title){
        if(mPastRecTable.containsKey(title)){
            return mPastRecTable.get(title);
        }
        else {
            return null;
        }
    }

    /*public Book getRecommendBook(int index){
        if(mRecommendList.size() < index || mRecommendList.size() == 0)
            return null;
        Book recommendBook;
        try{             //Because of thread problem, index might no longer be accurate
            recommendBook = mRecommendList.get(index);
        } catch (Exception e){
            recommendBook = null;
            Log.e(TAG, "Exception: ", e);
        }
        return recommendBook;
    }*/

    public void setmRecommendList(ArrayList<Book> mRecommendList) {
        this.mRecommendList = mRecommendList;
    }

    /*public void putToPastRec(int index){
        Book pastRecommendation = mRecommendList.get(index);
        //Take away tag since it's no longer in recList,
        //TODO: Add new tag according to its place in pastRecList
        pastRecommendation.setTag(-1);
        mPastRecList.add(pastRecommendation);
        mRecommendList.remove(index);
        Log.d(TAG, pastRecommendation.getmTitle() + " is put to PastRecList.");
    }*/

    public ArrayList<Book> getmRecommendList(){
        return mRecommendList;
    }

    public ArrayList<Book> getmPastRecList() {
        return mPastRecList;
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
            PreferenceManager.getDefaultSharedPreferences(mAppContext)
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
            Log.d(TAG, "Hashtables are saved.");
            return true;
        }catch (Exception e){
            Log.e(TAG, "Error in saving tables: ", e);
            return false;
        }
    }

    /*
    public Book getRandomBook(){
        int size = mRecommendList.size();
        Book randBook;
        if(size > 0){
            int randomIndex = new Random().nextInt(size);
            randBook = mRecommendList.get(randomIndex);
            randBook.setTag(randomIndex);
        }
        else {
            randBook = null;
        }

        return randBook;
    }*/

    /*private ArrayList<Book> loadList(String fileName){
        ArrayList<Book> bookList = null;
        try {
            bookList = new FileWriter(mAppContext).loadBooks(fileName);
        } catch (Exception e) {
            bookList = new ArrayList<Book>();
            Log.e(TAG, "Error loading booklists.");
        }
        return bookList;
    }*/

    /*public boolean save(){
        FileWriter fileWriter = new FileWriter(mAppContext);
        try {
            fileWriter.saveBooks(mRecommendList, FILENAME_REC_LIST);
            fileWriter.saveBooks(mPastRecList, FILENAME_REC_LIST_PAST);
            Log.d(TAG, "Arraylists are saved.");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error in saving lists.: ", e);
            return false;
        }
    }*/
}
