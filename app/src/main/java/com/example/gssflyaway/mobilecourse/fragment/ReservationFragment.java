package com.example.gssflyaway.mobilecourse.fragment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gssflyaway.mobilecourse.MyEvent;
import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.adapter.ReservationRecyclerAdapter;
import com.example.gssflyaway.mobilecourse.model.Reservation;
import com.example.gssflyaway.mobilecourse.model.ReserveModel;
import com.example.gssflyaway.mobilecourse.model.UserModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by ruluo1992 on 3/18/2016.
 */
public class ReservationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.current_list)
    public RecyclerView currentList;

    @Bind(R.id.swipe_refresh_layout)
    public SwipeRefreshLayout swipeRefreshLayout;

    private ReservationRecyclerAdapter adapter;

    private volatile boolean isCurrentDone;
    private volatile boolean isOldDone;

    private boolean needRefresh = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_reservation, container, false);
        ButterKnife.bind(this, mView);
        EventBus.getDefault().register(this);

        setupSwipeRefreshLayout();
        setupRecyclerList();
        onRefresh();
        return mView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MyEvent event) {
        if(event.type == MyEvent.Type.REFRESH_RESERVATION){
            onRefresh();
        }
        else if(event.type == MyEvent.Type.REFRESH_RESERVATION_AND_SHOWERR){
            Snackbar.make(swipeRefreshLayout, "订单失效！", Snackbar.LENGTH_SHORT).show();
            onRefresh();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRefresh();
        EventBus.getDefault().unregister(this);
    }

    private void setupSwipeRefreshLayout(){
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setupRecyclerList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        currentList.setLayoutManager(linearLayoutManager);
        adapter = new ReservationRecyclerAdapter(new ArrayList<Reservation>(), new ArrayList<Reservation>());
        currentList.setAdapter(adapter);
        DividerLine dividerLine = new DividerLine(DividerLine.VERTICAL);
        dividerLine.setSize(1);
        dividerLine.setColor(0xFFDDDDDD);
        currentList.addItemDecoration(dividerLine);
    }

    @Override
    public void onRefresh() {
        startRefresh();
        final List<Reservation> current = new ArrayList<>();
        final List<Reservation> old = new ArrayList<>();
        isCurrentDone = false;
        isOldDone = false;
        adapter.setOldData(old);
        adapter.setCurrentData(current);
        ReserveModel.getInstance().obGetCurrentReserve(UserModel.getInstance().getToken(getContext()))
                .subscribe(new Subscriber<List<Map>>() {
                    @Override
                    public void onCompleted() {
                        adapter.setCurrentData(current);
                        isCurrentDone = true;
                        if(isOldDone)
                            stopRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopRefresh();
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!");
                        e.printStackTrace();
                        Snackbar.make(swipeRefreshLayout, "刷新失败", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Map> maps) {
                        for(Map map : maps){
                            current.add(Reservation.fromMap(map));
                        }
                    }
                });
        ReserveModel.getInstance().obGetOldReserve(UserModel.getInstance().getToken(getContext()))
                .subscribe(new Subscriber<List<Map>>() {
                    @Override
                    public void onCompleted() {
                        adapter.setOldData(old);
                        isOldDone = true;
                        if(isCurrentDone)
                            stopRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Map> maps) {
                        for(Map map : maps){
                            old.add(Reservation.fromMap(map));
                        }
                    }
                });
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

    class DividerLine extends RecyclerView.ItemDecoration {
        /**
         * 水平方向
         */
        public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;

        /**
         * 垂直方向
         */
        public static final int VERTICAL = LinearLayoutManager.VERTICAL;

        // 画笔
        private Paint paint;

        // 布局方向
        private int orientation;
        // 分割线颜色
        private int color;
        // 分割线尺寸
        private int size;

        public DividerLine() {
            this(VERTICAL);
        }

        public DividerLine(int orientation) {
            this.orientation = orientation;

            paint = new Paint();
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            if (orientation == VERTICAL) {
                drawHorizontal(c, parent);
            } else {
                drawVertical(c, parent);
            }
        }

        /**
         * 设置分割线颜色
         *
         * @param color 颜色
         */
        public void setColor(int color) {
            this.color = color;
            paint.setColor(color);
        }

        /**
         * 设置分割线尺寸
         *
         * @param size 尺寸
         */
        public void setSize(int size) {
            this.size = size;
        }

        // 绘制垂直分割线
        protected void drawVertical(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + size;

                c.drawRect(left, top, right, bottom, paint);
            }
        }

        // 绘制水平分割线
        protected void drawHorizontal(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + size;

                c.drawRect(left, top, right, bottom, paint);
            }
        }
    }
}


