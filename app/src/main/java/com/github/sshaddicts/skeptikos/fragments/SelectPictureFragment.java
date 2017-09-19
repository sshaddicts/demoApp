package com.github.sshaddicts.skeptikos.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.sshaddicts.skeptikos.R;

public class SelectPictureFragment extends Fragment {

    private static final String USERNAME_ARG = "username";
    private String username;

    private PictureSelectedListener mListener;

    public SelectPictureFragment() {}

    public static SelectPictureFragment newInstance(String username) {
        SelectPictureFragment fragment = new SelectPictureFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME_ARG, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.username = getArguments().getString(USERNAME_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button button = (Button) view.findViewById(R.id.selectImageCapture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onPictureSelection();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PictureSelectedListener) {
            mListener = (PictureSelectedListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement PictureSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface PictureSelectedListener {
        // TODO: Update argument type and name
        void onPictureSelection();
    }
}
