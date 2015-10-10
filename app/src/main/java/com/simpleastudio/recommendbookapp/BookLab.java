package com.simpleastudio.recommendbookapp;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookLab implements Serializable {
    private static BookLab mLab;
    private Context mAppContext;

    public void setmRecommendList(ArrayList<Book> mRecommendList) {
        this.mRecommendList = mRecommendList;
    }

    private ArrayList<Book> mRecommendList;
    private ArrayList<Book> mPastRecList;

    private BookLab(Context c){
        mAppContext = c;
        mRecommendList = new ArrayList<Book>();
        mPastRecList = new ArrayList<Book>();
        for(int i = 0; i < 100; i++){
            Book b;
            if(i%2==0){
                b = new Book("random");
                b.setmThumbnailUrl("www.silly.com");
                b.setmAuthors("James Hammerton");
                b.setmDescription("This is a very short story");
            }
            else{
                b = new Book("Strick");
                b.setmThumbnailUrl("www.boring.com");
                b.setmAuthors("Bob Doe");
                b.setmDescription("This is a very dull story");
            }
            mRecommendList.add(b);
        }
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

}
