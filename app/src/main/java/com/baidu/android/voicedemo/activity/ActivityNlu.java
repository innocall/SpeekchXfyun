package com.baidu.android.voicedemo.activity;


import com.baidu.android.voicedemo.activity.setting.NluSetting;
import com.baidu.android.voicedemo.recognization.CommonRecogParams;
import com.baidu.android.voicedemo.recognization.nlu.NluRecogParams;


/**
 * Created by fujiayi on 2017/6/24.
 */

public class ActivityNlu extends ActivityRecog {
    {
        DESC_TEXT = "语义解析功能是指录音被识别出文字后, 对文字进行分析，如进行分词并尽可能获取文字的意图。\n" +
                "语义解析分为在线语义和本地语义：\n" +
                "1. 在线语义由百度服务器完成。 请点“设置”按钮选择开启“在线语义”。可以在官网应用内设置额外的35个领域。\n" +
                "2. 本地语义解析，请在开始识别ASR_START输入事件中的GRAMMER参数中设置bsg文件路径。如同时设置SLOT_DATA参数的会覆盖bsg文件中的同名词条。\n";

        enableOffline = false; // 请确认不使用离线语法功能后，改为false
    }

    public ActivityNlu() {
        super();
        settingActivityClass = NluSetting.class;
    }

    @Override
    protected CommonRecogParams getApiParams() {
        return new NluRecogParams(this);
    }

}
