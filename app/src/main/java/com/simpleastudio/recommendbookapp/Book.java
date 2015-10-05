package com.simpleastudio.recommendbookapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jonathan on 4/10/2015.
 */
public class Book implements Serializable {
    private String mTitle;
    private String mDescription;
    private ArrayList<String> mAuthors;
    private String mPublishDate;
    private String mIsbn;
    private int mAvgRating;

    public Book(String title){
        mTitle = title;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmAuthors() {
        String authorString = "";
        for(int i = 0; i < this.mAuthors.size(); i++){
            if(i==0){
                authorString = this.mAuthors.get(i);
            }
            else {
                authorString = authorString + ", " + this.mAuthors.get(i);
            }
        }
        return authorString;
    }

    public void setmAuthors(ArrayList<String> mAuthors) {
        this.mAuthors = mAuthors;
    }

    public String getmIsbn() {
        return mIsbn;
    }

    public void setmIsbn(String mIsbn) {
        this.mIsbn = mIsbn;
    }

    public int getmAvgRating() {
        return mAvgRating;
    }

    public void setmAvgRating(int mAvgRating) {
        this.mAvgRating = mAvgRating;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmPublishDate() {
        return mPublishDate;
    }

    public void setmPublishDate(String mPublishDate) {
        this.mPublishDate = mPublishDate;
    }
}
