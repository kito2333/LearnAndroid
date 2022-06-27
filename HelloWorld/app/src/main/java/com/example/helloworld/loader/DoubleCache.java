package com.example.helloworld.loader;

import android.graphics.Bitmap;

public class DoubleCache implements ImageCache {
    private final DiskCache mDiskCache = new DiskCache();
    private final MemoryCache mMemoryCache = new MemoryCache();

    public void push(String url, Bitmap bitmap) {
        mMemoryCache.push(url, bitmap);
        mDiskCache.push(url, bitmap);
    }

    public Bitmap get(String url) {
        if (url == null) {
            return null;
        }
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap == null) {
            return mDiskCache.get(url);
        }
        return bitmap;
    }
}
