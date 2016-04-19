package com.example.gssflyaway.mobilecourse.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.gssflyaway.mobilecourse.GlobalConstant;
import com.example.gssflyaway.mobilecourse.MyEvent;
import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.model.Reservation;
import com.example.gssflyaway.mobilecourse.model.ReserveModel;
import com.example.gssflyaway.mobilecourse.model.UserModel;
import com.github.yoojia.zxing.qrcode.Encoder;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by ruluo1992 on 3/21/2016.
 */
public class ReservationDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.company_icon)
    ImageView companyIcon;

    @Bind(R.id.company_name)
    TextView companyName;

    @Bind(R.id.reserve_info)
    TextView reserveInfo; //预留到17:55  共2个车位

    @Bind(R.id.park_ids)
    TextView parkIds;  // 车位号   448、332

    @Bind(R.id.reserve_time)
    TextView reserveTime; // 预留到   17：55

    @Bind(R.id.qrcode)
    ImageView qrCode;

    @Bind(R.id.reserve_detail)
    View reserveDetail;

    @Bind(R.id.btn_delay)
    Button btnDelay;

    private Reservation reservation;
    private Boolean isRunning = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);
        ButterKnife.bind(this);
        setupToolbar();

        Intent intent = getIntent();
        reservation = intent.getParcelableExtra("reservation");
        setData(reservation);
        setupButtons();

        isRunning = true;
        JudgeAvaliableTask task = new JudgeAvaliableTask(this);
        task.execute(reservation.id);
    }

    private void setupButtons() {
        btnDelay.setOnClickListener(this);
    }

    private void setData(Reservation reservation){
        companyName.setText(reservation.company);
        Date date = new Date(reservation.time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String info = String.format("预留到%s   共1个车位", format.format(date));
        reserveInfo.setText(info);
        String parks = String.format("车位号   %s", reservation.mark);
        parkIds.setText(parks);
        reserveTime.setText(String.format("预留到   %s", format.format(date)));

        qrCode.setImageBitmap(generateQRCode(
                ReserveModel.getInstance().getCheckinUrl(
                        reservation.id,
                        UserModel.getInstance().getToken(getApplicationContext())
                )
        ));
    }

    private Bitmap generateQRCode(String content) {
        final int dimension = 500;
//        com.github.yoojia.zxing.qrcode.Encoder encoder = Encoder.Builder();
        final Encoder encoder = new Encoder.Builder()
                .setBackgroundColor(0xFFFFFF) // 指定背景颜色，默认为白色
                .setCodeColor(0xFF000000) // 指定编码块颜色，默认为黑色
                .setOutputBitmapWidth(dimension) // 生成图片宽度
                .setOutputBitmapHeight(dimension) // 生成图片高度
                .setOutputBitmapPadding(0) // 设置为没有白边
                .build();

        Bitmap _QRCodeImage = encoder.encode(content);
        return _QRCodeImage;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.menu_cancel) {
            showCalcelConfirm();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reservation_detail, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        isRunning = false;
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void showDelayConfirm(final int plusFee, final Long newTime, final String id){
        new MaterialDialog.Builder(reserveInfo.getContext())
                .title("提示")
                .content("额外付费：" + plusFee + "元")
                .positiveText("确认")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ReserveModel.getInstance().obDelayReserve(newTime, id, plusFee).subscribe(new Action1<Map>() {
                            @Override
                            public void call(Map map) {
                                Snackbar.make(reserveDetail, "延期成功！", Snackbar.LENGTH_SHORT).show();
                                reservation.time = newTime;
                                setData(reservation);
                            }
                        });
                    }
                })
                .show();
    }

    private void showCalcelConfirm(){
        new MaterialDialog.Builder(reserveDetail.getContext())
                .title("确认")
                .content("确认取消？")
                .positiveText("确认")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String token = UserModel.getInstance().getToken(reserveDetail.getContext());
                        ReserveModel.getInstance().obCancelReserve(reservation.id, token)
                                .subscribe(new Subscriber<Map>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Map map) {
                                        Snackbar.make(reserveDetail, "取消成功！", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_delay) {
            final Calendar c = Calendar.getInstance();
            new TimePickerDialog(reserveDetail.getContext(),
                    // 绑定监听器
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view,
                                              int hourOfDay, int minute) {
                            if(view.isShown()) {
                                view.clearFocus();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                final Date date = c.getTime();
                                if (date.getTime() < reservation.time) {
                                    Snackbar.make(reserveDetail, "无效的时间", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    int diff = (int) (date.getTime() - reservation.time);
                                    int hour = diff / (60 * 60 * 1000) + 1;
                                    int plusFee = hour * GlobalConstant.TIME_COST;
                                    showDelayConfirm(plusFee, date.getTime(), reservation.id);
                                }
                            }
                        }
                    }
                    // 设置初始时间
                    , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                    // true表示采用24小时制
                    true).show();
        }
    }

    static class JudgeAvaliableTask extends AsyncTask<String, Void, Boolean>{
        private WeakReference<ReservationDetailActivity> activityWeakReference;

        public JudgeAvaliableTask(ReservationDetailActivity activity){
            activityWeakReference = new WeakReference<ReservationDetailActivity>(activity);
        }

        @Override
        protected void onPostExecute(Boolean avaliable) {
            if (!avaliable && activityWeakReference.get() != null) {
                MyEvent event = new MyEvent();
                event.type = MyEvent.Type.REFRESH_RESERVATION_AND_SHOWERR;
                EventBus.getDefault().post(event);
                activityWeakReference.get().finish();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            int count = 0;
            String token = UserModel.getInstance().getToken(activityWeakReference.get().getApplicationContext());
            while(activityWeakReference.get() != null &&
                    activityWeakReference.get().isRunning == true){
                try {
                    Boolean avaliable = false;
                    if(GlobalConstant.IS_DEBUG) {
                        avaliable = count > 10 ? false : true;
                    } else {
                        avaliable = ReserveModel.getInstance().isAvaliable(params[0], token);
                    }
                    if(!avaliable){
                        return false;
                    }
                    Thread.sleep(500);
                    count++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }
}
