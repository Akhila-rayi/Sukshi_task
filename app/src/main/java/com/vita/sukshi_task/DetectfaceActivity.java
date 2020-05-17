package com.vita.sukshi_task;

/*
 * Vishwam Corp CONFIDENTIAL

 * Vishwam Corp 2018
 * All Rights Reserved.

 * NOTICE:  All information contained herein is, and remains
 * the property of Vishwam Corp. The intellectual and technical concepts contained
 * herein are proprietary to Vishwam Corp
 * and are protected by trade secret or copyright law of U.S.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Vishwam Corp
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;


import com.vita.facedetection_lib.CameraSource;
import com.vita.facedetection_lib.CameraSourcePreview;
import com.vita.facedetection_lib.FaceDect;
import com.vita.facedetection_lib.GraphicOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.vita.facedetection_lib.FaceDect.previewFaceDetector;


public class DetectfaceActivity extends AppCompatActivity implements FaceDect.OnMultipleFacesDetectedListener, FaceDect.OnCaptureListener {

    private static final String TAG = "Custom Camera";
    private Context context;
    public CameraSource mCameraSource;

    FaceDect faceDect;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private boolean wasActivityResumed = false;

    ImageView previewImages;
    public static boolean takePicture;
    Timer timer1;
    ImageView camera;
    boolean ivclicked = false;
    boolean landmarksfound = false;
    private int rotationCounter = 0;
    com.vita.facedetection_lib.CameraActivity cameraActivity=new com.vita.facedetection_lib.CameraActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        context = getApplicationContext();
        takePicture = false;
        cameraActivity.settakepict(takePicture);


        camera = findViewById(R.id.camera);

        previewImages = findViewById(R.id.preview);
        RelativeLayout relativeLayout = findViewById(R.id.camRLayout);

        mPreview = findViewById(R.id.previewAuth);
        mGraphicOverlay = findViewById(R.id.faceOverlayAuth);
        timer1 = new Timer();

        createCameraSourceFront();
        startCameraSource();

        startTimer();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ivclicked = true;
                takePicture = true;
                cameraActivity.settakepict(takePicture);
            }
        });

    }

    private void startTimer() {


        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                takePicture = true;

                cameraActivity.settakepict(takePicture);
            }
        }, 0, 1000);
    }

    @Override
    public void onMultipleFacesDetected(int n) {
    }


    @Override
    public void onCapture(byte[] data, int angle) {

        cameraActivity.aftercapture(context,data,angle,mPreview,camera,landmarksfound,ivclicked);

    }

    private void createCameraSourceFront() {
        faceDect = new FaceDect(this, mGraphicOverlay);
        mCameraSource = new CameraSource.Builder(context, previewFaceDetector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(60.0f)
                .build();

        startCameraSource();
    }

    private void startCameraSource() {

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                // Log.e(TAG, "Unable to start caera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void stopCameraSource() {
        mPreview.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasActivityResumed) {
            createCameraSourceFront();
        }
        startCameraSource();
        timer1=new Timer();
        if (timer1 != null) {
            startTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasActivityResumed = true;
        stopCameraSource();
        if (timer1 != null) {
            timer1.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCameraSource();
        if (timer1 != null) {
            timer1.cancel();
        }
    }

}
