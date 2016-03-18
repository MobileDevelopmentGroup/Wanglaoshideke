package com.example.gssflyaway.mobilecourse.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.adapter.ReservationViewpagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ruluo1992 on 3/18/2016.
 */
public class ReservationFragment extends Fragment {
    @Bind(R.id.viewpager)
    public ViewPager viewPager;
    @Bind(R.id.tabs)
    public TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_reservation, container, false);
        ButterKnife.bind(this, mView);

        setupViewpagerAndTabs();
        return mView;
    }

    private void setupViewpagerAndTabs(){
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CurrentReservationFragment());
        fragments.add(new OldReservationFragment());

        List<String> titles = new ArrayList<>();
        titles.add("当前预约");
        titles.add("历史预约");

        ReservationViewpagerAdapter adapter = new ReservationViewpagerAdapter(
                getFragmentManager(),
                fragments,
                titles);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }
}
