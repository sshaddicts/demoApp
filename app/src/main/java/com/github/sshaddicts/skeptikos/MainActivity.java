package com.github.sshaddicts.skeptikos;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.sshaddicts.neuralclient.Client;
import com.github.sshaddicts.neuralclient.data.ProcessedData;
import com.github.sshaddicts.skeptikos.fragments.CustomView;
import com.github.sshaddicts.skeptikos.fragments.InfoDisplayFragment;
import com.github.sshaddicts.skeptikos.fragments.InfoDisplayFragment.InfoDisplayFragmentListener;
import com.github.sshaddicts.skeptikos.fragments.LogInFragment.LoginInteractionListener;
import com.github.sshaddicts.skeptikos.fragments.PrePostFragment;
import com.github.sshaddicts.skeptikos.fragments.PrePostFragment.PrePostFragmentListener;
import com.github.sshaddicts.skeptikos.fragments.SelectPictureFragment;
import com.github.sshaddicts.skeptikos.fragments.SelectPictureFragment.PictureSelectedListener;
import com.github.sshaddicts.skeptikos.model.AppDataManagerHolder.AppDataManager;
import com.github.sshaddicts.skeptikos.model.NeuralSwarmClient;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements CustomView, LoginInteractionListener,
        PictureSelectedListener, PrePostFragmentListener, InfoDisplayFragmentListener {
    private static final String TAG = MainActivity.class.toString();

    private FragmentManager fm;
    private Bitmap imageToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        NeuralSwarmClient neuralClient = new NeuralSwarmClient(this);
        try {
            neuralClient.connect();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        AppDataManager.setNeuralSwarmClient(neuralClient);

        setTheme(R.style.AppThemeDark);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fm = getSupportFragmentManager();
    }

    @Override
    public void receiveData(ProcessedData data) {
        //cancel splash screen
        Toast.makeText(getApplicationContext(), "Data received!", Toast.LENGTH_SHORT).show();
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
    public void onPictureSelected(Bitmap bmp, String fileName) {
        imageToSend = bmp;
        ByteBuffer bytes = ByteBuffer.allocate(bmp.getHeight() * bmp.getWidth() * 4);
        bmp.copyPixelsToBuffer(bytes);

        ViewGroup container = (ViewGroup) findViewById(R.id.fragmentContainer);
        container.removeAllViews();

        PrePostFragment fragment = PrePostFragment.newInstance(bytes.array(), bmp.getWidth(), bmp.getHeight());
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

        //send request...
        sendRequestForBitmap(imageToSend);

        //display smth

        ViewGroup container = (ViewGroup) findViewById(R.id.fragmentContainer);
        container.removeAllViews();
        //open view on done
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

    private void sendRequestForByteArray(byte[] data, int width, int height) {
        Log.d(TAG, "Data size: " + data.length);
        AppDataManager.getClient().requestImageProcessing(data, width, height);
    }

    private void sendRequestForBitmap(Bitmap bmp) {
        ByteBuffer buffer = ByteBuffer.allocate(bmp.getHeight() * bmp.getWidth() * 4);
        bmp.copyPixelsToBuffer(buffer);

        byte[] data = buffer.array();

        AppDataManager.getClient().requestImageProcessing(data, bmp.getWidth(), bmp.getHeight());
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
