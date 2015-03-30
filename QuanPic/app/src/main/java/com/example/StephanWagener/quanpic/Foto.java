package com.example.StephanWagener.quanpic;

import android.net.Uri;

/**
 * Created by StephanWagener on 06.03.2015.
 * Um Foto-Objekte zu erzeugen, die in der ListView angezeigt werden.
 */
public class Foto
{
    String name;
    Uri fotoImage;


    public Foto (String fotoName, Uri image)
    {
        name = fotoName;
        fotoImage = image;
    }

    public String getFotoName()
    {
        return name;
    }

    public Uri getFotoImage()
    {
        return fotoImage;
    }
}
