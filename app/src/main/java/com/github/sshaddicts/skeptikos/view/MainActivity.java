package com.github.sshaddicts.skeptikos.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.sshaddicts.skeptikos.R;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button baton = (Button) findViewById(R.id.mainActivityTakePictureButton);
        mImageView = (ImageView) findViewById(R.id.mainActivityImageView);

        baton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "starting a camera", Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap  = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }




    /**
     * Example of a call to a native method
     *  //TextView tv = (TextView) findViewById(R.id.sample_text);
     *  //tv.setText(stringFromJNI());
     *
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     *
     * //public native String stringFromJNI();
     *
     * Used to load the 'native-lib' library on application startup.
     *
     * // static {
     * //   System.loadLibrary("native-lib");
     * //}
     */
}
