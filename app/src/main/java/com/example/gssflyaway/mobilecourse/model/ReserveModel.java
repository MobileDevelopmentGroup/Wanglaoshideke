package com.example.gssflyaway.mobilecourse.model;

import android.media.session.MediaSession;

import com.example.gssflyaway.mobilecourse.GlobalConstant;
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
//    private  String CURRENT_RESERVE_URL = HOST + "/m/current_reserve";
//    private  String OLD_RESERVE_URL = HOST + "/m/old_reserve";
//    private  String NEW_RESERVE_URL = HOST + "/m/reserve";
//    private  String DELAY_RESERVE_URL = HOST + "/m/current_reserve/delay";
//    private  String CANCEL_RESERVE_URL = HOST + "/m/current_reserve/cancel";
//    private  String RESERVE_AVALIABLE = HOST + "/m/reserve_avaliable";

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


    public String getCANCEL_RESERVE_URL() {
        return HOST + "/m/current_reserve/cancel";
}

    public String getCURRENT_RESERVE_URL() {
        return HOST + "/m/current_reserve";
    }

    public String getDELAY_RESERVE_URL() {
        return HOST + "/m/current_reserve/delay";
    }

    public String getNEW_RESERVE_URL() {
        return HOST + "/m/reserve";
    }

    public String getOLD_RESERVE_URL() {
        return HOST + "/m/old_reserve";
    }

    public String getRESERVE_AVALIABLE() {
        return HOST + "/m/reserve_avaliable";
    }

    public String getCheckinUrl(String id, String token) {
        return String.format("%s?token=%s&id=%s", HOST + "/m/checkReserve", token, id);
    }

    public boolean isAvaliable(String id, String token) throws IOException {
        Map<String, String> param = new HashMap<>();
        param.put("id", id);
        param.put("token", token);
        String response = doGet(getRESERVE_AVALIABLE(), param);
        if(response.trim().equals("0"))
            return false;
        else
            return true;
    }

    private List<Map> getCurrentReserve(String token) throws IOException, InterruptedException {
        if(GlobalConstant.IS_DEBUG){
            List<Map> result = new ArrayList<>();
            for(int i = 0; i < 3; i++){
                Map map = new HashMap();
                map.put("time", new Double(System.currentTimeMillis() + 360000));
                map.put("park", "001");
                map.put("id", "1");
                map.put("fee", new Double(3));
                result.add(map);
            }
            Thread.sleep(1000);
            return result;
        }
        Map param = new HashMap();
        param.put("token", token);
        String response = doGet(getCURRENT_RESERVE_URL(), param);
        System.out.println("!!!!!!!!!!!!!!!!! get current reserve:" + token + " " + response);
        return gson.fromJson(response, ArrayList.class);
    }

    private List<Map> getOldReserve(String token) throws IOException, InterruptedException {
        if(GlobalConstant.IS_DEBUG){
            List<Map> result = new ArrayList<>();
            for(int i = 0; i < 10; i++){
                Map map = new HashMap();
                map.put("time", new Double(System.currentTimeMillis() + 360000));
                map.put("park", "001");
                map.put("id", "1");
                map.put("fee", new Double(3));
                result.add(map);
            }
            Thread.sleep(1000);
            return result;
        }
        Map param = new HashMap();
        param.put("token", token);
        String response = doGet(getOLD_RESERVE_URL(), param);
        System.out.println("!!!!!!!!!!!!!!!!!!! get old response " + response);
        return gson.fromJson(response, ArrayList.class);
    }

    private Map newReserve(Long time, String park, String token, Integer fee) throws IOException {
        Map param = new HashMap();
        param.put("time", time);
        param.put("parks", park);
        param.put("token", token);
        param.put("fee", fee);

        if(GlobalConstant.IS_DEBUG){
            Map result = new HashMap();
            result.put("status", "0");
            return result;
        }

        String response = doPost(getNEW_RESERVE_URL(), param);
        System.out.println("!!!!!!!!!!!!!!!! new reserve response:" + response);
        return gson.fromJson(response, HashMap.class);
    }

    private Map delayReserve(Long time, String reserveId, Integer plusFee) throws IOException {
        Map param = new HashMap();
        param.put(RESERVE_ID, reserveId);
        param.put(TIME, time);
        param.put(RESERVE_FEE, plusFee);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!delay param:" + param);

        if(GlobalConstant.IS_DEBUG){
            Map result = new HashMap();
            result.put("status", "0");
            result.put("msg", "");
            return result;
        }

        String response = doGet(getDELAY_RESERVE_URL(), param);
        System.out.println("!!!!!!!!!!!!!!!! delay reserve response:" + response);
        return gson.fromJson(response, HashMap.class);
    }

    private Map calcelReserve(String id, String token) throws IOException {
        Map param = new HashMap();
        param.put(RESERVE_ID, id);
        param.put("token", token);

        if(GlobalConstant.IS_DEBUG){
            Map result = new HashMap();
            result.put("status", "0");
            return result;
        }

        String response = doGet(getCANCEL_RESERVE_URL(), param);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!! cancel response" + response);
        return gson.fromJson(response, HashMap.class);
    }

    public String getCalcelUrl(String id, String token) {
        String url = String.format("%s?id=%s&token=&s", getCANCEL_RESERVE_URL(), id, token);
        return url;
    }

    public Observable<Map> obCancelReserve(final String id, final String token){
        return Observable.just("").
                map(new Func1<String, Map>() {
                    @Override
                    public Map call(String s) {
                        try {
                            return calcelReserve(id, token);
                        } catch (Exception e) {
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Map> obDelayReserve(final Long time, final String reserveId, final Integer plusFee){
        return Observable.just("").
                map(new Func1<String, Map>() {
                    @Override
                    public Map call(String s) {
                        try {
                            return delayReserve(time, reserveId, plusFee);
                        } catch (Exception e) {
                            throw Exceptions.propagate(e);
                        }
                    }
                }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
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
