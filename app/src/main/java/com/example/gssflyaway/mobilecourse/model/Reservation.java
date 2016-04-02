package com.example.gssflyaway.mobilecourse.model;

import com.example.gssflyaway.mobilecourse.GlobalConstant;

import java.util.Map;

/**
 * Created by ruluo1992 on 3/20/2016.
 */
public class Reservation {
    public String id;
    public String company;
    public String companyIcon;
    public String mark;
    public Long time;
    public Integer fee;

    public Reservation(String company, String companyIcon, Integer fee, String id, String mark, Long time) {
        this.company = company;
        this.companyIcon = companyIcon;
        this.fee = fee;
        this.id = id;
        this.mark = mark;
        this.time = time;
    }

    public static Reservation fromMap(Map map){
        Long time = ((Double)map.get(ReserveModel.TIME)).longValue();
        String park = map.get(ReserveModel.PARK).toString();
        String id = map.get(ReserveModel.RESERVE_ID).toString();
        Integer fee = ((Double) map.get(ReserveModel.RESERVE_FEE)).intValue();
        return new Reservation(GlobalConstant.PARK_NAME, "", fee, id, park, time);
    }
}
