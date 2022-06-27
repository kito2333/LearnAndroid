package com.example.helloworld.utils;

import java.io.Closeable;
import java.io.IOException;

public final class IOUtils {

    /**
     *
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
