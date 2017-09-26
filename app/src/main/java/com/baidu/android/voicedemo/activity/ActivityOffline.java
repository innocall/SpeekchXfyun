package com.baidu.android.voicedemo.activity;

import com.baidu.android.voicedemo.activity.setting.OfflineSetting;
import com.baidu.android.voicedemo.recognization.CommonRecogParams;
import com.baidu.android.voicedemo.recognization.offline.OfflineRecogParams;

/**
 * 用于展示离线语法
 */
public class ActivityOffline extends ActivityRecog {

    {
        DESC_TEXT = "此Activity 展示离线语法识别功能。即自定义有限的短句进行离线识别。SDK没有任意词离线识别功能。。\n" +
                " 看见下方日志中asr.loaded说明离线文件加载成功，在线识别说一句正常句子后，正式授权文件才会自动下载。\n" +
                " 正式授权文件自动下载后，断网可以测试:\n" +
                " 请大声说出： 打电话给赵六(离线)\n\n\n" +

                "集成指南：" +
                "如果集成后无法正常使用请检查正式授权文件问题:\n" +
                " 1. 是否在开放平台对应应用绑定了包名，本demo的包名是com.baidu.speech.recognizerdemo，" +
                "定义在build.gradle文件中。\n" +
                " 2. AndroidManifest.xml是否填写了正确的APP_ID APP_KEY 及 APP_SECRET\n" +
                "\n其它提示:\n" +
                " 1. 在加载离线引擎ASR_KWS_LOAD_ENGINE输入事件中的GRAMMER参数中设置bsg文件路径。此时如同时设置SLOT_DATA参数的会覆盖bsg文件中的同名词条。\n" +
                "如果删除DEMO代码里的SLOT_DATA参数后, 您可以测试本DEMO里bsg文件自带的:“打电话给张三(离线)”\n" +
                " 2. 卸载app后正式授权文件自动删除。\n" +
                " 3. 自定义离线语法的bsg文件，在http://yuyin.baidu.com/asr 下载。\n " ;
        enableOffline = true;
    }

    public ActivityOffline() {
        super();
        settingActivityClass = OfflineSetting.class;
    }

    @Override
    protected CommonRecogParams getApiParams() {
        return new OfflineRecogParams(this);
    }
}
