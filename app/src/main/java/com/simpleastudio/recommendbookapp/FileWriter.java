package com.simpleastudio.recommendbookapp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Jonathan on 14/10/2015.
 */
public class FileWriter {
    private static final String TAG = "FileWriter";
    private static final String FILE_NAME = "com.simpleastudio.recommendbookapp";
    private Context mAppContext;

    public FileWriter(Context c){
        mAppContext = c;
    }

    public void saveFile(String fileString){
        try{
            FileOutputStream fos = mAppContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
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
            FileInputStream inputStream = mAppContext.openFileInput(FILE_NAME);

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
}
