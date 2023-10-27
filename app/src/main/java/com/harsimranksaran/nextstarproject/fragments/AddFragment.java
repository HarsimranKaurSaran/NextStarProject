package com.harsimranksaran.nextstarproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.harsimranksaran.nextstarproject.R;
import com.harsimranksaran.nextstarproject.UploadImg;
import com.harsimranksaran.nextstarproject.UploadVid;

public class AddFragment extends Fragment{

    Button pickimg, pickvid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.activity_upload, container,false);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pickimg = view.findViewById(R.id._image);
        pickvid = view.findViewById(R.id._video);

        pickimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        pickvid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickVideo();
            }
        });
    }

    public void pickImage(){
        Intent intent = new Intent(getActivity(), UploadImg.class);
        startActivity(intent);
    }

    public void pickVideo(){
        Intent intent = new Intent(getActivity(), UploadVid.class);
        startActivity(intent);
    }
}
