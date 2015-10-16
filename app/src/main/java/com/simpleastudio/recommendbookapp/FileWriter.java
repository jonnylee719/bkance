package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

/**
 * Created by Jonathan on 14/10/2015.
 */
public class FileWriter {
    private static final String TAG = "FileWriter";
    private String mFileName;
    private Context mAppContext;

    public FileWriter(Context c){
        mAppContext = c;
    }

    public void saveBookList(ArrayList<Book> bookList, String fileName){
        try{
            FileOutputStream fos = mAppContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(bookList);
            os.close();
            fos.close();
            Log.d(TAG, "Arraylist is saved in " + fileName + ".");
        }catch (FileNotFoundException e){
            Log.e(TAG,"File not found", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException: ", e);
        }
    }

    public ArrayList<Book> loadBookList(String fileName){
        ArrayList<Book> bookList = new ArrayList<Book>();
        try{
            File mFile = new File(mAppContext.getFilesDir() + "/" + fileName);
            if(!mFile.exists()){
                Log.d(TAG, "arraylist file does not exist.");
                return bookList;
            }
            InputStream fis = mAppContext.openFileInput(mFileName);
            ObjectInputStream reader = new ObjectInputStream(fis);
            bookList = (ArrayList<Book>) reader.readObject();
            fis.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
            Log.e(TAG, "Exception: ", e);
            return bookList;
        }
        return bookList;
    }
}
