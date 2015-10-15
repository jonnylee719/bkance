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

    public void saveFile(String fileString){
        try{
            FileOutputStream fos = mAppContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            fos.write(fileString.getBytes());
            fos.close();
        } catch (FileNotFoundException fe) {
            Log.e(TAG, "File not found. Exception: ", fe);
        } catch (IOException ioe) {
            Log.e(TAG, "IOException: ", ioe);
        }
    }

    public String readFile(){
        String fileString = "";
        try{
            FileInputStream inputStream = mAppContext.openFileInput(mFileName);

            if(inputStream != null){
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }

                inputStream.close();
                fileString = stringBuilder.toString();
            }
        } catch (FileNotFoundException fe){
            Log.e(TAG, "File not found. Exception: ", fe);
        } catch (IOException ioe){
            Log.e(TAG, "IOException: ", ioe);
        }

        return fileString;
    }

    public void saveBookList(ArrayList<Book> bookList, String fileName){
        try{
            FileOutputStream fos = mAppContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(bookList);
            os.close();
            fos.close();
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
        }
        return bookList;
    }

    public void saveBookLab(BookLab bookLab){
        try{
            FileOutputStream fos = mAppContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(bookLab);
            os.close();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found exception: ", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException: ", e);
        }
    }

    public BookLab readBookLab(){
        BookLab bookLab = null;

        try{
            File mFile = new File(mAppContext.getFilesDir() + "/" + mFileName);
            if(!mFile.exists()){
                Log.d(TAG, "File does not exist");
                return bookLab;
            }
            InputStream fis = new FileInputStream(mFile);
            ObjectInputStream is = new ObjectInputStream(fis);
            bookLab = (BookLab) is.readObject();
            is.close();
            fis.close();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Booklab not found", e);
        } catch (OptionalDataException e) {
            Log.e(TAG, "OptionalDataException: ", e);
        } catch (FileNotFoundException fe){
            Log.e(TAG, "File not found.");
        } catch (StreamCorruptedException e) {
            Log.e(TAG, "Stream Corrupted. Exception: ", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException: ", e);
        }
        return bookLab;
    }
}
