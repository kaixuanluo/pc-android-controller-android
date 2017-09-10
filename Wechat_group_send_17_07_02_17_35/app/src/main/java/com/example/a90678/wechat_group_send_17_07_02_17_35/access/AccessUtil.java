package com.example.a90678.wechat_group_send_17_07_02_17_35.access;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import static java.lang.Thread.sleep;

/**
 * Created by 90678 on 2017/7/5.
 */

public class AccessUtil {

    static AccessibilityNodeInfo editText;
    public static List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    /**
     * 通过文本查找
     */
    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    //通过组件名递归查找编辑框
    public static void findNodeInfosByName(AccessibilityNodeInfo nodeInfo, String name) {
        if (TextUtils.isEmpty(name)) {
            L.e("name is null" + name);
            return;
        }
        if (nodeInfo == null) {
            L.e("nodeInfo is null " + nodeInfo);
            return;
        }
//        L.d("findNodeInfosByName "+nodeInfo.getText());
        if (name.equals(nodeInfo.getClassName())) {
            editText = nodeInfo;
            return;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            findNodeInfosByName(nodeInfo.getChild(i), name);
        }
    }
    public static  List<AccessibilityNodeInfo>  findNodeInfosById(AccessibilityNodeInfo nodeInfo, String id) {
        if (TextUtils.isEmpty(id)) {
            L.e("name is null" + id);
            return nodeInfoList;
        }
        if (nodeInfo == null) {
            L.e("nodeInfo is null " + nodeInfo);
            return nodeInfoList;
        }
//        L.d("findNodeInfosByName "+nodeInfo.getText());
        String viewIdResourceName = nodeInfo.getViewIdResourceName();
        L.d("viewIdResourceName " + viewIdResourceName + " child Count " + nodeInfo.getChildCount());
        if (id.equals(viewIdResourceName)) {
            nodeInfoList.add(nodeInfo);
            return nodeInfoList;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            findNodeInfosByName(nodeInfo.getChild(i), id);
        }
        return nodeInfoList;
    }

    /**
     * 点击事件
     */
    public static void performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

    /**
     * 返回事件
     */
    public static void performBackWithDelay(final AccessibilityService service, AccessibilityNodeInfo nodeInfo) {
        CharSequence packageName = nodeInfo.getPackageName();
        if (!(packageName+"").equals(WECHAT_PACKAGE_NAME)) {
            L.d("不是微信,不能后退 " + packageName);
            return;
        }
        if (service == null) {
            L.e("performBackWithDelay service is null");
            return;
        }
        if (service.getRootInActiveWindow() == null) {
            L.e("performBackWithDelay rootNode is null");
            return;
        }
        L.d("performBackWithDelay c ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 返回事件
     */
    public static void performBack(final AccessibilityService service, AccessibilityNodeInfo nodeInfo) {
        CharSequence packageName = nodeInfo.getPackageName();
        if (!(packageName+"").equals(WECHAT_PACKAGE_NAME)) {
            L.d("不是微信,不能后退 " + packageName);
            return;
        }
        if (service == null) {
            L.e("performBackWithDelay service is null");
            return;
        }
        if (service.getRootInActiveWindow() == null) {
            L.e("performBackWithDelay rootNode is null");
            return;
        }
        L.d("performBack Reboot ");
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
    /**
     * 返回事件
     */
    public static void performHome(final AccessibilityService service) {
        if (service == null) {
            L.e("performBackWithDelay service is null");
            return;
        }
        if (service.getRootInActiveWindow() == null) {
            L.e("performBackWithDelay rootNode is null");
            return;
        }
        L.d("performHome");
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    /**
     * 点击匹配的nodeInfo
     *
     * @param str text关键字
     */
    public static boolean openNext(AccessibilityNodeInfo nodeInfo, String str) {
        if (nodeInfo == null) {
            L.d("rootWindow为空");
            return false;
        }
        final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        if (list == null || list.isEmpty()) {
            return false;
        }
        if (list != null && list.size() > 0) {
            final AccessibilityNodeInfo info = list.get(list.size() - 1);
            if (info == null) {
                return false;
            }
            final AccessibilityNodeInfo parent = info.getParent();
            if(parent == null) {
                return false;
            }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Looper.prepare();
//                    try {
//                        sleep(2000);
////                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            return true;

        } else {
//            Toast.makeText(this, "找不到有效的节点", Toast.LENGTH_SHORT).show();
        }

        return false;

    }

    //延迟打开界面
    private void openDelay(final AccessibilityNodeInfo nodeInfo, final int delaytime, final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    sleep(delaytime);
                } catch (InterruptedException mE) {
                    mE.printStackTrace();
                }
                openNext(nodeInfo, text);
            }
        }).start();
    }

    //自动输入打招呼内容
    public static void inputText(Context context, AccessibilityService service, AccessibilityNodeInfo nodeInfo,
                                 final String hello) {
        //找到当前获取焦点的view
//        AccessibilityNodeInfo target = nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        AccessUtil.findNodeInfosByName(nodeInfo, "android.widget.EditText");
        AccessibilityNodeInfo target = editText;
        if (target == null) {
            L.d("inputHello: null");

            return;
        } else {
            L.d("inputHello: not null " + target.getText());
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("message", hello);
        clipboard.setPrimaryClip(clip);
        L.d("设置粘贴板");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            target.performAction(AccessibilityNodeInfo.ACTION_PASTE);
//        }
        target.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        L.d("获取焦点");
        target.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        L.d("粘贴内容");
//        openNext2("发送", nodeInfo, service);//点击发送
    }
    //自动输入打招呼内容
    public static boolean inputText2(Context context, AccessibilityService service, AccessibilityNodeInfo nodeInfo,
                                 final String hello) {
        //找到当前获取焦点的view
//        AccessibilityNodeInfo target = nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        AccessUtil.findNodeInfosByName(nodeInfo, "android.widget.EditText");
        AccessibilityNodeInfo target = editText;
        if (target == null) {
            L.d("inputHello: null");

            return false;
        } else {
            L.d("inputHello: not null " + target.getText());
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("message", hello);
        clipboard.setPrimaryClip(clip);
        L.d("设置粘贴板");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            target.performAction(AccessibilityNodeInfo.ACTION_PASTE);
//        }
        target.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        L.d("获取焦点");
        target.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        L.d("粘贴内容");
        return true;
    }


    private void sendGroupMsg(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            L.e("sendGroupMsg nodeInfo " + nodeInfo);
            return;
        }

        findChildView2(nodeInfo, "");
    }

    private void findChildView(AccessibilityNodeInfo info, String findText) {
        String text = info.getText() + "";
        boolean isContentTxl = text.equals(findText);
        if (info.getChildCount() == 0) {
            if (!TextUtils.isEmpty(text) && isContentTxl) {
                L.e("Text：" + text + "是否" + isContentTxl + "是否2" + text.equals("通讯录"));
                performClick(info);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    findChildView(info.getChild(i), findText);
                }
            }
        }
    }

    private void findChildView2(AccessibilityNodeInfo info, String parentText) {
        parentText = parentText + " |-- " + info.getText();
        L.d("得到控件 " + parentText);
        for (int i = 0; i < info.getChildCount(); i++) {
            AccessibilityNodeInfo child = info.getChild(i);
            if (child != null) {
//                L.d("得到子控件 " + child.getText());
                findChildView2(child, parentText + "");
            } else {
//                L.d("得到所有控件" + info.getText());
            }
        }
    }

    /**
     * 是否是在微信的主页面
     */
    public static boolean isWeChatMain (AccessibilityNodeInfo nodeInfo) {
        boolean b = !nodeInfo.findAccessibilityNodeInfosByText("微信").isEmpty()
                && !nodeInfo.findAccessibilityNodeInfosByText("通讯录").isEmpty()
                && !nodeInfo.findAccessibilityNodeInfosByText("发现").isEmpty()
                && !nodeInfo.findAccessibilityNodeInfosByText("我").isEmpty();
        return b;
    }

    public boolean isGroupReplyServiceEnabled(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
//            if (info.getId().equals(getPackageName() + "/.WeChatGroupSendService")) {
            if (info.getId().equals(context.getPackageName() + "/.access.WeChatAccessService")) {
                return true;
            }
        }
        return false;
    }

}
