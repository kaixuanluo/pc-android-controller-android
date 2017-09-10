package com.example.a90678.wechat_group_send_17_07_02_17_35.access.tuling;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface TulingApiService {

    //获取图灵机器人返回消息
    @GET("/openapi/api")
    Observable<TulingMsgBean> tulingmsg(@QueryMap(encoded = false) Map<String, String> map);
}