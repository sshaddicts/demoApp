package com.github.sshaddicts.skeptikos.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sshaddicts.skeptikos.R;

import java.util.List;

public class InfoDisplayFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private InfoDisplayFragmentListener mListener;

    public InfoDisplayFragment() {
    }

    //list of pairs is easily transformed to bundle, will fix later
    public static InfoDisplayFragment newInstance(List<Pair<String, Double>> data) {
        InfoDisplayFragment fragment = new InfoDisplayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_display, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSave();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof InfoDisplayFragmentListener) {
            mListener = (InfoDisplayFragmentListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement PrePostFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface InfoDisplayFragmentListener {
        // TODO: Update argument type and name
        void onSave();
    }
}
