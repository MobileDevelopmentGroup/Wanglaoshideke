package com.example.gssflyaway.mobilecourse.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.activity.ButtonPopupWindowActivity;


public class PersonInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_person_info, container, false);

        RelativeLayout protraitRl = (RelativeLayout) mView.findViewById(R.id.potraitPart);
        protraitRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getActivity(), ButtonPopupWindowActivity.class));
            }
        });

        RelativeLayout personidRl = (RelativeLayout)mView.findViewById(R.id.personid1);
        personidRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog rl1Dialog = new AlertDialog.Builder(getActivity()).setTitle("修改昵称").
                        setView(new EditText(getActivity())).setPositiveButton("确定", null).
                        setNegativeButton("取消", null).show();
            }
        });

        RelativeLayout extendRl2 = (RelativeLayout)mView.findViewById(R.id.expandedPart1);
        extendRl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog rl1Dialog = new AlertDialog.Builder(getActivity()).setTitle("修改手机号").
                        setView(new EditText(getActivity())).setPositiveButton("确定", null).
                        setNegativeButton("取消", null).show();
            }
        });


        return mView;
    }




}
