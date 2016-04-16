package com.example.gssflyaway.mobilecourse.activity;

import android.app.Activity;
import android.os.Bundle;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.example.gssflyaway.mobilecourse.R;

/**
 * Created by ruluo1992 on 4/15/2016.
 */
public class AMapActivity extends AMapBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basic_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setSettingMenuEnabled(true);
        options.setTilt(0);
        mAMapNaviView.setViewOptions(options);

//        mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode);
    }
}
