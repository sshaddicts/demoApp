package com.github.sshaddicts.skeptikos.view;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sshaddicts.neuralclient.data.ProcessedData;
import com.github.sshaddicts.skeptikos.R;
import com.github.sshaddicts.skeptikos.model.NeuralSwarmClient;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity implements CustomView {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Uri mCurrentPhotoPath;

    private ImageView imageView;
    private ProgressBar progressBar;

    NeuralSwarmClient client;

    private static final String TAG = MainActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.mainActivityImageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

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

                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.raw.numbers31);

                BitmapFactory.Options dimensions = new BitmapFactory.Options();
                dimensions.inJustDecodeBounds = true;
                Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.raw.numbers31, dimensions);
                int height = dimensions.outHeight;
                int width = dimensions.outWidth;

                Log.d(TAG, "bitmap to send dimensions: [" + width + ", " + height + "]");
                imageView.setImageBitmap(bmp);

                sendRequestForBitmap(bmp);
            }
        });
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    @Override
    public void receiveData(ProcessedData data) {
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

        mCurrentPhotoPath = getOutputMediaFileUri();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoPath);

        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Bitmap pic = getPic(uri.toString());
            Log.d(TAG, "dimensions are " + pic.getHeight() + ", " + pic.getWidth());
            sendRequestForBitmap(pic);
        }
    }

    private Bitmap getPic(String uri) {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        Bitmap fullSize = BitmapFactory.decodeFile(uri, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap thumbNail = BitmapFactory.decodeFile(uri, bmOptions);
        imageView.setImageBitmap(thumbNail);
        return fullSize;
    }

    private void sendRequestForBitmap(Bitmap bmp) {
        ByteBuffer buffer = ByteBuffer.allocate(bmp.getHeight() * bmp.getWidth() * 4);
        bmp.copyPixelsToBuffer(buffer);

        byte[] data = buffer.array();

        client.requestImageProcessing(data, bmp.getWidth(), bmp.getHeight());
        Toast.makeText(this.getApplicationContext(), "processing started", Toast.LENGTH_LONG).show();
        Log.e(TAG, "processing request sent");
    }
}
