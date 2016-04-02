package com.example.gssflyaway.mobilecourse.model;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ruluo1992 on 3/29/2016.
 */
public class ReserveModel extends BaseModel {
    private final String CURRENT_RESERVE_URL = HOST + "/m/current_reserve";
    private final String OLD_RESERVE_URL = HOST + "/m/old_reserve";
    private final String NEW_RESERVE_URL = HOST + "/m/reserve";

    public static final String TIME = "time";
    public static final String PARK = "park";
    public static final String RESERVE_ID = "id";
    public static final String RESERVE_FEE = "fee";

    public static final String STATUS = "status";
    public static final String MESSAGE = "msg";


    private static ReserveModel model = new ReserveModel();
    public static ReserveModel getInstance(){
        return model;
    }

    private Gson gson = new Gson();

    private List<Map> getCurrentReserve(String token) throws IOException {
        Map param = new HashMap();
        param.put("token", token);
        String response = doGet(CURRENT_RESERVE_URL, param);
        return gson.fromJson(response, ArrayList.class);
    }

    private List<Map> getOldReserve(String token) throws IOException {
        Map param = new HashMap();
        param.put("token", token);
        String response = doGet(OLD_RESERVE_URL, param);
        return gson.fromJson(response, ArrayList.class);
    }

    private Map newReserve(Long time, String park, String token, Integer fee) throws IOException {
        Map param = new HashMap();
        param.put("time", time);
        param.put("parks", park);
        param.put("token", token);
        param.put("fee", fee);

        String response = doPost(NEW_RESERVE_URL, param);
        return gson.fromJson(response, HashMap.class);
    }

    public Observable<Map> obNewReserve(final Long time, final String park, final String token, final Integer fee){
        return Observable.just("")
                .map(new Func1<String, Map>() {
                    @Override
                    public Map call(String s) {
                        try {
                            return newReserve(time, park, token, fee);
                        } catch (Exception e) {
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Map>> obGetCurrentReserve(String token){
        return Observable.just(token)
                .map(new Func1<String, List<Map>>() {
                    @Override
                    public List<Map> call(String s) {
                        try{
                            return getCurrentReserve(s);
                        }catch (Exception e){
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Map>> obGetOldReserve(String token){
        return Observable.just(token)
                .map(new Func1<String, List<Map>>() {
                    @Override
                    public List<Map> call(String s) {
                        try{
                            return getOldReserve(s);
                        }catch (Exception e){
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
