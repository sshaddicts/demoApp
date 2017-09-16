package com.github.sshaddicts.skeptikos.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sshaddicts.neuralclient.data.ProcessedData;
import com.github.sshaddicts.skeptikos.R;
import com.github.sshaddicts.skeptikos.model.NeuralSwarmClient;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity implements CustomView {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String filename;
    private String mCurrentPhotoPath;

    private Bitmap bitmap;
    private ImageView mImageView;

    NeuralSwarmClient client;

    private static final String TAG = MainActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.mainActivityImageView);

        client = new NeuralSwarmClient("test", "test", this);
        client.authenticateClient();
        try {
            client.connect();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Button button = (Button) findViewById(R.id.mainActivityTakePictureButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "starting a camera", Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    public void recieveData(ProcessedData data){
        Log.e(TAG, "data recieved");
        ObjectMapper mapper = new ObjectMapper();

        try {
            Log.e(TAG, mapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this.getApplicationContext(), "ERRORRORORO", Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            sendRequestForBitmap(getPic());
        }
    }

    private Bitmap getPic() {
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
        return bitmap;
    }

    private void sendRequestForBitmap(Bitmap bmp) {
        ByteBuffer buffer = ByteBuffer.allocate(bmp.getHeight() * bmp.getWidth() * 4);
        bmp.copyPixelsToBuffer(buffer);

        byte[] data = buffer.array();

        client.requestImageProcessing(data, bmp.getWidth(), bmp.getHeight());
        Toast.makeText(this.getApplicationContext(),"processing started",Toast.LENGTH_LONG).show();
        Log.e(TAG, "processing request sent");
    }

    private void setBitmap(Bitmap bitmapToSet) {
        mImageView.setImageBitmap(bitmapToSet);
    }


    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
