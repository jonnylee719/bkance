package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookLab implements Serializable {
    private static final String TAG = "BookLab";
    private static BookLab mLab;
    private Context mAppContext;
    private static final String FILENAME_REC_LIST = "recommendationList.dat";
    private static final String FILENAME_REC_LIST_PAST = "pastRecommendationList.dat";


    private ArrayList<Book> mRecommendList;
    private ArrayList<Book> mPastRecList;

    private BookLab(Context c){
        mAppContext = c;
        mRecommendList = loadList(FILENAME_REC_LIST);
        mPastRecList = loadList(FILENAME_REC_LIST_PAST);
    }

    public static BookLab get(Context c){
        if(mLab == null){
            Log.d(TAG, "Trying to load bookLab file");
            mLab = BookLab.loadBookLab(c.getApplicationContext());
        }
        return mLab;
    }

    public void addBook(Book b){
        mRecommendList.add(b);
    }

    public Book getRecommendBook(int index){
        if(mRecommendList.size() < index)
            return null;
        return mRecommendList.get(index);
    }

    public void setmRecommendList(ArrayList<Book> mRecommendList) {
        this.mRecommendList = mRecommendList;
    }

    public void putToPastRec(int index){
        Book pastRecommendation = mRecommendList.get(index);
        mPastRecList.add(pastRecommendation);
        mRecommendList.remove(index);
    }

    public ArrayList<Book> getmRecommendList(){
        return mRecommendList;
    }

    public ArrayList<Book> getmPastRecList() {
        return mPastRecList;
    }

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
    }

    private ArrayList<Book> loadList(String fileName){
        ArrayList<Book> bookList = new FileWriter(mAppContext).loadBookList(fileName);
        return bookList;
    }

    private static BookLab loadBookLab(Context c){
        BookLab bookLab;
        FileWriter fileWriter = new FileWriter(c);
        bookLab = fileWriter.readBookLab();
        if(bookLab == null){
            bookLab = new BookLab(c);
        }

        return bookLab;
    }

    public void save(){
        FileWriter fileWriter = new FileWriter(mAppContext);
        fileWriter.saveBookList(mRecommendList, FILENAME_REC_LIST);
        fileWriter.saveBookList(mPastRecList, FILENAME_REC_LIST_PAST);
    }
}
