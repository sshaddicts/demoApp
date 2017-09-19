package com.github.sshaddicts.skeptikos.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.sshaddicts.skeptikos.R;


public class PrePostFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private PrePostFragmentListener mListener;

    private byte[] image;

    public PrePostFragment() {}

    public static PrePostFragment newInstance(byte[] data) {
        PrePostFragment fragment = new PrePostFragment();
        Bundle args = new Bundle();
        args.putByteArray(ARG_PARAM1, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            image = getArguments().getByteArray(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pre_post, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Button done = (Button) view.findViewById(R.id.prePostDone);

        done.setOnClickListener(new View.OnClickListener() {
            //this has to retrieve byte data and send it back
            @Override
            public void onClick(View v) {
                onDone();
            }
        });
    }

    public void onDone() {
        if (mListener != null) {
            mListener.onDone();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PrePostFragmentListener) {
            mListener = (PrePostFragmentListener) activity;
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

    public interface PrePostFragmentListener {
        void onDone();
        void onCancel();
    }
}
