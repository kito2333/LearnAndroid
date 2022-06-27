package com.example.helloworld.loader;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryCache implements ImageCache {
    private LruCache<String, Bitmap> mImageCache;

    public MemoryCache() {
        init();
    }

    private void init() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 4;
        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    public void push(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            return;
        }
        mImageCache.put(key, bitmap);
    }

    public Bitmap get(String key) {
        if (key == null) {
            return null;
        }
        return mImageCache.get(key);
    }
}
