package com.simpleastudio.recommendbookapp;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Jonathan on 9/10/2015.
 */
public class BookLab {
    private static BookLab mLab;
    private Context mAppContext;
    private ArrayList<Book> mBookList;

    private BookLab(Context c){
        mAppContext = c;
        mBookList = new ArrayList<Book>();
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
            mBookList.add(b);
        }
    }

    public static BookLab get(Context c){
        if(mLab == null){
            mLab = new BookLab(c.getApplicationContext());
        }
        return mLab;
    }

    public void addBook(Book b){
        mBookList.add(b);
    }

    public ArrayList<Book> getBooks(){
        return mBookList;
    }
}
