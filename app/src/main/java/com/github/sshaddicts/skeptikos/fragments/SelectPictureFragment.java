package com.github.sshaddicts.skeptikos.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.sshaddicts.skeptikos.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectPictureFragment extends Fragment {

    private static final int IMAGE_SELECTION = 1;

    private static final String TAG = SelectPictureFragment.class.toString();

    private PictureSelectedListener mListener;

    public SelectPictureFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button takePicButton = (Button) view.findViewById(R.id.selectImageCapture);
        ImageButton selectImageFromGalleryButton = (ImageButton) view.findViewById(R.id.selectImageDone);
        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePicture();
            }
        });
        selectImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchSelectPicture();
            }
        });


        //GridView grid = (GridView) view.findViewById(R.id.selectImageGrid);
        //GridViewAdapter gridAdapter = new GridViewAdapter(this.getActivity().getApplicationContext(), R.id.gridRowImageView, getData());
        //grid.setAdapter(gridAdapter);
    }

    public void dispatchTakePicture() {
        //dispatch camera intent
    }

    public void dispatchSelectPicture() {
        //dispatch selection intent
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_SELECTION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            //send the bmp back
            try {
                InputStream stream = getActivity().getContentResolver().openInputStream(data.getData());
                if(stream != null){
                    Log.d(TAG, "Sending image to PrePost... Image size is " + stream.available());

                    Bitmap bitmap = BitmapFactory.decodeStream(stream);

                    if(mListener != null){
                        mListener.onPictureSelected(bitmap);
                    }
                }
            } catch (IOException | NullPointerException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Something went bad, sorry", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
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
        void onPictureSelected(Bitmap picture);
    }
}
