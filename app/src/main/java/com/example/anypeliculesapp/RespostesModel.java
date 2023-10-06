package com.example.anypeliculesapp;

import java.util.List;

public class RespostesModel {

    public RespostesModel(List<RespostaModel> respostes, float tiempo){
        this.respostes = respostes;
        this.tiempo = tiempo;
    }
    private List<RespostaModel> respostes;

    private float tiempo;

    public List<RespostaModel> getRespostes() {
        return respostes;
    }

    public void setRespostes(List<RespostaModel> respostes) {
        this.respostes = respostes;
    }
}
class RespostaModel {
    private int id;
    private String any;
    private boolean esCorrecta;

    public RespostaModel(int id, String any, boolean esCorrecta) {
        this.id = id;
        this.any = any;
        this.esCorrecta = esCorrecta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAny() {
        return any;
    }

    public void setAny(String any) {
        this.any = any;
    }
}
