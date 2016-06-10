package com.up_site.twocam.cameraPreview;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.up_site.twocam.DisplayOrientation;

import java.io.IOException;

/**
 * Created by Dmitry on 03.06.2016.
 */
public class BackCameraPreview extends CameraPreview {

    public BackCameraPreview(DisplayOrientation displayOrientation, Context context, Camera camera) {
        super(displayOrientation, context, camera);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        display.setCameraDisplayOrientation(0);
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}