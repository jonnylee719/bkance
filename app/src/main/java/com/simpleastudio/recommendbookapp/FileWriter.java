package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.io.Writer;
import java.nio.Buffer;
import java.util.ArrayList;
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

    /*
    public void saveBooks(ArrayList<Book> bookList, String fileName) throws JSONException, IOException{
        JSONArray array = new JSONArray();
        Gson gson = new Gson();
        for(Book book : bookList){
            array.put(gson.toJson(book));
        }

        Writer writer = null;
        try{
            OutputStream out = mAppContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        }finally {
            if(writer!=null){
                writer.close();
            }
        }
    }*/

    public void saveBooks(Hashtable<String, Book> table, String fileName) throws JSONException,
            IOException{
        JSONArray array = new JSONArray();
        Gson gson = new Gson();
        Enumeration e = table.keys();
        while(e.hasMoreElements()){
            array.put(gson.toJson(e.nextElement()));
        }

        Writer writer = null;
        try{
            OutputStream out = mAppContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
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
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            Gson gson = new Gson();
            for(int i = 0; i < array.length(); i++){
                Book book = gson.fromJson(array.getString(i), Book.class);
                hashtable.put(book.getmTitle(), book);
            }
        }catch (FileNotFoundException fe){}
        finally {
            if(reader!= null){
                reader.close();
            }
        }
        return hashtable;
    }

    /*public ArrayList<Book> loadBooks(String fileName) throws JSONException, IOException{
        ArrayList<Book> bookList = new ArrayList<Book>();
        BufferedReader reader = null;
        try{
            InputStream in = mAppContext.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            Gson gson = new Gson();
            for(int i = 0; i < array.length(); i++){
                Book book = gson.fromJson(array.getString(i), Book.class);
                bookList.add(book);
            }
        }catch (FileNotFoundException fe){}
        finally {
            if(reader!= null){
                reader.close();
            }
        }
        return bookList;
    }*/
}
