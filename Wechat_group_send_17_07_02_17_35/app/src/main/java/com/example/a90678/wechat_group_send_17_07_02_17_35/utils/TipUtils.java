package com.example.a90678.wechat_group_send_17_07_02_17_35.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 90678 on 2017/8/24.
 */

public class TipUtils {

    public static void showTip (Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
