package com.example.a90678.wechat_group_send_17_07_02_17_35.access;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 90678 on 2017/7/9.
 */

public class WeChatGroup {

    int i = 0;//从第四个开始打招呼
    int page = 1;//从地0也开始打招呼。

    private static String ID_SEND_IB = "com.tencent.mm:id/a5j";

    private static String CHAT = "的聊天";
    private static String CONTACT = "通讯录";
    private static String SENDMSG = "发消息";
    private static String SEND = "发送";

    private boolean mIsFinish;
    private boolean mIsGo = true;
    private boolean mIsNeedInit = true;
    private boolean mIsOpenContact;
    private boolean mIsOpenContactInfo;
    private boolean mIsOpenSend;//点击 发消息
    private boolean mIsInput;//输入消息
    private boolean mIsSendMsg;//,发送消息
    private boolean mIsSendMsgOk;//,发送消息
    private boolean mIsBack;//返回

    private static String CURRENT_INFO = "当前所在页面,详细资料";

    private static Timer mTimer;
    private static int times; //统计
    private static int delay = 10; //10秒钟的间隔

    public void change(Context context,
                       final AccessibilityNodeInfo nodeInfo,
                       final AccessibilityService service, String hello) {

        CharSequence contentDescription1 = nodeInfo.getContentDescription();
        if (contentDescription1 == null) {
            L.e("contentDescription1 is null");
            return;
        }

        final String contentDescription = contentDescription1 + "";
        if (TextUtils.isEmpty(contentDescription)) {
            L.e("contentDescription is empty");
            return;
        }

        if (mIsFinish) {
            L.d("mIsFinish " + mIsFinish);
            return;
        }

        L.d("在主页面  " + contentDescription + "  " + contentDescription.contains(CHAT));
//        当前所在页面,
//        if (contentDescription != null && contentDescription.contains(CHAT) && mIsNeedInit) {
//
//            L.d("进入初始化");
//
//            mIsGo = true;
//
//            mIsOpenContact = false;
//            mIsOpenContactInfo = false;
//            mIsOpenSend = false;
//            mIsInput = false;
//            mIsSendMsg = false;
//            mIsBack = false;
//
//            mIsNeedInit = false;
//        }

        L.d("mIsFinish " + mIsFinish);
        L.d("mIsGo " + mIsGo);
        L.d("mIsInput " + mIsInput);
        L.d("mIsOpenContact " + mIsOpenContact);
        L.d("mIsOpenContactInfo " + mIsOpenContactInfo);
        L.d("mIsOpenSend " + mIsOpenSend);
        L.d("mIsInput " + mIsInput);

        if (mIsGo) {
            if (!mIsOpenContact && contentDescription.contains(CHAT)) {
                AccessUtil.openNext(nodeInfo, CONTACT);
                mIsOpenContact = true;
            } else if (!mIsOpenContactInfo && mIsOpenContact && contentDescription.contains(CHAT)) {
                if (openContactInfo(nodeInfo)) {
                    L.d("openContactInfo ");
                    mIsOpenContactInfo = true;
                }
            } else if (!mIsOpenSend && mIsOpenContactInfo && contentDescription.equals(CURRENT_INFO)) {
                L.d("点击发消息");
                boolean sendClickOk = AccessUtil.openNext(nodeInfo, SENDMSG);
                if (sendClickOk) {
                    mIsOpenSend = true;
                }
            } else if (!mIsInput && mIsOpenSend && contentDescription.contains(CHAT)) {
                L.d("粘贴内容222222");
                boolean inputOk = AccessUtil.inputText2(context, service, nodeInfo, hello);
                if (inputOk) {
                    mIsInput = true;
                }
            } else if (!mIsSendMsg && mIsInput && contentDescription.contains(CHAT)) {
                L.d("发送消息界面");
                boolean sendOk = AccessUtil.openNext(nodeInfo, SEND);
                if (sendOk) {
                    mIsSendMsg = true;
                }
            } else if (mIsSendMsg && !mIsSendMsgOk) {

                List<AccessibilityNodeInfo> nodeInfos = nodeInfo.
                        findAccessibilityNodeInfosByViewId(ID_SEND_IB);
                if (!nodeInfos.isEmpty()) {
                    mIsSendMsgOk = true;
                }

            } else if (!mIsBack && mIsSendMsgOk) {

                mIsBack = true;

//                 AccessUtil.performBack(service);
//                 mIsNeedInit = true;
//                 i ++;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {
                            Thread.sleep(500);
                            AccessUtil.performBack(service, nodeInfo);
//                             mIsNeedInit = true;
                            init();
                            i++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } else {
//            AccessUtil.performBack(service);
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
                times++;
                L.e("界面多久没动" + times);
                if (times == delay) {
                    if (!mIsFinish && !AccessUtil.isWeChatMain(nodeInfo)) {//在主界面就不要返回了.
                        AccessUtil.performBack(service, nodeInfo);
                        init();
                    }
                    mTimer.cancel();
                    i++;
                }
            }
        };
        mTimer.schedule(timerTask, delay, 1000);

    }

    private void init() {
        mIsOpenContact = false;
        mIsOpenContactInfo = false;
        mIsOpenSend = false;
        mIsInput = false;
        mIsSendMsg = false;
        mIsSendMsgOk = false;
        mIsBack = false;
    }

    private boolean openContactInfo(AccessibilityNodeInfo nodeInfo) {
        //本页已全部打招呼，所以下滑列表加载下一页，每次下滑的距离是一屏

//        AccessUtil.findNodeInfosById(nodeInfo, "com.tencent.mm:id/id");

        List<AccessibilityNodeInfo> infosByViewId = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/id");
        if (infosByViewId == null || infosByViewId.isEmpty()) {
            L.e("infosbyViewId is null");
            return false;
        }

        int size = infosByViewId.size();
        int index = i % size;

        AccessibilityNodeInfo info = infosByViewId.get(index);

        L.d("i " + i + " page " + page + " size " + size + " index " + index + " info text" +
                " " + info.getText());

//        L.d("每条 node info " +info.findAccessibilityNodeInfosByText("微信团队"));
//        L.d("每条 " + info.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aig"));
//        L.d("每条 " + info.getChild(0)+"");
        if (info.getText().equals("微信团队")
                || info.getText().equals("文件传输助手")) {
            i++;//跳转到下一个
            L.d(" 这个不能发消息 " + info.getText());
            return false;
        }

        if (i < (size * page)) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            mIsOpenContactInfo = true;
            L.d("点击当前 node " + info.getText());
        } else if (i == size * page) {
            //本页已全部打招呼，所以下滑列表加载下一页，每次下滑的距离是一屏
            for (int j = 0; j < nodeInfo.getChild(0).getChildCount(); j++) {
                if (nodeInfo.getChild(0).getChild(j).getClassName().equals("android.widget.ListView")) {
                    AccessibilityNodeInfo node_lsv = nodeInfo.getChild(0).getChild(j);
                    node_lsv.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    page++;
                }
            }
        } else {
            //发送完毕
            mIsFinish = true;
        }

        return false;
    }
}
