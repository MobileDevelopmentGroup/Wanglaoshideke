package com.example.gssflyaway.mobilecourse.fragment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.adapter.ReservationRecyclerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ruluo1992 on 3/18/2016.
 */
public class ReservationFragment extends Fragment {

    @Bind(R.id.current_list)
    public RecyclerView currentList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_reservation, container, false);
        ButterKnife.bind(this, mView);

        setupRecyclerList();
        return mView;
    }

    private void setupRecyclerList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        currentList.setLayoutManager(linearLayoutManager);
        ReservationRecyclerAdapter adapter = new ReservationRecyclerAdapter(null, 10);
        currentList.setAdapter(adapter);
        DividerLine dividerLine = new DividerLine(DividerLine.VERTICAL);
        dividerLine.setSize(1);
        dividerLine.setColor(0xFFDDDDDD);
        currentList.addItemDecoration(dividerLine);
    }
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
