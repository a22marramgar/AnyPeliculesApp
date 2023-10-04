package com.example.anypeliculesapp;

import java.util.List;

public class Pregunta {

    private int _id;
    private int id_increment = 1;
    private String _nom;

    private List<String> _respostes;

    private int _resposta_correcta;

    private String _URLImage;

    public Pregunta(String nom, List<String> respostes, int resposta_correcta){
        this(nom,respostes,resposta_correcta,null);
    }

    public Pregunta(String nom, List<String> respostes, int resposta_correcta, String URLImage){
        this._id = id_increment;
        id_increment++;
        this._nom = nom;
        this._respostes = respostes;
        this._resposta_correcta = resposta_correcta;
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

    public int getRespostaCorrecta(){
        return this._resposta_correcta;
    }
    public String getURLImage() {
        return this._URLImage;
    }

}
