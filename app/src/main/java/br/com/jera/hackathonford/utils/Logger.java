package br.com.jera.hackathonford.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by marco on 05/02/15.
 */
public class Logger {

    public static final String APP_TAG = "HACKATHON";

    public static void d(String txt){
        if(!TextUtils.isEmpty(txt)){
            Log.d(APP_TAG, txt);
        }

    }

    public static void i(String txt){
        if(!TextUtils.isEmpty(txt)) {
            Log.i(APP_TAG, txt);
        }
    }

    public static void e(String txt){
        if(!TextUtils.isEmpty(txt)) {
            Log.e(APP_TAG, txt);
        }
    }

    public static void w(String txt){
        if(!TextUtils.isEmpty(txt)) {
            Log.w(APP_TAG, txt);
        }
    }

}
