package com.example.StephanWagener.quanpic;

// Imports needed for used functionalities.

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

// Main Class. Links layout and implementation.
public class MainActivity extends ActionBarActivity {

    // Global variables
    private CameraBridgeViewBase cameraView;
    private boolean isMedianCut = true;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    cameraView.enableView();
                    setFunctionality();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private CameraBridgeViewBase.CvCameraViewFrame currentInputFrame;

    // Initializing required elements when starting the application.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//TODO der FULLSCREEN muss noch umgesetzt werden!!!
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        cameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
    }

    private void setFunctionality()
    {
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {

            @Override
            public void onCameraViewStopped() {}

            @Override
            public void onCameraViewStarted(int width, int height) {}

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
            {
                currentInputFrame = inputFrame;
                if (isMedianCut)
                {
                    //TODO median cut implementation
                    Mat rgb = inputFrame.rgba();
                    Mat gray = new Mat();
                    Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);
                    return gray;
                }
                else
                {
                    //TODO population implementation
                    return inputFrame.rgba();
                }
            }
        });

        cameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    //saveImage(currentInputFrame.rgba());

                    isMedianCut = !isMedianCut;
                    if (isMedianCut)
                    {
                        Toast.makeText(getApplicationContext(), "Das \"Median-Cut-Verfahren\" wurde aktiviert.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Das \"Popularitätsverfahren\" wurde aktiviert.", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                }
                return false;
            }
        });
    }

 /**   public void saveImage (Mat mat) {
        Mat mIntermediateMat = new Mat();

        Imgproc.cvtColor(mat, mIntermediateMat, Imgproc.COLOR_RGBA2BGR, 3);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String filename = "image.jpg";
        File file = new File(path, filename);

        Boolean bool = null;
        filename = file.toString();
        bool = Highgui.imwrite(filename, mIntermediateMat);

        if (bool == true)
            Toast.makeText(getApplicationContext(), "SUCCESS writing image to external storage", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Fail writing image to external storage", Toast.LENGTH_SHORT).show();
    } */

    @Override
    public void onPause()
    {
        super.onPause();
        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraView != null)
            cameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }
}
