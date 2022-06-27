package com.example.helloworld.loader;

import android.graphics.Bitmap;

public interface ImageCache {
    void push(String url, Bitmap bitmap);

    Bitmap get(String url);
}
