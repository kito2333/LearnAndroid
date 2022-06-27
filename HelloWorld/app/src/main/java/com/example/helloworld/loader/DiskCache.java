package com.example.helloworld.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DiskCache implements ImageCache {
    static String cacheDir = "sdcard/cache/";

    public Bitmap get(String url) {
        if (url == null) {
            return null;
        }
        return BitmapFactory.decodeFile(cacheDir + url);
    }

    // 尝试把Bitmap压缩到SD卡内存中
    public void push(String url, Bitmap bitmap) {
        if (url == null) {
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(cacheDir + url)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ignore) {
        }
    }
}
