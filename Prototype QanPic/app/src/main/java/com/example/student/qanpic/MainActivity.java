package com.example.student.qanpic;

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
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends ActionBarActivity {

    Button saveButton;
    Button analyseButton;
    TextView name;
    ImageView foto;
    ListView listView;
    List<Foto> fotos;
    Uri imageUri;
    File bildFile = new File(Environment.getExternalStorageDirectory() + "\\Fotoapp\\bild.png");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveButton = (Button) findViewById(R.id.buttonSave);
        analyseButton = (Button) findViewById(R.id.buttonAnalyse);
        name = (TextView) findViewById(R.id.fotoName);
        foto = (ImageView) findViewById(R.id.imageView);
        listView = (ListView) findViewById(R.id.fotoListView);

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

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Wähle weise:");
                String[] options = new String[] {"Foto machen", "Foto auswählen"};
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
                builder.setCancelable(true);
                builder.show();
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setEnabled(!name.getText().toString().trim().equals(""));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Das Bild wurde gespeichert.", Toast.LENGTH_SHORT).show();
                populateList();
                fotos.add(new Foto(name.getText().toString(), imageUri));
            }
        });

        analyseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Die Analyse war erfolgreich.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void chooseFoto()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Foto auswählen"), 1);
    }

    public void populateList()
    {
        ArrayAdapter<Foto> adapter = new FotoListAdapter();
        listView.setAdapter(adapter);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1)
        {
            imageUri = data.getData();
            foto.setImageURI(data.getData());
        }
        if (resultCode == RESULT_OK && requestCode == 2)
        {
            Bitmap bild = BitmapFactory.decodeFile(bildFile.getAbsolutePath());
            foto.setImageBitmap(bild);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FotoListAdapter extends ArrayAdapter <Foto>
    {
        public FotoListAdapter ()
        {
            super(MainActivity.this, R.layout.fotolist_item, fotos);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            if (view == null)
            {
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