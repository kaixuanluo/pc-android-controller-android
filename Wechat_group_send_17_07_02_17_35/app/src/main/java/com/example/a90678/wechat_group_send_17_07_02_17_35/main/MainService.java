package com.example.a90678.wechat_group_send_17_07_02_17_35.main;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.example.a90678.wechat_group_send_17_07_02_17_35.access.WeChatAccessService;
import com.example.a90678.wechat_group_send_17_07_02_17_35.eventBusUtil.EventBusConstants;
import com.example.a90678.wechat_group_send_17_07_02_17_35.motionClick.MotionClickService;
import com.example.a90678.wechat_group_send_17_07_02_17_35.screenCapture.ScreenRecodeService;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by 90678 on 2017/7/28.
 */

public class MainService extends Service
        implements AccessibilityManager.AccessibilityStateChangeListener {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private AccessibilityManager accessibilityManager;

    Intent motionClickIntent;
    Intent screenRecodeIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);

        motionClickIntent = new Intent(this, MotionClickService.class);

        screenRecodeIntent = new Intent(this, ScreenRecodeService.class);

        startAllService();

    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Toast.makeText(this, "rebind,", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusConstants.StopAllService stop) {
        L.d("stopAllService");
        stopAllService();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusConstants.StartAllService start) {
        L.d("startAllService");
        startAllService();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusConstants.SocketError error) {
        L.e("MainActivity socket error...");
        stopAllService();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    startAllService();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBusConstants.IsOpenScreenRecode recode) {
        //        //先开启录屏权限。。。
//        if (!isGroupReplyServiceEnabled()) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        L.e("未开启AccessService ...");
//                        Thread.sleep(300);
//                        openAccessibility();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//            return;
//        } else {
//        }
    }

    private void startAllService() {

        L.d("开启所有的 Service ...");
        startService(motionClickIntent);

        startService(screenRecodeIntent);

    }

    private void stopAllService() {

        L.d("关闭所有的 Service...");
        EventBus.getDefault().post(new ScreenRecodeService.StopSelf());
        EventBus.getDefault().post(new MotionClickService.MCSStopSelf());
    }

    public void openAccessibility() {
        try {
            Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            accessibleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (accessibleIntent == null) {
            } else {
                startActivity(accessibleIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isGroupReplyServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
//            if (info.getId().equals(getPackageName() + "/.WeChatGroupSendService")) {
            if (info.getId().equals(getPackageName() + "/.access.WeChatAccessService")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        L.d("MainActivity  " + enabled);
    }

}
