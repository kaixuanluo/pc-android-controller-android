package com.example.a90678.wechat_group_send_17_07_02_17_35.access;

import android.app.Notification;
import android.app.PendingIntent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by 90678 on 2017/7/5.
 */

public class WeChatMsg {

    String chatRecord = "";
    String chatName = "";
    private void getWeChatLog(AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            //获取所有聊天的线性布局
            List<AccessibilityNodeInfo> listChatRecord =
                    rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/o");
            if (listChatRecord.size() == 0) {
                return;
            }
            //获取最后一行聊天的线性布局（即是最新的那条消息）
            AccessibilityNodeInfo finalNode = listChatRecord.get(listChatRecord.size() - 1);
            //获取聊天对象list（其实只有size为1）
            List<AccessibilityNodeInfo> imageName = 
                    finalNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/i_");
            //获取聊天信息list（其实只有size为1）
            List<AccessibilityNodeInfo> record = 
                    finalNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ib");
            if (imageName.size() != 0) {
                if (record.size() == 0) {
                    //判断当前这条消息是不是和上一条一样，防止重复
                    if (!chatRecord.equals("对方发的是图片或者表情")) {
                        //获取聊天对象
                        chatName = imageName.get(0).getContentDescription().toString().replace("头像", "");
                        //获取聊天信息
                        chatRecord = "对方发的是图片或者表情";

                        Log.e("AAAA", chatName + "：" + "对方发的是图片或者表情");
                    }
                } else {
                    //判断当前这条消息是不是和上一条一样，防止重复
                    if (!chatRecord.equals(record.get(0).getText().toString())) {
                        //获取聊天对象
                        chatName = imageName.get(0).getContentDescription().toString().replace("头像", "");
                        //获取聊天信息
                        chatRecord = record.get(0).getText().toString();

                        Log.e("AAAA", chatName + "：" + chatRecord);
                    }
                }
            }
        }
    }

    public static void sendNotify(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            String message = texts.get(0).toString();

            //过滤微信内部通知消息
            if (isInside(message)) {
                return;
            }

            //模拟打开通知栏消息
            if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                Log.i("demo", "标题栏canReply=true");
                try {
                    Notification notification = (Notification) event.getParcelableData();
                    PendingIntent pendingIntent = notification.contentIntent;
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //常见的微信内部通知，可自行测试并修改
    private static boolean isInside(String msg) {
        boolean result = false;
        if (msg.equals("已复制") || msg.equals("已分享") || msg.equals("已下载"))
            result = true;
        if (msg.length() > 6 && (msg.substring(0, 6).equals("当前处于移动") || msg.substring(0, 6).equals("无法连接到服") || msg.substring(0, 6).equals("图片已保存至") || msg.substring(0, 6).equals("网络连接不可")))
            result = true;
        return result;
    }

    private void tulingReply(final StaticData.DataMsg dataMsg) {

//        TulingReplyMsg tulingReplyMsg = new TulingReplyMsg(dataMsg.getDataStr());
//        tulingReplyMsg.replyMsg(new TulingReplyMsgInterface() {
//            @Override
//            public void replyI(String msg) {
//                reply(msg);
//
//                dataMsg.setReply(true);
//
//                performBackWithDelay(AutoReplyService.this);
//            }
//        });
    }
}
