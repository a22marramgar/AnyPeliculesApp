package com.example.anypeliculesapp;

import java.util.List;

public class JsonResponseModel {
    private List<Pregunta> preguntes;

    public List<Pregunta> getPreguntes() {
        return preguntes;
    }

    public void setPreguntes(List<Pregunta> preguntes) {
        this.preguntes = preguntes;
    }
}