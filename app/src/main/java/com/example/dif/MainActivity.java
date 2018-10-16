package com.example.dif;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;


import android.content.res.Resources;
import android.hardware.camera2.CameraDevice;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.util.SparseArray;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.security.Policy;
import java.util.List;

import android.widget.Toast;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.widget.ZoomControls;


public class MainActivity extends AppCompatActivity {
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int requestCameraPermissionID = 1001;
    TextRecognizer textRecognizer;
    public static final int REQUEST_PERM_WRITE_STORAGE = 102;
    String s;
    String a;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    EditText phone;
    String number;
    int freeze;
    int maxzoom;
    int currentzoom;
    int pWidth;
    int pLenght;


    private Camera camera;
    Camera.Parameters param;
    TextView TB;


    public void freeze(View view) {
        if (freeze != 0) {
            freeze = 0;
            Toast.makeText(getApplicationContext(), "unfreezed",
                    Toast.LENGTH_LONG).show();
        } else {
            freeze = 1;
            Toast.makeText(getApplicationContext(), "freezed.",
                    Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //phone = (EditText) findViewById(R.id.editText);
        cameraView = (SurfaceView) findViewById(R.id.surface);
        textView = (TextView) findViewById(R.id.text);
        TB = (TextView) findViewById(R.id.TestBox);
        camera = Camera.open();
        param = camera.getParameters();
        maxzoom = param.getMaxZoom();





        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector Dependencies are not yet available.");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }
                try {
                    if (cameraSource != null) {
                        cameraSource.start(cameraView.getHolder());
                    }

                } catch (IOException e) {
                    e.getStackTrace();
                }

            }
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERM_WRITE_STORAGE);

            } else {

                startCamera();
            }

        }

    }



    public void startCamera() {
        cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels)//1280,1024
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();


        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }


            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
                }


            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = items.valueAt(i);

                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            String s = stringBuilder.toString();
                            if (freeze != 1) {
                                textView.setText(s);

                            }
                        }
                    });
                }
            }
        });
    }


    public void onclick(View view) {
        a = textView.getText().toString();

        Intent sendintent = new Intent();
        sendintent.setAction(Intent.ACTION_SEND);
        sendintent.putExtra(Intent.EXTRA_TEXT, a);
        sendintent.setType("text/plain");
        startActivity(sendintent);

    }

    public void unzoom(View view) {

        if(param.getZoom()>0) {
            currentzoom = param.getZoom() - maxzoom;
        }
            else{
                currentzoom = 0;
                }
      //  param.setZoom(currentzoom);
      //  camera.setParameters(param);
        TB.setText(Integer.toString(currentzoom));
        //TB.setText(Integer.toString(Resources.getSystem().getDisplayMetrics().widthPixels));
    }

    public void zoom(View view) {
        param.setZoom(52);
        camera.setParameters(param);
        if(param.getZoom()< param.getMaxZoom()) {
            currentzoom = param.getZoom() + maxzoom;
        }else{
            currentzoom =  param.getMaxZoom();
        }
        //    param.setZoom(currentzoom);
          //  camera.setParameters(param);
            TB.setText(Integer.toString(currentzoom));
         //TB.setText(Integer.toString(Resources.getSystem().getDisplayMetrics().heightPixels));
    }
}



