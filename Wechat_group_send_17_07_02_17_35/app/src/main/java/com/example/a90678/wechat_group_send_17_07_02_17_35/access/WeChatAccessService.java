package com.example.a90678.wechat_group_send_17_07_02_17_35.access;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by 90678 on 2017/7/5.
 */

public class WeChatAccessService extends BaseAccessService {
    private boolean canReply = false;//能否回复且每次收到消息只回复一次
    private int type = 0;

    private boolean mIsNeedCloseWeChat = false;

    private Activity mActivity;
    private Intent mIntent;

    WeChatNearly mWeChatNearly;
    WeChatGroup mWeChatGroup;
    WeChatAutoReply mWeChatAutoReply;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            L.d("openContactInfo nodeInfo is null");
            return;
        }

        L.d("得到当前包名 "+rootInActiveWindow.getPackageName() + " 类名 " + rootInActiveWindow.getClass());

        if (rootInActiveWindow.getPackageName() != null &&
                !(rootInActiveWindow.getPackageName() + "").equals("com.tencent.mm")) {
            L.e("不是 微信 返回");
            return;
        }

        if (mIsNeedCloseWeChat) {
            if (rootInActiveWindow.getPackageName() != null &&
                    (rootInActiveWindow.getPackageName() + "").equals(AccessUtil.WECHAT_PACKAGE_NAME)) {

                if (AccessUtil.isWeChatMain(rootInActiveWindow)) {
                    mIsNeedCloseWeChat = false;
                    L.d("ismain");

                    mActivity.startActivity(mIntent);
                } else {
                    AccessUtil.performBack(this, rootInActiveWindow);
                }
                return;
            } else {
//                mIsNeedCloseWeChat = false;

//                if (mIntent != null && mActivity != null) {
//                    mActivity.startActivity(mIntent);
//                }
            }
        }

        int eventType = event.getEventType();
        switch (eventType) {
            //第一步：监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                WeChatMsg.sendNotify(event);
                break;
            //第二步：监听是否进入微信聊天界面
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                WeChatNearly.nearly(this, getRootInActiveWindow(), this, "你好");
                L.d("type " + type);
                switch (type) {
                    case Mode.autoHello://自动打招呼
                        mWeChatNearly.change(this, rootInActiveWindow, this, "你好");
                        break;
                    case Mode.group://群发
                        mWeChatGroup.change(this, rootInActiveWindow, this, "你好");
                        break;
                    case Mode.autoChat://自动聊天
                        mWeChatAutoReply.change(this, rootInActiveWindow, this, "你好");
                        break;
                    case Mode.closeService://无法实现
                        AccessUtil.openNext(rootInActiveWindow, "测试");
                        break;
                }
                break;
            default:
                break;
        }
    }

    //服务开启时初始化
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Toast.makeText(this, "_已开启微信助手服务_", Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Mode mode) {

        L.d("onMessage received ");

        type = mode.switchMode;

        mIsNeedCloseWeChat = mode.needCloseWechat;

        mActivity = mode.activity;
        mIntent = mode.intent;

        mWeChatNearly = new WeChatNearly();
        mWeChatAutoReply = new WeChatAutoReply();
        mWeChatGroup = new WeChatGroup();

        L.d("type mode " + type);
    }

    public static class Mode {
        public static int switchMode = 0;
        public static final int group = 1;//群发
        public static final int autoChat = 2;//自动聊天
        public static final int autoHello = 3;//打招呼
        public static final int closeService = 4;//关闭服务
        public static boolean needCloseWechat = false;
        public static Intent intent;
        public static Activity activity;
    }
}
