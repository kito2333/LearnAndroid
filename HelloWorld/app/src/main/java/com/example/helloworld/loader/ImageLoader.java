package com.example.helloworld.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    ImageCache mImageCache = new MemoryCache();

    ExecutorService mExecutorService
            = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    Handler mUiHandler = new Handler(Looper.getMainLooper()); // update UI

    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    public void displayImage(final String url, final ImageView imageView) {
        Bitmap cacheBitmap = mImageCache.get(url);
        if (cacheBitmap != null) {
            updateImageView(imageView, cacheBitmap);
        }
        requestDowanload(url, imageView);
    }

    private void requestDowanload(final String url, @NonNull final ImageView imageView) {
        imageView.setTag(url);
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadBitmap(url);
                if (bitmap == null) {
                    return;
                }
                if (imageView.getTag().equals(url)) {
                    updateImageView(imageView, bitmap);
                }
                mImageCache.push(url, bitmap);
            }
        });
    }

    private Bitmap downloadBitmap(String url) {
        Bitmap result = null;
        try {
            URL downloadURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) downloadURL.openConnection();
            result = BitmapFactory.decodeStream(connection.getInputStream());
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void updateImageView(final ImageView imageView, final Bitmap bitmap) {
        if (imageView == null || bitmap == null) {
            return;
        }
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }
}
