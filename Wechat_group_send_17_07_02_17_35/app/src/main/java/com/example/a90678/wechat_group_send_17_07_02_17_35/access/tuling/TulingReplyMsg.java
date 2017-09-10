package com.example.a90678.wechat_group_send_17_07_02_17_35.access.tuling;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 90678 on 2017/5/15.
 */

public class TulingReplyMsg {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public TulingReplyMsg(String msg) {
        this.msg = msg;
    }

    public void replyMsg(final TulingReplyMsgInterface tlrmi) {

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(
                        HttpLoggingInterceptor.Level.BODY))
                ;

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder
                .client(okBuilder.build())
                .baseUrl("http://www.tuling123.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()));

        Retrofit build = retrofitBuilder.build();
        TulingApiService tulingApiService = build
                .create(TulingApiService.class);
        Map<String, String > map = new HashMap<>();
        map.put("key", "4872a234a9fe82234788e2a6adde33c9");
        String[] split = msg.split(":");
        if (split.length>1) {
            map.put("info", split[1]);
            map.put("userid", split[0]);
        } else {
            map.put("info", split[0]);
        }

        Observable<TulingMsgBean> tulingMsgBeanObservable =
                tulingApiService.tulingmsg(map);

        tulingMsgBeanObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseLoadingSubscriber(this))
                .subscribe(new Subscriber<TulingMsgBean>() {

                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {
                                   tlrmi.replyI(e.toString());
                               }

                               @Override
                               public void onNext(TulingMsgBean tulingMsgBean) {
                                   if (tulingMsgBean == null) {
                                       return;
                                   }

                                   String msg = tulingMsgBean.getMsg();

                                   if (msg == null) {

                                       return;
                                   }

                                   String url = tulingMsgBean.getUrl();
                                   if (url == null || TextUtils.isEmpty(url)) {
                                       tlrmi.replyI(msg);
                                   } else {
                                       tlrmi.replyI(msg + "\n" + url);
                                   }
                                   Log.e(" 服务器返回 "," " + msg);
                               }
                           }
                );
    }

}
