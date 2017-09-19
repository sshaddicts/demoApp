package com.github.sshaddicts.skeptikos;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.sshaddicts.neuralclient.data.ProcessedData;
import com.github.sshaddicts.skeptikos.fragments.CustomView;
import com.github.sshaddicts.skeptikos.fragments.InfoDisplayFragment;
import com.github.sshaddicts.skeptikos.fragments.LogInFragment;
import com.github.sshaddicts.skeptikos.fragments.LogInFragment.LoginInteractionListener;
import com.github.sshaddicts.skeptikos.fragments.PrePostFragment;
import com.github.sshaddicts.skeptikos.fragments.PrePostFragment.PrePostFragmentListener;
import com.github.sshaddicts.skeptikos.fragments.SelectPictureFragment;
import com.github.sshaddicts.skeptikos.fragments.SelectPictureFragment.PictureSelectedListener;
import com.github.sshaddicts.skeptikos.model.AppDataManagerHolder.AppDataManager;
import com.github.sshaddicts.skeptikos.model.NeuralSwarmClient;
import com.github.sshaddicts.skeptikos.fragments.InfoDisplayFragment;
import com.github.sshaddicts.skeptikos.fragments.InfoDisplayFragment.InfoDisplayFragmentListener;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements CustomView, LoginInteractionListener,
        PictureSelectedListener, PrePostFragmentListener, InfoDisplayFragmentListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PAGE_NUMBER = 2;

    private static final String TAG = MainActivity.class.toString();

    private String username;

    //private ViewPager pager;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NeuralSwarmClient neuralClient = new NeuralSwarmClient("test", "test", this);
        AppDataManager.setNeuralSwarmClient(neuralClient);

        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        Log.d(TAG, "Setting pager to display login fragment...");
    }

    @Override
    public void receiveData(ProcessedData data) {
        Toast.makeText(getApplicationContext(), "Data received!", Toast.LENGTH_SHORT).show();
    }

    public void showLoginScreen() {
        LogInFragment login = new LogInFragment();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fragmentContainer, login);
        transaction.commit();
    }

    @Override
    public void onAuthenticated(String username) {
        this.username = username;

        ViewGroup container = (ViewGroup) findViewById(R.id.fragmentContainer);
        container.removeAllViews();

        SelectPictureFragment fragment = SelectPictureFragment.newInstance(this.username);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void onRegisterNewAccount() {
        //switch to registration fragment
        Toast.makeText(getApplicationContext(), "WE DID IT TO THE REGISTER", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPictureSelection() {
        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();

        ViewGroup container = (ViewGroup) findViewById(R.id.fragmentContainer);
        container.removeAllViews();

        PrePostFragment fragment = PrePostFragment.newInstance(new byte[5]);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void onDone() {
        //send request...
        //AppDataManager.getClient().requestImageProcessing(data);


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
    public void onCancel() {

    }

    private static void sendRequestForByteArray(byte[] data, int width, int height) {
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
}
