package com.example.anypeliculesapp;

import android.content.Context;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class Pregunta {

    private int _id;
    private int id_increment = 1;
    private String _nom;

    private List<String> _respostes;

    private String _URLImage;

    public Pregunta(String nom, List<String> respostes){
        this(nom,respostes,null);
    }

    public Pregunta(String nom, List<String> respostes, String URLImage){
        this._id = id_increment;
        id_increment++;
        this._nom = nom;
        this._respostes = respostes;
        this._URLImage = URLImage;
    }

    public int getId() {
        return _id;
    }

    public String getNom() {
        return _nom;
    }


    public List<String> getRespostes() {
        return this._respostes;
    }

    public String getURLImage() {
        return this._URLImage;
    }

}
