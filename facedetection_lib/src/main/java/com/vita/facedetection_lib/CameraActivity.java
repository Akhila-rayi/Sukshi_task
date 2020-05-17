package com.vita.facedetection_lib;

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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.vita.facedetection_lib.FaceDect.previewFaceDetector;


public class CameraActivity extends AppCompatActivity implements FaceDect.OnMultipleFacesDetectedListener, FaceDect.OnCaptureListener {

    public static boolean takePicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_camera);


    }

    public void settakepict(boolean status) {

        takePicture = status;
    }


    public void aftercapture(Context context, byte[] data, int angle, CameraSourcePreview mPreview, ImageView camera, boolean landmarksfound, boolean ivclicked) {


        Log.e("@@@CAMERA TOOK PIC", angle + "");
        Bitmap OriginalBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        //matrix.postRotate(angle);
        matrix.preScale(-1.0f, 1.0f);
        Bitmap rotatedbitmap = Bitmap.createBitmap(OriginalBitmap, 0, 0, OriginalBitmap.getWidth(), OriginalBitmap.getHeight(), matrix, true);
        //saveFile(rotatedbitmap);
        Frame frame = new Frame.Builder().setBitmap(rotatedbitmap).build();
        SparseArray<Face> faceSparseArray = previewFaceDetector.detect(frame);

        Log.e("@@ffdsd", faceSparseArray.size() + "");


        if (faceSparseArray.size() > 0) {

            for (int i = 0; i < faceSparseArray.size(); ++i) {

                Face face = faceSparseArray.valueAt(i);
                Log.e("@@ffdsd_landmarks", face.getLandmarks().size() + "");
                if (face.getLandmarks().size() > 0) {

                    landmarksfound = true;
                    float val_y = face.getEulerY();
                    float val_z = face.getEulerZ();

                    if (val_y < 12.0f && val_y > -12.0f) {

                        if (val_z > 80.0f && val_z < 90.0f) {

                            camera.setBackground(context.getResources().getDrawable(R.drawable.enable_camera));
                            Toast.makeText(context, "Straight Face", Toast.LENGTH_SHORT).show();
                            mPreview.stop();
                            saveFile(rotatedbitmap);

                        } else {

                            camera.setBackground(context.getResources().getDrawable(R.drawable.marker));

                        }

                    } else if (val_y < -36.0f) {

                        Toast.makeText(context, "face tilted more to Left", Toast.LENGTH_SHORT).show();

                    } else if (val_y > 36.0f) {

                        Toast.makeText(context, "face tilted more to Right", Toast.LENGTH_SHORT).show();

                    } else if (val_y > -36.0f && val_y < -12.0f) {

                        Toast.makeText(context, "face tilted to Left", Toast.LENGTH_SHORT).show();

                    } else if (val_y > 12.0f && val_y < 36.0f) {

                        Toast.makeText(context, "face tilted to Right", Toast.LENGTH_SHORT).show();

                    } else {
                        camera.setBackground(context.getResources().getDrawable(R.drawable.marker));
                    }
                    Log.e("@@facealg", val_y + "___" + val_z);

                } else {

                    landmarksfound = false;
                    break;
                }
            }
            if (!landmarksfound) {
                camera.setBackground(context.getResources().getDrawable(R.drawable.marker));
            }


        } else {
            camera.setBackground(context.getResources().getDrawable(R.drawable.marker));
        }
    }


    public void saveFile(Bitmap bitmap) {

        File file = getOutputMediaFile();
        String path = file.getPath();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getOutputMediaFile() {

        final String TAG = "CameraPreview";

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Camera");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        long time = System.currentTimeMillis();
        File file = new File(mediaStorageDir.getPath() + File.separator + time + ".jpg");

        return file;
    }

    @Override
    public void onMultipleFacesDetected(int n) {

    }

    @Override
    public void onCapture(byte[] data, int angle) {

    }
}
