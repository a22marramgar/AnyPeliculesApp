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

    private int nom_size;
    private List<String> _respostes;

    private int respostes_size;
    private String _URLImage;

    private Context context;
    private TextView _nom_textview;

    private RadioGroup _respostes_radiogroup;

    private RadioButton[] respostes_radiobutton;

    public Pregunta(String nom, List<String> respostes, Context context){
        this(nom,respostes,null, context);
    }

    public Pregunta(String nom, List<String> respostes, String URLImage,Context context){
        this._id = id_increment;
        id_increment++;
        this._nom = nom;
        this._respostes = respostes;
        this._URLImage = URLImage;

        this._nom_textview = new TextView(context);
        this.nom_size = 34;
        this._nom_textview.setText(this._nom);
        this._nom_textview.setTextSize(this.nom_size);
        this._nom_textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        this._respostes_radiogroup = new RadioGroup(context);
        this._respostes_radiogroup.setGravity(Gravity.CENTER);
        this.respostes_size = 20;
        this.respostes_radiobutton = new RadioButton[respostes.size()];
        for(int i =0; i<respostes.size(); i++) {
            this.respostes_radiobutton[i] = new RadioButton(context);
            this.respostes_radiobutton[i].setText(respostes.get(i).toString());
            this.respostes_radiobutton[i].setTextSize(respostes_size);
            this.respostes_radiobutton[i].setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            this._respostes_radiogroup.addView(this.respostes_radiobutton[i]);

        }
    }

    public int getId() {
        return _id;
    }

    public String getNom() {
        return _nom;
    }

    public TextView getNom_textview(){
        return this._nom_textview;
    }

    public List<String> getRespostes() {
        return this._respostes;
    }

    public RadioGroup getRespostes_radiogroup(){
        return this._respostes_radiogroup;
    }

    public RadioButton[] getRespostes_radiobutton(){
        return this.respostes_radiobutton;
    }

    public String getURLImage() {
        return this._URLImage;
    }

    public Context getContext(){
        return this.context;
    }

    public void setNomSize(int size){
        this.nom_size = size;
        this._nom_textview.setTextSize(this.nom_size);
    }

    public void setRespostes_size(int size){
        this.respostes_size = size;
        for(int i = 0; i<this.respostes_radiobutton.length; i++){
            this.respostes_radiobutton[i].setTextSize(this.respostes_size);
        }
    }

}
