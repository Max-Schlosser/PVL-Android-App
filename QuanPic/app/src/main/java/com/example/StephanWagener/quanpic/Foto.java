package com.example.StephanWagener.quanpic;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by StephanWagener on 06.03.2015.
 * Um Foto-Objekte zu erzeugen, die in der ListView angezeigt werden.
 */
public class Foto
{
    String name;
    Uri foto;
    ArrayList<File> arrayOfPictures = new ArrayList<File>();


    public Foto (String fotoName, Uri image, File picture)
    {
        name = fotoName;
        foto = image;
        arrayOfPictures.add(picture);
    }

    public String getFotoName()
    {
        return name;
    }

    public Uri getFotoImage()
    {
        return foto;
    }
}
