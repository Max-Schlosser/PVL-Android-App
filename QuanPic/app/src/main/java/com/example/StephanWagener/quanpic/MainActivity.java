package com.example.StephanWagener.quanpic;

// Importe für benutzte Funktionalitäten
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

// Main Class. Verbindet Layout mit Implementierung.
public class MainActivity extends ActionBarActivity {

    // Globale Variablen.
    Button saveButton;
    Button analyseButton;
    TextView name;
    ImageView foto;
    ListView listView;
    List<Foto> fotos;
    Uri imageUri;
    File bildFile = new File(Environment.getExternalStorageDirectory() + "\\Fotoapp\\bild.png");

    // Initialisieren erforderlicher Elemente beim Öffnen der App.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GUI Elemente initialisieren anhand der vergebenen Ids im Layout.
        saveButton = (Button) findViewById(R.id.buttonSave);
        analyseButton = (Button) findViewById(R.id.buttonAnalyse);
        name = (TextView) findViewById(R.id.fotoName);
        foto = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.fotoListView);

        // Speichern-Button deaktivieren und Tabs initialisieren.
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

        // Funktionalitäten, die beim Klicken auf das Image ausgeführt werden sollen.
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Wähle weise:");
                CharSequence[] options = new CharSequence[] {"Foto machen", "Foto auswählen"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            takeFoto();
                        }
                        if (which == 1) {
                            chooseFoto();
                        }
                    }
                });
                builder.setNegativeButton("Abbrechen", null);
                builder.create().show();
            }
        });

        // Prüfen auf einen nicht leeren oder nur ausschließlich aus Leerzeichen bestehenden Text.
        // Schlägt diese Prüfung fehl, dann wird der Button nicht aktiv.
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

        // Funktionalitäten des Speichern-Button.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Das Bild wurde gespeichert.", Toast.LENGTH_SHORT).show();
                fotos.add(new Foto(name.getText().toString(), imageUri, bildFile));
                populateList();

                // adapter foto insert
            }
        });

        // Funktionalitäten des Analysieren-Button.
        analyseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Die Analyse war erfolgreich.", Toast.LENGTH_SHORT).show();

                // quantisation have to implement here
            }
        });
    }

    // Stellt die Option dar, dass Foto aus der Gallerie auszuwhlen.
    public void chooseFoto()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Foto auswählen"), 1);
    }

    // Aktualisieren der Liste der Fotos.
    public void populateList()
    {
        ArrayAdapter<Foto> adapter = new FotoListAdapter();
        listView.setAdapter(adapter);

        // List of taken pictures
    }

    // Stellt die Option dar, dass Foto aufzunehmen.
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

    // Überschriebene Methode, die das ausgewählte oder geschossene Foto anzeigt.
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

    // Private Klasse, die die Liste mit der ListView aus dem Layout verbindet.
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