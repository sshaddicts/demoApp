package com.github.sshaddicts.skeptikos.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.github.sshaddicts.skeptikos.R;

import java.nio.ByteBuffer;


public class PrePostFragment extends Fragment {

    private PrePostFragmentListener mListener;

    private byte[] imageData;
    private int width;
    private int height;

    public PrePostFragment() {}

    public static PrePostFragment newInstance(byte[] data, int width, int height) {
        PrePostFragment fragment = new PrePostFragment();
        Bundle args = new Bundle();
        args.putByteArray("p1", data);
        args.putInt("p2", width);
        args.putInt("p3", height);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageData = getArguments().getByteArray("p1");
            width = getArguments().getInt("p2");
            height = getArguments().getInt("p3");
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

        ImageView imageView = (ImageView) view.findViewById(R.id.prePostImage);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(imageData));

        imageView.setImageBitmap(bitmap);

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
            mListener.onPrePostConfirm();
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
        void onPrePostConfirm();
        void onPrePostCancel();
    }
}
