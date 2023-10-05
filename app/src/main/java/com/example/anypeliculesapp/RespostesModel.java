package com.example.anypeliculesapp;

import java.util.List;

public class RespostesModel {

    public RespostesModel(List<RespostaModel> respostes){
        this.respostes = respostes;
    }
    private List<RespostaModel> respostes;

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

    public RespostaModel(int id, String any) {
        this.id = id;
        this.any = any;
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
