package com.example.gssflyaway.mobilecourse.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.adapter.GalleryViewPagerAdapter;
import com.viewpagerindicator.LinePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ruluo1992 on 3/17/2016.
 */
public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener{
    @Bind(R.id.viewpager)
    public ViewPager viewPager;
    @Bind(R.id.indicator)
    public LinePageIndicator indicator;

    private boolean isScrolling = false;
    private boolean isRunning = false;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 执行滑动到下一个页面
            if(!isScrolling)
                viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % viewPager.getChildCount());
                // 在发一个handler延时
            if(isRunning)
                handler.sendEmptyMessageDelayed(0, 2000);
        };
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        setupViewpager(viewPager, indicator);
        return view;
    }

    private void setupViewpager(ViewPager viewPager, LinePageIndicator indicator){
        List<Fragment> fragments = new ArrayList<>();

        fragments.add(ImgFragment.newInstance("http://www.bit.edu.cn/images/content/2013-12/20131221104632050251.jpg"));
        fragments.add(ImgFragment.newInstance("http://www.bit.edu.cn/images/content/2013-12/20131224132442252113.jpg"));
        fragments.add(ImgFragment.newInstance("http://www.bit.edu.cn/images/content/2013-12/20131221104733262621.jpg"));

        GalleryViewPagerAdapter adapter = new GalleryViewPagerAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(this);

        indicator.setViewPager(viewPager);
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state == 1)
            isScrolling = true;
        else
            isScrolling = false;
    }
}
