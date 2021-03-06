package com.example.gssflyaway.mobilecourse.model;

import com.example.gssflyaway.mobilecourse.GlobalConstant;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ruluo1992 on 3/29/2016.
 */
public class ParkModel extends BaseModel {
    public static final String PARK_NAME = "name";
    public static final String PARKS = "parks";

    private static ParkModel model = new ParkModel();
    public static ParkModel getInstance(){
        return model;
    }

//    private final String PARK_INFO_URL = HOST + "/m/parkinfo";

    private Gson gson = new Gson();

    private Map getParkInfo() throws IOException {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!GlobalConstant.IS_DEBUG) {
            String response = doGet(getParkInfoUrl(), new HashMap());
            System.out.println("!!!!!!!!!!!!!!!! get park response:" + response);
            return gson.fromJson(response, HashMap.class);
        }

        Map map = new HashMap();
        map.put("name", "新华国际影城");
        map.put("parks", "01,02,03,04,05,06,07,08,01,02,03,04,05,06,07,08,01,02,03,04,05,06,07,08,01,02,03,04,05,06,07,08,01,02,03,04,05,06,07,08");
        return map;
    }

    public Observable<Map> obGetParkInfo(){
        return Observable.just("")
                .map(new Func1<String, Map>() {
                    @Override
                    public Map call(String s) {
                        try {
                            return getParkInfo();
                        } catch (Exception e){
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private String getParkInfoUrl(){
        return HOST + "/m/parkinfo";
    }
}
