package com.example.a90678.wechat_group_send_17_07_02_17_35.motionClick.bean;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneInfo {

    public static String getImei(Context context) {
//        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        return "";
    }

    public static String getPhoneName() {
        return android.os.Build.MODEL;
    }

    public static String getVersionName() {
        return android.os.Build.VERSION.RELEASE ;
    }
}
