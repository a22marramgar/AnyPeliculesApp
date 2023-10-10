package com.example.anypeliculesapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    final int N_PREGUNTAS = 10;
    final boolean RANDOM = true;
    @GET("/getPreguntes?number="+N_PREGUNTAS+"&random="+RANDOM)
    Call<JsonResponseModel> getPreguntes();

    @POST("/postRespostes")
    Call<Void> postRespuestas(@Body RespostesModel respostes);
}
