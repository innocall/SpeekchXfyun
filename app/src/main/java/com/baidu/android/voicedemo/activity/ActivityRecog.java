package com.baidu.android.voicedemo.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;

import com.baidu.android.voicedemo.control.MyRecognizer;
import com.baidu.android.voicedemo.recognization.CommonRecogParams;
import com.baidu.android.voicedemo.recognization.IStatus;
import com.baidu.android.voicedemo.recognization.MessageStatusRecogListener;
import com.baidu.android.voicedemo.recognization.StatusRecogListener;
import com.baidu.android.voicedemo.recognization.offline.OfflineRecogParams;
import com.baidu.android.voicedemo.translate.TransApi;
import com.xlsz.xfyun.XfyunUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 识别的基类Activity。封装了识别的大部分逻辑，包括MyRecognizer的初始化，资源释放、
 * <p>
 * 大致流程为
 * 1. 实例化MyRecognizer ,调用release方法前不可以实例化第二个。参数中需要开发者自行填写语音识别事件的回调类，实现开发者自身的业务逻辑
 * 2. 如果使用离线语法功能，需要调用loadOfflineEngine。在线功能不需要。
 * 3. 根据识别的参数文档，或者demo中测试出的参数，组成json格式的字符串。调用 start 方法
 * 4. 在合适的时候，调用release释放资源。
 * <p>
 * Created by fujiayi on 2017/6/20.
 */

public abstract class ActivityRecog extends ActivityCommon implements IStatus {

    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    protected MyRecognizer myRecognizer;

    /*
     * Api的参数类，仅仅用于生成调用START的json字符串，本身与SDK的调用无关
     */
    protected CommonRecogParams apiParams;

    /*
     * 本Activity中是否需要调用离线语法功能。根据此参数，判断是否需要调用SDK的ASR_KWS_LOAD_ENGINE事件
     */
    protected boolean enableOffline = false;


    /**
     * 控制UI按钮的状态
     */
    private int status;

    /**
     * 日志使用
     */
    private static final String TAG = "ActivityRecog";

    private XfyunUtils xfyunUtils;
    private SharedPreferences sp;
    /**
     * 在onCreate中调用。初始化识别控制类MyRecognizer
     */
    protected void initRecog() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        StatusRecogListener listener = new MessageStatusRecogListener(handler);
        myRecognizer = new MyRecognizer(this, listener);
        apiParams = getApiParams();
        status = STATUS_NONE;
        if (enableOffline) {
            myRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams());
        }
        xfyunUtils = new XfyunUtils(ActivityRecog.this);
    }

    /**
     * 销毁时需要释放识别资源。
     */
    @Override
    protected void onDestroy() {
        myRecognizer.release();
        xfyunUtils.onDestroy();
        super.onDestroy();
    }

    /**
     * 开始录音，点击“开始”按钮后调用。
     */
    protected void start() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ActivityRecog.this);
        Map<String, Object> params = apiParams.fetch(sp);
        myRecognizer.start(params);
    }


    /**
     * 开始录音后，手动停止录音。SDK会识别在此过程中的录音。点击“停止”按钮后调用。
     */
    private void stop() {
        myRecognizer.stop();
    }

    /**
     * 开始录音后，取消这次录音。SDK会取消本次识别，回到原始状态。点击“取消”按钮后调用。
     */
    private void cancel() {
        myRecognizer.cancel();
    }


    /**
     * @return
     */
    protected abstract CommonRecogParams getApiParams();

    // 以上为 语音SDK调用，以下为UI部分
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {
        super.initView();
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (status) {
                    case STATUS_NONE: // 初始状态
                        start();
                        status = STATUS_WAITING_READY;
                        updateBtnTextByStatus();
                        txtLog.setText("");
                        txtResult.setText("");
                        break;
                    case STATUS_WAITING_READY: // 调用本类的start方法后，即输入START事件后，等待引擎准备完毕。
                    case STATUS_READY: // 引擎准备完毕。
                    case STATUS_SPEAKING:
                    case STATUS_FINISHED:// 长语音情况
                    case STATUS_RECOGNITION:
                        stop();
                        status = STATUS_STOPPED; // 引擎识别中
                        updateBtnTextByStatus();
                        break;
                    case STATUS_STOPPED: // 引擎识别中
                        cancel();
                        status = STATUS_NONE; // 识别结束，回到初始状态
                        updateBtnTextByStatus();
                        break;
                }

            }
        });
    }

    private static final String APP_ID = "20170922000084623";
    private static final String SECURITY_KEY = "fGbUNaWPyIz2p1CCNyu3";

    protected void appendText(String msg) {
        String str = txtResult.getText().toString();
        if (str != null && !str.equals("")) {
            msg = str + "\n" + msg;
        }
        txtResult.setText(msg);
    }

    protected void handleMsg(Message msg) {
        super.handleMsg(msg);

        switch (msg.what) { // 处理MessageStatusRecogListener中的状态回调
            case STATUS_FINISHED:
                if (msg.arg2 == 1){
                    appendText("语音识别结果：" + msg.obj.toString());
                    //开始翻译文本
                    startTranslte(msg.obj.toString());
                }
                //故意不写break
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                updateBtnTextByStatus();
                break;
            case TRANSLATE_FINISHEN:
                appendText("翻译后结果：" + msg.obj.toString());
                break;

        }
    }


    private void startTranslte(final String query) {
        final String from = sp.getString("_translate", "en");
        final String tra = from.split(",")[0].trim();
        final String voicer = sp.getString("_voicer", "xiaoyan");
        new Thread(){
            @Override
            public void run() {
                super.run();
                TransApi api = new TransApi(APP_ID, SECURITY_KEY);
                String result = api.getTransResult(query, "auto", tra);
                System.out.println(result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String to = jsonObject.getString("to");
                    JSONArray jsonObject1 = jsonObject.getJSONArray("trans_result");
                    JSONObject jsonObject2 = new JSONObject(jsonObject1.get(0).toString());
                    String dst = jsonObject2.getString("dst");
                    //语音播报
                    xfyunUtils.startSpeek(dst,voicer);
                    Message msg = new Message();
                    msg.what = TRANSLATE_FINISHEN;
                    msg.obj = dst;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                btn.setText("开始录音");
                btn.setEnabled(true);
                setting.setEnabled(true);
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                btn.setText("停止录音");
                btn.setEnabled(true);
                setting.setEnabled(false);
                break;

            case STATUS_STOPPED:
                btn.setText("取消整个识别过程");
                btn.setEnabled(true);
                setting.setEnabled(false);
                break;
        }
    }
}
