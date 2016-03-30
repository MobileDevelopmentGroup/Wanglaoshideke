package com.example.gssflyaway.mobilecourse.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gssflyaway.mobilecourse.GlobalConstant;
import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.model.ParkModel;
import com.example.gssflyaway.mobilecourse.model.ReserveModel;
import com.example.gssflyaway.mobilecourse.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by ruluo1992 on 3/21/2016.
 */
public class ReservationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.company_name)
    TextView nameView;

    @Bind(R.id.left_num)
    TextView leftNumView;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.time_view)
    TextView timeView;

    @Bind(R.id.time_cost)
    TextView timeCost;

    @Bind(R.id.pipei)
    TextView pipei;

    @Bind(R.id.fee)
    TextView feeView;

    @Bind(R.id.confirm)
    Button btnConfirm;

    @Bind(R.id.btn_choose)
    Button btnChoose;

    private long reserveTime;
    private int reserveFee;
    private String reservePark;
    private String[] avaliableParks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        ButterKnife.bind(this);

        setupToolbar();
        setupSwipeRefreshLayout();

        onRefresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_choose)
    public void choosePark(){
        new MaterialDialog.Builder(this)
                .title("自选停车位")
                .items(this.avaliableParks)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        setPipei(text.toString());
                    }
                })
                .show();
    }

    @OnClick(R.id.confirm)
    public void sendNewReserve(){
        if(UserModel.getInstance().isLogin(this)) {
            String token = UserModel.getInstance().getToken(this);
            ReserveModel.getInstance().obNewReserve(this.reserveTime, this.reservePark, token, this.reserveFee)
                    .subscribe(new Subscriber<Map>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Snackbar.make(swipeRefreshLayout, "预约失败！", Snackbar.LENGTH_SHORT);
                        }

                        @Override
                        public void onNext(Map map) {
                            String status = map.get(UserModel.STATUS).toString();
                            if("0".equals(status))
                                Snackbar.make(swipeRefreshLayout, "预约成功！", Snackbar.LENGTH_SHORT);
                            else
                                Snackbar.make(swipeRefreshLayout, "预约失败！", Snackbar.LENGTH_SHORT);
                        }
                    });
        }

    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void setupSwipeRefreshLayout(){
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        startRefresh();
        ParkModel.getInstance().obGetParkInfo().subscribe(new Subscriber<Map>() {
            @Override
            public void onCompleted() {
                stopRefresh();
            }

            @Override
            public void onError(Throwable e) {
                Snackbar.make(swipeRefreshLayout, "刷新失败！", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Map map) {
                String name = map.get(ParkModel.PARK_NAME).toString();
                String parks = map.get(ParkModel.PARKS).toString();
                String[] parkArr = parks.split(",");
                setAvaliableParks(parkArr);
                setCompanyName(name);
                setLeftNum(parkArr.length);
                if(parkArr.length != 0)
                    setPipei(parkArr[0]);
                else
                    setPipei(null);
                setTimeCost(GlobalConstant.TIME_COST);
                long defaultTime = System.currentTimeMillis() + (1 * 60 * 60 * 1000);
                setTime(defaultTime);
            }
        });
    }

    public void setCompanyName(String name){
        nameView.setText(name);
    }

    public void setLeftNum(int num){
        leftNumView.setText("剩余车位数：" + num);
    }

    public void setTimeCost(int cost){
        timeCost.setText(cost + " 元/时");
    }

    public void setTime(long time){
        this.reserveTime = time;
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        String str = format.format(date);
        timeView.setText(str);
        long disM = time - System.currentTimeMillis();
        int disH = 0;
        if(disM % 3600 == 0)
            disH = (int) (disM / 3600);
        else
            disH = (int) (disM / 3600 + 1);
        setCost(disH * GlobalConstant.TIME_COST);
    }

    public void setCost(int cost){
        this.reserveFee = cost;
        feeView.setText("预计费用：" + cost + "元");
    }

    public void setPipei(String park){
        if(park == null) {
            pipei.setText("无可用车位");
            btnConfirm.setEnabled(false);
        }
        else {
            pipei.setText("已匹配：" + park);
            btnConfirm.setEnabled(true);
            this.reservePark = park;
        }
    }

    public void setAvaliableParks(String[] parks){
        this.avaliableParks = parks;
    }

    private void startRefresh() {
        if(!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }

    private void stopRefresh(){
        swipeRefreshLayout.setRefreshing(false);
    }
}
