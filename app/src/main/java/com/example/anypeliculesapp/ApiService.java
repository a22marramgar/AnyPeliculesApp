package com.example.anypeliculesapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("/getPreguntes")
    Call<JsonResponseModel> getPreguntes();

    @POST("/postRespostes")
    Call<RespostesModel> postRespuestas(@Body RespostesModel respostes);
}
