package com.example.a90678.wechat_group_send_17_07_02_17_35.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.example.a90678.wechat_group_send_17_07_02_17_35.access.AccessUtil;

/**
 * Created by 90678 on 2017/7/27.
 */

public class Navigator {

    public static void launchMain (Context context) {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        context.startActivity(intent);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName("com.example.a90678.wechat_group_send_17_07_02_17_35",
                "com.example.a90678.wechat_group_send_17_07_02_17_35.access.MainActivity");
        intent.setComponent(cn);
        if (intent == null) {
            L.e("intent is null ");
            return;
        }
        ;
        context.startActivity(intent);
    }
}
