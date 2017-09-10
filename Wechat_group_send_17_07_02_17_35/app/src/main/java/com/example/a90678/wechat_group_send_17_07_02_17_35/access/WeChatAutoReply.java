package com.example.a90678.wechat_group_send_17_07_02_17_35.access;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Looper;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 90678 on 2017/7/10.
 */

public class WeChatAutoReply {

    private static String WECHAT = "微信";
    private static String SEND = "发送";

    private static String ID_WECHAT = "com.tencent.mm:id/bw3";//微信界面
    private static String ID_MSG_NO = "com.tencent.mm:id/i8";//消息数量
    private static String ID_BACK = "android:id/text1";
    //    private static String ID_WECHAT2 = "com.tencent.mm:id/bw3"
    private static String ID_IN_CHAT = "com.tencent.mm:id/a4l";
    private static String ID_SEND_IB = "com.tencent.mm:id/a5j";

    private boolean mIsClickWeChat;
    private boolean mIsOpenContact;
    private boolean mIsInputText;
    private boolean mIsSend;
    private boolean mIsSendOk;
    private boolean mIsBack;

    private static boolean mIsNeedInit;

    private static Timer mTimer;
    private static int times; //统计
    private static int delay = 5; //10秒钟的间隔

    private static boolean isNotMain;//是否在住界面

    public void change(Context context,
                              final AccessibilityNodeInfo nodeInfo,
                              final AccessibilityService service, String hello) {

        isNotMain = nodeInfo.findAccessibilityNodeInfosByViewId
                (ID_WECHAT).isEmpty();

//        L.d("misNeedInit " + mIsNeedInit);

        L.d("mIsClickWeChat " + mIsClickWeChat);
        L.d("mIsOpenContact " + mIsOpenContact);
        L.d("mIsInputText " + mIsInputText);
        L.d("mIsSend " + mIsSend);
        L.d("mIsBack " + mIsBack);

//        if (mIsNeedInit) {
//            mIsClickWeChat = false;
//            mIsOpenContact = false;
//            mIsInputText = false;
//            mIsSend = false;
//
//            mIsNeedInit = false;
//        }

        if (!isNotMain) {
            //如果不是微信页面则需要点击，是微信页面不需要点击，直接进入下一步
            mIsClickWeChat = true;
        }

        L.d("界面变化");

        if (!mIsClickWeChat) {
            boolean isOpenOk = AccessUtil.openNext(nodeInfo, WECHAT);
            if (isOpenOk) {
                mIsClickWeChat = true;
            }
        } else if (mIsClickWeChat
                && !mIsOpenContact
                ) {
            L.d("打开联系人");
            if (openContactInfo(nodeInfo)) {
                mIsOpenContact = true;
            }
        } else if (mIsOpenContact
                && !mIsInputText
                ) {
            if (AccessUtil.inputText2(context, service, nodeInfo, hello)) {
                mIsInputText = true;
            }
        } else if (mIsInputText
                && !mIsSend
                ) {

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Looper.prepare();
//                    try {
//                        Thread.sleep(500);

                        if (AccessUtil.openNext(nodeInfo, SEND)) {
                            mIsSend = true;
                        }

//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        } else if (mIsSend && !mIsSendOk) {
            List<AccessibilityNodeInfo> nodeInfos = nodeInfo.
                    findAccessibilityNodeInfosByViewId(ID_SEND_IB);
            if (!nodeInfos.isEmpty()){
                mIsSendOk = true;
            }
        } else if (mIsSendOk && !mIsBack) {

            //                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Looper.prepare();
//                                    try {
//                                        Thread.sleep(1000);

            AccessUtil.performBack(service, nodeInfo);

            init();

//            mIsClickWeChat = false;
//            mIsOpenContact = false;
//            mIsInputText = false;
//                                        mIsSend = false;
//                                        mIsBack = false;

//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }).start();

        }

//如果当前界面5秒钟没有变化.则出错了,后退.
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        times = 0;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                times = times + 1;
//                L.e("界面多久没动" + times + " isnotmain " + service.getRootInActiveWindow()
//                        .findAccessibilityNodeInfosByViewId
//                        (ID_WECHAT).isEmpty());

                if (service == null) {
                    return;
                }
                if (service.getRootInActiveWindow() == null) {
                    return;
                }
                List<AccessibilityNodeInfo> infoList = service.getRootInActiveWindow().findAccessibilityNodeInfosByViewId(ID_IN_CHAT);
                boolean empty = (infoList == null || infoList.isEmpty()) ? true : false;
                L.d("界面多久没栋 " + times + " 是否是空的" + empty);
                if (times == delay) {
                    if (!empty) {//在主界面就不要返回了.
                        AccessUtil.performBack(service, nodeInfo);

                        init();

                    }
                    mTimer.cancel();
                }
            }
        };
        mTimer.schedule(timerTask, delay, 1000);

    }

    private void init() {
        mIsOpenContact = false;
        mIsInputText = false;
        mIsSend= false;
        mIsSendOk = false;
    }

    private boolean openContactInfo(AccessibilityNodeInfo nodeInfo) {
        //本页已全部打招呼，所以下滑列表加载下一页，每次下滑的距离是一屏

//        AccessUtil.findNodeInfosById(nodeInfo, "com.tencent.mm:id/id");

        final List<AccessibilityNodeInfo> infosByViewId = nodeInfo.findAccessibilityNodeInfosByViewId(ID_MSG_NO);
        if (infosByViewId == null || infosByViewId.isEmpty()) {
            L.e("infosbyViewId is null");
            return false;
        } else {
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    Thread.sleep(500);

                    AccessibilityNodeInfo info = infosByViewId.get(0);
//        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    mIsOpenContact = true;

                    L.d(" size " + infosByViewId.size());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return true;

    }
}
