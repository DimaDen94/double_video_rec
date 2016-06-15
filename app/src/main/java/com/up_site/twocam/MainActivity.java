package com.up_site.twocam;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;


import com.up_site.twocam.cameraPreview.BackCameraPreview;
import com.up_site.twocam.cameraPreview.FrontCameraPreview;
import com.up_site.twocam.utils.SDWorker;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DisplayOrientation {
    private Camera backCamera;
    private Camera frontCamera;
    private BackCameraPreview backCamPreview;
    private FrontCameraPreview frontCamPreview;
    private MediaRecorder mediaRecorderFront;
    private MediaRecorder mediaRecorderBack;
    private File videoFileFront;
    private File videoFileBack;
    public static String TAG = "DualCamActivity";
    public Button btnStart;
    public Button btnStop;
    Camera.Size videoSizeBack;
    Camera.Size videoSizeFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fullscreen and remove title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        //keeping the device awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        File directory = SDWorker.createDirectory();
        videoFileBack = SDWorker.generateFileUri(directory, 0);
        videoFileFront = SDWorker.generateFileUri(directory, 1);

        // Create an instance of Camera
        backCamera = getCameraInstance(0);
        frontCamera = getCameraInstance(1);

        // Create camera Preview view and set it as the content of our activity.
        backCamPreview = new BackCameraPreview(this, this, backCamera);
        frontCamPreview = new FrontCameraPreview(this, this, frontCamera);

        FrameLayout backPreview = (FrameLayout) findViewById(R.id.back_camera_preview);
        FrameLayout frontPreview = (FrameLayout) findViewById(R.id.front_camera_preview);

        backPreview.addView(backCamPreview);
        frontPreview.addView(frontCamPreview);

        //init button
        btnStart = (Button) findViewById(R.id.button);
        btnStop = (Button) findViewById(R.id.button2);

        //this view to front
        backPreview.bringToFront();
        btnStart.bringToFront();
        btnStop.bringToFront();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setPreviewSize(true);
    }


    @Override
    protected void onPause() {

        super.onPause();

        if (backCamera != null)
            backCamera.release();
        backCamera = null;
        if (frontCamera != null)
            frontCamera.release();
        frontCamera = null;


    }

    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Camera " + cameraId + " not available! " + e.toString());
        }
        return c; // returns null if camera is unavailable
    }

    void setPreviewSize(boolean fullScreen) {

        // получаем размеры экрана
        Display display = getWindowManager().getDefaultDisplay();
        boolean widthIsMax = display.getWidth() > display.getHeight();

        // определяем размеры превью камеры
        Camera.Size size = backCamera.getParameters().getPreviewSize();

        RectF rectDisplay = new RectF();
        RectF rectPreview = new RectF();

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0, 0, display.getWidth(), display.getHeight());

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0, 0, size.width, size.height);
        } else {
            // превью в вертикальной ориентации
            rectPreview.set(0, 0, size.height, size.width);
        }

        Matrix matrix = new Matrix();
        // подготовка матрицы преобразования
        if (!fullScreen) {
            // если превью будет "втиснут" в экран (второй вариант из урока)
            matrix.setRectToRect(rectPreview, rectDisplay,
                    Matrix.ScaleToFit.START);
        } else {
            // если экран будет "втиснут" в превью (третий вариант из урока)
            matrix.setRectToRect(rectDisplay, rectPreview,
                    Matrix.ScaleToFit.START);
            matrix.invert(matrix);
        }
        // преобразование
        matrix.mapRect(rectPreview);




        backCamPreview.getLayoutParams().height = (int) (rectPreview.bottom);
        backCamPreview.getLayoutParams().width = (int) (rectPreview.right);
        frontCamPreview.getLayoutParams().height = (int)(rectPreview.bottom/4);
        frontCamPreview.getLayoutParams().width = (int)(rectPreview.right/4);
        setParametersToCamera(backCamera, 0);
        setParametersToCamera(frontCamera, 1);
    }

    int w;

    private void setParametersToCamera(Camera camera, int number) {
        Camera.Parameters params = camera.getParameters();

        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);


        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        List<Camera.Size> videoSizes = params.getSupportedVideoSizes();

        int num = 0;
        for (int i = 0; i < videoSizes.size(); i++) {
            Log.d("log", videoSizes.get(i).height + " " + videoSizes.get(i).width);

        }

        for (int i = 0; i < previewSizes.size(); i++) {
            Log.d("log", previewSizes.get(i).height + " " + previewSizes.get(i).width);
            if (previewSizes.get(i).width < w)
                num = i;
        }
        Log.d("log", num + "");


        if (number == 0) {
            videoSizeBack = previewSizes.get(3);
            params.setPreviewSize(videoSizeBack.width, videoSizeBack.height);

        } else {
            videoSizeFront = previewSizes.get(1);
            params.setPreviewSize(videoSizeFront.width, videoSizeFront.height);

        }

        camera.setParameters(params);
    }


    public void setCameraDisplayOrientation(int cameraId) {
        // определяем насколько повернут экран от нормального положения
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int cameraRotationOffset = info.orientation;

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;


        // задняя камера
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = ((360 - degrees) + info.orientation);
        } else
            // передняя камера
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = ((360 - degrees) - info.orientation);
                result += 360;
            }
        result = result % 360;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            backCamera.setDisplayOrientation(result);
        } else
            frontCamera.setDisplayOrientation(result);


    }

    private void setRecorderOrientation(MediaRecorder recorder, int n) {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int rot;
        Camera.CameraInfo info = new Camera.CameraInfo();

        if (n == 0) {
            Camera.getCameraInfo(0, info);
            rot = (info.orientation - degrees + 360) % 360;
            recorder.setOrientationHint(rot);
        } else {
            Camera.getCameraInfo(1, info);
            rot = (info.orientation + degrees) % 360;
            recorder.setOrientationHint(rot);
        }
    }

    public void onClickStartRecord(View view) {

        mediaRecorderBack = new MediaRecorder();
        mediaRecorderFront = new MediaRecorder();

        setRecorderOrientation(mediaRecorderBack, 0);
        setRecorderOrientation(mediaRecorderFront, 2);


        if (prepareVideoRecorder(mediaRecorderBack, backCamera, videoFileBack, 0)
                && prepareVideoRecorder(mediaRecorderFront, frontCamera, videoFileFront, 1)) {
            mediaRecorderBack.start();
            mediaRecorderFront.start();
        } else {
            releaseMediaRecorder(mediaRecorderBack, backCamera);
            releaseMediaRecorder(mediaRecorderFront, frontCamera);
        }
    }

    public void onClickStopRecord(View view) {
        if (mediaRecorderBack != null) {
            mediaRecorderBack.stop();
            releaseMediaRecorder(mediaRecorderBack, backCamera);
        }
        if (mediaRecorderFront != null) {
            mediaRecorderFront.stop();
            releaseMediaRecorder(mediaRecorderFront, frontCamera);
        }
        SDWorker.galleryAddPic(videoFileBack,this);
        SDWorker.galleryAddPic(videoFileFront,this);
    }

    private boolean prepareVideoRecorder(MediaRecorder recorder, Camera camera, File file, int n) {

        camera.unlock();
        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        recorder.setProfile(CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH));
        recorder.setOutputFile(file.getAbsolutePath());
        if (n == 0)
            recorder.setVideoSize(videoSizeBack.width, videoSizeBack.height);
        else
            recorder.setVideoSize(videoSizeFront.width, videoSizeFront.height);
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            releaseMediaRecorder(recorder, camera);
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder(MediaRecorder recorder, Camera camera) {
        if (recorder != null) {
            recorder.reset();
            recorder.release();
            camera.lock();
        }
    }
}
