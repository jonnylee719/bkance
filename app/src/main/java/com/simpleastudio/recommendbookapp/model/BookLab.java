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
    private static final String FILENAME_FAV_LIST = "favoriteBooks.json";
    public static final String PREF_REC = "randomRecTitle";

    private Hashtable<String, Book> mRecTable;
    public static final String REC_TABLE = "REC_TABLE";
    private Hashtable<String, Book> mPastRecTable;
    public static final String PAST_REC_TABLE = "PAST_REC_TABLE";
    private Hashtable<String, String> mThumbnailUrlTable;
    public static final String THUMBNAIL_TABLE = "THUMBNAIL_TABLE";
    private Hashtable<String, Book> mFavoriteBooksTable;
    public static final String FAV_BOOKS_TABLE = "FAV_BOOKS_TABLE";

    private BookLab(Context c){
        mAppContext = c;
        mRecTable = loadTable(FILENAME_REC_LIST);
        mPastRecTable = loadTable(FILENAME_REC_LIST_PAST);
        mFavoriteBooksTable = loadTable(FILENAME_FAV_LIST);
        mThumbnailUrlTable = loadThumbnailUrl(FILENAME_THUMBNAIL_URL);
    }

    public static BookLab get(Context c){
        if(mLab == null){
            //Log.d(TAG, "Trying to load bookLab file");
            mLab = new BookLab(c.getApplicationContext());
        }
        return mLab;
    }

    public Hashtable getTable(String tableName){
        Hashtable tableToReturn;
        switch (tableName){
            case REC_TABLE:
                tableToReturn = mRecTable;
                break;
            case PAST_REC_TABLE:
                tableToReturn = mPastRecTable;
                break;
            case FAV_BOOKS_TABLE:
                tableToReturn = mFavoriteBooksTable;
                break;
            case THUMBNAIL_TABLE:
                tableToReturn = mThumbnailUrlTable;
                break;
            default:
                tableToReturn = null;
                break;
        }
        return tableToReturn;
    }

    public boolean clearTable(String tableName){
        boolean tableCleared;
        switch (tableName){
            case REC_TABLE:
                mRecTable.clear();
                tableCleared = mRecTable.isEmpty();
                break;
            case PAST_REC_TABLE:
                mPastRecTable.clear();
                tableCleared = mPastRecTable.isEmpty();
                break;
            case FAV_BOOKS_TABLE:
                mFavoriteBooksTable.clear();
                tableCleared = mFavoriteBooksTable.isEmpty();
                break;
            case THUMBNAIL_TABLE:
                mThumbnailUrlTable.clear();
                tableCleared = mThumbnailUrlTable.isEmpty();
                break;
            default:
                tableCleared = false;
                break;
        }
        return tableCleared;
    }

    public boolean addBookToTable(String tableName, Book book){
        boolean bookAdded;
        switch (tableName){
            case REC_TABLE:
                bookAdded = addBook(book, mRecTable);
                break;
            case PAST_REC_TABLE:
                bookAdded = addBook(book, mPastRecTable);
                break;
            case FAV_BOOKS_TABLE:
                bookAdded = addBook(book, mFavoriteBooksTable);
                break;
            default:
                bookAdded = false;
                break;
        }
        Log.d(TAG, "Book added to " + tableName + " : " + bookAdded);
        return bookAdded;
    }

    private boolean addBook(Book book, Hashtable<String, Book> table){
        if(table != null){
            table.put(book.getmTitle(), book);
            Log.d(TAG, "Book added: " + table.contains(book));
            return true;
        }
        else {
            return false;
        }
    }

    public Book getBookFromTable(String tableName, String title){
        Book retrievedBook;
        switch (tableName){
            case REC_TABLE:
                retrievedBook = getBook(title, mRecTable);
                break;
            case PAST_REC_TABLE:
                retrievedBook = getBook(title, mPastRecTable);
                break;
            case FAV_BOOKS_TABLE:
                retrievedBook = getBook(title, mFavoriteBooksTable);
                break;
            default:
                retrievedBook = null;
                break;
        }
        Log.d(TAG, "Retrieved Book: " + retrievedBook);
        return retrievedBook;
    }

    private Book getBook(String title, Hashtable<String, Book> table){
        if(table != null && table.containsKey(title)){
            Book b = table.get(title);
            return b;
        }
        else {
            return null;
        }
    }

    public boolean removeBookFromTable(String tableName, String title){
        boolean bookRemoved;
        switch (tableName){
            case REC_TABLE:
                bookRemoved = removeBook(title, mRecTable);
                break;
            case PAST_REC_TABLE:
                bookRemoved = removeBook(title, mPastRecTable);
                break;
            case FAV_BOOKS_TABLE:
                bookRemoved = removeBook(title, mFavoriteBooksTable);
                break;
            default:
                bookRemoved = false;
                break;
        }
        return bookRemoved;
    }

    private boolean removeBook(String title, Hashtable<String, Book> table){
        if(table != null && table.containsKey(title)){
            table.remove(title);
            return true;
        }
        else {
            return false;
        }
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
            boolean bookPut = this.addBook(bookRec, mPastRecTable);
            Log.d(TAG, bookRec.toString() + " put to past rec table: " + bookPut);
            this.removeBook(bookRec.getmTitle(), mRecTable);
        }
        return bookRec;
    }

    public boolean save(){
        FileWriter mFileWriter = new FileWriter(mAppContext);
        try{
            mFileWriter.saveBooks(mRecTable, FILENAME_REC_LIST);
            mFileWriter.saveBooks(mPastRecTable, FILENAME_REC_LIST_PAST);
            mFileWriter.saveBooks(mFavoriteBooksTable, FILENAME_FAV_LIST);
            mFileWriter.saveThumbnailURL(mThumbnailUrlTable, FILENAME_THUMBNAIL_URL);
            //Log.d(TAG, "Hashtables are saved.");
            return true;
        }catch (Exception e){
            //Log.e(TAG, "Error in saving tables: ", e);
            return false;
        }
    }
}
