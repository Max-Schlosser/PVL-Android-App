package com.example.StephanWagener.quanpic;

// Imports needed for used functionalities.

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
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
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;

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
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private CameraBridgeViewBase.CvCameraViewFrame currentInputFrame;
    private MenuItem popItem;
    private MenuItem medItm;

    // Initializing required elements when starting the application.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
//TODO der FULLSCREEN muss noch umgesetzt werden!!!
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
                    saveImage(currentInputFrame.rgba());
                    return true;
                }
                return false;
            }
        });
    }

    public void saveImage (Mat mat) {
        Mat mIntermediateMat = new Mat();

        Imgproc.cvtColor(mat, mIntermediateMat, Imgproc.COLOR_RGBA2BGR, 3);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String filename = "image.jpg";
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        popItem = menu.add("Popularitätsverfahren");
        medItm = menu.add("Median-Cut-Verfahren");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item == popItem)
        {
            isMedianCut = false;
            Toast.makeText(getApplicationContext(), "Das \"Popularitätsverfahren\" wurde aktiviert.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            isMedianCut = true;
            Toast.makeText(getApplicationContext(), "Das \"Median-Cut-Verfahren\" wurde aktiviert.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
