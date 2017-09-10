package com.example.a90678.wechat_group_send_17_07_02_17_35.screenCapture;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.os.AsyncTaskCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.example.a90678.wechat_group_send_17_07_02_17_35.access.AccessUtil;
import com.example.a90678.wechat_group_send_17_07_02_17_35.eventBusUtil.EventBusConstants;
import com.example.a90678.wechat_group_send_17_07_02_17_35.main.MainService;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;
import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.Navigator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by branch on 2016-5-25.
 * <p>
 * 启动悬浮窗界面
 */
public class ScreenCaptureService extends Service {

    public static Intent newIntent(Context context, Intent mResultData) {

        Intent intent = new Intent(context, ScreenCaptureService.class);

        if (mResultData != null) {
            intent.putExtras(mResultData);
        }
        return intent;
    }

    public Socket mSocket;
    public InputStream mIs;
    public OutputStream mOs;
    public DataOutputStream mDos;
    public DataInputStream mDis;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private static Intent mResultData = null;

    private ImageReader mImageReader;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    public static final int CAPTURE = 3;

    private boolean mIsLoop = true;

    private boolean mImageIsClose = true;
    private boolean mIsGetImgOk = false;

    private long mLastCaptureTime;

    private long lastTime = System.currentTimeMillis();

    Thread mThreadInitAll;
    Thread mThreadLooper;
    Thread mThreadCapture;
    Thread mThreadImgCompress;

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CAPTURE:

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        startCaptureKIKATT();
                    } else {
                        if (mVirtualDisplay == null) {
                            L.e("mVirtualDisplay is null");
//                        initScreenRecoder();
                        } else {
                            startCapture();
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        L.d("screenCaptureService is create");
        initAll();

    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        L.d("screenCaptureService is rebind");
        initAll();
    }

    private void initAll () {

        mThreadInitAll = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    initScreenInfo();
                    initImageReader();
                    initMediaProjection();
                    initVirtualDisplay();
                    try {
                        Thread.sleep(3000);
                        startCapture();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    startCaptureKIKATT();
                }
            }
        });
        mThreadInitAll.start();

        L.d("ScreenCapture Service is oncreate ");

        EventBus.getDefault().register(this);

        mThreadLooper = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsLoop) {
                    try {
                        Message msg = Message.obtain();
                        msg.what = CAPTURE;
                        myHandler.sendMessage(msg);
                        if (new AccessUtil().isGroupReplyServiceEnabled(ScreenCaptureService.this)) {
                            Thread.sleep(10000);//10秒自动刷新
                        } else {
                            Thread.sleep(500);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThreadLooper.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ContentChange contentChange) {
        if (contentChange.stopSelf) {
            L.e("停止当前 ScreenCapture Service ");
            mIsLoop = false;
            stopSelf();
        } else {
            L.d("界面变化。。。");
            if (System.currentTimeMillis() - lastTime > 500) {
                mThreadCapture = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 1; i < 4; i++) {
                                if (!mIsLoop) {
                                   break;
                                }
                                Thread.sleep(i * 200);
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                    startCaptureKIKATT();
                                } else {
                                    startCapture();
                                }
                            }
                            lastTime = System.currentTimeMillis();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                mThreadCapture.start();
            }
        }
    }

    public static class ContentChange {
        public boolean stopSelf;
    }

    public static void setResultData(Intent mResultData) {
        ScreenCaptureService.mResultData = mResultData;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initScreenInfo() {
        mLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi / 1;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;

    }

    private void initImageReader() {
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1);
    }

    /**
     * 初始化 mediaProjection 录屏服务管理器.
     */
    public void initMediaProjection() {

        //如果主界面传过来的请求截图权限是空的
        //重新到主界面请求截图权限.

        //获取录屏服务管理器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mResultData == null) {
                L.e("mResultData is null");
                Navigator.launchMain(this);
            } else {
                try {
                    L.d("初始化 mMediaProjection " + (mMediaProjection == null));
                    if (mMediaProjection == null) {
                        mMediaProjection = ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE))
                                .getMediaProjection(Activity.RESULT_OK, mResultData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 将录屏服务的内容显示到Image面板上.
     */
    private void initVirtualDisplay() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            DisplayManager mDisplayManager = (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
            if (mDisplayManager == null) {
                L.e(" 获取 播放管理器失败 ... ");
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                L.d("当前安卓 版本 " + Build.VERSION.SDK_INT + " 进入到图片展示 ");
                mVirtualDisplay = mDisplayManager.createVirtualDisplay("Remote Droid", mScreenWidth,
                        mScreenHeight, mScreenDensity,
                        mImageReader.getSurface(),
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC | DisplayManager.VIRTUAL_DISPLAY_FLAG_SECURE);
            }
        } else {
            if (mMediaProjection == null) {
                L.e("mMediaProjection is null ...");
                return;
            } else {
                mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                        mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        mImageReader.getSurface(), null, null);
            }
        }
    }

    private void startCaptureKIKATT() {
        L.d("kikat 开始截图...");
        String path = "/mnt/sdcard/ScreenShot.png";
        ScreentShotUtil.getInstance().takeScreenshot(this, path);
//        Bitmap bitmap = KIKATSCUtil.doCaptureScreeKITKAT();
////        Bitmap bitmap = KIKATSCUtil.acquireScreenshot(this);
        Bitmap bitmap = null;
        try {
            bitmap = ImageCompressUtil.getThumbUploadPathBitmap(new File(path));
//            bitmap = ScreentShotUtil.getInstance().takeSSBitmap(this, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            L.e("bitmap is null");
        } else {
            L.d("截图不为空 ");
            compressAndWrite(bitmap);
        }
    }

    /**
     * 开始截图. 获取 image 面板上的图片.
     */
    private void startCapture() {
        if (System.currentTimeMillis() - mLastCaptureTime < 200) {
            L.e("截图间隔时间过短...");
            return;
        }
        mLastCaptureTime = System.currentTimeMillis();
        Image image = null;
        try {
            if (mImageReader == null) {
                L.e("mImageReader is null ...");
                return;
            }

//            if(!mImageIsClose) {
//                L.e("mImage is not close...");
//                return;
//            }
            mIsGetImgOk = false;
            image = mImageReader.acquireLatestImage();
            mIsGetImgOk = true;
            mImageIsClose = false;
        } catch (Exception e) {
            e.printStackTrace();
            mImageIsClose = true;
        }

        if (image == null) {
            L.e(" 获取 image 为空 结束..");
            return;
        } else {
            L.d("获取图片不为空...");
            SaveTask mSaveTask = new SaveTask();
            AsyncTaskCompat.executeParallel(mSaveTask, image);
        }
    }

    private void compressAndWrite(Bitmap bitmap) {
        if (mDos == null) {
            Log.e("FloatWin ", "未连接上, mDos 为空");
            return;
        }

        if (bitmap == null) {
            L.e("bitmp 空");
            return;
        }

        L.d("进入图片压缩...");
        final Bitmap[] bitmapCopy = {bitmap};
        mThreadImgCompress = new Thread(new Runnable() {
            @Override
            public void run() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //读取图片到ByteArrayOutputStream
                bitmapCopy[0] = ImageCompressUtil.compressImage(Bitmap.createScaledBitmap(bitmapCopy[0], 270, 480, true));
                bitmapCopy[0].compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] bytes = baos.toByteArray();
                try {
                    L.d("图片压缩完毕。。。");
                    if (mDos == null) {
                        L.e("mDis 为空...");
                        return;
                    }
                    if (mSocket == null) {
                        L.e("mSocket is null...");
                        return;
                    }
                    if (mSocket.isClosed()) {
                        L.e("mSocket is closed...");
                        return;
                    }
                    if (mSocket.isConnected()) {
                        mDos.write(bytes);
                        mDos.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new EventBusConstants.SocketError());
                    stopSelf();
                    return;
                }
            }
        });
        mThreadImgCompress.start();

    }

    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Image... params) {

            if (params == null || params.length < 1 || params[0] == null) {

                L.e(" params is null ...");
                return null;
            }

            if (mImageIsClose) {
                L.e("image is closed...");
                return null;
            }
            Image image = params[0];

            try {
                int width = image.getWidth();
                int height = image.getHeight();
                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();
                //每个像素的间距
                int pixelStride = planes[0].getPixelStride();
                //总的间距
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                image.close();
                mImageIsClose = true;

                compressAndWrite(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            mImageIsClose = true;
        }
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaProjection.stop();
            }
            mMediaProjection = null;
        }
    }

    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    @Override
    public void onDestroy() {
        // to remove mFloatLayout from windowManager
        super.onDestroy();
//        if (mFloatView != null) {
//            mWindowManager.removeView(mFloatView);
//        }
        stopVirtual();

        tearDownMediaProjection();

        EventBus.getDefault().unregister(this);

        mIsLoop = false;

        if (myHandler != null) {
            myHandler = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mMediaProjection != null) {
                mMediaProjection.stop();
                mMediaProjection = null;
            }
        }

        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }

        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }

        L.d("screenCaptureService is destroy");
    }


}
