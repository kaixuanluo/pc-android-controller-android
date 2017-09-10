package com.example.a90678.wechat_group_send_17_07_02_17_35.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.a90678.wechat_group_send_17_07_02_17_35.main.MainActivity;

/**
 * Created by 90678 on 2017/8/28.
 */

public class Reboot extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        L.d("重启了..." + intent.getAction());
//        if (intent.getAction().equals(ACTION)) {
//            L.d("重启了 2 ..." + intent.getAction());
            Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
//        }
    }
}