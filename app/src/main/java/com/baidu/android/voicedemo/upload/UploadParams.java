package com.baidu.android.voicedemo.upload;

import android.content.Context;

import com.baidu.android.voicedemo.util.AuthInfo;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fujiayi on 2017/6/24.
 */

public class UploadParams {

    private final String TAG = "WakeupParams";

    private  Map<String,Object> authInfo;

    public UploadParams(final Context context){
        authInfo = AuthInfo.getAuthParams(context);
    }

    public  Map<String,Object> fetch(){
        Map<String,Object> params = new HashMap<String,Object>(authInfo);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        params.put("name", "contacts");
        params.put("words", new JSONArray().put("陈恭华").put("王柳青").put("李同欢").put("李小婉").put("陈广文").toString());
        return params;
    }
}
