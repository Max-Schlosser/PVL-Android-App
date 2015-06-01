package com.example.StephanWagener.quanpic;

// Importe für die verschiedenen Funktionalitäten.
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

// Die Hauptklasse, die beim Start erstellt und initialisiert wird.
// Hier werden die Funktionen eingebunden und es wird auf bestimmte
// Aktivitäten des Nutzers reagiert.
public class MainActivity extends Activity {

    // Globale Variablen für die App
    //Das GUI-Element zur Anzeige der quantisierten Bilder.
    private CameraBridgeViewBase cameraView;

    //Das Flag zur Änderung des Verfahrens.
    private Boolean isMedianCut = null;

    //Ein Zeitstempel, um die Zeit des Haltens des Fingers auf dem Bildschirm zu messen.
    long time = 0;

    //Das aktuell angezeigte Bild, dass dann gespeichert werden kann.
    private Mat currentInput;

    //Initialisierung und Aktivierung des Frames zuständig. Den OpenCVManager einbinden.
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

    // Initialisierung wichtiger Elemente und setzen des Layouts mit Listenern.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        cameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        setFunctionality();
    }

    //Setzen der Listener und einbinden der Funktionen, der dann eintretenden Aktionen.
    private void setFunctionality()
    {
        cameraView.setVisibility(SurfaceView.VISIBLE);

        //Überschreiben des Frames mit dem ausgewählten Verfahren.
        cameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {

            @Override
            public void onCameraViewStopped() {}

            @Override
            public void onCameraViewStarted(int width, int height) {}

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
            {
                if (isMedianCut == null)
                {
                    //Kein Verfahren.
                    currentInput = inputFrame.rgba();
                    return inputFrame.rgba();
                }
                else if (isMedianCut)
                {
                    //Median Filter.
                    Mat medianFilteredMat = new Mat();
                    Imgproc.medianBlur(inputFrame.rgba(), medianFilteredMat, 3);
                    currentInput = medianFilteredMat;
                    return currentInput;
                }
                else
                {
                    //NeuQuant.
                    byte[] byteArray = new byte[(int) (inputFrame.rgba().total() * inputFrame.rgba().channels())];
                    NeuQuant nq = new NeuQuant(byteArray, inputFrame.rgba().rows(), 15);

                    Mat jpegData = new Mat(inputFrame.rgba().rows(), inputFrame.rgba().cols(), CvType.CV_8UC3);
                    jpegData.put(0, 0, nq.process().clone());

                    Mat mat = Highgui.imdecode(jpegData, Highgui.IMREAD_UNCHANGED);

                    currentInput = mat;
                    return currentInput;
                }
            }
        });

        //Funktionalitäten, die durch Touch ausgelöst werden.
        //Beim tippen wird gespeichert und beim Halten auf den
        //Bildschirm wird das Verfahren gewechseln.
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
                        if (isMedianCut == null)
                        {
                            isMedianCut = true;
                        }
                        else if (isMedianCut)
                        {
                            isMedianCut = false;
                        }
                        else
                        {
                            isMedianCut = null;
                        }
                        if (isMedianCut == null)
                        {
                            Toast.makeText(getApplicationContext(), "Kein Verfahren ist aktiviert.", Toast.LENGTH_SHORT).show();
                        }
                        else if (isMedianCut)
                        {
                            Toast.makeText(getApplicationContext(), "Das \"Median-Cut-Verfahren\" wurde aktiviert.", Toast.LENGTH_SHORT).show();
                        }
                        else if (!isMedianCut)
                        {
                            Toast.makeText(getApplicationContext(), "Das \"NeuQuant-Verfahren\" wurde aktiviert.", Toast.LENGTH_SHORT).show();
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

    //Das Speichern des momentan angezeigten Bildes als PNG-File im Ordner QuanPic unter Pictures.
    public void saveImage (Mat mat)
    {
        Mat mIntermediateMat = new Mat();
        Imgproc.cvtColor(mat, mIntermediateMat, Imgproc.COLOR_RGBA2BGRA, 3);
        File path = new File(Environment.getExternalStorageDirectory() + "/Pictures/QuanPic/");
        path.mkdirs();

        String filename = "quanpic" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".png";
        File file = new File(path, filename);

        Boolean bool = Highgui.imwrite(file.toString(), mIntermediateMat);;

        if (bool == true)
            Toast.makeText(getApplicationContext(), "Ihr Bild wurde erfolgreich gespeichert.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Beim Speichern des Bildes ist ein Fehler aufgetreten!", Toast.LENGTH_SHORT).show();
    }

    //Override-Methoden, um die OneCvView zu schließen oder zu pausieren.
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

    //Laden des OpenCvManagers.
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
