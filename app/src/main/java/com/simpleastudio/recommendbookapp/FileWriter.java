package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simpleastudio.recommendbookapp.model.Book;
import com.simpleastudio.recommendbookapp.model.BookLab;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Hashtable;

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

    public void saveBooks(Hashtable<String, Book> table, String fileName) throws JSONException,
            IOException{
        Gson gson = new Gson();
        String json = gson.toJson(table);

        Writer writer = null;
        try{
            OutputStream out = mAppContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(json);
        }finally {
            if(writer!=null){
                writer.close();
            }
        }
    }

    public Hashtable<String, Book> loadBooks(String fileName) throws JSONException, IOException{
        Hashtable<String, Book> hashtable = new Hashtable<String, Book>();
        BufferedReader reader = null;
        try{
            InputStream in = mAppContext.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                jsonString.append(line);
            }

            Type hashTableType = new TypeToken<Hashtable<String, Book>>(){}.getType();
            Gson gson = new Gson();
            hashtable = gson.fromJson(jsonString.toString(), hashTableType);
            //Log.d(TAG, "File loaded.");
        }catch (FileNotFoundException fe){}
        finally {
            if(reader!= null){
                reader.close();
            }
        }
        return hashtable;
    }

    public void saveThumbnailURL(Hashtable<String, String> table, String fileName) throws JSONException,
            IOException{
        Gson gson = new Gson();
        String json = gson.toJson(table);

/*        //For testing and debugging
        Enumeration<String> urls = table.elements();
        Enumeration<String> titles = table.keys();
        while(urls.hasMoreElements()){
            String url = urls.nextElement();
            String title = titles.nextElement();
            Log.i(TAG, title + "'s thumbnail url: " + url);
        }*/

        Writer writer = null;
        try{
            OutputStream out = mAppContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(json);
        }finally {
            if(writer!=null){
                writer.close();
            }
        }
    }

    public Hashtable<String, String> loadThumbnailURL(String fileName) throws JSONException, IOException{
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        BufferedReader reader = null;
        try{
            InputStream in = mAppContext.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                jsonString.append(line);
            }

            Type hashTableType = new TypeToken<Hashtable<String, String>>(){}.getType();
            Gson gson = new Gson();
            hashtable = gson.fromJson(jsonString.toString(), hashTableType);
            //Log.d(TAG, "File loaded.");
        }catch (FileNotFoundException fe){}
        finally {
            if(reader!= null){
                reader.close();
            }
        }
        return hashtable;
    }
}
