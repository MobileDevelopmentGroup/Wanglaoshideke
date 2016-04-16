package com.example.gssflyaway.mobilecourse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.gssflyaway.mobilecourse.GlobalConstant;

import java.util.Map;

/**
 * Created by ruluo1992 on 3/20/2016.
 */
public class Reservation implements Parcelable{
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(company);
        dest.writeString(companyIcon);
        dest.writeString(mark);
        dest.writeLong(time);
        dest.writeInt(fee);
    }

    public static final Parcelable.Creator<Reservation> CREATOR = new Parcelable.Creator<Reservation>()
    {
        public Reservation createFromParcel(Parcel in)
        {
            return new Reservation(in);//创建一个有参构造函数
        }

        public Reservation[] newArray(int size)
        {
            return new Reservation[size];
        }
    };

    public Reservation(Parcel in)//根据写入的顺序依次读取
    {
        id = in.readString();
        company = in.readString();
        companyIcon = in.readString();
        mark = in.readString();
        time = in.readLong();
        fee = in.readInt();
    }
}
