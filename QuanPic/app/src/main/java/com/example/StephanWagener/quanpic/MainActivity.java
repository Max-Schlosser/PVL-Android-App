package com.example.StephanWagener.quanpic;

// Imports needed for used functionalities.
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

// Main Class. Links layout and implementation.
public class MainActivity extends ActionBarActivity {

    // Global variables
    Button saveButton;
    Button analyseButton;
    TextView name;
    ImageView foto;
    ListView listView;
    List<Foto> fotos;
    Uri imageUri;
    File bildFile = new File(Environment.getExternalStorageDirectory() + "\\Fotoapp\\" + System.currentTimeMillis() +".png" );

    // Initializing required elements when starting the application.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing GUI elements through the given layout ID.
        saveButton = (Button) findViewById(R.id.buttonSave);
        analyseButton = (Button) findViewById(R.id.buttonAnalyse);
        name = (TextView) findViewById(R.id.fotoName);
        foto = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.fotoListView);

        // Disabling save button and initializing tabs.
        saveButton.setEnabled(false);
        TabHost fotoTabHost = (TabHost) findViewById(R.id.tabHost);
        fotoTabHost.setup();

        TabHost.TabSpec tabSpec = fotoTabHost.newTabSpec("maker");
        tabSpec.setContent(R.id.tabFotoMaker);
        tabSpec.setIndicator("FotoMaker");
        fotoTabHost.addTab(tabSpec);

        tabSpec = fotoTabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabFotoList);
        tabSpec.setIndicator("FotoList");
        fotoTabHost.addTab(tabSpec);

        // Determining functionalities to be executed when the image is clicked.
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = {"Foto schießen","Foto auswählen"};
                AlertDialog.Builder dialog = new AlertDialog.Builder (MainActivity.this);
                dialog.setTitle("Wählen Sie:");
                dialog.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.setItems(items, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface d, int choice) {
                        if (choice == 0) {
                            takeFoto();
                        } else if (choice == 1) {
                            chooseFoto();
                        }
                    }
                });
                dialog.show();
            }
        });

        // Checking for an empty string or one that consists only of whitespaces.
        // If that is the case, the button does not get activated.
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // save button is enable if the text is not equal to null or only consist of spaces
                saveButton.setEnabled(!name.getText().toString().trim().equals(""));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Save-button functionalities.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Das Bild wurde gespeichert.", Toast.LENGTH_SHORT).show();
                fotos.add(new Foto(name.getText().toString(), imageUri, bildFile));
                populateList();

                // adapter foto insert
            }
        });

        // Analyze-button functionalities.
        analyseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Die Analyse war erfolgreich.", Toast.LENGTH_SHORT).show();

                // quantisation have to implement here
            }
        });
    }

    // Displays the option to choose a foto from the gallery.
    public void chooseFoto()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Foto auswählen"), 1);
    }

    // Refreshing the list of fotos.
    public void populateList()
    {
        ArrayAdapter<Foto> adapter = new FotoListAdapter();
        listView.setAdapter(adapter);

        // List of taken pictures
    }

    // Displays the option to take a new picture.
    public void takeFoto()
    {
        try
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, bildFile);
            startActivityForResult(intent, 1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Keine kompatible Kamera.", Toast.LENGTH_SHORT).show();
        }

    }

    // Overridden method that displays the chosen or newly taken picture.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1)
        {
            imageUri = data.getData();
            foto.setImageURI(data.getData());
        }
        else if (resultCode == RESULT_OK && requestCode == 2)
        {
            Bitmap bild = BitmapFactory.decodeFile(bildFile.getAbsolutePath());
            foto.setImageBitmap(bild);
        }
    }

    // Private class that links the list with the ListView from the layout.
    private class FotoListAdapter extends ArrayAdapter <Foto> {
        public FotoListAdapter() {
            super(MainActivity.this, R.layout.fotolist_item, fotos);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.fotolist_item, parent, false);
            }
            Foto currentFoto = fotos.get(position);
            TextView name = (TextView) findViewById(R.id.listFotoName);
            name.setText(currentFoto.getFotoName());
            ImageView fotoListItem = (ImageView) findViewById(R.id.listFotoImage);
            fotoListItem.setImageURI(currentFoto.getFotoImage());

            return view;
        }
    }
}