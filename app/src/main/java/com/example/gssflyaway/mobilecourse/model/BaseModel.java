package com.example.gssflyaway.mobilecourse.model;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by ruluo1992 on 3/29/2016.
 */
public class BaseModel {
    protected OkHttpClient client = new OkHttpClient();
    protected final String HOST = "http://127.0.0.1";

    protected String getUrlParamsByMap(Map map,
                                           boolean isSort) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        List<String> keys = new ArrayList<String>(map.keySet());
        if (isSort) {
            Collections.sort(keys);
        }
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key).toString();
            sb.append(key + "=" + value);
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.lastIndexOf("&"));
        }
        return s;
    }

    protected String doGet(String url, Map params) throws IOException {
        String paramStr = getUrlParamsByMap(params, false);
        url = url + "?" + paramStr;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    protected String doPost(String url, Map params) throws IOException {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for(Object key : params.keySet()){
            builder.add(key.toString(), params.get(key).toString());
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
