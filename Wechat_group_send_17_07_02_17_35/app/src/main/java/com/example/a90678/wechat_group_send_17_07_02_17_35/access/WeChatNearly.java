package com.example.a90678.wechat_group_send_17_07_02_17_35.access;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

/**
 * Created by 90678 on 2017/7/5.
 */

public class WeChatNearly {

    public  int i = 0;//记录已打招呼的人数
    private  int page = 1;//记录附近的人列表页码,初始页码为1

    private  AccessibilityNodeInfo mLastInfo;

    public boolean isGo = true;

    public int step = 0;

    public boolean mIsOpenFind;

    public boolean mIsOpenNearly;

    public boolean mIsOpenContactInfo;

    public boolean mIsOpenHello;

    public boolean mIsOpenSendHello;

    public boolean mIsOpenSendHelloOk;

    public boolean mIsFinish;

    public boolean mIsScrolled;

    private AccessibilityNodeInfo mLastNode;

    private static Timer mTimer;
    private static int times; //统计
    private static int delay = 5; //10秒钟的间隔

    private static String CHAT = "的聊天";
    private static String FIND = "发现";
    private static String NEARLY = "附近的人";
    private static String OPEN_HELLO = "打招呼";
    private static String CURRENT_NEARLY = "当前所在页面,附近的人";
    private static String CURRENT_CONTACT_INFO = "当前所在页面,详细资料";
    private static String CURRENT_SEND_HELLO = "当前所在页面,打招呼";

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
            L.d("is finish ");
            return;
        }

//        当前所在页面,详细资料
//        当前所在页面,打招呼
        if (contentDescription != null) {
            if (contentDescription.equals(CURRENT_NEARLY) || contentDescription.contains(CHAT)) {
                isGo = true;

                mIsOpenNearly = false;
                mIsOpenContactInfo = false;
                mIsOpenHello = false;
                mIsOpenSendHello = false;
                mIsOpenSendHelloOk = false;
            }

        }

        L.d("isGo " + isGo + " step " + step);
        L.d("misOpenFind " + mIsOpenFind);
        L.d("mIsOpenNearly " + mIsOpenNearly);
        L.d("mIsOpenContactInfo " + mIsOpenContactInfo);
        L.d("mIsOpenHello " + mIsOpenHello);
        L.d("mIsOpenSendHelloOk " + mIsOpenSendHelloOk);

        if (isGo) {
            if (contentDescription.contains(CHAT) && !mIsOpenFind) {
                AccessUtil.openNext(nodeInfo, FIND);
                mIsOpenFind = true;
            } else if (contentDescription.contains(CHAT) && !mIsOpenNearly) {
                AccessUtil.openNext(nodeInfo, NEARLY);
                mIsOpenNearly = true;
            } else if (contentDescription.equals(CURRENT_NEARLY) && !mIsOpenContactInfo) {
                openContactInfo(nodeInfo, service);//打开附近的人详情
                mIsOpenContactInfo = true;
            } else if (contentDescription.equals(CURRENT_CONTACT_INFO) && !mIsOpenHello) {
//                startHello(nodeInfo);//点击打招呼
                AccessUtil.openNext(nodeInfo, OPEN_HELLO);
                mIsOpenHello = true;
            } else if (contentDescription.equals(CURRENT_SEND_HELLO) && !mIsOpenSendHello) {
                boolean b = AccessUtil.inputText2(context, service, nodeInfo, hello);//点击发送招呼
                if (b) {
                    mIsOpenSendHello = true;
                }
            } else if (mIsOpenSendHello && !mIsOpenSendHelloOk) {
//                 openNext2(hello, nodeInfo, service);
                AccessUtil.openNext(nodeInfo, "发送");
//                mIsOpenSendHelloOk = true;
            }
        } else {
//            if (contentDescription.contains(CHAT) && !mIsOpenFind) {
//                AccessUtil.performBackWithDelay(service);
//                mIsOpenFind = false;
//            } else if (contentDescription.contains(CHAT) && !mIsOpenNearly) {
//                AccessUtil.performBackWithDelay(service);
//                mIsOpenNearly = false;
//            } else
                if (contentDescription.equals(CURRENT_NEARLY) && mIsOpenContactInfo) {
                AccessUtil.performBackWithDelay(service, nodeInfo);
                mIsOpenContactInfo = false;
            } else if (contentDescription.equals(CURRENT_CONTACT_INFO) && mIsOpenHello) {
                AccessUtil.performBackWithDelay(service, nodeInfo);
                mIsOpenHello = false;
            } else if (contentDescription.equals(CURRENT_SEND_HELLO) && mIsOpenSendHello) {
                AccessUtil.performBackWithDelay(service, nodeInfo);
                mIsOpenSendHello = false;
            } else if (mIsOpenSendHelloOk) {
                    AccessUtil.performBackWithDelay(service, nodeInfo);
                    mIsOpenSendHelloOk = false;
                }
        }

        //如果当前界面5秒钟没有变化.则出错了,后退.
        if (mTimer !=null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        times = 0;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                times++;
                L.e("界面多久没动" + times);
                if (times == delay){
                    if (!mIsFinish  && !contentDescription.equals(CURRENT_NEARLY)) {
                        AccessUtil.performBack(service, nodeInfo);
                    } else if (mIsFinish && !AccessUtil.isWeChatMain(nodeInfo)) {
                        AccessUtil.performBack(service, nodeInfo);
                    }
                    mTimer.cancel();
                    i++;
                }
            }
        };
        mTimer.schedule(timerTask, delay, 1000);
    }

    private void openContactInfo(final AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        //当前在附近的人界面就点选人打招呼
        if (nodeInfo == null) {
            L.d("openContactInfo nodeInfo is null");
            return;
        }
        final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("以内");
        int size = list.size();
        if (size == 0) {
            return;
        }
        if (size == 1) {
            return;
        }

//        //滑动之前的最后一条和滑动之后的最后一条相等.
//        if (mIsScrolled) {
//            if ((list.get(list.size()-1).toString()).equals(mLastInfo.toString())) {
//                //全部打招呼完毕
//                mIsFinish = true;
//                L.e(" mIsFinish "+mIsFinish);
//                L.e("没有更多");
//                AccessUtil.performBack(service);
//                return;
//            }
//            mIsScrolled = false;
//        }
//
//        mLastInfo = list.get(list.size() - 1);

        if( i == 30 ) {
            mIsFinish = true;
        }

        L.d("附近的人列表人数: " + size);
        if (i < (list.size() * page)) {

            list.get(i % list.size()).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            final AccessibilityNodeInfo parent = list.get(i % list.size()).getParent();
            if (parent == null) {
                L.e("parent is null");
                return;
            } else {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            sleep(3000);
//                            Looper.prepare();
//                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                nearlyLoadSuccess();
            }

        } else if (i == list.size() * page) {

            //本页已全部打招呼，所以下滑列表加载下一页，每次下滑的距离是一屏
            for (int j = 0; j < nodeInfo.getChild(0).getChildCount(); j++) {
                if (nodeInfo.getChild(0).getChild(j).getClassName().equals("android.widget.ListView")) {
                    AccessibilityNodeInfo node_lsv = nodeInfo.getChild(0).getChild(j);
                    node_lsv.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    final List<AccessibilityNodeInfo> list2 = nodeInfo.findAccessibilityNodeInfosByText("以内");

                    if (list2 == null) {
                        L.e("list is null");
                        return;
                    }
                    L.d("列表人数: " + list2.size());
                    if (list2.isEmpty()) {
                        L.e(" 附近的人 列表人数为空 ");
                        //重新打开
                        return;
                    }
                    if (list2.size() <= 1) {
                        L.e(" 附近的人 列表人数 " + list2.size());
                        return;
                    }
                    final AccessibilityNodeInfo info = list2.get(1);
                    if (info == null) {
                        L.e("info is null");
                        return;
                    }
                    final AccessibilityNodeInfo parent = info.getParent();
                    if (parent == null) {
                        L.e("perent is null");
                        return;
                    }
                    mIsScrolled = true;
                    page++;
                }
            }
        } else {
//            mIsFinish = true;
        }
    }

    private void openNext2(String str, AccessibilityNodeInfo nodeInfo, final AccessibilityService service) {
        if (nodeInfo == null) {
            L.d("rootWindow为空");
            return;
        } else {
            L.d("openNext2 " + nodeInfo.getText());
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        if (list.isEmpty()) {
            return;
        } else {
            CharSequence text = list.get(list.size() - 1).getText();
            if (TextUtils.isEmpty(text)) {
                return;
            } else {
                L.d("openNext2 " + text);
            }
        }

        i++;
        if (isGo) {
            isGo = false;
        }
    }

    private static void nearlyLoadSuccess() {

    }

    private static void startHello(AccessibilityNodeInfo nodeInfo) {

        if (nodeInfo == null) {
            L.d("rootWindow为空");
            return;
        }
        final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("打招呼");
        if (list == null || list.isEmpty()) {
            return;
        }
        if (list.size() > 0) {
            final AccessibilityNodeInfo info = list.get(list.size() - 1);
            final AccessibilityNodeInfo parent = info.getParent();
            if (parent == null) {
                L.e("parent is null");
                return;
            }

            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        }
    }

}
