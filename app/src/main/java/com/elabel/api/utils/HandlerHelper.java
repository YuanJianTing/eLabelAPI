package com.elabel.api.utils;

import android.os.Handler;
import android.os.Looper;

public class HandlerHelper {
    private final static Handler handler;
    static {
        handler = new Handler(Looper.getMainLooper());
    }
    public static void postDelayed(Runnable r, long delayMillis){
        removeCallbacks(r);
        handler.postDelayed(r,delayMillis);
    }


    public static void removeCallbacks(Runnable r){
        handler.removeCallbacks(r);
    }

    public static void startTask(Runnable r){
        Thread thread=new Thread(r);
        thread.start();
    }
}
