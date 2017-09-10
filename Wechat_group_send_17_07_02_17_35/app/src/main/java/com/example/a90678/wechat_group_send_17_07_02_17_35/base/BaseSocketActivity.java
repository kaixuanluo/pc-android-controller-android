package com.example.a90678.wechat_group_send_17_07_02_17_35.base;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by 90678 on 2017/7/28.
 */

public class BaseSocketActivity extends BaseActivity {

    public static InputStream mIs;
    public static OutputStream mOs;
    public static DataOutputStream mDos;
    public static DataInputStream mDis;
    public static Socket mSocket;

    public static void setIs(InputStream is) {
        mIs = is;
    }

    public static void setOs(OutputStream os) {
        mOs = os;
    }

    public static void setDos(DataOutputStream dos) {
        mDos = dos;
    }

    public static void setDis(DataInputStream dis) {
        mDis = dis;
    }

    public static InputStream getmIs() {
        return mIs;
    }

    public static OutputStream getmOs() {
        return mOs;
    }

    public static DataOutputStream getmDos() {
        return mDos;
    }

    public static DataInputStream getmDis() {
        return mDis;
    }

    public static Socket getmSocket() {
        return mSocket;
    }

    public static void setmSocket(Socket mSocket) {
        BaseSocketActivity.mSocket = mSocket;
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        try {
//            if (mIs != null) {
//                mIs.close();
//                mIs = null;
//            }
//            if (mOs != null) {
//                mOs.close();
//                mOs = null;
//            }
//            if (mDis != null) {
//                mDis.close();
//                mDis = null;
//            }
//            if (mDos != null) {
//                mDos.close();
//                mDos = null;
//            }
//        } catch (IOException e) {
//
//        }
//    }
}
