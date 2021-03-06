package com.simpleastudio.recommendbookapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jonathan on 4/10/2015.
 */
public class Book implements Serializable {
    private String mTitle;
    private String mId;
    private String mDescription;
    private String mAuthors;
    private Date mPublishDate;
    private int mDay = -1;
    private int mMonth = -1;
    private int mYear = -1;
    private String mIsbn;
    private int mRatingCount = -1;
    private double mAvgRating = -1;
    private String mThumbnailUrl;

    @Override
    public String toString(){
        return this.getmTitle();
    }

    public Book(String title){
        mTitle = title;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmIsbn() {
        return mIsbn;
    }

    public void setmIsbn(String mIsbn) {
        this.mIsbn = mIsbn;
    }

    public double getmAvgRating() {
        return mAvgRating;
    }

    public void setmAvgRating(double mAvgRating) {
        this.mAvgRating = mAvgRating;
    }

    public String getmTitle() {
        return mTitle;
    }

    public Date getmPublishDate() {
        return mPublishDate;
    }

    public void setmPublishDate(Date mPublishDate) {
        this.mPublishDate = mPublishDate;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public String getmThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setmThumbnailUrl(String url) {
        mThumbnailUrl = url;
    }

    public int getmRatingCount() {
        return mRatingCount;
    }

    public void setmRatingCount(int mRatingCount) {
        this.mRatingCount = mRatingCount;
    }

    public String getmAuthors() {
        return mAuthors;
    }

    public void setmAuthors(String mAuthors) {
        this.mAuthors = mAuthors;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

}
