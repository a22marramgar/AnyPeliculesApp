package com.example.anypeliculesapp;

import java.util.List;

public class JsonResponseModel {
    private List<Pregunta1> preguntes;

    public List<Pregunta1> getPreguntes() {
        return preguntes;
    }

    public void setPreguntes(List<Pregunta1> preguntes) {
        this.preguntes = preguntes;
    }
}

class Pregunta1 {
    private int id;
    private String pregunta;
    private List<Resposta> respostes;
    private int resposta_correcta;
    private String imatge;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public List<Resposta> getRespostes() {
        return respostes;
    }

    public void setRespostes(List<Resposta> respostes) {
        this.respostes = respostes;
    }

    public int getResposta_correcta() {
        return resposta_correcta;
    }

    public void setResposta_correcta(int resposta_correcta) {
        this.resposta_correcta = resposta_correcta;
    }

    public String getImatge() {
        return imatge;
    }

    public void setImatge(String imatge) {
        this.imatge = imatge;
    }
}

class Resposta {
    private int id;
    private String any;

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