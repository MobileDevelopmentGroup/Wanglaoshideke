package com.example.gssflyaway.mobilecourse.activity;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gssflyaway.mobilecourse.GlobalConstant;
import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.model.ParkModel;
import com.example.gssflyaway.mobilecourse.model.ReserveModel;
import com.example.gssflyaway.mobilecourse.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by ruluo1992 on 3/21/2016.
 */
public class ReservationActivity extends AppCompatActivity{

    @Bind(R.id.company_name)
    TextView nameView;

    @Bind(R.id.left_num)
    TextView leftNumView;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.container)
    View container;

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

    private boolean isRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        ButterKnife.bind(this);
        isRunning = true;

        setupToolbar();
        setupSwipeRefreshLayout();

        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.time_view)
    public void chooseTime(){
        final Calendar c = Calendar.getInstance();
                // 创建一个TimePickerDialog实例，并把它显示出来
        new TimePickerDialog(this,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        Date date = c.getTime();
                        if(date.getTime() < System.currentTimeMillis()){
                            Snackbar.make(swipeRefreshLayout, "无效的时间", Snackbar.LENGTH_SHORT).show();
                        }
                        else
                            setTime(date.getTime());
                    }
                }
                // 设置初始时间
                , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                // true表示采用24小时制
                true).show();
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
                            stopRefresh();
                        }

                        @Override
                        public void onError(Throwable e) {
                            if(!isRunning)
                                return;
                            Snackbar.make(swipeRefreshLayout, "预约失败！", Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(Map map) {
                            if(!isRunning)
                                return;
                            Snackbar.make(swipeRefreshLayout, "预约成功！", Snackbar.LENGTH_SHORT).show();
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
        isRunning = false;
    }

    private void setupSwipeRefreshLayout(){
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
//        swipeRefreshLayout.setOnRefreshListener(this);
    }

//    @Override
    public void refresh() {
        container.setVisibility(View.GONE);
        startRefresh();
        ParkModel.getInstance().obGetParkInfo().subscribe(new Subscriber<Map>() {
            @Override
            public void onCompleted() {
                stopRefresh();
            }

            @Override
            public void onError(Throwable e) {
                if(!isRunning)
                    return;
                Snackbar.make(swipeRefreshLayout, "刷新失败！", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Map map) {
                if(!isRunning)
                    return;
                container.setVisibility(View.VISIBLE);
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
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String str = format.format(date);
        timeView.setText(str);
        long disM = time - System.currentTimeMillis();
        int disH = 0;
        if(disM % 3600000 == 0)
            disH = (int) (disM / 3600000);
        else
            disH = (int) (disM / 3600000 + 1);
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
