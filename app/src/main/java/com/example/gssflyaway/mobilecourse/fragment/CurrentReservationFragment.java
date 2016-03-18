package com.example.gssflyaway.mobilecourse.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gssflyaway.mobilecourse.R;

/**
 * Created by ruluo1992 on 3/18/2016.
 */
public class CurrentReservationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_current, container, false);
        return mView;
    }
}
