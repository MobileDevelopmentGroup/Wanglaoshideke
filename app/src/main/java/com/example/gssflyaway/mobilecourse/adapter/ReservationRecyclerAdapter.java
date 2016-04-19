package com.example.gssflyaway.mobilecourse.adapter;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.example.gssflyaway.mobilecourse.GlobalConstant;
import com.example.gssflyaway.mobilecourse.MyEvent;
import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.activity.ReservationDetailActivity;
import com.example.gssflyaway.mobilecourse.model.Reservation;
import com.example.gssflyaway.mobilecourse.model.ReservationModel;
import com.example.gssflyaway.mobilecourse.model.ReserveModel;
import com.example.gssflyaway.mobilecourse.model.UserModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by ruluo1992 on 3/20/2016.
 */
public class ReservationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Reservation> currentData;
    private List<Reservation> oldData;

    private int currentLen;
    private int oldLen;

    public enum HEADER_TYPE{
        CURRENT_HEADER, CURRENT_ITEM, OLD_HEADER, OLD_ITEM;
    }

    public ReservationRecyclerAdapter(List<Reservation> currentData, List<Reservation> oldData) {
        this.currentData = currentData;
        this.oldData = oldData;
        currentLen = currentData.size();
        oldLen = oldData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HEADER_TYPE.CURRENT_HEADER.ordinal()){
            TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation_header, parent, false);
            return new HeaderHolder(textView);
        }
        if(viewType == HEADER_TYPE.CURRENT_ITEM.ordinal()){
            SwipeLayout swipeLayout = (SwipeLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation, parent, false);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper));
            return new CurrentItemHolder(swipeLayout);
        }
        if(viewType == HEADER_TYPE.OLD_HEADER.ordinal()){
            TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation_header, parent, false);
            return new HeaderHolder(textView);
        }
        if(viewType == HEADER_TYPE.OLD_ITEM.ordinal()){
            SwipeLayout swipeLayout = (SwipeLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation, parent, false);
            swipeLayout.setSwipeEnabled(false);
            return new OldItemHolder(swipeLayout);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder){
            HeaderHolder headerHolder = (HeaderHolder) holder;
            if(position == 0)
                headerHolder.textView.setText("当前预约");
            else
                headerHolder.textView.setText("历史预约");
        }
        else if(holder instanceof CurrentItemHolder){
            CurrentItemHolder currentItemHolder = (CurrentItemHolder) holder;
            Reservation reservation = currentData.get(position - 1);
            currentItemHolder.markCompanyTv.setText(reservation.company + " " + reservation.mark);
            currentItemHolder.timeTv.setText(formateTime(reservation.time));
            currentItemHolder.reservation = reservation;
            ImageLoader.getInstance().displayImage("http://pic.xoyo.com/bbs/2011/05/05/1105052118830c2d625c0efe2e.jpg", currentItemHolder.companyIcon);
        }
        else if(holder instanceof OldItemHolder){
            OldItemHolder oldItemHolder = (OldItemHolder) holder;
            Reservation reservation = oldData.get(position - currentLen - 2);
            oldItemHolder.markCompanyTv.setText(reservation.company + " " + reservation.mark);
            oldItemHolder.timeTv.setText(formateTime(reservation.time));
            ImageLoader.getInstance().displayImage("http://pic.xoyo.com/bbs/2011/05/05/1105052118830c2d625c0efe2e.jpg", oldItemHolder.companyIcon);
        }
    }

    private String formateTime(long time){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 a HH:mm");
        String str = format.format(date);
        return str;
    }

    @Override
    public int getItemCount() {
//        if(currentLen == 0)
//            return 0;
//        if(oldLen == 0)
//            return currentLen + 1;
        return currentLen + oldLen + 2;
    }

    public void setCurrentData(List<Reservation> data){
        this.currentData = data;
        this.currentLen = currentData.size();
        notifyDataSetChanged();
    }

    public void setOldData(List<Reservation> data){
        this.oldData = data;
        this.oldLen = data.size();
        notifyDataSetChanged();
    }

    class HeaderHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public HeaderHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    class CurrentItemHolder extends RecyclerView.ViewHolder{
        public TextView timeTv;
        public TextView markCompanyTv;
        public View surfaceView;
        public SwipeLayout swipeLayout;
        public Button btnDelay;
        public Button btnCancel;
        public Reservation reservation;
        public CircleImageView companyIcon;
        public CurrentItemHolder(final View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView;
            timeTv = (TextView) itemView.findViewById(R.id.time);
            markCompanyTv = (TextView) itemView.findViewById(R.id.mark_company);
            surfaceView = itemView.findViewById(R.id.surface);
            btnDelay = (Button) swipeLayout.findViewById(R.id.btn_delay);
            btnCancel = (Button) swipeLayout.findViewById(R.id.btn_cancel);
            companyIcon = (CircleImageView) swipeLayout.findViewById(R.id.company_icon);
            timeTv.setTextColor(Color.parseColor("#000000"));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ReservationDetailActivity.class);
                    intent.putExtra("reservation", reservation);
                    itemView.getContext().startActivity(intent);
                }
            };

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeLayout.close();
                    //TODO 更新UI
                    showCalcelConfirm();
                }
            });
            btnDelay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeLayout.close();
                    final Calendar c = Calendar.getInstance();
                    new TimePickerDialog(swipeLayout.getContext(),
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
                                            Snackbar.make(swipeLayout, "无效的时间", Snackbar.LENGTH_SHORT).show();
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
            });
            timeTv.setOnClickListener(listener);
            markCompanyTv.setOnClickListener(listener);
        }

        public void showDelayConfirm(final int plusFee, final Long newTime, final String id){
            new MaterialDialog.Builder(swipeLayout.getContext())
                    .title("提示")
                    .content("额外付费：" + plusFee + "元")
                    .positiveText("确认")
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            ReserveModel.getInstance().obDelayReserve(newTime, id, plusFee).subscribe(new Subscriber<Map>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(Map map) {
                                    Snackbar.make(swipeLayout, "延期成功！", Snackbar.LENGTH_SHORT).show();
                                    MyEvent event = new MyEvent();
                                    event.type = MyEvent.Type.REFRESH_RESERVATION;
                                    EventBus.getDefault().post(event);
                                }
                            });
                        }
                    })
                    .show();
        }

        private void showCalcelConfirm(){
            new MaterialDialog.Builder(swipeLayout.getContext())
                    .title("确认")
                    .content("确认取消？")
                    .positiveText("确认")
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String token = UserModel.getInstance().getToken(swipeLayout.getContext());
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
                                            Snackbar.make(swipeLayout, "取消成功！", Snackbar.LENGTH_SHORT).show();
                                            MyEvent event = new MyEvent();
                                            event.type = MyEvent.Type.REFRESH_RESERVATION;
                                            EventBus.getDefault().post(event);
                                        }
                                    });
                        }
                    })
                    .show();
        }
    }

    class OldItemHolder extends RecyclerView.ViewHolder{
        public TextView timeTv;
        public TextView markCompanyTv;
        public CircleImageView companyIcon;
        public OldItemHolder(View itemView) {
            super(itemView);
            timeTv = (TextView) itemView.findViewById(R.id.time);
            markCompanyTv = (TextView) itemView.findViewById(R.id.mark_company);
            companyIcon = (CircleImageView) itemView.findViewById(R.id.company_icon);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return HEADER_TYPE.CURRENT_HEADER.ordinal();
        if(position > 0 && position < currentLen + 1)
            return HEADER_TYPE.CURRENT_ITEM.ordinal();
        if(position == currentLen + 1)
            return HEADER_TYPE.OLD_HEADER.ordinal();
        return HEADER_TYPE.OLD_ITEM.ordinal();
    }
}
