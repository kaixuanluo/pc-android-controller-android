package com.example.a90678.wechat_group_send_17_07_02_17_35.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 90678 on 2017/8/24.
 */

public class SPUtils {

    public static final String IP = "ip";

    private static SharedPreferences getSp (Context context) {
        SharedPreferences sp = context.getSharedPreferences("GCSP", Context.MODE_PRIVATE);
        return sp;
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences.Editor edit = getSp(context).edit();
        return edit;
    }

    public static void setIp (Context context, String ip) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(IP, ip);
        editor.apply();
    }

    public static String getIp (Context context) {
        String ip = getSp(context).getString(IP, "");
        return ip;
    }
}
