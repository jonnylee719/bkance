package com.simpleastudio.recommendbookapp;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookLab implements Serializable {
    private static BookLab mLab;
    private Context mAppContext;


    private ArrayList<Book> mRecommendList;
    private ArrayList<Book> mPastRecList;

    private BookLab(Context c){
        mAppContext = c;
        mRecommendList = new ArrayList<Book>();
        mPastRecList = new ArrayList<Book>();
    }

    public static BookLab get(Context c){
        if(mLab == null){
            mLab = new BookLab(c.getApplicationContext());
        }
        return mLab;
    }

    public void addBook(Book b){
        mRecommendList.add(b);
    }

    public Book getRecommendBook(int index){
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
        int randomIndex = new Random().nextInt(size);

        Book randBook = mRecommendList.get(randomIndex);
        randBook.setTag(randomIndex);
        return randBook;
    }
}
