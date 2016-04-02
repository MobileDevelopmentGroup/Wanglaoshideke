package com.example.gssflyaway.mobilecourse.adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.activity.ReservationDetailActivity;
import com.example.gssflyaway.mobilecourse.model.Reservation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        }
        else if(holder instanceof OldItemHolder){
            OldItemHolder oldItemHolder = (OldItemHolder) holder;
            Reservation reservation = oldData.get(position - currentLen - 2);
            oldItemHolder.markCompanyTv.setText(reservation.company + " " + reservation.mark);
            oldItemHolder.timeTv.setText(formateTime(reservation.time));
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
        if(currentLen == 0)
            return 0;
        if(oldLen == 0)
            return currentLen + 1;
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
        public CurrentItemHolder(final View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView;
            timeTv = (TextView) itemView.findViewById(R.id.time);
            markCompanyTv = (TextView) itemView.findViewById(R.id.mark_company);
            surfaceView = itemView.findViewById(R.id.surface);

            timeTv.setTextColor(Color.parseColor("#000000"));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ReservationDetailActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            };
            timeTv.setOnClickListener(listener);
            markCompanyTv.setOnClickListener(listener);
        }
    }

    class OldItemHolder extends RecyclerView.ViewHolder{
        public TextView timeTv;
        public TextView markCompanyTv;
        public OldItemHolder(View itemView) {
            super(itemView);
            timeTv = (TextView) itemView.findViewById(R.id.time);
            markCompanyTv = (TextView) itemView.findViewById(R.id.mark_company);
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
