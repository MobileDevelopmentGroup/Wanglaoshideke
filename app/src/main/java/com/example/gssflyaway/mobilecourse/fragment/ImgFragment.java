package com.example.gssflyaway.mobilecourse.fragment;

import android.graphics.ImageFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.gssflyaway.mobilecourse.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URL;

/**
 * Created by ruluo1992 on 3/17/2016.
 */
public class ImgFragment extends Fragment {
    private static String URL = "URL";

    public static ImgFragment newInstance(String imgUrl){
        ImgFragment fragment = new ImgFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(URL, imgUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView imageView = (ImageView) inflater.inflate(R.layout.fragment_img, container, false);
        String imgUrl = getArguments().getString(URL);

        ImageLoader.getInstance().displayImage(imgUrl, imageView);
        return imageView;
    }
}
