package com.example.a90678.wechat_group_send_17_07_02_17_35.access;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.example.a90678.wechat_group_send_17_07_02_17_35.main.MainService;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.ServiceManager;

import org.greenrobot.eventbus.EventBus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 90678 on 2017/7/27.
 */

public class BaseAccessService extends AccessibilityService {

    private static InputStream mIs;
    private static OutputStream mOs;
    private static DataOutputStream mDos;
    private static DataInputStream mDis;

    public static void setIs(InputStream is) {
        mIs = is;
    }

    public static void setOs(OutputStream os) {
        mOs = os;
    }

    public static void setDos(DataOutputStream dos) {
        mDos = dos;
    }

    public static void setDis(DataInputStream dis) {
        mDis = dis;
    }

    public static InputStream getmIs() {
        return mIs;
    }

    public static OutputStream getmOs() {
        return mOs;
    }

    public static DataOutputStream getmDos() {
        return mDos;
    }

    public static DataInputStream getmDis() {
        return mDis;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final EventBus eventBus = EventBus.getDefault();
        L.d("界面有变化.发送消息..." );
        String mainService = "com.example.a90678.wechat_group_send_17_07_02_17_35.main.MainService";
        if (ServiceManager.isServiceWork(this,
                mainService)) {
//            eventBus.post(new ScreenCaptureService.ContentChange());
        } else {
            startService(new Intent(this, MainService.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        EventBus.getDefault().unregister(this);

        L.d("关闭");
        //Toast.makeText(this, "_已关闭微信助手服务_", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "服务中断...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        L.d("service start");
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().register(this);
        L.d("service startCommand ");
        return super.onStartCommand(intent, flags, startId);
    }

}
