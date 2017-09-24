package com.github.sshaddicts.skeptikos;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.sshaddicts.skeptikos.fragments.CustomView;
import com.github.sshaddicts.skeptikos.fragments.InfoDisplayFragment;
import com.github.sshaddicts.skeptikos.fragments.InfoDisplayFragment.InfoDisplayFragmentListener;
import com.github.sshaddicts.skeptikos.fragments.LogInFragment.LoginInteractionListener;
import com.github.sshaddicts.skeptikos.fragments.PrePostFragment.PrePostFragmentListener;
import com.github.sshaddicts.skeptikos.fragments.SelectPictureFragment;
import com.github.sshaddicts.skeptikos.fragments.SelectPictureFragment.PictureSelectedListener;
import com.github.sshaddicts.skeptikos.model.AppDataManagerHolder.AppDataManager;
import com.github.sshaddicts.skeptikos.model.CustomHttpClient;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements CustomView, LoginInteractionListener,
        PictureSelectedListener, PrePostFragmentListener, InfoDisplayFragmentListener {
    private static final String TAG = MainActivity.class.toString();

    private FragmentManager fm;
    private Bitmap imageToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomHttpClient client = new CustomHttpClient(this);

        AppDataManager.setNeuralSwarmClient(client);

        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        InputStream inputStream = getResources().openRawResource(R.raw.numbers31);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        ByteBuffer buffer = ByteBuffer.allocate((int) (bitmap.getWidth() * bitmap.getHeight() * 4));
        bitmap.copyPixelsToBuffer(buffer);

        fm = getSupportFragmentManager();
    }

    @Override
    public void receiveData(Response data) {
        //cancel splash screen
        try {
            Log.e(TAG, "resp" + data.message());

            Log.e("DATA RECIEVED", new String(data.body().bytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void receiveError(Throwable e) {
        Log.e(TAG, e.getMessage());
    }

    @Override
    public void onAuthenticated() {
        ViewGroup container = (ViewGroup) findViewById(R.id.fragmentContainer);
        container.removeAllViews();

        SelectPictureFragment fragment = new SelectPictureFragment();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void onAuthorized(){

    }

    @Override
    public void onPictureSelected(Uri bmp, String fileName) {

        try {
            Log.e(TAG, "sending request");
            sendRequestForUri(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ViewGroup container = findViewById(R.id.fragmentContainer);
        container.removeAllViews();

        InfoDisplayFragment fragment = InfoDisplayFragment.newInstance(new ArrayList<Pair<String, Double>>());
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();

        if(fileName != null){
            new File(fileName).deleteOnExit();
        }
    }

    @Override
    public void onPrePostConfirm() {
        sendRequestForBitmap(imageToSend);

        ViewGroup container = findViewById(R.id.fragmentContainer);
        container.removeAllViews();
        InfoDisplayFragment fragment = InfoDisplayFragment.newInstance(new ArrayList<Pair<String, Double>>());
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void onPrePostCancel() {
        //go back, pop stack
        fm.popBackStack();
    }

    private void sendRequestForByteArray(byte[] data) {
        Log.d(TAG, "Data size: " + data.length);
        try {
            AppDataManager.getClient().run(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequestForUri(Uri data) throws IOException {
        InputStream stream = getContentResolver().openInputStream(data);
        if(stream != null){

            int length = stream.available();

            Log.d(TAG, "Sending image to PrePost... Image size is ("+ length +")");

            byte[] byteData = new byte[length];

            stream.read(byteData);
            sendRequestForByteArray(byteData);
            stream.close();
        }
    }

    private void sendRequestForBitmap(Bitmap bmp) {
        ByteBuffer buffer = ByteBuffer.allocate(bmp.getHeight() * bmp.getWidth() * 4);
        bmp.copyPixelsToBuffer(buffer);

        byte[] data = buffer.array();

        try {
            AppDataManager.getClient().run(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this.getApplicationContext(), "Processing started", Toast.LENGTH_LONG).show();
        Log.e(TAG, "processing request sent");
    }

    @Override
    public void onSave() {
        Toast.makeText(getApplicationContext(), "FULL PIPELINE DONE", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "activity destroyed");
        super.onDestroy();
    }
}
