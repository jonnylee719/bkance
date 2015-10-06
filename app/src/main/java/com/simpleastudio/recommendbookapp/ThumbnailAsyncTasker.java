package com.simpleastudio.recommendbookapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jonathan on 5/10/2015.
 */
public class ThumbnailAsyncTasker extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "ThumbnailAsyncTasker";
    ImageView mImageView;

    public ThumbnailAsyncTasker(ImageView imageView){
        mImageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urlString = params[0];
        try {
            byte[] bitmapBytes = getUrlBytes(urlString);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, "IOException: ", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap thumbnail){
        mImageView.setImageBitmap(thumbnail);
    }

    byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead=in.read(buffer))>0){
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }
}
