package com.example.StephanWagener.quanpic;

// Imports needed for used functionalities.

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.DateFormat;

// Main Class. Links layout and implementation.
public class MainActivity extends Activity {

    // Global variables
    private CameraBridgeViewBase cameraView;
    private boolean isMedianCut = true;
    long time = 0;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    cameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private Mat currentInput;
    private MenuItem popItem;
    private MenuItem medItm;

    // Initializing required elements when starting the application.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        cameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        setFunctionality();
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
                if (isMedianCut)
                {
                    //TODO median cut implementation
                    Mat rgb = inputFrame.rgba();
                    Mat gray = new Mat();
                    Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);
                    currentInput = gray;
                    return gray;
                }
                else
                {
                    //TODO population implementation
                    currentInput = inputFrame.rgba();
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
                    time = event.getEventTime();
                    return true;
                }
                if (event.getActionMasked() == MotionEvent.ACTION_UP)
                {
                    if (event.getEventTime() - time > 1500)
                    {
                        isMedianCut = !isMedianCut;
                        if (isMedianCut)
                        {
                            Toast.makeText(getApplicationContext(), "Das \"Median-Cut-Verfahren\" wurde aktiviert.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Das \"Popularitätsverfahren\" wurde aktiviert.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        saveImage(currentInput);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void saveImage (Mat mat) {
        Mat mIntermediateMat = new Mat();

        try
        {
            Imgproc.cvtColor(mat, mIntermediateMat, Imgproc.COLOR_RGBA2BGRA, 3);
        }
        catch (CvException e)
        {
            Imgproc.cvtColor(mat, mIntermediateMat, Imgproc.COLOR_GRAY2BGRA, 3);
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String filename = "image" + DateFormat.getDateTimeInstance().toString() + ".png";
        File file = new File(path, filename);

        Boolean bool = null;
        filename = file.toString();
        bool = Highgui.imwrite(filename, mIntermediateMat);

        if (bool == true)
            Toast.makeText(getApplicationContext(), "Ihr Bild wurde erfolgreich gespeichert.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Beim Speichern des Bildes ist ein Fehler aufgetreten!", Toast.LENGTH_SHORT).show();
    }

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
        if (!OpenCVLoader.initDebug())
        {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        }
        else
        {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
