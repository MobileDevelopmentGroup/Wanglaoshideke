package com.example.gssflyaway.mobilecourse.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gssflyaway.mobilecourse.GlobalConstant;
import com.example.gssflyaway.mobilecourse.R;
import com.example.gssflyaway.mobilecourse.model.BaseModel;

/**
 * Created by ruluo1992 on 4/16/2016.
 */
public class SettingFragment extends Fragment {
    EditText hostText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_setting, container, false);
        hostText = (EditText) mView.findViewById(R.id.host);
        Button button = (Button) mView.findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hostIp = hostText.getText().toString().trim();
                if(hostIp.equals("")) {
                    GlobalConstant.IS_DEBUG = true;
                } else {
                    GlobalConstant.IS_DEBUG = false;
                    BaseModel.HOST = String.format("http://%s:8080", hostIp);
                }
            }
        });
        return mView;
    }
}
