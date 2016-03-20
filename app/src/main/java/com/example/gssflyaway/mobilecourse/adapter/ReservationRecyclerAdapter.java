package com.example.gssflyaway.mobilecourse.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.model.Reservation;

import java.util.List;

/**
 * Created by ruluo1992 on 3/20/2016.
 */
public class ReservationRecyclerAdapter extends RecyclerView.Adapter<ReservationRecyclerAdapter.MyHolder> {

    private List<Reservation> data;
    private int length;

    public ReservationRecyclerAdapter(List<Reservation> data, int length) {
        this.data = data;
        this.length = length;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SwipeLayout swipeLayout = (SwipeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper));
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!! into create view holder");
        return new MyHolder(swipeLayout);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return length;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public MyHolder(View itemView) {
            super(itemView);
        }
    }
}
