package com.example.a90678.wechat_group_send_17_07_02_17_35.screenCapture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 90678 on 2017/7/27.
 */

public class KIKATSCUtil {

    /**
     * 截图
     */
    public static Bitmap doCaptureScreeKITKAT() {
        List<String> command = new ArrayList<>();
        command.add("/system/bin/screencap -p /sdcard/ " + System.currentTimeMillis()+".png");
//        command.add("screenshot");
//        command.add("screencap -p");
//        command.add("screencap");
//        command.add("input keyevent screencap -p");
        L.d("进入截图");
        return getScreenShotStream(command.toArray(new String[]{}), true,
                true);
    }

    /**
     * 获取截屏流，转换成位图
     *
     * @param commands
     * @param isRoot
     * @param isNeedResultMsg
     */
    public static Bitmap getScreenShotStream(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        if (commands == null || commands.length == 0) {
            return null;
        }
        Process process = null;
        DataOutputStream os;
        try {
            process = Runtime.getRuntime().exec(isRoot ? "su" : "sh");
//            process = Runtime.getRuntime().exec("sh");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes("\\\\n");
                os.flush();
            }
            os.writeBytes("exit\\\\n");
            L.d("截图命令");
//            os.writeBytes(commands[0]);
            L.d("截图命令输入完成");
            os.flush();
            if (isNeedResultMsg) {
                L.d("读取截图流");
                Bitmap bitmap = BitmapFactory.decodeStream(process.getInputStream());
                L.d("读取截图流完成 ...");
                if (bitmap == null) {
                    return null;
                } else {
                    return bitmap;
                }
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (process != null) {
                    process.exitValue();
                }
            } catch (IllegalThreadStateException e) {
                process.destroy();
            }
        }
        return null;
    }

    /**
     *
     * @param path 图片保存路径
     */
    public void screenshot(String path){
        Process process = null;
        try{
            process = Runtime.getRuntime().exec("su");
            PrintStream outputStream = null;
            try {
                outputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
                outputStream.println("screencap -p " + path);
                outputStream.flush();
            }catch(Exception e){
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            process.waitFor();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(process != null){
                process.destroy();
            }
        }
    }

    private static final String DEVICE_NAME = "/dev/graphics/fb0";
    @SuppressWarnings("deprecation")
    public static Bitmap acquireScreenshot(Context context) {
        WindowManager mWinManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = mWinManager.getDefaultDisplay();
        display.getMetrics(metrics);
        // 屏幕高
        int height = metrics.heightPixels;
        // 屏幕的宽
        int width = metrics.widthPixels;

        int pixelformat = display.getPixelFormat();
        PixelFormat localPixelFormat1 = new PixelFormat();
        PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
        // 位深
        int deepth = localPixelFormat1.bytesPerPixel;

        byte[] arrayOfByte = new byte[height * width * deepth];
        try {
            // 读取设备缓存，获取屏幕图像流
            InputStream localInputStream = readAsRoot();
            DataInputStream localDataInputStream = new DataInputStream(
                    localInputStream);
            localDataInputStream.readFully(arrayOfByte);
            localInputStream.close();

            int[] tmpColor = new int[width * height];
            int r, g, b;
            for (int j = 0; j < width * height * deepth; j += deepth) {
                b = arrayOfByte[j] & 0xff;
                g = arrayOfByte[j + 1] & 0xff;
                r = arrayOfByte[j + 2] & 0xff;
                tmpColor[j / deepth] = (r << 16) | (g << 8) | b | (0xff000000);
            }
            // 构建bitmap
            Bitmap scrBitmap = Bitmap.createBitmap(tmpColor, width, height,
                    Bitmap.Config.ARGB_8888);
            return scrBitmap;

        } catch (Exception e) {
            L.d( "#### 读取屏幕截图失败");
            e.printStackTrace();
        }
        return null;

    }

    /**
     * @Title: readAsRoot
     * @Description: 以root权限读取屏幕截图
     * @throws Exception
     * @throws
     */
    public static InputStream readAsRoot() throws Exception {
        File deviceFile = new File(DEVICE_NAME);
        Process localProcess = Runtime.getRuntime().exec("su");
        String str = "cat " + deviceFile.getAbsolutePath() + "\n";
        localProcess.getOutputStream().write(str.getBytes());
        return localProcess.getInputStream();
    }


}
