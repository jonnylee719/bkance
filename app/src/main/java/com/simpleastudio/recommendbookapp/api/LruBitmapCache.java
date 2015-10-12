package com.simpleastudio.recommendbookapp.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Jonathan on 12/10/2015.
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache{

    public LruBitmapCache(int maxSize){
        super(maxSize);
    }

    public LruBitmapCache(Context c){
        this(getCacheSize(c));
    }

    @Override
    protected int sizeOf(String key, Bitmap value){
        return value.getRowBytes()* value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url){
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap){
        put(url, bitmap);
    }

    public static int getCacheSize(Context c){
        final DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        //4 bytes per pixel
        final int screenBytes = screenHeight*screenWidth * 4;
        return screenBytes * 3;
    }
}
