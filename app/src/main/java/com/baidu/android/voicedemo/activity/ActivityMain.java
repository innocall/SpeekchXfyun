package com.baidu.android.voicedemo.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.baidu.speech.asr.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 这个Activity用于展示和调用AndroidManifest.xml里定义的其它Activity。本身没有业务逻辑。
 * 请在AndroidManifest.xml中定义 APP_ID APP_KEY APP_SECRET
 *
 * ActivityOnline : 在线识别，用于展示在线情况下的识别参数和效果。
 * ActivityOffline：离线语法识别。用于展示离线情况下，离线语法的参数和效果。并可以测试在线的去情况下，离线语法的参数并不启用。
 * ActivityNlu： 语义理解。 在识别出文字的情况下，百度语音在线服务端和本地SDK可以对这些识别出的文字做语义解析（即分词）。
 *              其中语音服务端做的语义解析必须在线。即语义解析的中文为在线识别成功后的结果。
 *              本地SDK做的语义解析，会覆盖服务端做的语义解析结果。语义解析的中文可以是在线识别成功后的结果，也可以是离线语法的识别结果
 * ActivityAllRecog ： 全部识别功能，涵盖前面三个acitivty的所有功能。
 * ActivityWakeUp： 唤醒词功能。 唤醒词指的是SDK收到某个关键词的声音后回调用户的代码，与Android 系统的锁屏唤醒无关。
 *
 * AcitivityMiniRecog： 内含有调用SDK识别功能的最小代码。用于debug和反馈SDK问题。
 */
public class ActivityMain extends ListActivity {
    public static final String CATEGORY_SAMPLE_CODE = "com.baidu.speech.recognizerdemo.intent.category.SAMPLE_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化科大讯飞语音合成代码
        // 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
        // 设置你申请的应用appid
        SpeechUtility.createUtility(this, "appid=59c89b5b");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove(SpeechConstant.IN_FILE).commit(); // infile参数用于控制识别一个PCM音频流（或文件），每次进入程序都将该值清除，以避免体验时没有使用录音的问题
        setListAdapter(new SimpleAdapter(this, getData(), android.R.layout.simple_list_item_1,
                new String[]{
                        "title"
                }, new int[]{
                android.R.id.text1
        }));
    }

    /**
     * 获取Demo列表
     *
     * @return
     */
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(CATEGORY_SAMPLE_CODE);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        if (null == list)
            return myData;

        int len = list.size();

        for (int i = 0; i < len; i++) {
            ResolveInfo info = list.get(i);
            if (!getPackageName().equalsIgnoreCase(info.activityInfo.packageName)) {
                continue;
            }
            CharSequence labelSeq = info.loadLabel(pm);
            CharSequence description = null;
            if (info.activityInfo.descriptionRes != 0) {
                description = pm.getText(info.activityInfo.packageName,
                        info.activityInfo.descriptionRes, null);
            }

            String label = labelSeq != null ? labelSeq.toString() : info.activityInfo.name;
            addItem(myData,
                    label,
                    activityIntent(info.activityInfo.applicationInfo.packageName,
                            info.activityInfo.name), description);
        }
        return myData;
    }

    private void addItem(List<Map<String, Object>> data, String name, Intent intent,
                         CharSequence description) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put("title", name);
        if (description != null) {
            temp.put("description", description.toString());
        }
        temp.put("intent", intent);
        data.add(temp);
    }

    private Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);

        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }

}

