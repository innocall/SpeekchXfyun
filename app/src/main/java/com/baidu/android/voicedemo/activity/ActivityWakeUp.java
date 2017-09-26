package com.baidu.android.voicedemo.activity;

import android.view.View;

import com.baidu.android.voicedemo.control.MyWakeup;
import com.baidu.android.voicedemo.recognization.IStatus;
import com.baidu.android.voicedemo.wakeup.IWakeupListener;
import com.baidu.android.voicedemo.wakeup.SimpleWakeupListener;
import com.baidu.android.voicedemo.wakeup.WakeupParams;
import com.baidu.speech.recognizerdemo.R;

import java.util.Map;

/**
 * 唤醒词功能
 */
public class ActivityWakeUp extends ActivityCommon implements IStatus {
    {
        layout = R.layout.common_without_setting;
        DESC_TEXT = "唤醒词功能即SDK识别唤醒词，或者认为是SDK识别出用户一大段话中的\"关键词\"。 与Android系统自身的锁屏唤醒无关\n" +
                "唤醒词是纯离线功能，需要获取正式授权文件（与离线语法的正式授权文件是同一个）。 第一次联网使用唤醒词功能自动获取正式授权文件。之后可以断网测试\n" +
                "请说“小度你好”或者 “百度一下”\n\n"+
                "集成指南：如果无法正常使用请检查正式授权文件问题:\n" +
                " 1. 是否在AndroidManifest.xml配置了APP_ID\n" +
                " 2. 是否在开放平台对应应用绑定了包名, 本demo的包名是com.baidu.speech.recognizerdemo" +
                "定义在build.gradle文件中。\n" +
                "\n";
    }
    private static final String TAG = "ActivityWakeUp";
    private MyWakeup myWakeup;

    private int status = STATUS_NONE;

    protected void initView() {
        super.initView();
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (status) {
                    case STATUS_NONE:
                        start();
                        status = STATUS_WAITING_READY;
                        updateBtnTextByStatus();
                        txtLog.setText("");
                        txtResult.setText("");
                        break;
                    case STATUS_WAITING_READY:
                        stop();
                        status = STATUS_NONE;
                        updateBtnTextByStatus();
                        break;
                }

            }
        });
    }

    private void start() {
        WakeupParams wakeupParams = new WakeupParams(this);
        Map<String,Object> params = wakeupParams.fetch();
        myWakeup.start(params);
    }


    private void stop() {
        myWakeup.stop();
    }


    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                btn.setText("启动唤醒");
                break;
            case STATUS_WAITING_READY:
                btn.setText("停止唤醒");
                break;
        }
    }
    @Override
    protected void initRecog() {
        IWakeupListener listener = new SimpleWakeupListener();
        myWakeup = new MyWakeup(this,listener);
    }

    @Override
    protected void onDestroy() {
        myWakeup.release();
        super.onDestroy();
    }
}
