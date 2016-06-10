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
public abstract class CameraPreview  extends SurfaceView implements SurfaceHolder.Callback {
    public SurfaceHolder mHolder;
    public Camera mCamera;
    public DisplayOrientation display;
    public String TAG = "BackCameraPreview";
    public CameraPreview(DisplayOrientation displayOrientation, Context context, Camera camera) {
        super(context);
        mCamera = camera;
        this.display = displayOrientation;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }
}
