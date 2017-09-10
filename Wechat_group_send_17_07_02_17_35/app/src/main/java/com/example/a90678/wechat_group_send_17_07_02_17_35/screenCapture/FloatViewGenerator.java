package com.example.a90678.wechat_group_send_17_07_02_17_35.screenCapture;

import android.view.GestureDetector;

/**
 * Created by 90678 on 2017/7/27.
 */

public class FloatViewGenerator {

//    private GestureDetector mGestureDetector;

//    private ImageView mFloatView;


//    private void createFloatView() {
//        mGestureDetector = new GestureDetector(getApplicationContext(), new FloatGestrueTouchListener());

//        mLayoutParams = new WindowManager.LayoutParams();
//        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//
//        DisplayMetrics metrics = new DisplayMetrics();
//        mWindowManager.getDefaultDisplay().getMetrics(metrics);
//        mScreenDensity = metrics.densityDpi / 1;
//        mScreenWidth = metrics.widthPixels;
//        mScreenHeight = metrics.heightPixels;
//
//        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        mLayoutParams.format = PixelFormat.RGBA_8888;
//        // 设置Window flag
//        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//        mLayoutParams.x = mScreenWidth;
//        mLayoutParams.y = 100;
//        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//
//        mFloatView = new ImageView(getApplicationContext());
//        mFloatView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_imagetool_crop));
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            if (!Settings.canDrawOverlays(this)) {
////                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
////                        Uri.parse("package:" + getPackageName()));
////                startActivity(intent);
////            } else {
////                mWindowManager.addView(mFloatView, mLayoutParams);
////            }
////        } else {
////            mWindowManager.addView(mFloatView, mLayoutParams);
////        }
//
//        mFloatView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return mGestureDetector.onTouchEvent(event);
//            }
//        });
//
//    }

//    private class FloatGestrueTouchListener implements GestureDetector.OnGestureListener {
//        int lastX, lastY;
//        int paramX, paramY;
//
//        @Override
//        public boolean onDown(MotionEvent event) {
//            lastX = (int) event.getRawX();
//            lastY = (int) event.getRawY();
//            paramX = mLayoutParams.x;
//            paramY = mLayoutParams.y;
//            return true;
//        }
//
//        @Override
//        public void onShowPress(MotionEvent e) {
//
//        }
//
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
////            initScreenRecoder();
//            return true;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            int dx = (int) e2.getRawX() - lastX;
//            int dy = (int) e2.getRawY() - lastY;
//            mLayoutParams.x = paramX + dx;
//            mLayoutParams.y = paramY + dy;
//            // 更新悬浮窗位置
////            mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
//            return true;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            return false;
//        }
//    }

}
