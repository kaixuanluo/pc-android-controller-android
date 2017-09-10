package com.example.a90678.wechat_group_send_17_07_02_17_35.screenCapture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import com.example.a90678.wechat_group_send_17_07_02_17_35.main.MainService;
import com.example.a90678.wechat_group_send_17_07_02_17_35.constants.AccessConstants;
import com.example.a90678.wechat_group_send_17_07_02_17_35.eventBusUtil.EventBusConstants;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 90678 on 2017/8/3.
 */

public class ScreenRecodeService extends Service {

    private static final String TAG = "ScreenRecodeService";
    private MediaCodec encoder = null;
    private VirtualDisplay virtualDisplay;

    static MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;

    private ServerSocket mSS;

    private List<Socket> mSocketList = new ArrayList<>();

    private static Intent mResultData = null;

    public static Intent mCapturePermissionIntent;

    public Socket mSocket;
    public InputStream mIs;
    public OutputStream mOs;
    public DataOutputStream mDos;
    public DataInputStream mDis;

    private boolean isNeedSendData = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        requestCapturePermission();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void requestCapturePermission() {
        Intent intent = new Intent(this, ScreenPermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScreenCapturePermission permission) {
//        permission.data;
        EventBus.getDefault().post(new EventBusConstants.IsOpenScreenRecode());
        L.d("接收到权限。。。");
        mCapturePermissionIntent = permission.data;

        initSocket();
        initScreenManager();
    }

    public static class ScreenCapturePermission {
        public Intent data;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopSelf(StopSelf stopSelf) {
        stopSelf();
    }

    public static class StopSelf {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEncodeStart (EventBusConstants.ScreenRecodeStart start) {
//        if (encoder != null) {
//            encoder.start();
//        }
//        sendData1();
        isNeedSendData = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEncodeStop (EventBusConstants.ScreenRecodeStop stop) {
//        if (encoder != null) {
//            encoder.stop();
//        }
        isNeedSendData = false;
    }

    private void initSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSS = new ServerSocket(AccessConstants.PORT_VIDEO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initScreenManager() {
        L.d("初始化录屏。。。");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager)
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);

            mMediaProjection = mMediaProjectionManager.getMediaProjection(Activity.RESULT_OK,
                    mCapturePermissionIntent);
        }
        startDisplayManager();
        new Thread(new EncoderWorker()).start();
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void startScreenCapture() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mMediaProjectionManager = (MediaProjectionManager)
//                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        }
//
//        startActivityForResult(
//                mMediaProjectionManager.createScreenCaptureIntent(),
//                REQUEST_MEDIA_PROJECTION);
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_MEDIA_PROJECTION) {
//            if (resultCode != Activity.RESULT_OK) {
//                Toast.makeText(this, "User cancelled the access", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
//
//            startDisplayManager();
//
//            new Thread(new EncoderWorker()).start();
//
//        }
//    }

    @TargetApi(19)
    private class EncoderWorker implements Runnable {

        @Override
        public void run() {

            if (mSS == null) {
                return;
            }

            boolean isAccept = true;
//            while (isAccept) {
//                try {
//                    Socket socket = mSS.accept();
//                    new Thread(new SendData1()).start();
//                    mSocketList.add(socket);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                try {
                    Log.d(TAG, "连接Socket 中...");
                    mSocket = mSS.accept();
                    mIs = mSocket.getInputStream();
                    mOs = mSocket.getOutputStream();
                    mDis = new DataInputStream(mIs);
                    mDos = new DataOutputStream(mOs);
                    Log.d(TAG, "连接Socket 成功... ");
                    mSocketList.add(mSocket);
                } catch (IOException e) {
                    e.printStackTrace();
//                    if (mSocketList.contains(mSocket)) {
//                        mSocketList.remove(mSocket);
//                    }
                    EventBus.getDefault().post(new EventBusConstants.SocketError());
                }

                if (mDos == null) {
                    Log.e(TAG, " mDos is null return...");
                    return;
                }

//                new Thread(new SendData1()).start();

                sendData1();
//            }

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }).start();
        }
    }

    private class SendData1 implements Runnable {

        @Override
        public void run() {
            sendData1();
        }
    }

    private void sendData1() {
        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();

        boolean encoderDone = false;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        String infoString;

        while (!encoderDone) {

            if (!isNeedSendData) {
                continue;
            }

            int encoderStatus;

            try {
                encoderStatus = encoder.dequeueOutputBuffer(info, 1000);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                break;
            }

            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                //Log.d(TAG, "no output from encoder available");
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = encoder.getOutputBuffers();
                Log.d(TAG, "encoder output buffers changed");
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // not expected for an encoder
                MediaFormat newFormat = encoder.getOutputFormat();
                Log.d(TAG, "encoder output format changed: " + newFormat);
            } else if (encoderStatus < 0) {
                Log.e(TAG, "encoderStatus < 0");
                continue;
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    Log.d(TAG, "============It's NULL. BREAK!=============");
                    return;
                }

//                        infoString = info.offset + "," + info.size + "," +
//                                info.presentationTimeUs + "," + info.flags;
//                        try {
//                            mDos.write(infoString.getBytes());
//                            Log.d(TAG, "输出 info " + infoString);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                final byte[] b = new byte[info.size];
                try {
                    if (info.size != 0) {
                        encodedData.limit(info.offset + info.size);
                        encodedData.position(info.offset);
                        encodedData.get(b, info.offset, info.offset + info.size);

                        try {
                            if (mDos == null) {
                                return;
                            }
                            mDos.write(b);
                            mDos.flush();
//                            Log.d(TAG, "输出 ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                            }
//                        }).start();

                    }

                } catch (BufferUnderflowException e) {
                    e.printStackTrace();
                }

                encoderDone = (info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;

                try {
                    if (encoder == null) {
                        Log.e("ServerService ", "encoder is null");
                        return;
                    }
                    encoder.releaseOutputBuffer(encoderStatus, false);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @TargetApi(19)
    public void startDisplayManager() {
        DisplayManager mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Surface encoderInputSurface = null;
        try {
            encoderInputSurface = createDisplaySurface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            virtualDisplay = mDisplayManager.createVirtualDisplay("Remote Droid",
                    AccessConstants.PHONE_WIDTH, AccessConstants.PHONE_HEIGHT, 50,
                    encoderInputSurface,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC | DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE);
        } else {
            if (mMediaProjection != null) {
                virtualDisplay = mMediaProjection.createVirtualDisplay("Remote Droid",
                        AccessConstants.PHONE_WIDTH, AccessConstants.PHONE_HEIGHT, AccessConstants.DPI,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        encoderInputSurface, null, null);
            } else {
                Log.e(TAG, "Something went wrong. Please restart the app.");
            }
        }

//        sendData2();
        encoder.start();
    }

    /**
     * Create the display surface out of the encoder. The data to encoder will be fed from this
     * Surface itself.
     *
     * @return
     * @throws IOException
     */
    @TargetApi(19)
    private Surface createDisplaySurface() throws IOException {

        Log.i(TAG, "Starting encoder");
        encoder = DeEncodecCommon.getMediaCodec();
        Surface surface = encoder.createInputSurface();
        return surface;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (encoder != null) {
            encoder.release();
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }

        if (mMediaProjection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaProjection.stop();
            }
        }

        if (mMediaProjectionManager != null) {
        }

        if (mSS != null) {
            try {
                mSS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

