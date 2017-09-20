package com.github.sshaddicts.skeptikos.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.sshaddicts.skeptikos.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SelectPictureFragment extends Fragment {

    private static final int IMAGE_SELECTION = 1;
    private static final int IMAGE_CAPTURE = 2;
    private static final String TAG = SelectPictureFragment.class.toString();
    private Uri fileLocation;
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
        Button selectImageFromGalleryButton = (Button) view.findViewById(R.id.selectImageDone);
        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dispatchTakePicture();
            }
        });
        selectImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchSelectPicture();
            }
        });
    }

    public void dispatchTakePicture() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, fileLocation);
        startActivityForResult(takePicture, IMAGE_CAPTURE);
    }

    public void dispatchSelectPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_SELECTION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case IMAGE_SELECTION:{
                if(resultCode == Activity.RESULT_OK){
                    try {
                        InputStream stream = getActivity().getContentResolver().openInputStream(data.getData());
                        if(stream != null){
                            Bitmap bitmap = BitmapFactory.decodeStream(stream);
                            Log.d(TAG, "Sending image to PrePost... Image size is " +bitmap.getWidth() +","
                                    + bitmap .getHeight()+ "("+ stream.available()+")");

                            if(mListener != null){
                                mListener.onPictureSelected(bitmap, null);
                            }
                        }
                    } catch (IOException | NullPointerException e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Something went bad, sorry", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }break;
            case IMAGE_CAPTURE:{
                if(resultCode == Activity.RESULT_OK){
                    Log.d(TAG, "Trying to open " + fileLocation + "...");
                    Bitmap fullCapturedImage = BitmapFactory.decodeFile(fileLocation.toString());
                    File f = new File(String.valueOf(fileLocation));
                    if(!f.exists())
                        Log.e(TAG, "File does not exist for some reason");
                    if(fullCapturedImage == null){
                        Log.e(TAG, "Recieved bitmap is null! AAAAAA");
                    }else{
                        if(mListener != null){
                            mListener.onPictureSelected(fullCapturedImage, fileLocation.toString());
                        }
                    }

                }
            }break;
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
        void onPictureSelected(Bitmap picture, String fileName);
    }

    public File createFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd_G_HH.mm.s", Locale.US).format(new Date());
        String filename = "skept_" + timeStamp;

        File directory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        assert directory != null;
        Log.d(TAG, "Image will be saved to " + directory.getAbsolutePath());

        File imageFile = new File(directory, filename + ".jpg");

        //creates a file if it doesn't exist
        if(!imageFile.exists() && imageFile.createNewFile()){
            fileLocation = Uri.fromFile(imageFile);
            Log.d(TAG, "Image file url: " + fileLocation);
            if(imageFile.exists()){
                Log.d(TAG, "FILE EXISTS, BLIN");
            }else{
                Log.d(TAG, "THE BLINMOBILE");
            }
            return imageFile;
        }else{
            throw new IOException("File was not created for some reason.");
        }
    }
}
