package com.example.student.qanpic;

import android.media.Image;
import android.net.Uri;
import android.text.Html;

/**
 * Created by student on 06.03.2015.
 * Um Foto-Objekte zu erzeugen, die in der ListView angezeigt werden.
 */
public class Foto
{
    String name;
    Uri foto;

    public Foto (String fotoName, Uri image)
    {
        name = fotoName;
        foto = image;
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
