package com.example.gssflyaway.mobilecourse.model;

/**
 * Created by ruluo1992 on 3/20/2016.
 */
public class Reservation {
    public String id;
    public String company;
    public String companyIcon;
    public String mark;
    public Long time;
    public Double fee;

    public Reservation(String company, String companyIcon, Double fee, String id, String mark, Long time) {
        this.company = company;
        this.companyIcon = companyIcon;
        this.fee = fee;
        this.id = id;
        this.mark = mark;
        this.time = time;
    }
}
