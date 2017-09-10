package com.example.a90678.wechat_group_send_17_07_02_17_35.motionClick;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.example.a90678.wechat_group_send_17_07_02_17_35.main.MainService;
import com.example.a90678.wechat_group_send_17_07_02_17_35.constants.AccessConstants;
import com.example.a90678.wechat_group_send_17_07_02_17_35.eventBusUtil.EventBusConstants;
import com.example.a90678.wechat_group_send_17_07_02_17_35.motionClick.bean.BaseOperationBean;
import com.example.a90678.wechat_group_send_17_07_02_17_35.motionClick.bean.MouseBean;
import com.example.a90678.wechat_group_send_17_07_02_17_35.motionClick.bean.ScreenBean;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by 90678 on 2017/7/25.
 */

public class MotionClickService extends Service {

    int deviceWidth, deviceHeight;

    private Thread mThreadShell;

    private boolean mIsLoop = true;

    public Socket mSocket;
    public InputStream mIs;
    public OutputStream mOs;
    public DataOutputStream mDos;
    public DataInputStream mDis;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        L.d("MotionClickService is create ");

        DisplayMetrics dm = new DisplayMetrics();
        Display mDisplay = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mDisplay.getMetrics(dm);
        deviceWidth = dm.widthPixels;
        deviceHeight = dm.heightPixels;

        initSocket();

        EventBus.getDefault().register(this);
    }

    private void initSocket() {
        L.d("初始化 Socket ...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    L.d("链接服务器中...");
//                    L.d("ip " + AccessConstants.IP +" port " + AccessConstants.PORT);
//                    mSocket = new Socket(AccessConstants.IP, AccessConstants.PORT);
                    String ip = SPUtils.getIp(MotionClickService.this);
                    L.d("ip " + ip +" port " + AccessConstants.PORT);
                    mSocket = new Socket(ip, AccessConstants.PORT);
                    L.d("连接服务器成功...");
                    mSocket.setKeepAlive(true);
                    mOs = mSocket.getOutputStream();
                    mDos = new DataOutputStream(mOs);
                    mIs = mSocket.getInputStream();
                    mDis = new DataInputStream(mIs);

                    receiveShell();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stopSocket() {
        L.d("关闭 Socket...");
        try {
            if (mSocket != null) {
                mSocket.close();
            }
            if (mDis != null) {
                mDis.close();
            }
            if (mDos != null) {
                mDos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MCSStopSelf stopSelf) {
        L.d("MotionClickService stop self...");
        stopSelf();
    }

    public static class MCSStopSelf {

    }

    public void receiveShell() {
        mThreadShell = new Thread(new Runnable() {
            @Override
            public void run() {
//                    dos.writeUTF("{\"" + "name\":" + PhoneInfo.getPhoneName() + ","
//                            + "\"" + "version\":" + PhoneInfo.getVersionName() + ","
//                            + "\"" + "imei\":" + PhoneInfo.getImei(MotionClickService.this) + "}");
//                    dos.flush();
                while (mIsLoop) {

                    L.d("进入接收消息中...");
                    if (mDis == null) {
//                        EventBus.getDefault().post(new MainActivity.SocketError());
                        L.e("mDis is null...");
                        break;
                    }
                    if (mSocket == null) {
                        L.e("mSocket is null...");
                        break;
                    }
                    if (mSocket.isClosed()) {
                        L.e("mSocket is closed...");
                        break;
                    }
                    String utf = null;

                    try {
                        utf = mDis.readUTF();
//                        mDos.writeUTF("get ");
//                        mDos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        L.e("错误了..." + e);
                        mIsLoop = false;
                        EventBus.getDefault().post(new EventBusConstants.SocketError());
                        break;
                    }

                    final String finalUtf = utf;
                    L.d("得到服务器消息11111111 " + finalUtf);
//                    exeShell(finalUtf);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject touch = null;
                            try {
                                touch = new JSONObject(finalUtf);
                                String type0 = touch.getString(BaseOperationBean.TYPE0);
                                switch (type0) {
                                    case MouseBean.MOUSE:
                                        exeShell(touch);
                                        break;
                                    case ScreenBean.SCREEN:
                                        exeScreen(touch);
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            }
        });
        mThreadShell.start();
    }

    private void exeScreen(JSONObject touch) {
        try {
            String type = touch.getString(ScreenBean.OPERATION);
            switch (type) {
                case ScreenBean.START:
                    EventBus.getDefault().post(new EventBusConstants.ScreenRecodeStart());
                    break;
                case ScreenBean.STOP:
                    EventBus.getDefault().post(new EventBusConstants.ScreenRecodeStop());
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void exeShell(JSONObject touch) {
        try {
            float x = Float.parseFloat(touch.getString(MouseBean.X_DP)) * deviceWidth;
            float y = Float.parseFloat(touch.getString(MouseBean.Y_DP)) * deviceHeight;
            String eventType = touch.getString(MouseBean.TYPE);
            Utils utils = Utils.getInstance(MotionClickService.this);
            L.d("得到服务器消息2222222 " + touch.toString());
//                            if (eventType.equals(MouseBean.LEFT_DOWN)) {
//                                input.injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, 0,
//                                        SystemClock.uptimeMillis(), x, y, 1.0f);
//                            } else if (eventType.equals(MouseBean.LEFT_UP)) {
//                                input.injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, 1,
//                                        SystemClock.uptimeMillis(), x, y, 1.0f);
//                            }
//                            else if (eventType.equals(MouseBean.LEFT_DOWN_MOVE)) {
//                                input.injectMotionEvent(InputDeviceCompat.SOURCE_TOUCHSCREEN, 2,
//                                        SystemClock.uptimeMillis(), x, y, 1.0f);
//                            } else
            if (eventType.equals(MouseBean.LEFT_CLICK)) {
                utils.execShellClickCmd(x, y);
            } else if (eventType.equals(MouseBean.RIGHT_CLICK)) {
                //打开菜单
                utils.execShellCmd(KeyEvent.KEYCODE_MENU);
            } else if (eventType.equals(MouseBean.LEFT_SWIPE)) {
                String xdp2 = touch.getString(MouseBean.X_DP2);
                float x2 = 0;
                if (!TextUtils.isEmpty(xdp2)) {
                    x2 = Float.parseFloat(xdp2) * deviceWidth;
                }
                String ydp2 = touch.getString(MouseBean.Y_DP2);
                float y2 = 0;
                if (!TextUtils.isEmpty(ydp2)) {
                    y2 = Float.parseFloat(ydp2) * deviceHeight;
                }
                utils.execShellSwipeCmd(x, y, x2, y2);
            } else if (eventType.equals(MouseBean.LEFT_TOUCH)) {
                utils.execShellTouchCmd2(x, y);
            } else if (eventType.equals(MouseBean.POWER_OFF)) {
                utils.shutdown();
            } else if (eventType.equals(MouseBean.REBORT)) {
                utils.reboot();
            } else if (eventType.equals(MouseBean.HOME)) {
                utils.execShellCmd(KeyEvent.KEYCODE_HOME);
            } else if (eventType.equals(MouseBean.BACK)) {
                utils.execShellCmd(KeyEvent.KEYCODE_BACK);
            }
            L.d("执行命令完成...");
        } catch (Exception e) {
            e.printStackTrace();
            L.e("执行命令错误...");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSocket();
        mIsLoop = false;
        L.d("MotionClickService destroy");
    }
}
