package com.baidu.android.voicedemo.recognization;

/**
 * Created by fujiayi on 2017/6/14.
 */

public interface IStatus {

    public static final int STATUS_NONE = 2;

    public static final int STATUS_READY = 3;
    public static final int STATUS_SPEAKING = 4;
    public static final int STATUS_RECOGNITION  = 5;

    public static final int STATUS_FINISHED = 6;
    public static final int STATUS_STOPPED  = 10;

    public static final int STATUS_WAITING_READY = 8001;
    public static final int WHAT_MESSAGE_STATUS = 9001;

    public static final int TRANSLATE_FINISHEN = 20001;
}
