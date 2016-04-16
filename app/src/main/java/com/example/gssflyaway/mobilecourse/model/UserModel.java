package com.example.gssflyaway.mobilecourse.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.gssflyaway.mobilecourse.GlobalConstant;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

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
public class UserModel extends BaseModel{
    private final String LOGIN_URL = HOST + "/m/login";
    private final String REGISTER_URL = HOST + "/m/register";
    private final String USERINFO_URL = HOST + "/m/personInfo/userInfo";

    public static final String STATUS = "status";
    public static final String MESSAGE = "msg";

    public static final String USERNAME = "username";
    public static final String PHONE = "phone";
    public static final String GENDER = "gender";
    public static final String AVATAR = "avatar";
    public static final String PASSWORD = "passwd";

    private static final String TOKEN = "TOKEN";

    private static UserModel model = new UserModel();
    public static UserModel getInstance(){
        return model;
    }

    private Gson gson = new Gson();

    // passwd 明文密码
    public Map login(String username, String passwd) throws IOException {
        String md5pwd = "";
        try {
            md5pwd = new String(Hex.encodeHex(DigestUtils.md5(passwd)));
        } catch (Exception e){
            e.printStackTrace();
        }
        Map param = new HashMap();
        param.put(USERNAME, username);
        param.put(PASSWORD, md5pwd);

        if (!GlobalConstant.IS_DEBUG) {
            String response = doPost(LOGIN_URL, param);
            System.out.println("!!!!!!!!!!!!!! login response:" + response);
            return gson.fromJson(response, HashMap.class);
        }
        Map result = new HashMap();
        result.put(STATUS, "0");
        result.put(MESSAGE, "14594071369770000");
        return result;
    }

    private Map register(String username, String passwd, String phone) throws IOException {
        String md5pwd = DigestUtils.md2Hex(passwd);
        Map param = new HashMap();
        param.put(USERNAME, username);
        param.put(PASSWORD, md5pwd);
        param.put(PHONE, phone);

        String response = doPost(REGISTER_URL, param);
        return gson.fromJson(response, HashMap.class);
    }

    private Map getUserInfo(String token) throws IOException {
        Map param = new HashMap();
        param.put("token", token);
        String response = doGet(USERINFO_URL, param);
        Map result = gson.fromJson(response, HashMap.class);
        if (GlobalConstant.IS_DEBUG) {
            result.put(AVATAR, "http://pic.xoyo.com/bbs/2011/05/05/1105052118830c2d625c0efe2e.jpg");
        }
        return result;
    }


    public Observable<Map> obLogin(String username, String passwd){
        return Observable.just(new String[]{username, passwd})
                .map(new Func1<String[], Map>() {
                    @Override
                    public Map call(String[] strings) {
                        try{
                            return login(strings[0], strings[1]);
                        }catch (Exception e){
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Map> obRegister(String username, String passwd, String phone){
        return Observable.just(new String[]{username, passwd, phone})
                .map(new Func1<String[], Map>() {
                    @Override
                    public Map call(String[] strings) {
                        try{
                            return register(strings[0], strings[1], strings[2]);
                        }catch (Exception e){
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Map> obGetUserInfo(String token){
        return Observable.just(token)
                .map(new Func1<String, Map>() {
                    @Override
                    public Map call(String s) {
                        try{
                            return getUserInfo(s);
                        }catch (Exception e){
                            throw Exceptions.propagate(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public String getToken(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString(TOKEN, "");
        return token;
    }

    public void saveToken(Context context, String token){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public boolean isLogin(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString(TOKEN, "");
        if(token.equals(""))
            return true;
        else
            return true;
    }

    public void clearToken(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN, "");
        editor.commit();
    }
}
