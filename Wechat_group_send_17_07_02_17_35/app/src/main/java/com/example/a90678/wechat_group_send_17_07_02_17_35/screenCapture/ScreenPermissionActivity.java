package com.example.a90678.wechat_group_send_17_07_02_17_35.screenCapture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 90678 on 2017/7/27.
 */

public class ScreenPermissionActivity extends AppCompatActivity {

    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        setTheme(android.R.style.Theme_Dialog);//这个在这里设置 之后导致 的问题是 背景很黑
        super.onCreate(savedInstanceState);

        //如下代码 只是想 启动一个透明的Activity 而上一个activity又不被pause
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setDimAmount(0f);

//        requestScreenShot();

//        PermissionsChecker pc = new PermissionsChecker(this);
//        if (pc.lacksPermissions("android.permission.CAPTURE_VIDEO_OUTPUT",
//                "android.permission.CAPTURE_SECURE_VIDEO_OUTPUT")) {
//        } else {
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivityForResult(
                        mediaProjectionManager.createScreenCaptureIntent(),
                        REQUEST_MEDIA_PROJECTION);
            }
//        }

    }

//
//    public void requestScreenShot() {
//        if (Build.VERSION.SDK_INT >= 21) {
//            startActivityForResult(
//                    ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent(),
//                    REQUEST_MEDIA_PROJECTION
//            );
//        }
//        else
//        {
//            toast("版本过低,无法截屏");
//        }
//    }
//
//    private void toast(String str) {
//        Toast.makeText(ScreenPermissionActivity.this,str,Toast.LENGTH_LONG).show();
//    }
//
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                switch (requestCode) {
                    case REQUEST_MEDIA_PROJECTION:
                        if (resultCode == RESULT_OK && data != null) {
                            ScreenCaptureService.setResultData(data);
//                            startService(screenCaptureIntent);
                            ScreenRecodeService.ScreenCapturePermission event = new ScreenRecodeService.ScreenCapturePermission();
                            event.data = data;
                            EventBus.getDefault().post(event);
                            finish();
                        }
                        break;
                }
            }
        }
    }
}
