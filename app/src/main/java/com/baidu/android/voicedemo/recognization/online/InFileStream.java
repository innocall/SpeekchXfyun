package com.baidu.android.voicedemo.recognization.online;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;
import com.baidu.android.voicedemo.util.Logger;
/**
 * Created by fujiayi on 2017/6/20.
 */

public class InFileStream {

    private static Activity context;

    private static final String TAG = "InFileStream";
    public static void setContext(Activity context){
        InFileStream.context = context;
    }

    public static InputStream create16kStream(){
        InputStream is = null;
        Logger.info(TAG,"cmethod call");
        try {
            is = context.getAssets().open("outfile.pcm");
            Logger.info(TAG,"create input stream ok" + is.available());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return is;
    }
}